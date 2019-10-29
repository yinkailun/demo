## 1。线程五种状态
    1. (new),新建线程，继承Thread或者实现runnable()接口。
    2. (runnable),可执行，使用start()方法,变得可执行，获取运行所需的全部资源，只需等待获取CPU使用权。
    3. (running)获取CPU使用权，执行代码
    4. 阻塞(block)
        由于某种原因放弃CPU使用权，线程挂起，停止运行，直到线程通过某种途径变成runnable.
        1.等待阻塞：调用wait()方法，线程释放所有CPU资源，jvm将其放入等待池，这种状态不能自动唤醒，只能其他线程调用notify或者notifyAll唤醒
        2.同步阻塞，执行获取对象同步锁，该同步锁被其他线程占用，jvm将其方法锁池中。
        3.其他阻塞，join()或者sleep()，或者发出I/O请求（发起系统调用system call），jvm将其设为阻塞状态，直到join线程结束或者超时，sleep超时，I/O请求结束，转入可执行状态。
    5. (dead),线程完成或者异常结束run方法，线程结束。
    
## 2. wait和sleep区别
    1.wait是object的方法，sleep是Thread的方法，wait会释放CPU资源，释放对象锁，只有其他线程通过notify或者notifyAll才可进入runnable,
    2.sleep不会释放对象锁，线程挂起。
    
## 3.start和终止
    1.线程启动是通过start方法，调用start()方法，而不是直接调用run方法,是通过native start0，然后调用的
    2.终止，stop方法已不建议使用(不能保证资源正常释放)，建议使用interrupt
        - interrupt只是对线程状态的一个标记，该值为true，说明其他线程通知该线程，可以进行中断操作了，具体如何操作，取决于该线程本身
        - 实现原理是：改interrupt为true，通过ParkEvent.unpark()唤醒线程
    3.线程复位
        - 通过newThread().interrupt()方法 ,标记该线程就可以进行中断，如何进行复位呢？
        - Thread.interrupted():对当前线程进行复位，吧interrupt改为false;
        - 其他复位方式,sleep,join,wait方法会抛出interrputException，这几个方法都属于阻塞方法，阻塞方法可能等不到外部的触发事件而无法终止，
        所以允许一个线程请求在自己停止正在做的事(调用interrput())，当interrput状态改变时，这几个方法会抛出异常，告诉调用者该线程被中断。
        抛出该异常说明：
          1.由外部线程请求终止该线程；
          2.由于调用interrupt()方法，该线程被唤醒；
          3.检查当前线程interrupt是否为true；
          4.为true，则复位(改为false)，抛出异常)
    
    
## 挂起和恢复
- 都是在LockSupport类里面实现，其底层实现都是在Unsafe里面实现
- 挂起通过park方法，线程将一直阻塞到超时或者恢复
- 恢复通过unpark
## LockSupport函数列表
- // 返回提供给最近一次尚未解除阻塞的 park 方法调用的 blocker 对象，如果该调用不受阻塞，则返回 null。
static Object getBlocker(Thread t)
- // 为了线程调度，禁用当前线程，除非许可可用。
static void park()
- // 为了线程调度，在许可可用之前禁用当前线程。
static void park(Object blocker)
- // 为了线程调度禁用当前线程，最多等待指定的等待时间，除非许可可用。
static void parkNanos(long nanos)
- // 为了线程调度，在许可可用前禁用当前线程，并最多等待指定的等待时间。
static void parkNanos(Object blocker, long nanos)
- // 为了线程调度，在指定的时限前禁用当前线程，除非许可可用。
static void parkUntil(long deadline)
- // 为了线程调度，在指定的时限前禁用当前线程，除非许可可用。
static void parkUntil(Object blocker, long deadline)
- // 如果给定线程的许可尚不可用，则使其可用。
static void unpark(Thread thread)

## park和wait的区别
- wait方法会释放对象的j监视器，唤醒时需要重新获取监视器，park不用
- park，是根据许可(最多一个)若有许可，则返回，不阻塞，否则线程将阻塞挂起。unpark相同
- 但是两者都可以被interrupted