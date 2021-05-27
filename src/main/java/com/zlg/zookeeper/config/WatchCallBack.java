package com.zlg.zookeeper.config;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.CountDownLatch;

public class WatchCallBack implements Watcher, AsyncCallback.StatCallback, AsyncCallback.DataCallback {
  ZooKeeper zk;
  MyConf conf;
  CountDownLatch cc = new CountDownLatch(1);

  public void setZk(ZooKeeper zk) {
    this.zk = zk;
  }

  public void setConf(MyConf conf) {
    this.conf = conf;
  }

  //getData()异步回调 data-callback
  @Override
  public void processResult(int rc, String path, Object ctx, byte[] data, Stat stat) {
    System.out.println("----------- data callback");
    if (data != null) {// getData-1，存在数据，解析返回
      String s = new String(data);
      conf.setConf(s);
      cc.countDown();
    }
  }

  //exists()异步回调 stat-callback
  @Override
  public void processResult(int rc, String path, Object ctx, Stat stat) {
    System.out.println("---------stat callback");
    if (stat != null) {//exists-2. 节点存在时，getData 注册watch；并设置getData的callback
      zk.getData("/AppConf", this, this, "abc");
    }
  }

  //watch callback
  @Override
  public void process(WatchedEvent event) {
    switch (event.getType()) {
      case None:
        break;
      // 手动创建节点，exists注册的watch回调通知；
      case NodeCreated:// exists-2，节点不存在时，等待节点创建
        // 节点创建后，getData() 注册watch，发生节点事件会再次watch回调；并设置getData的callback
        System.out.println("---------nodeCreated");
        zk.getData("/AppConf", this, this, "abc");
        break;
      case NodeDeleted:
        //容忍性，是否要求强一致性。如果要求删除节点后，实时处理
        conf.setConf("");//清空（这里模拟简单处理，清空，重新阻塞等待节点创建）
        cc = new CountDownLatch(1);
        break;
      case NodeDataChanged://getData-2，没有数据时，等待节点修改
        // 节点修改后，再次getData 注册watch，发生节点事件会再次watch回调；并设置getData的callback
        zk.getData("/AppConf", this, this, "abc");
        break;
      case NodeChildrenChanged:
        break;
    }

  }

  public void await() {
    System.out.println("-----await");
    //exists-1. exists完成：注册watch；设置stat-callback
    zk.exists("/AppConf",this,this, "abc");
    try {
      cc.await();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
