package concurrent.queue.blockingqueue;

import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;

/**
 * @author yinkailun
 * @description:
 * @date 2019-08-15 10:16 AM
 */
public class SynchronousQueueTest {
    public static void main(String[] args) throws InterruptedException {
        final SynchronousQueue<Integer> queue = new SynchronousQueue<Integer>(false);

        new Thread(() -> put(queue, "put1", 1)).start();
        new Thread(() -> put(queue, "put2", 2)).start();
        Thread.sleep(2000);
        new Thread(() -> take(queue, "take1")).start();
    }

    public static void take(BlockingQueue queue, String name) {
        System.out.println(name + " take start");
        try {
            System.out.println(name + " take from putThread: " + queue.take());
        } catch (InterruptedException e) {
        }
        System.out.println(name + " take concurrent.thread end");
    }

    public static void put(BlockingQueue queue, String name, int val) {
        System.out.println(name + " put start");
        try {
            queue.put(val);
        } catch (InterruptedException e) {
        }
        System.out.println(name + " put end");
    }
}
