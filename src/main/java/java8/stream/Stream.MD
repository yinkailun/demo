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
2. 终止操作(只有终止操作才会处理数据,d惰性求值)
    - 非短路(如foreach,toArray)
    - 短路(anyMatch,findFirst...)
阶段1，存储顶层数据，以及处理对象