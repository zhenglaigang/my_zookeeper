package com.zlg.zookeeper.config;

import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class ZKUtils {

  private static ZooKeeper zk;
  //可以指定parent path，自己使用自己的目录
  private static String address = "192.168.220.221:2181,192.168.220.222:2181,192.168.220.223:2181,192.168.220.224:2181/testConf";
  private static DefaultWatch watch = new DefaultWatch();
  private static CountDownLatch init = new CountDownLatch(1);

  public static ZooKeeper getZk() {
    try {
      zk = new ZooKeeper(address, 1000, watch);
      watch.setCc(init);
      init.await();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return zk;
  }
}
