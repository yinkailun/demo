package designpattern.producerAndConsumer;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @author yinkailun
 * @description:
 * @date 2019-07-18 3:40 PM
 */
public class Demo{

	private static BlockingQueue<String> queue = new ArrayBlockingQueue(10);
	static class Producer{
		public void addQueue(String msg){
			try {
				System.out.println("send msg:"+msg);
				queue.put(msg);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	static class Consumer implements Runnable {

		@Override
		public void run() {
			try{
				String msg ;
				System.out.println("consumer start.");
				while ((msg = queue.take()) != null){

					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					System.out.println("consume:"+msg);
				}
			}catch (Exception e){
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		Producer producer = new Producer();
		new Thread(new Consumer()).start();
		for(int i =0;i<1000;i++){
			producer.addQueue(String.valueOf(i));
		}
	}
}

