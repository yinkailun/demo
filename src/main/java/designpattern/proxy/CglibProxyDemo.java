package designpattern.proxy;

import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * cglib动态代理
 * *****************
 * 使用开源asm库，通过修改字节码文件生成代理类的子类，因此某个类若为final,无法生成代理类。
 */
public class CglibProxyDemo implements MethodInterceptor {
    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        System.out.println("start");
        Object invoke = methodProxy.invokeSuper(o, objects);
        System.out.println("end");
        return invoke;
    }

    public static void main(String[] args) {

        Enhancer enhancer = new Enhancer(){{
            setCallback(new CglibProxyDemo());
            setSuperclass(Demo.class);
        }};
        Demo o = (Demo) enhancer.create();
        String re = o.say("hello");
        System.out.println("result:" + re);
    }
}
