package designpattern.chainofresponsibility;

/**
 * @author yinkailun
 * @description:
 * @date 2019-07-26 4:58 PM
 */
public class ProcessC extends ProcessInterface{

	@Override
	void process(String info) {
		System.out.println("process C :" + info);
		if(nextProcess != null){
			nextProcess.process(info);
		}
	}
}
