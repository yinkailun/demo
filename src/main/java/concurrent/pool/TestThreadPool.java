package concurrent.pool;

import sun.nio.ch.ThreadPool;

import java.util.concurrent.*;

/**
 * @author yinkailun
 * @description:
 * @date 2019-08-12 2:58 PM
 */
public class TestThreadPool {

    public static void main(String[] args) {

        Executor threadPoolExecutor = Executors.newCachedThreadPool();
        Executors.newFixedThreadPool(1);
        Executors.newScheduledThreadPool(1);
        Executors.newSingleThreadExecutor();
        Executors.newWorkStealingPool();
//		ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1,5,
//				200, TimeUnit.MILLISECONDS,new ArrayBlockingQueue<>(10),new ThreadPoolExecutor.CallerRunsPolicy());
        for (int i = 0; i < 100; i++) {
            int finalI = i;
            threadPoolExecutor.execute(() -> {
                try {
                    System.out.println(finalI);
                    System.out.println(Thread.currentThread());
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
    }

}
