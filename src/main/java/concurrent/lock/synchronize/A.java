package concurrent.lock.synchronize;

public class A{

    //同步代码块
    private void test(){
        synchronized (this){
            System.out.println("synchronized !");
        }
    }

    //同步方法
    private synchronized void test1(){
        System.out.println("haha !");
    }
}