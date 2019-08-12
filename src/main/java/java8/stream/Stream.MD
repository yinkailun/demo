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
                                                      .......
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
0. 遍历开始位置
![image.png](https://i.loli.net/2019/08/09/lhcHC15I8qUExWR.png)
1. wrappedSink初始为filter中的
![image.png](https://i.loli.net/2019/08/09/VyBocdwLuM57QCv.png)
2. 遍历所欲元素，执行wrapSink的accept方法，即上图的accept方法，不满足条件，继续，满足条件向下执行accept方法，downstream为sorted的
![image.png](https://i.loli.net/2019/08/09/9MkHUicWb3osTdV.png)
sorted 的accept方法其实是将上层数据存储到list中
![image.png](https://i.loli.net/2019/08/09/DbNA5fElm1G3JP4.png)
3. 遍历结束后，filter执行end,会调用sorted的end,其实就是对list的排序操作，然后启动downstream.遍历list数据，执行downstream的accept方法,此时downstream为map
![image.png](https://i.loli.net/2019/08/09/YwCfj8n5JVcuMpE.png)
![image.png](https://i.loli.net/2019/08/09/yKoIVGpHvJSAlsR.png)
4. map的accept操作,处理参数，继续向下传播此时downstream为collector
![image.png](https://i.loli.net/2019/08/09/DpmBtoslfHUzO6K.png)
![image.png](https://i.loli.net/2019/08/09/l4dor7xufDqyLhA.png)
5. 最后一步accumulator.accept(state,t),存储数据，accumulator是自己定义的搜集器eg.Collectors.toList()，可以看出，先new一个list,然后执行add操作。
![image.png](https://i.loli.net/2019/08/09/8glhveDm716itLX.png)

阶段2，才会触发数据处理操作,