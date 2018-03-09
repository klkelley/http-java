package me.karakelley.http;

public class ServerConfiguration {
  private final String[] args;
  private final int DEFAULT_PORT = 0;
  private Integer port;
  private static ServerLogger logger;

  public ServerConfiguration(String[] args, ServerLogger logger) {
    this.args = args;
    this.logger = logger;
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
      logger.info(ex.getMessage());
      System.exit(1);
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
