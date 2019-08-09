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
		stream.count();
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
    //collection的实现，返回Header(stream的实现类,数据在spliterator()中存储)
    default Stream<E> stream() {
        return StreamSupport.stream(spliterator(), false);
    }
        ```
阶段2，才会触发数据处理操作,