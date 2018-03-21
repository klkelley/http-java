package me.karakelley.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerConfiguration {
  private final String[] args;
  private final int DEFAULT_PORT = 0;
  private Integer port = -1;
  final Logger logger = LoggerFactory.getLogger(ServerConfiguration.class);

  public ServerConfiguration(String[] args) {
    this.args = args;
  }

  public void setPort() {
    if (noArguments()) {
      port = DEFAULT_PORT;
    } else {
      validatePort();
    }
  }

  public int getPort() {
    setPort();
    return port;
  }

  private void validatePort() {
    try {
      tooManyArguments();
      notAPort();
    } catch (RuntimeException ex) {
      logger.info("Ouch!", ex);
    }
  }

  private boolean noArguments() {
    return args.length == 0;
  }

  private void tooManyArguments() {
    if (args.length != 2) {
      throw new RuntimeException("Not enough arguments");
    }
  }

  private void notAPort() {
    try {
      port = Integer.parseInt(args[1]);
    } catch (NumberFormatException e) {
      throw new RuntimeException(e);
    }
  }
}
