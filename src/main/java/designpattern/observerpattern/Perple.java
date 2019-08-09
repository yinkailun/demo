package designpattern.observerpattern;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Observable;
import java.util.Observer;

/**
 * @author yinkailun
 * @description:观察者
 * @date 2019-07-31 11:45 AM
 */
@Data
@AllArgsConstructor
public class Perple implements Observer {
	private String name;

	@Override
	public void update(Observable o, Object arg) {
		System.out.println(this.name+"观察到了:"+arg);
	}

}
