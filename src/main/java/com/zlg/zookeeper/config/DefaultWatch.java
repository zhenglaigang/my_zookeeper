package com.zlg.zookeeper.config;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import java.util.concurrent.CountDownLatch;

public class DefaultWatch implements Watcher {
  CountDownLatch cc;

  public void setCc(CountDownLatch cc) {
    this.cc = cc;
  }
  @Override
  public void process(WatchedEvent event) {
    System.out.println("default watch"+ event.toString());
    switch (event.getState()) {
      case Unknown:
        break;
      case Disconnected:
        break;
      case NoSyncConnected:
        break;
      case SyncConnected:
        cc.countDown();
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
  }
}
