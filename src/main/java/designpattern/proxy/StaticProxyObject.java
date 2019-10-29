package designpattern.proxy;

/**
 * 代理任务三个角色：
 * 1.抽象对象角色
 * 2.实际对象角色
 * 3.代理对象角色
 * 代理对象要和实际对象一样，实现抽象对象的方法
 * 静态代理缺点：
 * 要和代理对象一样实现同样的接口，会有很多代理类，如果接口增加，代理对象也要改动
 * 解决方案是用动态代理
 */
public class StaticProxyObject implements MethodInterferce{
    MethodInterferce realObject = new RealObject();
    @Override
    public String process(String str) {
        System.out.println("代理任务之前");
        realObject.process(str);
        System.out.println("代理任务之后");
        return "aaa";
    }

    public static void main(String[] args) {
        new StaticProxyObject().process("hello");
    }
}
