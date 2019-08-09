package designpattern.chainofresponsibility;

/**
 * @author yinkailun
 * @description:
 * @date 2019-07-26 4:55 PM
 */
public class ProcessA extends ProcessInterface{

	@Override
	public void process(String info) {
		System.out.println("process A :" + info);
		if(nextProcess != null){
			nextProcess.process(info);
		}
	}
}
