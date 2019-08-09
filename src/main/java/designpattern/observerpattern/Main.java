package designpattern.observerpattern;

/**
 * @author yinkailun
 * @description:
 * @date 2019-07-31 3:30 PM
 */
public class Main {
	public static void main(String[] args) {
		//使用jdk自带的观察者模式
		Perple perpl1 = new Perple("观察者1");
		Perple perpl2 = new Perple("观察者2");
		Perple perpl3 = new Perple("观察者3");
		Monkey monkey = new Monkey();
		monkey.addObserver(perpl1);
		monkey.addObserver(perpl2);
		monkey.addObserver(perpl3);
		monkey.action("lauht");
		monkey.action("cry");
	}

}
