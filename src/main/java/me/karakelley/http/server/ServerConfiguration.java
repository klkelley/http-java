package me.karakelley.http.server;

public class ServerConfiguration {
  private String port;
  private Handler handler;

  public void setPort(String port) {
    this.port = port;
  }

  public int getPort() {
    return Integer.parseInt(port);
  }

  public void setHandler(Handler handler) {
    this.handler = handler;
  }

  public Handler getHandler() {
    return handler;
  }
}
