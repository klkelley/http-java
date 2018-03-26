package me.karakelley.http;

import me.karakelley.http.controllers.Controller;

public class ServerConfiguration {
  private String port;
  private Controller controller;

  public void setPort(String port) {
    this.port = port;
  }

  public int getPort() {
    return Integer.parseInt(port);
  }

  public void setController(Controller controller) {
    this.controller = controller;
  }

  public Controller getController() {
    return controller;
  }
}
