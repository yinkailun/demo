package designpattern.proxy;

import org.springframework.cglib.proxy.InvocationHandler;
import org.springframework.cglib.proxy.Proxy;

import java.lang.reflect.Method;

/**
 * 动态代理有两种，一种是jdk动态代理，一种是cglib动态代理，都是在运行时动态生成代理对象。
 * jdk动态代理：
 * *******************
 * 获取代理类的方法Proxy.newProxyInstance(classloader,interferce,invocationhandler);
 * 被代理对象的类加载器，接口方法，以及代理类所要执行的方法。
 * 不管是动态代理还是静态代理，被代理的类都要实现接口方法，
 * 若只是想代理一个普通的类，可以使用cglib代理方式(子类代理)
 */
public class DynamicProxyObject implements InvocationHandler{
    private Object target;
    public DynamicProxyObject(Object target){
        this.target = target;
    }
    @Override
    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
        System.out.println("begin");
//        System.out.println("method:" + method.getName()+method.getParameterTypes()[0]);
//        System.out.println("objects:" + objects[0]);
        Object invoke = method.invoke(target, objects);
        System.out.println("end");
        return invoke;
    }


    public static void main(String[] args) {
        MethodInterferce target = new RealObject();
        MethodInterferce o1 = (MethodInterferce) Proxy.newProxyInstance(target.getClass().getClassLoader(),
                target.getClass().getInterfaces(), new DynamicProxyObject(target));
        String s = o1.process("hello world");
        System.out.println(s);
    }
}