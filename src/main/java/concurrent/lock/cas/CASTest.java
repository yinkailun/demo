package concurrent.lock.cas;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author yinkailun
 * @description:
 * @date 2019-08-15 3:16 PM
 */
public class CASTest {
    /**
     * 悲观锁，synchronize(同步锁)是一种悲观锁，一个线程一旦获取锁，其他线程就必须挂起(block),然后等待
     * 乐观锁，cas是一种乐观锁，当不使用锁，判断是否满足条件，若不满足条件则重试,直到成功
     * CAS底层操作是一个CPU原子指令，所以CAS本身不存在数据不一致的问题。
     * 优点：不需要加锁，不用挂起线程再改变线程到running状态以及用户态内核态的切换，提高速度
     * 缺点：1.不停自旋会耗费CPU资源，一般会有线程sleep限制(这个要用代码实现，CAS并不支持)；
     * 2.只能保证单个变量的原子性,并不能保证代码块的原子性
     * 3.容日导致ABA问题，就是数据从A->B->A，最终数据表面没有发生变化，但实际发生了变化，解决方案可以加一个version字段来标记。
     * CAS：compare and swap（比较并替换）
     * Unsafe类执行操作,其内部的一些方法可以像c的指针一样直接操作内存
     * 执行函数：CAS(V,E,N)
     * V:要更新的变量；E:期望值；N:新值
     * 如果V = E,则执行更新操作，否则不更新，或者自旋或其他策略尽心重复操作
     */

    public static void main(String[] args) {
        AtomicBoolean atomicBoolean = new AtomicBoolean(true);
        atomicBoolean.compareAndSet(false, true);
    }


}
