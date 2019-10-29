package designpattern.chainofresponsibility;

/**
 * @author yinkailun
 * @description:
 * @date 2019-07-26 4:55 PM
 */
public abstract class ProcessInterface {
    protected ProcessInterface nextProcess;

    protected void setNext(ProcessInterface processInterface) {
        nextProcess = processInterface;
    }

    abstract void process(String info);
}
