package com.zlg.zookeeper.config;

import org.apache.zookeeper.ZooKeeper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestConfig {

  private ZooKeeper zk;

  @Before
  public void  conn() {
    zk = ZKUtils.getZk();
  }

  @After
  public void close() {
    try {
      zk.close();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void getConf() {
    WatchCallBack watchCallBack = new WatchCallBack();
    watchCallBack.setZk(zk);
    MyConf myConf = new MyConf();
    watchCallBack.setConf(myConf);

    //分两种场景：1.节点不存在 2.节点存在
    watchCallBack.await();


    //真正的业务逻辑发生在这，前面的为工具类
    while (true) {
      if (myConf.getConf().equals("")) {
        System.out.println("-------conf diu le------");
        watchCallBack.await();
      }else {
        System.out.println(myConf.getConf());
      }
      try {
        Thread.sleep(200);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
}
