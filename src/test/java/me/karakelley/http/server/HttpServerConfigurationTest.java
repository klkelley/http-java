package me.karakelley.http.server;

import me.karakelley.http.server.Handler;
import me.karakelley.http.server.ServerConfiguration;
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
    Handler handler = request -> null;
    serverConfig = new ServerConfiguration();
    serverConfig.setHandler(handler);
    assertTrue(serverConfig.getHandler() == handler);
  }
}