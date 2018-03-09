package me.karakelley.http;

import me.karakelley.http.helpers.EchoClient;
import me.karakelley.http.mocks.MockLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

class ServerTest {
  private EchoClient client;
  private Server server;
  private Server serverOnSamePort;
  private ServerLogger mockLogger = new MockLogger("Servertest");
  private ServerConfiguration serverConfiguration;
  int count;
  int maxTries;

  @BeforeEach
  void setUp() {
    client = new EchoClient();
    count = 0;
    maxTries = 4;
  }

  @Test
  void testEchoesBackGivenAPort() throws Exception {
    serverConfiguration = new ServerConfiguration(new String[]{"-p", "5000"}, mockLogger);
    server = new Server(serverConfiguration.getPort(), mockLogger);
    ExecutorService executorService = Executors.newSingleThreadExecutor();
    executorService.submit(() -> server.start());

    while (true) {
      try {
        client.connect("127.0.0.1", 5000);
        if (client.sendMessage("success").equals("echo: success")) break;
      } catch (ConnectException ex) {
        Thread.sleep(10);
        count++;
        if (count > maxTries) {
          throw ex;
        }
      }
    }
    assertEquals("echo: hey", client.sendMessage("hey"));
  }

  @Test
  void testServerDefaultsToPortZeroWhenNoPorGiven() throws Exception {
    serverConfiguration = new ServerConfiguration(new String[]{}, mockLogger);
    server = new Server(serverConfiguration.getPort(), mockLogger);
    ExecutorService executorService = Executors.newSingleThreadExecutor();
    executorService.submit(() -> server.start());
    String message = null;

    try {
      client.connect("127.0.0.1", 0);
    } catch (ConnectException | NoRouteToHostException ex) {
      message = ex.getMessage();
    }
    assertEquals(message, "Can't assign requested address (Address not available)");
  }

  @Test
  void testPortIsUnavailable() throws Exception {
    serverConfiguration = new ServerConfiguration(new String[]{"-p", "4050"}, mockLogger);
    serverOnSamePort = new Server(serverConfiguration.getPort(), mockLogger);
    ExecutorService executorService = Executors.newSingleThreadExecutor();
    executorService.submit(() -> serverOnSamePort.start());
    Thread.sleep(10);

    server = new Server(serverConfiguration.getPort(), mockLogger);
    ExecutorService executorService1 = Executors.newFixedThreadPool(1);
    executorService1.submit(() -> server.start());

    while (true) {
      try {
        client.connect("127.0.0.1", 4050);
        if (client.sendMessage("success").equals("echo: success")) break;
      } catch (ConnectException ex) {
        Thread.sleep(10);
        count++;
        if (count > maxTries) {
          throw ex;
        }
      }
    }
    Object message = mockLogger.getMessages().get(1);
    assertTrue(message.toString().contains("Address already in use (Bind failed)"));
  }

  @Test
  void testPortTooLarge() throws Exception {
    serverConfiguration = new ServerConfiguration(new String[]{"-p", "404345"}, mockLogger);
    server = new Server(serverConfiguration.getPort(), mockLogger);
    ExecutorService executorService = Executors.newSingleThreadExecutor();
    executorService.submit(() -> server.start());

    String message;
    while (true) {
      try {
        message = mockLogger.getMessages().get(0).toString();
        if (message.contains("range")) break;
      } catch (RuntimeException ex) {
        Thread.sleep(10);
        count++;
        if (count > maxTries) {
          throw ex;
        }
      }
    }
    assertTrue(message.contains("Port value out of range"));
  }
}
