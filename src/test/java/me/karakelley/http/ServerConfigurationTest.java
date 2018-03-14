package me.karakelley.http;

import me.karakelley.http.mocks.MockLogger;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ServerConfigurationTest {
  ServerConfiguration serverConfig;
  final ServerLogger mockLogger = new MockLogger("ServerConfiguration");

  @Test
  void testNoPortGiven() {
    serverConfig = new ServerConfiguration(new String[]{}, mockLogger);
    serverConfig.setPort();
    assertEquals(0, serverConfig.getPort());
  }

  @Test
  void testTooLittleArguments() {
    serverConfig = new ServerConfiguration(new String[]{"5000"}, mockLogger);
    serverConfig.setPort();

    Object message = mockLogger.getMessages().get(0);
    assertTrue(message.toString().contains("Not enough arguments"));
  }

  @Test
  void testGivenAPort() {
    serverConfig = new ServerConfiguration(new String[]{"-p", "5000"}, mockLogger);
    serverConfig.setPort();

    assertEquals(5000, serverConfig.getPort());
  }

  @Test
  void testInvalidPort() {
    serverConfig = new ServerConfiguration(new String[]{"-p", "badinput"}, mockLogger);
    serverConfig.setPort();

    Object message = mockLogger.getMessages().get(0);
    assertTrue(message.toString().contains("lang.NumberFormatException: For input string: "));
  }

  @Test
  void testTooManyArguments() {
    serverConfig = new ServerConfiguration(new String[]{"-p", "badinput", "otherstuff"}, mockLogger);
    serverConfig.setPort();
    Object message = mockLogger.getMessages().get(0);
    assertTrue(message.toString().contains("Not enough arguments"));
  }

}