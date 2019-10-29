package concurrent.thread;

/**
 * @author yinkailun
 * @description:
 * @date 2019-08-15 5:04 PM
 */
public class ThreadTest {
    public static void main(String[] args) {
        Thread a = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.interrupted());
            }
        });
        a.start();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        a.interrupt();

    }

}
