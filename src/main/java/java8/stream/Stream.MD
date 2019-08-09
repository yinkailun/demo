Stream 源码解析
```
//阶段1
		List<Integer> list = new ArrayList<>(Arrays.asList(1,2,3,4,5));
		Stream<String> stream = list.stream().filter(x -> {
			return x > 2;
		}).distinct().map( c->{
			return c+"@@@";
		});
//阶段2		
		stream.collect();
```
## 操作类型
1. 中间操作(Intermediate operations)
    - 无状态(stateless, 不需要获取之前处理的数据，如filter)
    - 有状态(stateful, 需要获取之前处理的数据，如distinct)
2. 终止操作(只有终止操作才会处理数据,惰性求值)
    - 非短路(如foreach,toArray)
    - 短路(anyMatch,findFirst...)
阶段1，存储顶层数据(.stream)，以及处理对象的操作的记录(filter/distinct的实现，每个stream会记录自己的parent stream)
    - stream()
    ```
    //collection的实现，返回Header(stream的实现类,数据在spliterator()中存储,stream本身不存储数据)
    default Stream<E> stream() {
        return StreamSupport.stream(spliterator(), false);
    }
    ```
    - filter()
    ```
    public final Stream<P_OUT> filter(Predicate<? super P_OUT> predicate) {
        Objects.requireNonNull(predicate);
        //返回无状态的操作对象(StatelessOp imp Stream);
        //记录对应的upstream,在wrap时包装
        return new StatelessOp<P_OUT, P_OUT>(this, StreamShape.REFERENCE,
                                     StreamOpFlag.NOT_SIZED) {
            @Override
            **@1**
            // 该方法在处理数据之前调用，将stream进行wrap类似于filterStream(distinctStream(...)),到时候执行的时候依次执行
            Sink<P_OUT> opWrapSink(int flags, Sink<P_OUT> sink) {
                //对sink进行包装，记录downstream，处理的时候层层处理
                return new Sink.ChainedReference<P_OUT, P_OUT>(sink) {
                    @Override
                    public void begin(long size) {
                        downstream.begin(-1);
                    }

                    @Override
                    public void accept(P_OUT u) {
                        if (predicate.test(u))
                            **@2**
                            downstream.accept(u);
                    }
                };
            }
        };
    }
    ```
    - distinct()
    返回一个StatefulOp,有状态的operation,有点略复杂。
    ```
    static <T> ReferencePipeline<T, T> makeRef(AbstractPipeline<?, T, ?> upstream) {
        return new ReferencePipeline.StatefulOp<T, T>(upstream, StreamShape.REFERENCE,
                                                      StreamOpFlag.IS_DISTINCT | StreamOpFlag.NOT_SIZED) {

            <P_IN> Node<T> reduce(PipelineHelper<T> helper, Spliterator<P_IN> spliterator) {
                // If the stream is SORTED then it should also be ORDERED so the following will also
                // preserve the sort order
                TerminalOp<T, LinkedHashSet<T>> reduceOp
                        = ReduceOps.<T, LinkedHashSet<T>>makeRef(LinkedHashSet::new, LinkedHashSet::add,
                                                                 LinkedHashSet::addAll);
                return Nodes.node(reduceOp.evaluateParallel(helper, spliterator));
            }

            @Override
            <P_IN> Node<T> opEvaluateParallel(PipelineHelper<T> helper,
                                              Spliterator<P_IN> spliterator,
                                              IntFunction<T[]> generator) {
                if (StreamOpFlag.DISTINCT.isKnown(helper.getStreamAndOpFlags())) {
                    // No-op
                    return helper.evaluate(spliterator, false, generator);
                }
                else if (StreamOpFlag.ORDERED.isKnown(helper.getStreamAndOpFlags())) {
                    return reduce(helper, spliterator);
                }
                else {
                    // Holder of null state since ConcurrentHashMap does not support null values
                    AtomicBoolean seenNull = new AtomicBoolean(false);
                    ConcurrentHashMap<T, Boolean> map = new ConcurrentHashMap<>();
                    TerminalOp<T, Void> forEachOp = ForEachOps.makeRef(t -> {
                        if (t == null)
                            seenNull.set(true);
                        else
                            map.putIfAbsent(t, Boolean.TRUE);
                    }, false);
                    forEachOp.evaluateParallel(helper, spliterator);

                    // If null has been seen then copy the key set into a HashSet that supports null values
                    // and add null
                    Set<T> keys = map.keySet();
                    if (seenNull.get()) {
                        // TODO Implement a more efficient set-union view, rather than copying
                        keys = new HashSet<>(keys);
                        keys.add(null);
                    }
                    return Nodes.node(keys);
                }
            }

            @Override
            <P_IN> Spliterator<T> opEvaluateParallelLazy(PipelineHelper<T> helper, Spliterator<P_IN> spliterator) {
                if (StreamOpFlag.DISTINCT.isKnown(helper.getStreamAndOpFlags())) {
                    // No-op
                    return helper.wrapSpliterator(spliterator);
                }
                else if (StreamOpFlag.ORDERED.isKnown(helper.getStreamAndOpFlags())) {
                    // Not lazy, barrier required to preserve order
                    return reduce(helper, spliterator).spliterator();
                }
                else {
                    // Lazy
                    return new StreamSpliterators.DistinctSpliterator<>(helper.wrapSpliterator(spliterator));
                }
            }

            ** @3**
            @Override
            Sink<T> opWrapSink(int flags, Sink<T> sink) {
                Objects.requireNonNull(sink);

                if (StreamOpFlag.DISTINCT.isKnown(flags)) {
                    return sink;
                } else if (StreamOpFlag.SORTED.isKnown(flags)) {
                    return new Sink.ChainedReference<T, T>(sink) {
                        boolean seenNull;
                        T lastSeen;

                        @Override
                        public void begin(long size) {
                            seenNull = false;
                            lastSeen = null;
                            downstream.begin(-1);
                        }

                        @Override
                        public void end() {
                            seenNull = false;
                            lastSeen = null;
                            downstream.end();
                        }

                        @Override
                        public void accept(T t) {
                            if (t == null) {
                                if (!seenNull) {
                                    seenNull = true;
                                    downstream.accept(lastSeen = null);
                                }
                            } else if (lastSeen == null || !t.equals(lastSeen)) {
                                downstream.accept(lastSeen = t);
                            }
                        }
                    };
                } else {
                    return new Sink.ChainedReference<T, T>(sink) {
                        Set<T> seen;

                        @Override
                        public void begin(long size) {
                            seen = new HashSet<>();
                            downstream.begin(-1);
                        }

                        @Override
                        public void end() {
                            seen = null;
                            downstream.end();
                        }

                        @Override
                        public void accept(T t) {
                            if (!seen.contains(t)) {
                                seen.add(t);
                                downstream.accept(t);
                            }
                        }
                    };
                }
            }
        };
    }
    ```
   - collect() 
   ```
            //执行此处代码
            collect: container = evaluate(ReduceOps.makeRef(collector));
            evaluate: terminalOp.evaluateSequential(this, sourceSpliterator(terminalOp.getOpFlags()));
            evaluateSequential(ReduceOps实现):
            //执行逻辑
            [return helper.wrapAndCopyInto(makeSink(), spliterator).get();]
            //wrapSink(sink)操做，会依次调用distinct->filter的onWrapSink方法，生成最终的包装类
             copyInto(wrapSink(Objects.requireNonNull(sink)), spliterator);
             //执行copyInto方法
   ```
## eg filter -> sorted() -> map
1. wrappedSink初始为filter中的
![image.png](https://i.loli.net/2019/08/09/VyBocdwLuM57QCv.png)
2. 遍历所欲元素，执行wrapSink的accept方法，即上图的accept方法，不满足条件，继续，满足条件向下执行，downstream为sorted的
![image.png](https://i.loli.net/2019/08/09/9MkHUicWb3osTdV.png)
3. 阿萨斯
![image.png](https://i.loli.net/2019/08/09/987IQdx1aXMjsUe.png)
![image.png](https://i.loli.net/2019/08/09/YUWXvN9Moq7fsCu.png) 
阶段2，才会触发数据处理操作,