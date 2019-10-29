package concurrent.lock.reentrant;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @author yinkailun
 * @description:重入锁
 * @date 2019-08-23 4:19 PM
 */
public class ReentrantLockTest {
    static ReentrantLock lock = new ReentrantLock();

    public static void main(String[] args) {

        for (int i = 0; i < 100; i++) {
            int finalI = i;
            new Thread(() -> testReentrantLock(finalI)).start();
        }
    }

    private static void testReentrantLock(int num) {
        lock.lock();
        try {
            System.out.println(String.format("Thread %s start", num));
            Thread.sleep(100);
            System.out.println(String.format("Thread %s end", num));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}
