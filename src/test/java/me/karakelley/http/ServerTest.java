package me.karakelley.http;

import ch.qos.logback.classic.Logger;
import me.karakelley.http.helpers.EchoClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.net.ConnectException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

class ServerTest {
  private EchoClient client;
  private Server server;
  private Server serverOnSamePort;
  private ServerConfiguration serverConfiguration;
  int count;
  int maxTries;

  private static Logger rootLogger;
  private static Appender appender;

  @BeforeEach
  void setUp() {
    rootLogger = (Logger) LoggerFactory.getLogger("ROOT");
    appender = (Appender) rootLogger.getAppender("appender");
    appender.setPrefix("test");
    appender.start();
    client = new EchoClient();
    count = 0;
    maxTries = 4;
  }

  @AfterEach
  void tearDown() {
    appender.stop();
  }

  @Test
  void testEchoesBackGivenAPort() throws Exception {
    serverConfiguration = new ServerConfiguration(new String[]{"-p", "5000"});
    server = new Server(serverConfiguration.getPort());
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
    serverConfiguration = new ServerConfiguration(new String[]{});
    server = new Server(serverConfiguration.getPort());
    ExecutorService executorService = Executors.newSingleThreadExecutor();
    executorService.submit(() -> server.start());

    while (true) {
      try {
        client.connect("127.0.0.1", server.getPortNumber());
        if (client.sendMessage("success").equals("echo: success")) break;
      } catch (ConnectException | NullPointerException ex) {
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
  void testPortIsUnavailable() throws Exception {
    serverConfiguration = new ServerConfiguration(new String[]{"-p", "4050"});
    serverOnSamePort = new Server(serverConfiguration.getPort());
    ExecutorService executorService = Executors.newSingleThreadExecutor();
    executorService.submit(() -> serverOnSamePort.start());
    Thread.sleep(10);

    server = new Server(serverConfiguration.getPort());
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
    assertTrue(appender.getEvents().contains("[INFO] Address already in use (Bind failed)"));
  }

  @Test
  void testPortTooLarge() throws Exception {
    serverConfiguration = new ServerConfiguration(new String[]{"-p", "404345"});
    server = new Server(serverConfiguration.getPort());
    ExecutorService executorService = Executors.newSingleThreadExecutor();
    executorService.submit(() -> server.start());

    String message;
    while (true) {
      try {
        Thread.sleep(15);
        break;
      } catch (RuntimeException ex) {
        count++;
        if (count > maxTries) {
          throw ex;
        }
      }
    }
    assertTrue(appender.getEvents().contains("[INFO] Port value out of range: 404345"));
  }
}
