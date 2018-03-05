package me.karakelley.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;


public class ServerLogger {
  private final String name;
  private static Logger logger;
  private final List messages = new ArrayList();

  public ServerLogger(String name) {
    this.name = name;
    this.logger = LoggerFactory.getLogger(name);
  }

  public void info(String msg) {
    logger.info(msg);
  }

  public List getMessages() {
    return messages;
  }
}
