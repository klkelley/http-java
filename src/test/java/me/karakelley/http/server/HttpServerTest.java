package me.karakelley.http.server;

import ch.qos.logback.classic.Logger;
import me.karakelley.http.filesystem.PublicDirectory;
import me.karakelley.http.handlers.Application;
import me.karakelley.http.ApplicationFactory;
import me.karakelley.http.helpers.ClientHelper;
import me.karakelley.http.helpers.TempFilesHelper;
import me.karakelley.http.utility.InMemoryAppender;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;
import static sun.management.snmp.jvminstr.JvmThreadInstanceEntryImpl.ThreadStateMap.Byte0.runnable;

class HttpServerTest {

  @Test
  void testSendsResponseGivenValidRequest() throws Exception {
    ClientHelper client = new ClientHelper();
    HttpServer httpServer = configureWithNoDirectory("0");
    ExecutorService executorService = Executors.newSingleThreadExecutor();
    executorService.submit(httpServer::start);
    client.connectWithTry("127.0.0.1", httpServer);
    List<String> response = client.sendMessage("GET / HTTP/1.1\r\n\r\n");

    assertTrue(response.contains("HTTP/1.1 200 OK"));
    assertTrue(response.contains("Hello World"));
  }

  @Test
  void testSendsResponseWithHeadersGivenValidRequest() throws Exception {
    ClientHelper client = new ClientHelper();
    HttpServer httpServer = configureWithNoDirectory("0");
    ExecutorService executorService = Executors.newFixedThreadPool(5);
    executorService.submit(httpServer::start);
    client.connectWithTry("127.0.0.1", httpServer);
    List<String> response = client.sendMessage("GET / HTTP/1.1\r\n\r\n");

    assertTrue(response.contains("Content-Type: text/plain") && response.contains("Content-Length: 11"));
  }

  @Test
  void testSends404GivenInvalidPath() throws Exception {
    ClientHelper client = new ClientHelper();
    HttpServer httpServer = configureWithNoDirectory("0");
    ExecutorService executorService = Executors.newSingleThreadExecutor();
    executorService.submit(httpServer::start);
    client.connectWithTry("127.0.0.1", httpServer);

    List<String> response = client.sendMessage("GET /nowhere HTTP/1.1\r\n\r\n");
    assertTrue(response.contains("HTTP/1.1 404 Not Found"));
  }

  @Test
  void testSends400GivenPartialRequest() throws Exception {
    ClientHelper client = new ClientHelper();
    HttpServer httpServer = configureWithNoDirectory("0");
    ExecutorService executorService = Executors.newSingleThreadExecutor();
    executorService.submit(httpServer::start);
    client.connectWithTry("127.0.0.1", httpServer);

    List<String> response = client.sendMessage("GET /\r\n\r\n");
    assertTrue(response.contains("HTTP/1.1 400 Bad Request"));
  }

  @Test
  void testSends400GivenInvalidMethod() throws Exception {
    ClientHelper client = new ClientHelper();
    HttpServer httpServer = configureWithNoDirectory("0");
    ExecutorService executorService = Executors.newSingleThreadExecutor();
    executorService.submit(httpServer::start);
    client.connectWithTry("127.0.0.1", httpServer);

    List<String> response = client.sendMessage("GIT / HTTP/1.1\r\n\r\n");
    assertTrue(response.contains("HTTP/1.1 400 Bad Request"));
  }

  @Test
  void testSends400GivenInvalidProtocol() throws Exception {
    ClientHelper client = new ClientHelper();
    HttpServer httpServer = configureWithNoDirectory("0");
    ExecutorService executorService = Executors.newSingleThreadExecutor();
    executorService.submit(httpServer::start);
    client.connectWithTry("127.0.0.1", httpServer);

    List<String> response = client.sendMessage("GET / HT\r\n\r\n");
    assertTrue(response.contains("HTTP/1.1 400 Bad Request"));
  }

  @Test
  void testPortIsUnavailable() {
    List<String> events = withCapturedLogging(() -> {
      ClientHelper client = new ClientHelper();
      ServerConfiguration config = new ServerConfiguration();
      config.setPort("0");
      Map<String, String> args = new HashMap<>();
      config.setHandler(new ApplicationFactory().create(args));
      config.setPort("4000");
      HttpServer httpServer = new HttpServer(config, new ConnectionHandler(), new RequestReaderFactory());
      ExecutorService executorService = Executors.newSingleThreadExecutor();
      executorService.submit(httpServer::start);
      try {
        Thread.sleep(10);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      HttpServer httpServerOnSamePort = new HttpServer(config, new ConnectionHandler(), new RequestReaderFactory());
      ExecutorService executorService1 = Executors.newSingleThreadExecutor();
      executorService1.submit(httpServerOnSamePort::start);
      try {
        client.connectWithTry("127.0.0.1", httpServer);
      } catch (InterruptedException e) {
        fail(e);
      }
    });
    assertTrue(events.contains("Address already in use (Bind failed)"));
  }

  @Test
  void testPortTooLarge() {
    List<String> events = withCapturedLogging(() -> {
      HttpServer httpServer = configureWithNoDirectory("45456456");
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
    });
    assertTrue(events.contains("Port value out of range: 45456456"));
  }

  @Test
  void testSocketNotAvailable() {
    ClientHelper client = new ClientHelper();
    HttpServer httpServer = configureWithNoDirectory("0");
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
    HttpServer httpServer = configureWithNoDirectory("4000");
    ExecutorService executorService = Executors.newFixedThreadPool(1);
    executorService.submit(httpServer::start);
    client.connectWithTry("127.0.0.1", httpServer);
    List<String> response = client.sendMessage("GET /redirectme HTTP/1.1\r\n\r\n");

    assertTrue(response.contains("HTTP/1.1 301 Moved Permanently") && response.contains("Location: http://localhost:4000/"));
  }

  @Test
  void testRedirectPathOnlyAcceptsGet() throws Exception {
    ClientHelper client = new ClientHelper();
    HttpServer httpServer = configureWithNoDirectory("0");
    ExecutorService executorService = Executors.newSingleThreadExecutor();
    executorService.submit(httpServer::start);
    client.connectWithTry("127.0.0.1", httpServer);
    List<String> response = client.sendMessage("POST /redirectme HTTP/1.1\r\n\r\n");

    assertTrue(response.contains("HTTP/1.1 405 Method Not Allowed"));
  }

  @Test
  void test405WhenPostingToRoot() throws Exception {
    ClientHelper client = new ClientHelper();
    HttpServer httpServer = configureWithNoDirectory("0");
    ExecutorService executorService = Executors.newSingleThreadExecutor();
    executorService.submit(httpServer::start);
    client.connectWithTry("127.0.0.1", httpServer);
    List<String> response = client.sendMessage("POST / HTTP/1.1\r\n\r\n");

    assertTrue(response.contains("HTTP/1.1 405 Method Not Allowed"));
  }

  @Test
  void testListSubPathDirectories() throws IOException, InterruptedException {
    ClientHelper client = new ClientHelper();
    HttpServer httpServer = configureWithDirectory("./src/test", "0");
    ExecutorService executorService = Executors.newSingleThreadExecutor();
    executorService.submit(httpServer::start);
    client.connectWithTry("127.0.0.1", httpServer);
    List<String> response = client.sendMessage("GET /resources/ HTTP/1.1\r\n\r\n");

    assertTrue(response.contains("HTTP/1.1 200 OK"));
  }

  @Test
  void testDisplaysFilesAtRoot() {
    TempFilesHelper.withTempDirectory(directory -> {
      Path fileOne = TempFilesHelper.createTempFile(directory, "/test1");
      HttpServer httpServer = configureWithDirectory(directory.toString(), "0");
      ClientHelper client = new ClientHelper();
      ExecutorService executorService = Executors.newSingleThreadExecutor();
      executorService.submit(httpServer::start);

      try {
        client.connectWithTry("127.0.0.1", httpServer);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      List<String> response = client.sendMessage("GET / HTTP/1.1\r\n\r\n");
      assertTrue(response.contains("Content-Type: text/html"));
      assertTrue(response.contains("<p><a href=\"/test1.txt\">test1.txt</a></p>"));
    });
  }

  @Test
  void testSend404WhenPathDoesNotExist() {
    TempFilesHelper.withTempDirectory(directory -> {
      Path fileOne = TempFilesHelper.createTempFile(directory, "/test1");
      HttpServer httpServer = configureWithDirectory(directory.toString(), "0");
      ClientHelper client = new ClientHelper();
      ExecutorService executorService = Executors.newSingleThreadExecutor();
      executorService.submit(httpServer::start);

      try {
        client.connectWithTry("127.0.0.1", httpServer);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      List<String> response = client.sendMessage("GET /hey HTTP/1.1\r\n\r\n");
      assertTrue(response.contains("HTTP/1.1 404 Not Found"));
    });
  }

  @Test
  void testReturnsFileContentsRequested() {
    TempFilesHelper.withTempDirectory(directory -> {
      Path fileOne = TempFilesHelper.createTempFile(directory, "/test1");
      TempFilesHelper.createContents("Hello World", fileOne);
      HttpServer httpServer = configureWithDirectory(directory.toString(), "0");
      ClientHelper client = new ClientHelper();

      ExecutorService executorService = Executors.newSingleThreadExecutor();
      executorService.submit(httpServer::start);
      try {
        client.connectWithTry("127.0.0.1", httpServer);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      List<String> response = client.sendMessage("GET /test1.txt HTTP/1.1\r\n\r\n");
      assertTrue(response.contains("Content-Type: text/plain"));
      assertTrue(response.contains("Hello World"));
    });
  }

  @Test
  void testCreatesFileWithPostRequest() {
    TempFilesHelper.withTempDirectory(directory -> {
      Path fileOne = TempFilesHelper.createTempFile(directory, "/test1");
      HttpServer httpServer = configureWithDirectory(directory.toString(), "0");
      ClientHelper client = new ClientHelper();

      ExecutorService executorService = Executors.newSingleThreadExecutor();
      executorService.submit(httpServer::start);
      try {
        client.connectWithTry("127.0.0.1", httpServer);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      List<String> response = client.sendMessage("POST /testmore.txt HTTP/1.1\r\nContent-Length: 3\r\n\r\nhey");
      assertTrue(response.contains("HTTP/1.1 201 Created"));
    });
  }

  @Test
  void testPostFollowedByGetRequest() {
    TempFilesHelper.withTempDirectory(directory -> {
      HttpServer httpServer = configureWithDirectory(directory.toString(), "0");
      ClientHelper client = new ClientHelper();
      ClientHelper client2 = new ClientHelper();
      ExecutorService executorService = Executors.newSingleThreadExecutor();
      executorService.submit(httpServer::start);
      try {
        client.connectWithTry("127.0.0.1", httpServer);
        client2.connectWithTry("127.0.0.1", httpServer);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      List<String> postResponse = client.sendMessage("POST /testmore.txt HTTP/1.1\r\nContent-Length: 3\r\n\r\nhey");
      List<String> getResponse = client2.sendMessage("GET /testmore.txt HTTP/1.1\r\n\r\n");
      assertTrue(postResponse.contains("HTTP/1.1 201 Created"));
      assertTrue(getResponse.contains("hey") && getResponse.contains("HTTP/1.1 200 OK"));
    });
  }

  @Test
  void sends204ForPutRequestForExistingResource() {
    TempFilesHelper.withTempDirectory(directory ->  {
      Path file = TempFilesHelper.createTempFile(directory, "/test1");
      TempFilesHelper.createContents("Hello", file);
      HttpServer httpServer = configureWithDirectory(directory.toString(), "0");
      ClientHelper client = new ClientHelper();
      ClientHelper client2 = new ClientHelper();
      ExecutorService executorService = Executors.newSingleThreadExecutor();
      executorService.submit(httpServer::start);
      try {
        client.connectWithTry("127.0.0.1", httpServer);
        client2.connectWithTry("127.0.0.1", httpServer);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      List<String> response = client.sendMessage("PUT /test1.txt HTTP/1.1\r\nContent-Length: 11\r\n\r\nHello World");
      List<String> getResponse = client2.sendMessage("GET /test1.txt HTTP/1.1\r\n\r\n");
      assertTrue(response.contains("HTTP/1.1 204 No Content"));
      assertTrue(getResponse.contains("Hello World"));
    });
  }

  @Test
  void test404ReturnedForAttemptedDirectoryTraversal() {
    TempFilesHelper.withTempDirectory(directory -> {
      Path fileOne = TempFilesHelper.createTempFile(directory, "/test1");
      TempFilesHelper.createContents("Hello World", fileOne);
      HttpServer httpServer = configureWithDirectory(directory.toString(), "0");
      ClientHelper client = new ClientHelper();
      ExecutorService executorService = Executors.newSingleThreadExecutor();
      executorService.submit(httpServer::start);

      try {
        client.connectWithTry("127.0.0.1", httpServer);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      List<String> response;
      response = client.sendMessage("GET /../ HTTP/1.1\r\n\r\n");
      assertTrue(response.contains("HTTP/1.1 404 Not Found"));
    });
  }

  @Test
  void test204ResponseForDeleteRequest() {
    TempFilesHelper.withTempDirectory(directory -> {
      Path fileOne = TempFilesHelper.createTempFile(directory, "/test1");
      HttpServer httpServer = configureWithDirectory(directory.toString(),"0");
      ClientHelper client = new ClientHelper();
      ExecutorService executorService = Executors.newSingleThreadExecutor();
      executorService.submit(httpServer::start);

      try {
        client.connectWithTry("127.0.0.1", httpServer);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      List<String> response = client.sendMessage("DELETE /test1.txt HTTP/1.1\r\n\r\n");
      assertTrue(response.contains("HTTP/1.1 204 No Content"));
    });
  }

  @Test
  void test404ForGetRequestAfterResourceDeleted() {
    TempFilesHelper.withTempDirectory(directory ->  {
      Path file = TempFilesHelper.createTempFile(directory, "/test1");
      TempFilesHelper.createContents("Hello", file);
      HttpServer httpServer = configureWithDirectory(directory.toString(),"0");
      ClientHelper client = new ClientHelper();
      ClientHelper client2 = new ClientHelper();
      ExecutorService executorService = Executors.newSingleThreadExecutor();
      executorService.submit(httpServer::start);
      try {
        client.connectWithTry("127.0.0.1", httpServer);
        client2.connectWithTry("127.0.0.1", httpServer);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      List<String> response = client.sendMessage("DELETE /test1.txt HTTP/1.1\r\n\r\n");
      List<String> getResponse = client2.sendMessage("GET /test1.txt HTTP/1.1\r\n\r\n");
      assertTrue(response.contains("HTTP/1.1 204 No Content"));
      assertTrue(getResponse.contains("HTTP/1.1 404 Not Found"));
    });
  }

  @Test
  void whenNoDirectoryIsGivenTheApplicationHasNoPublicDirectory() throws InterruptedException {
    ClientHelper client = new ClientHelper();
    HttpServer httpServer = configureWithNoDirectory("0");
    ExecutorService executorService = Executors.newSingleThreadExecutor();
    executorService.submit(httpServer::start);
    client.connectWithTry("127.0.0.1", httpServer);
    List<String> response = client.sendMessage("GET / HTTP/1.1\r\n\r\n");

    assertTrue(response.contains("Hello World"));
  }

  @Test
  void whenDirectoryArgumentIsPassedApplicationIsConfiguredWithPublicDirectory() throws InterruptedException {
    TempFilesHelper.withTempDirectory(directory -> {
      Path file = TempFilesHelper.createTempFile(directory, "/test1");
      TempFilesHelper.createContents("Hello There From The Test Suite!", file);
      ClientHelper client = new ClientHelper();
      HttpServer httpServer = configureWithDirectory(directory.toString(),"0");
      ExecutorService executorService = Executors.newSingleThreadExecutor();
      executorService.submit(httpServer::start);

      try {
        client.connectWithTry("127.0.0.1", httpServer);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      List<String> response = client.sendMessage("GET /test1.txt HTTP/1.1\r\n\r\n");
      assertTrue(response.contains("Hello There From The Test Suite!"));
    });
  }

  private List<String> withCapturedLogging(Runnable runnable) {
    Logger logger = (Logger) LoggerFactory.getLogger("ROOT");
    InMemoryAppender inMemoryAppender = (InMemoryAppender) logger.getAppender("InMemoryAppender");
    inMemoryAppender.setPrefix("test");
    inMemoryAppender.start();
    runnable.run();
    inMemoryAppender.stop();
    return inMemoryAppender.getEvents();
  }

  private HttpServer configureWithDirectory(String directory, String port) {
    ServerConfiguration config = new ServerConfiguration();
    Map<String, String> args = new HashMap<>();
    args.put("directory", directory);
    config.setPort(port);
    config.setHandler(new ApplicationFactory().create(args));
    return new HttpServer(config, new ConnectionHandler(), new RequestReaderFactory());
  }

  private HttpServer configureWithNoDirectory(String port) {
    ServerConfiguration config = new ServerConfiguration();
    Map<String, String> args = new HashMap<>();
    config.setPort(port);
    config.setHandler(new ApplicationFactory().create(args));
    return new HttpServer(config, new ConnectionHandler(), new RequestReaderFactory());
  }
}
