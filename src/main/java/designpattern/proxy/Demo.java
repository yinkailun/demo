package designpattern.proxy;

public class Demo {
    String say(String str) {
        String re = "say:" + str;
        System.out.println(re);
        return re;
    }
}