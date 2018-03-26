package me.karakelley.http;

import ch.qos.logback.classic.Logger;
import me.karakelley.http.controllers.Application;
import me.karakelley.http.helpers.ClientHelper;
import me.karakelley.http.helpers.TempFilesHelper;
import me.karakelley.http.utility.InMemoryAppender;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

class HttpServerTest {
  private static InMemoryAppender inMemoryAppender;

  @Test
  void testSendsResponseGivenValidRequest() throws Exception {
    ClientHelper client = new ClientHelper();
    ServerConfiguration config = new ServerConfiguration();
    config.setPort("0");
    config.setController(new Application());
    HttpServer httpServer = new HttpServer(config);
    ExecutorService executorService = Executors.newSingleThreadExecutor();
    executorService.submit(httpServer::start);
    client.connectWithTry("127.0.0.1", httpServer);
    ArrayList<String> response = client.sendMessage("GET / HTTP/1.1 \r\n");

    assertTrue(response.contains("HTTP/1.1 200 OK"));
    assertTrue(response.contains("Hello World"));
  }

  @Test
  void testSendsResponseWithHeadersGivenValidRequest() throws Exception {
    ClientHelper client = new ClientHelper();
    ServerConfiguration config = new ServerConfiguration();
    config.setPort("0");
    config.setController(new Application());
    HttpServer httpServer = new HttpServer(config);
    ExecutorService executorService = Executors.newSingleThreadExecutor();
    executorService.submit(httpServer::start);
    client.connectWithTry("127.0.0.1", httpServer);
    ArrayList<String> response = client.sendMessage("GET / HTTP/1.1 \r\n");

    assertTrue(response.contains("Content-Type: text/plain") && response.contains("Content-Length: 11"));
  }

  @Test
  void testSends400GivenInvalidPath() throws Exception {
    ClientHelper client = new ClientHelper();
    ServerConfiguration config = new ServerConfiguration();
    config.setPort("0");
    config.setController(new Application(new PublicDirectory("nodirectory")));
    HttpServer httpServer = new HttpServer(config);
    ExecutorService executorService = Executors.newSingleThreadExecutor();
    executorService.submit(httpServer::start);
    client.connectWithTry("127.0.0.1", httpServer);

    ArrayList<String> response = client.sendMessage("GET /nowhere HTTP/1.1 \r\n");
    assertTrue(response.contains("HTTP/1.1 404 Not Found"));
  }

  @Test
  void testSends400GivenPartialRequest() throws Exception {
    ClientHelper client = new ClientHelper();
    ServerConfiguration config = new ServerConfiguration();
    config.setPort("0");
    config.setController(new Application(new PublicDirectory("nodirectory")));
    HttpServer httpServer = new HttpServer(config);
    ExecutorService executorService = Executors.newSingleThreadExecutor();
    executorService.submit(httpServer::start);
    client.connectWithTry("127.0.0.1", httpServer);

    ArrayList<String> response = client.sendMessage("GET / \r\n");
    assertTrue(response.contains("HTTP/1.1 404 Not Found"));
  }

  @Test
  void testSends400GivenInvalidMethod() throws Exception {
    ClientHelper client = new ClientHelper();
    ServerConfiguration config = new ServerConfiguration();
    config.setPort("0");
    config.setController(new Application());
    HttpServer httpServer = new HttpServer(config);
    ExecutorService executorService = Executors.newSingleThreadExecutor();
    executorService.submit(httpServer::start);
    client.connectWithTry("127.0.0.1", httpServer);

    ArrayList<String> response = client.sendMessage("GIT / HTTP/1.1\r\n");
    assertTrue(response.contains("HTTP/1.1 404 Not Found"));
  }

  @Test
  void testSends400GivenInvalidProtocol() throws Exception {
    ClientHelper client = new ClientHelper();
    ServerConfiguration config = new ServerConfiguration();
    config.setPort("0");
    config.setController(new Application());
    HttpServer httpServer = new HttpServer(config);
    ExecutorService executorService = Executors.newSingleThreadExecutor();
    executorService.submit(httpServer::start);
    client.connectWithTry("127.0.0.1", httpServer);

    ArrayList<String> response = client.sendMessage("GET / HT\r\n");
    assertTrue(response.contains("HTTP/1.1 404 Not Found"));
  }

  @Test
  void testPortIsUnavailable() {
    withAppender(logger -> {
      ClientHelper client = new ClientHelper();
      ServerConfiguration config = new ServerConfiguration();
      config.setPort("0");
      config.setController(new Application());
      config.setPort("4000");
      HttpServer httpServer = new HttpServer(config);
      ExecutorService executorService = Executors.newSingleThreadExecutor();
      executorService.submit(httpServer::start);
      try {
        Thread.sleep(10);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      HttpServer httpServerOnSamePort = new HttpServer(config);
      ExecutorService executorService1 = Executors.newFixedThreadPool(1);
      executorService1.submit(httpServerOnSamePort::start);
      try {
        client.connectWithTry("127.0.0.1", httpServer);
      } catch (InterruptedException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }

      assertTrue(inMemoryAppender.getEvents().contains("Address already in use (Bind failed)"));
    });
  }

  @Test
  void testPortTooLarge() throws Exception {
    withAppender(logger -> {
      ServerConfiguration config = new ServerConfiguration();
      config.setPort("45456456");
      config.setController(new Application());
      HttpServer httpServer = new HttpServer(config);
      ExecutorService executorService = Executors.newSingleThreadExecutor();
      executorService.submit(httpServer::start);
      int count = 0;
      int maxTries = 4;

      try {
        Thread.sleep(10);
      } catch (RuntimeException ex) {
        count++;
        if (count > maxTries) {
          throw ex;
        }
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      assertTrue(inMemoryAppender.getEvents().contains("Port value out of range: 45456456"));
    });
  }

  @Test
  void testSocketNotAvailable() {
    ClientHelper client = new ClientHelper();
    ServerConfiguration config = new ServerConfiguration();
    config.setPort("0");
    config.setController(new Application());
    HttpServer httpServer = new HttpServer(config);
    ExecutorService executorService = Executors.newSingleThreadExecutor();
    executorService.submit(httpServer::start);

    try {
      client.connect("127.0.0.1", 4050);
    } catch (Exception ex) {
      assertEquals("Connection refused (Connection refused)", ex.getMessage());
    }
  }

  @Test
  void testRedirectsWhenPathIsRedirectme() throws Exception {
    ClientHelper client = new ClientHelper();
    ServerConfiguration config = new ServerConfiguration();
    config.setPort("4000");
    config.setController(new Application());
    HttpServer httpServer = new HttpServer(config);
    ExecutorService executorService = Executors.newSingleThreadExecutor();
    executorService.submit(httpServer::start);
    client.connectWithTry("127.0.0.1", httpServer);

    ArrayList<String> response = client.sendMessage("GET /redirectme HTTP/1.1\r\n");
    assertTrue(response.contains("HTTP/1.1 301 Moved Permanently") && response.contains("Location: http://localhost:4000/"));
  }

  @Test
  void testRootPathOnlyAcceptsGet() throws Exception {
    ClientHelper client = new ClientHelper();
    ServerConfiguration config = new ServerConfiguration();
    config.setPort("0");
    config.setController(new Application());
    HttpServer httpServer = new HttpServer(config);
    ExecutorService executorService = Executors.newSingleThreadExecutor();
    executorService.submit(httpServer::start);
    client.connectWithTry("127.0.0.1", httpServer);

    ArrayList<String> response = client.sendMessage("POST / HTTP/1.1\r\n");
    assertTrue(response.contains("HTTP/1.1 404 Not Found"));
  }

  @Test
  void testRedirectPathOnlyAcceptsGet() throws Exception {
    ClientHelper client = new ClientHelper();
    ServerConfiguration config = new ServerConfiguration();
    config.setPort("0");
    config.setController(new Application());
    HttpServer httpServer = new HttpServer(config);
    ExecutorService executorService = Executors.newSingleThreadExecutor();
    executorService.submit(httpServer::start);
    client.connectWithTry("127.0.0.1", httpServer);

    ArrayList<String> response = client.sendMessage("POST /redirectme HTTP/1.1\r\n");
    assertTrue(response.contains("HTTP/1.1 404 Not Found"));
  }

  @Test
  void testDisplaysFilesAtRoot() {
    TempFilesHelper.withTempDirectory(directory -> {
      Path fileOne = TempFilesHelper.createTempFile(directory);
      Path fileTwo = TempFilesHelper.createTempFile(directory);

      ServerConfiguration config = new ServerConfiguration();
      config.setController(new Application(PublicDirectory.create(directory.toString())));
      config.setPort("0");
      HttpServer httpServer = new HttpServer(config);
      ClientHelper client = new ClientHelper();

      ExecutorService executorService = Executors.newSingleThreadExecutor();
      executorService.submit(httpServer::start);
      try {
        client.connectWithTry("127.0.0.1", httpServer);
      } catch (InterruptedException | IOException e) {
        e.printStackTrace();
      }

      ArrayList<String> response = null;
      try {
        response = client.sendMessage("GET / HTTP/1.1\r\n");
      } catch (IOException e) {
        e.printStackTrace();
      }
      assertTrue(response.size() == 6);
    });
  }

  private void withAppender(Consumer<Logger> loggerConsumer) {
    try {
      Logger rootLogger = (Logger) LoggerFactory.getLogger("ROOT");
      loggerConsumer = rootLogger1 -> {
        inMemoryAppender = (InMemoryAppender) rootLogger1.getAppender("InMemoryAppender");
        inMemoryAppender.setPrefix("test");
        inMemoryAppender.start();
      };
      loggerConsumer.accept(rootLogger);
    } finally {
      inMemoryAppender.stop();
    }
  }
}
