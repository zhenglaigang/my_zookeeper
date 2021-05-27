package com.zlg.zookeeper.config;

public class MyConf {
  private String conf;

  //这个class是你未来实际业务最关心的地方
  public String getConf() {
    return conf;
  }

  public void setConf(String conf) {
    this.conf = conf;
  }
}
