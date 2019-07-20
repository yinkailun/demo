package zookeeper.test;

import org.apache.zookeeper.*;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @author yinkailun
 * @description:
 * @date 2019-07-19 3:11 PM
 */
public class TestZooKeeperConnection implements Watcher{

	private static CountDownLatch countDownLatch = new CountDownLatch(1);
	public static void main(String[] args) throws KeeperException, InterruptedException {
		try {
			ZooKeeper zooKeeper = new ZooKeeper("10.211.55.4:2181",5000,new TestZooKeeperConnection());
//			zooKeeper.create("/uu","test".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
			zooKeeper.create("/ii","test".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL, new TestCallBack(),"hello world.");
//			zooKeeper.setData()
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void process(WatchedEvent watchedEvent) {
		System.out.println("watcherType:"+watchedEvent.getType());
	}
}
