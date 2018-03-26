package me.karakelley.http;

import me.karakelley.http.controllers.Controller;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HttpServerConfigurationTest {
  ServerConfiguration serverConfig;

  @Test
  void testGetPort() {
    serverConfig = new ServerConfiguration();
    serverConfig.setPort("0");
    assertEquals(0, serverConfig.getPort());
  }

  @Test
  void testGetController() {
    Controller controller = request -> null;
    serverConfig = new ServerConfiguration();
    serverConfig.setController(controller);
    assertTrue(serverConfig.getController() == controller);
  }
}