## 同步队列
- 作为阻塞队列的实现，实际中很少用到，但在线程池创建CachedThreadPool时有使用
- 特点
  * put操作在相匹配的take操作执行完成之后才会继续执行
  * 是一个同步阻塞队列，每个插入操作都要等待其他线程相应的移除操作，比较像是消费者和生产者的会和通道，比较适合"切换"和"传递"这种场景
  * SQ不存储元素，peek拿到的数据时null
  * 有公平策略FIFO(TransferQueue)和非公平策略LIFO(TransferStack)
  * ![image.png](https://i.loli.net/2019/08/16/LozABqTGPydIteD.png)
  * 通过自旋和LockSupport的park和unpark实现阻塞
- 公平模式和非公平模式
  * TransferQueue(fair FIFO)
    1. put操作操作
    ```java
    public void put(E e) throws InterruptedException {
        if (e == null) throw new NullPointerException();
        if (transferer.transfer(e, false, 0) == null) {
            Thread.interrupted();
            throw new InterruptedException();
        }
    }
     //transfer中有一行代码，是对普通操作进行阻塞
     Object x = awaitFulfill(s, e, timed, nanos);
    ```
    ![image.png](https://i.loli.net/2019/08/23/voMtcH3mGxBlNa4.png)
    先进行自旋，自旋一定次数之后进入阻塞状态，使用LockSupport.park
    2. take操作 
    ![image.png](https://i.loli.net/2019/08/23/3gcDdObfnz5omHP.png)
    在这里使用FIFO策略对对应的tread的进行LockSupport.unpark(),唤醒对应的put操作。
- 非公平模式
  * TransferStack(LIFO)
  1. put操作一样，自旋+park
  2. take操作是在tryMatch中进行unpark操作。
  ```java
            boolean tryMatch(SNode s) {
                if (match == null &&
                    UNSAFE.compareAndSwapObject(this, matchOffset, null, s)) {
                    Thread w = waiter;
                    if (w != null) {    // waiters need at most one unpark
                        waiter = null;
                        LockSupport.unpark(w);
                    }
                    return true;
                }
                return match == s;
            }
```    
