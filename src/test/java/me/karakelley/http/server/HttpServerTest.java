package me.karakelley.http.server;

import ch.qos.logback.classic.Logger;
import me.karakelley.http.filesystem.PublicDirectory;
import me.karakelley.http.handlers.Application;
import me.karakelley.http.helpers.ClientHelper;
import me.karakelley.http.helpers.TempFilesHelper;
import me.karakelley.http.utility.InMemoryAppender;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

class HttpServerTest {


  @Test
  void testSendsResponseGivenValidRequest() throws Exception {
    ClientHelper client = new ClientHelper();
    ServerConfiguration config = new ServerConfiguration();
    config.setPort("0");
    config.setHandler(new Application());
    HttpServer httpServer = new HttpServer(config, new ConnectionHandler(), new RequestReaderFactory());
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
    ServerConfiguration config = new ServerConfiguration();
    config.setPort("0");
    config.setHandler(new Application());
    HttpServer httpServer = new HttpServer(config, new ConnectionHandler(), new RequestReaderFactory());
    ExecutorService executorService = Executors.newFixedThreadPool(5);
    executorService.submit(httpServer::start);
    client.connectWithTry("127.0.0.1", httpServer);
    List<String> response = client.sendMessage("GET / HTTP/1.1\r\n\r\n");

    assertTrue(response.contains("Content-Type: text/plain") && response.contains("Content-Length: 11"));
  }

  @Test
  void testSends404GivenInvalidPath() throws Exception {
    ClientHelper client = new ClientHelper();
    ServerConfiguration config = new ServerConfiguration();
    config.setPort("0");
    config.setHandler(new Application());
    HttpServer httpServer = new HttpServer(config, new ConnectionHandler(), new RequestReaderFactory());
    ExecutorService executorService = Executors.newSingleThreadExecutor();
    executorService.submit(httpServer::start);
    client.connectWithTry("127.0.0.1", httpServer);

    List<String> response = client.sendMessage("GET /nowhere HTTP/1.1\r\n\r\n");
    assertTrue(response.contains("HTTP/1.1 404 Not Found"));
  }

  @Test
  void testSends400GivenPartialRequest() throws Exception {
    ClientHelper client = new ClientHelper();
    ServerConfiguration config = new ServerConfiguration();
    config.setPort("0");
    config.setHandler(new Application());
    HttpServer httpServer = new HttpServer(config, new ConnectionHandler(), new RequestReaderFactory());
    ExecutorService executorService = Executors.newSingleThreadExecutor();
    executorService.submit(httpServer::start);
    client.connectWithTry("127.0.0.1", httpServer);

    List<String> response = client.sendMessage("GET /\r\n\r\n");
    assertTrue(response.contains("HTTP/1.1 400 Bad Request"));
  }

  @Test
  void testSends400GivenInvalidMethod() throws Exception {
    ClientHelper client = new ClientHelper();
    ServerConfiguration config = new ServerConfiguration();
    config.setPort("0");
    config.setHandler(new Application());
    HttpServer httpServer = new HttpServer(config, new ConnectionHandler(), new RequestReaderFactory());
    ExecutorService executorService = Executors.newSingleThreadExecutor();
    executorService.submit(httpServer::start);
    client.connectWithTry("127.0.0.1", httpServer);

    List<String> response = client.sendMessage("GIT / HTTP/1.1\r\n\r\n");
    assertTrue(response.contains("HTTP/1.1 400 Bad Request"));
  }

  @Test
  void testSends400GivenInvalidProtocol() throws Exception {
    ClientHelper client = new ClientHelper();
    ServerConfiguration config = new ServerConfiguration();
    config.setPort("0");
    config.setHandler(new Application());
    HttpServer httpServer = new HttpServer(config, new ConnectionHandler(), new RequestReaderFactory());
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
      config.setHandler(new Application());
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
        e.printStackTrace();
      }
    });
    assertTrue(events.contains("Address already in use (Bind failed)"));
  }

  @Test
  void testPortTooLarge() {
    List<String> events = withCapturedLogging(() -> {
      ServerConfiguration config = new ServerConfiguration();
      config.setPort("45456456");
      config.setHandler(new Application());
      HttpServer httpServer = new HttpServer(config, new ConnectionHandler(), new RequestReaderFactory());
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
    ServerConfiguration config = new ServerConfiguration();
    config.setPort("0");
    config.setHandler(new Application());
    HttpServer httpServer = new HttpServer(config, new ConnectionHandler(), new RequestReaderFactory());
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
    config.setHandler(new Application());
    HttpServer httpServer = new HttpServer(config, new ConnectionHandler(), new RequestReaderFactory());
    ExecutorService executorService = Executors.newFixedThreadPool(1);
    executorService.submit(httpServer::start);
    client.connectWithTry("127.0.0.1", httpServer);

    List<String> response = client.sendMessage("GET /redirectme HTTP/1.1\r\n\r\n");
    assertTrue(response.contains("HTTP/1.1 301 Moved Permanently") && response.contains("Location: http://localhost:4000/"));
  }

  @Test
  void testRedirectPathOnlyAcceptsGet() throws Exception {
    ClientHelper client = new ClientHelper();
    ServerConfiguration config = new ServerConfiguration();
    config.setPort("0");
    config.setHandler(new Application());
    HttpServer httpServer = new HttpServer(config, new ConnectionHandler(), new RequestReaderFactory());
    ExecutorService executorService = Executors.newSingleThreadExecutor();
    executorService.submit(httpServer::start);
    client.connectWithTry("127.0.0.1", httpServer);

    List<String> response = client.sendMessage("POST /redirectme HTTP/1.1\r\n\r\n");
    assertTrue(response.contains("HTTP/1.1 405 Method Not Allowed"));
  }

  @Test
  void test405WhenPostingToRoot() throws Exception {
    ClientHelper client = new ClientHelper();
    ServerConfiguration config = new ServerConfiguration();
    config.setPort("0");
    config.setHandler(new Application());
    HttpServer httpServer = new HttpServer(config, new ConnectionHandler(), new RequestReaderFactory());
    ExecutorService executorService = Executors.newSingleThreadExecutor();
    executorService.submit(httpServer::start);
    client.connectWithTry("127.0.0.1", httpServer);

    List<String> response = client.sendMessage("POST / HTTP/1.1\r\n\r\n");
    assertTrue(response.contains("HTTP/1.1 405 Method Not Allowed"));
  }

  @Test
  void testListSubPathDirectories() throws IOException, InterruptedException {
    ClientHelper client = new ClientHelper();
    ServerConfiguration config = new ServerConfiguration();
    config.setPort("0");
    config.setHandler(new Application(PublicDirectory.create("./src/test/")));
    HttpServer httpServer = new HttpServer(config, new ConnectionHandler(), new RequestReaderFactory());
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

      ServerConfiguration config = new ServerConfiguration();
      config.setHandler(new Application(PublicDirectory.create(directory.toString())));
      config.setPort("0");
      HttpServer httpServer = new HttpServer(config, new ConnectionHandler(), new RequestReaderFactory());
      ClientHelper client = new ClientHelper();

      ExecutorService executorService = Executors.newSingleThreadExecutor();
      executorService.submit(httpServer::start);
      try {
        client.connectWithTry("127.0.0.1", httpServer);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      List<String> response = null;
      response = client.sendMessage("GET / HTTP/1.1\r\n\r\n");

      assertTrue(response.contains("Content-Type: text/html"));
      assertTrue(response.contains("<p><a href=\"/test1.txt\">test1.txt</a></p>"));
    });
  }

  @Test
  void testSend404WhenPathDoesNotExist() {
    TempFilesHelper.withTempDirectory(directory -> {
      Path fileOne = TempFilesHelper.createTempFile(directory, "/test1");

      ServerConfiguration config = new ServerConfiguration();
      config.setHandler(new Application(PublicDirectory.create(directory.toString())));
      config.setPort("0");
      HttpServer httpServer = new HttpServer(config, new ConnectionHandler(), new RequestReaderFactory());
      ClientHelper client = new ClientHelper();

      ExecutorService executorService = Executors.newSingleThreadExecutor();
      executorService.submit(httpServer::start);
      try {
        client.connectWithTry("127.0.0.1", httpServer);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      List<String> response = null;
      response = client.sendMessage("GET /hey HTTP/1.1\r\n\r\n");

      assertTrue(response.contains("HTTP/1.1 404 Not Found"));
    });
  }

  @Test
  void testReturnsFileContentsRequested() {
    TempFilesHelper.withTempDirectory(directory -> {
      Path fileOne = TempFilesHelper.createTempFile(directory, "/test1");
      TempFilesHelper.createContents("Hello World", fileOne);
      ServerConfiguration config = new ServerConfiguration();
      config.setHandler(new Application(PublicDirectory.create(directory.toString())));
      config.setPort("0");
      HttpServer httpServer = new HttpServer(config, new ConnectionHandler(), new RequestReaderFactory());
      ClientHelper client = new ClientHelper();

      ExecutorService executorService = Executors.newSingleThreadExecutor();
      executorService.submit(httpServer::start);
      try {
        client.connectWithTry("127.0.0.1", httpServer);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      List<String> response = null;
      response = client.sendMessage("GET /test1.txt HTTP/1.1\r\n\r\n");

      assertTrue(response.contains("Content-Type: text/plain"));
      assertTrue(response.contains("Hello World"));
    });
  }

  @Test
  void testCreatesFileWithPostRequest() {
    TempFilesHelper.withTempDirectory(directory -> {
      Path fileOne = TempFilesHelper.createTempFile(directory, "/test1");
      ServerConfiguration config = new ServerConfiguration();
      config.setHandler(new Application(PublicDirectory.create(directory.toString())));
      config.setPort("0");
      HttpServer httpServer = new HttpServer(config, new ConnectionHandler(), new RequestReaderFactory());
      ClientHelper client = new ClientHelper();

      ExecutorService executorService = Executors.newSingleThreadExecutor();
      executorService.submit(httpServer::start);
      try {
        client.connectWithTry("127.0.0.1", httpServer);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      List<String> response = null;
      response = client.sendMessage("POST /testmore.txt HTTP/1.1\r\nContent-Length: 3\r\n\r\nhey");
      assertTrue(response.contains("HTTP/1.1 201 Created"));
    });
  }

  @Test
  void testPostFollowedByGetRequest() {
    TempFilesHelper.withTempDirectory(directory -> {
      ServerConfiguration config = new ServerConfiguration();
      config.setHandler(new Application(PublicDirectory.create(directory.toString())));
      config.setPort("0");
      HttpServer httpServer = new HttpServer(config, new ConnectionHandler(), new RequestReaderFactory());
      ClientHelper client = new ClientHelper();
      ClientHelper client2 = new ClientHelper();

      ExecutorService executorService = Executors.newSingleThreadExecutor();
      executorService.submit(httpServer::start);
      try {
        client.connectWithTry("127.0.0.1", httpServer);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      try {
        client2.connectWithTry("127.0.0.1", httpServer);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      List<String> postResponse;
      List<String> getResponse;
      postResponse= client.sendMessage("POST /testmore.txt HTTP/1.1\r\nContent-Length: 3\r\n\r\nhey");
      getResponse= client2.sendMessage("GET /testmore.txt HTTP/1.1\r\n\r\n");


      assertTrue(postResponse.contains("HTTP/1.1 201 Created"));
      assertTrue(getResponse.contains("hey") && getResponse.contains("HTTP/1.1 200 OK"));
    });
  }

  @Test
  void sends200ForPutRequestForExistingResource() {
    TempFilesHelper.withTempDirectory(directory ->  {
      Path file = TempFilesHelper.createTempFile(directory, "/test1");
      TempFilesHelper.createContents("Hello", file);
      ServerConfiguration config = new ServerConfiguration();
      config.setHandler(new Application(PublicDirectory.create(directory.toString())));
      config.setPort("0");
      HttpServer httpServer = new HttpServer(config, new ConnectionHandler(), new RequestReaderFactory());
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
      assertTrue(response.contains("HTTP/1.1 200 OK"));
      assertTrue(getResponse.contains("Hello World"));
    });
  }

  @Test
  void test404ReturnedForAttemptedDirectoryTraversal() {
    TempFilesHelper.withTempDirectory(directory -> {
      Path fileOne = TempFilesHelper.createTempFile(directory, "/test1");
      TempFilesHelper.createContents("Hello World", fileOne);
      ServerConfiguration config = new ServerConfiguration();
      config.setHandler(new Application(PublicDirectory.create(directory.toString())));
      config.setPort("0");
      HttpServer httpServer = new HttpServer(config, new ConnectionHandler(), new RequestReaderFactory());
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
      ServerConfiguration config = new ServerConfiguration();
      config.setHandler(new Application(PublicDirectory.create(directory.toString())));
      config.setPort("0");
      HttpServer httpServer = new HttpServer(config, new ConnectionHandler(), new RequestReaderFactory());
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
      ServerConfiguration config = new ServerConfiguration();
      config.setHandler(new Application(PublicDirectory.create(directory.toString())));
      config.setPort("0");
      HttpServer httpServer = new HttpServer(config, new ConnectionHandler(), new RequestReaderFactory());
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

  private List<String> withCapturedLogging(Runnable runnable) {
    Logger logger = (Logger) LoggerFactory.getLogger("ROOT");
    InMemoryAppender inMemoryAppender = (InMemoryAppender) logger.getAppender("InMemoryAppender");
    inMemoryAppender.setPrefix("test");
    inMemoryAppender.start();
    runnable.run();
    inMemoryAppender.stop();
    return inMemoryAppender.getEvents();
  }
}
