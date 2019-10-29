package designpattern.proxy;

public class RealObject implements MethodInterferce{
    @Override
    public String process(String str) {
        System.out.println("实际执行者开始执行"+str);
        return "我是实际执行者"+str;
    }
}
