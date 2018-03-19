package me.karakelley.http;

import ch.qos.logback.classic.Logger;
import me.karakelley.http.helpers.ClientHelper;
import me.karakelley.http.utility.InMemoryAppender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

class HttpServerTest {
  private ClientHelper client;
  private HttpServer httpServer;
  private HttpServer httpServerOnSamePort;

  private static Logger rootLogger;
  private static InMemoryAppender inMemoryAppender;

  @BeforeEach
  void setUp() {
    rootLogger = (Logger) LoggerFactory.getLogger("ROOT");
    inMemoryAppender = (InMemoryAppender) rootLogger.getAppender("InMemoryAppender");
    inMemoryAppender.setPrefix("test");
    inMemoryAppender.start();
    client = new ClientHelper();
  }

  @AfterEach
  void tearDown() {
    inMemoryAppender.stop();
  }

  @Test
  void testSendsResponseWithBodyGivenValidRequest() throws Exception {
    httpServer = new HttpServer(0);
    ExecutorService executorService = Executors.newSingleThreadExecutor();
    executorService.submit(() -> httpServer.start());
    Thread.sleep(10);
    client.connectWithTry("127.0.0.1", httpServer.getPortNumber());

    assertTrue(client.sendMessage("GET / HTTP/1.1 \r\n").contains("Hello World"));
  }

  @Test
  void testSendsResponseGivenValidRequest() throws Exception {
    httpServer = new HttpServer(0);
    ExecutorService executorService = Executors.newSingleThreadExecutor();
    executorService.submit(() -> httpServer.start());
    Thread.sleep(10);
    client.connectWithTry("127.0.0.1", httpServer.getPortNumber());

    ArrayList<String> response = client.sendMessage("GET / HTTP/1.1 \r\n");
    assertTrue(response.contains("HTTP/1.1 200 OK"));
  }

  @Test
  void testSendsResponseWithHeadersGivenValidRequest() throws Exception {
    httpServer = new HttpServer(0);
    ExecutorService executorService = Executors.newSingleThreadExecutor();
    executorService.submit(() -> httpServer.start());
    Thread.sleep(10);
    client.connectWithTry("127.0.0.1", httpServer.getPortNumber());

    assertTrue(client.sendMessage("GET / HTTP/1.1 \r\n").contains("Content-Type: text/plain"));
  }

  @Test
  void testSends400GivenInvalidPath() throws Exception {
    httpServer = new HttpServer(0);
    ExecutorService executorService = Executors.newSingleThreadExecutor();
    executorService.submit(() -> httpServer.start());
    Thread.sleep(10);
    client.connectWithTry("127.0.0.1", httpServer.getPortNumber());

    ArrayList<String> response = client.sendMessage("GET /nowhere HTTP/1.1 \r\n");
    assertTrue(response.contains("HTTP/1.1 404 Not Found"));
  }

  @Test
  void testSends400GivenPartialRequest() throws Exception {
    httpServer = new HttpServer(4000);
    ExecutorService executorService = Executors.newSingleThreadExecutor();
    executorService.submit(() -> httpServer.start());
    Thread.sleep(10);
    client.connectWithTry("127.0.0.1", 4000);

    ArrayList<String> response = client.sendMessage("GET /\r\n");
    assertTrue(response.contains("HTTP/1.1 404 Not Found"));
  }

  @Test
  void testSends400GivenInvalidMethod() throws Exception {
    httpServer = new HttpServer(4000);
    ExecutorService executorService = Executors.newSingleThreadExecutor();
    executorService.submit(() -> httpServer.start());
    client.connectWithTry("127.0.0.1", 4000);

    assertTrue(client.sendMessage("GIT / HTTP/1.1\r\n").contains("HTTP/1.1 404 Not Found"));
  }

  @Test
  void testSends400GivenInvalidProtocol() throws Exception {
    httpServer = new HttpServer(4000);
    ExecutorService executorService = Executors.newSingleThreadExecutor();
    executorService.submit(() -> httpServer.start());
    client.connectWithTry("127.0.0.1", 4000);

    assertTrue(client.sendMessage("GET / HT\r\n").contains("HTTP/1.1 404 Not Found"));
  }

  @Test
  void testPortIsUnavailable() throws Exception {
    httpServer = new HttpServer(0);
    ExecutorService executorService = Executors.newSingleThreadExecutor();
    executorService.submit(() -> httpServer.start());
    Thread.sleep(10);

    httpServerOnSamePort = new HttpServer(httpServer.getPortNumber());
    ExecutorService executorService1 = Executors.newFixedThreadPool(1);
    executorService1.submit(() -> httpServerOnSamePort.start());
    client.connectWithTry("127.0.0.1", httpServer.getPortNumber());

    assertTrue(inMemoryAppender.getEvents().contains("Address already in use (Bind failed)"));
  }

  @Test
  void testPortTooLarge() throws Exception {
    httpServer = new HttpServer(404345);
    ExecutorService executorService = Executors.newSingleThreadExecutor();
    executorService.submit(() -> httpServer.start());
    int count = 0;
    int maxTries = 4;

    try {
      Thread.sleep(10);
    } catch (RuntimeException ex) {
      count++;
      if (count > maxTries) {
        throw ex;
      }
    }
    assertTrue(inMemoryAppender.getEvents().contains("Port value out of range: 404345"));
  }

  @Test
  void testSocketNotAvailable() {
    httpServer = new HttpServer(4050);
    ExecutorService executorService = Executors.newSingleThreadExecutor();
    executorService.submit(() -> httpServer.start());

    try {
      client.connect("127.0.0.1", 4050);
    } catch (Exception ex) {
      assertEquals("Connection refused (Connection refused)", ex.getMessage());
    }
  }
}
