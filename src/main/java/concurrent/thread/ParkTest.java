package concurrent.thread;

import java.util.concurrent.locks.LockSupport;

/**
 * @author yinkailun
 * @description:
 * @date 2019-08-23 5:09 PM
 */
public class ParkTest {
    private static Thread T;

    public static void main(String[] args) {
        T = new Thread(() -> {
            System.out.println("T start.");
            LockSupport.park();
            System.out.println("T end.");
        });
        T.start();
        new Thread(() -> {
            System.out.println("unpark T start");
            try {
                Thread.sleep(1000);
                LockSupport.unpark(T);
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("unpark T end");
        }).start();
    }

}
