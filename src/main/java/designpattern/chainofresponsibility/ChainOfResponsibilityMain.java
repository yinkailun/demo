package designpattern.chainofresponsibility;

/**
 * @author yinkailun
 * @description:
 * @date 2019-07-26 5:00 PM
 */
public class ChainOfResponsibilityMain {
    public static void main(String[] args) {
        ProcessInterface process = new ProcessA();
        ProcessInterface processB = new ProcessB();
        ProcessInterface processC = new ProcessC();
        process.setNext(processB);
//		processB.setNext(processC);
        process.process("开始执行");
    }

}
