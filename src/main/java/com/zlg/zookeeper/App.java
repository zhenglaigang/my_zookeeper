package com.zlg.zookeeper;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.CountDownLatch;

/**
 * Hello zookeeper:
 * new zk
 * 增删改查
 * 注册watch、回调
 * 两种API模式的使用
 */
public class App {
  public static void main(String[] args) throws Exception {
    System.out.println("Hello World!");

    //zk是有session概念的，没有连接池的概念
    //watch 分两种：
      // 1. 一种是new zk时注册的watch。是session级别的，与path、znode 无关
      // 2. 另一种是 znode 事件的watch。注册只发生在读类型调用（如 get exists）；回调只发生一次
    final CountDownLatch countDownLatch = new CountDownLatch(1);
    final ZooKeeper zk = new ZooKeeper("192.168.220.221,192.168.220.222,192.168.220.223,192.168.220.224",
        3000, new Watcher() {
      //session Watch的回调方法
      @Override
      public void process(WatchedEvent event) {
        Event.KeeperState state = event.getState();
        Event.EventType type = event.getType();
        String path = event.getPath();
        System.out.println("session watchedEvent:" + event.toString());

        //可以根据zk状态做相应处理
        switch (state) {
          case Unknown:
            break;
          case Disconnected:
            break;
          case NoSyncConnected:
            break;
          case SyncConnected:
            countDownLatch.countDown();
            System.out.println("connected");
            break;
          case AuthFailed:
            break;
          case ConnectedReadOnly:
            break;
          case SaslAuthenticated:
            break;
          case Expired:
            break;
        }

        //可以根据事件类型，做相应处理
        switch (type) {
          case None:
            break;
          case NodeCreated:
            break;
          case NodeDeleted:
            break;
          case NodeDataChanged:
            break;
          case NodeChildrenChanged:
            break;
        }
      }
    });

    countDownLatch.await();
    ZooKeeper.States state = zk.getState();
    switch (state) {
      case CONNECTING:
        System.out.println("ing.......");
        break;
      case ASSOCIATING:
        break;
      case CONNECTED:
        System.out.println("ed..........");
        break;
      case CONNECTEDREADONLY:
        break;
      case CLOSED:
        break;
      case AUTH_FAILED:
        break;
      case NOT_CONNECTED:
        break;
    }


    //两类API模型 ：同步返回  异步回调

    //1.同步返回
    String pathName = zk.create("/abab", "olddata".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);

    //取数据  返回值-真正的数据；state返回节点信心数据
    final Stat stat = new Stat();
    byte[] ababs = zk.getData("/abab", new Watcher() {
      //节点事件 watch的回调方法
      @Override
      public void process(WatchedEvent event) {
        System.out.println("getData watchedEvent:" + event.toString());

        //可以再注册watch，注册分两种：true，表示default watch即session-watch；指定watch，znode事件的watch
        try {
          //zk.getData("/abab", true, stat);    //default watch
          zk.getData("/abab", this, stat);  //znode watch
        } catch (KeeperException e) {
          e.printStackTrace();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }, stat);
    System.out.println(new String(ababs));

    //发生修改事件，触发回调
    Stat stat1 = zk.setData("/abab", "newdata".getBytes(), 0);
    //再次修改，还会触发吗
    zk.setData("/abab", "newdata01".getBytes(), stat1.getVersion());


    //2.异步回调
    System.out.println("-------------async start--------------");
    zk.getData("/abab", false, new AsyncCallback.DataCallback() {
      @Override
      public void processResult(int i, String path, Object ctx, byte[] bytes, Stat stat) {
        System.out.println("-----------async callback");
        System.out.println(new String(bytes));
        System.out.println(ctx.toString());
      }
    }, "abc");
    System.out.println("------------async over -------------");


    //阻塞住
    try {
      Thread.sleep(1231231);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
