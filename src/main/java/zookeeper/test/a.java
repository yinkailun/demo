package zookeeper.test;

import org.apache.zookeeper.AsyncCallback;

/**
 * @author yinkailun
 * @description:
 * @date 2019-07-19 6:31 PM
 */

class TestCallBack implements AsyncCallback.StringCallback {

    @Override
    public void processResult(int i, String s, Object o, String s1) {
        System.out.println("i" + i + ",s:" + s + ",o:" + o + ",s1:" + s1);
    }
}

