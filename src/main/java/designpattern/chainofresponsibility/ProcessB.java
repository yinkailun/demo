package designpattern.chainofresponsibility;

/**
 * @author yinkailun
 * @description:
 * @date 2019-07-26 4:57 PM
 */
public class ProcessB extends ProcessInterface{

	@Override
	void process(String info) {
		System.out.println("process B :" + info);
		if(nextProcess != null){
			nextProcess.process(info);
		}
	}
}
