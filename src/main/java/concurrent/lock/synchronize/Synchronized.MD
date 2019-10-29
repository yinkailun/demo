# synchronized
 - 是一个重量级锁，JavaSE1.6之后做了改进，显得不是那么笨重
 - synchronized可以把任一非null对象作为锁，在jvm hotspot中，被称为对象监视器(Object monitor)
 - synchronized内置锁是一种对象锁(锁的是对象而不是对象的引用)，粒度是对象，可充入，防止死锁(eg：子类的同步方法调用父类的同步方法，不重入，会发生死锁)
 
## Synchronized修饰符
- 修饰静态方法(对当前类加锁)
  要获取当前类的锁
- 修饰非静态方法(对调用方法的实例加锁)
  要获取当前对象的锁
- 修饰代码块(对指定的对象加锁)
  要获取指定对象的锁
## 同步方法和同步代码块
- ![image.png](https://i.loli.net/2019/09/16/H8RQWvn4NzGuO9E.png)
- javap 反编译class文件
命令：javap -verbose -private A
```
{
  public concurrent.lock.synchronize.A();
    descriptor: ()V
    flags: ACC_PUBLIC
  private void test();
    descriptor: ()V
    flags: ACC_PRIVATE
    Code:
      stack=2, locals=3, args_size=1
         0: aload_0
         1: dup
         2: astore_1
         3: monitorenter
         4: getstatic     #2                  // Field java/lang/System.out:Ljava/io/PrintStream;
         7: ldc           #3                  // String synchronized !
         9: invokevirtual #4                  // Method java/io/PrintStream.println:(Ljava/lang/String;)V
        12: aload_1
        13: monitorexit
        14: goto          22
        17: astore_2
        18: aload_1
        19: monitorexit
        20: aload_2
        21: athrow
        22: return
  private synchronized void test1();
    descriptor: ()V
    flags: ACC_PRIVATE, ACC_SYNCHRONIZED
    Code:
      stack=2, locals=1, args_size=1
         0: getstatic     #2                  // Field java/lang/System.out:Ljava/io/PrintStream;
         3: ldc           #5                  // String haha !
         5: invokevirtual #4                  // Method java/io/PrintStream.println:(Ljava/lang/String;)V
         8: return
      LineNumberTable:
        line 14: 0
        line 15: 8
}
```
- 同步代码块
  从反编译可见，同步代码快有monitorenter和两个monitorexit(一个正常退出，一个异常退出)。
  线程执行monitorrenter时需要获取monitor的所有权，相当于获得锁。
  - 如果monitor的进入数为0，说明该monitor没有线程持有，可以获取到该monitor，进入数+1，该线程持有该monitor
  - 如果该线程已持有该monitor，则重入次数+1
  - 如果进入次数不为0,说明其他线程持有该monitor，当前线程挂起，直到monitor进入数为0,再次重新尝试获取monitor
- 同步方法是通过'ACC_SYNCHRONIZED'标识符来确定是否被设置，原理并无太大差别,只是标记一个是进入数(monitorenter/monitorexit触发)判断，一个是ACC_SYNCHRONIZED常量标记。

## Java对象的存储以及synchronized核心原理
- synchronized(lock)是基于lock对象的生命周期来控制锁的粒度的。
- 对象在内存中的布局
  - 对象头:Header
  - 实例数据:Instance Data
  - 对齐填充:Padding
- 对象头和monitor是实现synchronized的基础
  1. 对象头主要包含两部分数据：Mark Word(标记字段)和 Klass Pointer(类型指针)
    - Mark Word:存储对象运行时数据，如hashcode,GC分带年龄，锁状态标志，线程持有的锁，偏向线程id，偏向时间戳(是实现轻量级锁和偏向锁的关键)
    - Klass Pointer指向类元数据的指针，确定是哪个类的实例
  2. Monitor:通常被描述为对象监视器
    - Contention List：所有请求锁的线程将被首先放置到该竞争队列
    - Entry List：Contention List中那些有资格成为候选人的线程被移到Entry List
    - Wait Set：那些调用wait方法被阻塞的线程被放置到Wait Set
    - OnDeck：任何时刻最多只能有一个线程正在竞争锁，该线程称为OnDeck
    - Owner：获得锁的线程称为Owner
    - !Owner：释放锁的线程
    过程描述：1.参加竞争的线程会添加到ContentionList中，是一个FIFO队列(新增在队首，获取在队尾);
    2.EntryList和ContentionList在逻辑上同属等待队列，但Contention会被线程频繁访问(进队出队操作)，为了降低ContentionList的压力，创建EntryList.
    3.Owner线程执行unlock之后，如果发现Entry为空，则将Contention移到Entry中。
    4.Owner执行unlock之后，会从Entry指定一个线程(一般为Head)，作为Ready(OnDeck)线程，OnDeck线程重新竞争锁。
    5.OnDeck获取锁变为Owner，获取不到则保留在Entry,被阻塞则转移到Wait Set ,唤醒之后重新回到Entry。
    
## 1.6之后synchronized的优化
1. 自旋锁
