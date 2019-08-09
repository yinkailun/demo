package designpattern.observerpattern;

import java.util.Observable;

/**
 * @author yinkailun
 * @description:被观察者
 * @date 2019-07-31 11:42 AM
 */
public class Monkey extends Observable {

	public void action(String action){
		System.out.println("猴子执行动作："+action);
		//必须设置更改才会触发
		setChanged();
		notifyObservers(action);
	}
}
