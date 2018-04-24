package me.karakelley.http.server;

import ch.qos.logback.classic.Logger;
import me.karakelley.http.authorization.AlwaysAuthorized;
import me.karakelley.http.handlers.AuthorizedApplicationFactory;
import me.karakelley.http.authorization.BasicAuthorizer;
import me.karakelley.http.helpers.ClientHelper;
import me.karakelley.http.helpers.RequestStringBuilder;
import me.karakelley.http.helpers.TempFilesHelper;
import me.karakelley.http.helpers.InMemoryAppender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

class HttpServerTest {

  RequestStringBuilder requestBuilder;
  String requestString;
  ClientHelper client;

  @BeforeEach
  void setUp() {
    requestBuilder = new RequestStringBuilder();
    client = new ClientHelper();
  }

  @Test
  void testSendsResponseGivenValidRequest() {
    HttpServer httpServer = configureWithNoDirectory("0");
    startOnNewThread(httpServer);
    connectClient(httpServer, client);

    List<String> response = client.sendMessage(basicGetRequest());

    assertTrue(response.contains("HTTP/1.1 200 OK"));
    assertTrue(response.contains("Hello World"));
  }

  @Test
  void testSendsResponseWithHeadersGivenValidRequest() {
    HttpServer httpServer = configureWithNoDirectory("0");
    startOnNewThread(httpServer);
    connectClient(httpServer, client);
    List<String> response = client.sendMessage(basicGetRequest());

    assertTrue(response.contains("Content-Type: text/plain") && response.contains("Content-Length: 11"));
  }

  @Test
  void testSends404GivenInvalidPath() {
    HttpServer httpServer = configureWithNoDirectory("0");
    startOnNewThread(httpServer);
    connectClient(httpServer, client);

    requestString = requestBuilder.setMethod("GET").setPath("/nowhere").build();
    List<String> response = client.sendMessage(requestString);

    assertTrue(response.contains("HTTP/1.1 404 Not Found"));
  }

  @Test
  void testSends400GivenPartialRequest() {
    HttpServer httpServer = configureWithNoDirectory("0");
    startOnNewThread(httpServer);
    connectClient(httpServer, client);

    List<String> response = client.sendMessage("GET /\r\n\r\n");
    assertTrue(response.contains("HTTP/1.1 400 Bad Request"));
  }

  @Test
  void testSends400GivenInvalidMethod() {
    HttpServer httpServer = configureWithNoDirectory("0");
    startOnNewThread(httpServer);
    connectClient(httpServer, client);
    requestString = requestBuilder.setMethod("GIT").setPath("/").build();

    List<String> response = client.sendMessage(requestString);
    assertTrue(response.contains("HTTP/1.1 400 Bad Request"));
  }

  @Test
  void testSends400GivenInvalidProtocol() {
    HttpServer httpServer = configureWithNoDirectory("0");
    startOnNewThread(httpServer);
    connectClient(httpServer, client);

    List<String> response = client.sendMessage("GET / HT\r\n\r\n");
    assertTrue(response.contains("HTTP/1.1 400 Bad Request"));
  }

  @Test
  void testPortIsUnavailable() {
    List<String> events = withCapturedLogging(() -> {
      HttpServer httpServer = configureWithNoDirectory("4000");
      startOnNewThread(httpServer);
      sleep();

      HttpServer httpServerOnSamePort = configureWithNoDirectory("4000");
      startOnNewThread(httpServerOnSamePort);
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
      startOnNewThread(httpServer);
      sleep();
    });
    assertTrue(events.contains("Port value out of range: 45456456"));
  }

  @Test
  void testSocketNotAvailable() {
    HttpServer httpServer = configureWithNoDirectory("0");
    startOnNewThread(httpServer);

    try {
      client.connect("127.0.0.1", 4050);
    } catch (Exception ex) {
      assertEquals("Connection refused (Connection refused)", ex.getMessage());
    }
  }

  @Test
  void testRedirectsWhenPathIsRedirectme() {
    HttpServer httpServer = configureWithNoDirectory("4000");
    startOnNewThread(httpServer);
    connectClient(httpServer, client);
    requestString = requestBuilder.setMethod("GET").setPath("/redirectme").build();

    List<String> response = client.sendMessage(requestString);
    assertTrue(response.contains("HTTP/1.1 301 Moved Permanently") && response.contains("Location: http://localhost:4000/"));
  }

  @Test
  void testRedirectPathOnlyAcceptsGet() {
    HttpServer httpServer = configureWithNoDirectory("0");
    startOnNewThread(httpServer);
    connectClient(httpServer, client);

    requestString = requestBuilder.setMethod("POST").setPath("/redirectme").setHeader("Authorization", "Basic YWRtaW46Y2hpY2FnbzMy").build();
    List<String> response = client.sendMessage(requestString);

    assertTrue(response.contains("HTTP/1.1 405 Method Not Allowed"));
  }

  @Test
  void test405WhenPostingToRoot() {
    HttpServer httpServer = configureWithNoDirectory("0");
    startOnNewThread(httpServer);
    connectClient(httpServer, client);

    requestString = requestBuilder.setMethod("POST").setPath("/").setHeader("Authorization", "Basic YWRtaW46Y2hpY2FnbzMy").build();
    List<String> response = client.sendMessage(requestString);

    assertTrue(response.contains("HTTP/1.1 405 Method Not Allowed"));
  }

  @Test
  void testListSubPathDirectories() {
    HttpServer httpServer = configureWithDirectory("./src/test", "0");
    startOnNewThread(httpServer);
    connectClient(httpServer, client);

    requestString = requestBuilder.setMethod("GET").setPath("/resources/").build();
    List<String> response = client.sendMessage(requestString);

    assertTrue(response.contains("HTTP/1.1 200 OK"));
  }

  @Test
  void testDisplaysFilesAtRoot() {
    TempFilesHelper.withTempDirectory(directory -> {
      TempFilesHelper.createTempFile(directory, "/test1.txt");
      HttpServer httpServer = configureWithDirectory(directory.toString(), "0");
      startOnNewThread(httpServer);
      connectClient(httpServer, client);

      List<String> response = client.sendMessage(basicGetRequest());

      assertTrue(response.contains("Content-Type: text/html"));
      assertTrue(response.get(4).contains("<p><a href=\"/test1.txt\">test1.txt</a></p>"));
    });
  }

  @Test
  void testSend404WhenPathDoesNotExist() {
    TempFilesHelper.withTempDirectory(directory -> {
      TempFilesHelper.createTempFile(directory, "/test1.txt");
      HttpServer httpServer = configureWithDirectory(directory.toString(), "0");
      startOnNewThread(httpServer);
      connectClient(httpServer, client);

      requestString = requestBuilder.setMethod("GET").setPath("/hey").build();
      List<String> response = client.sendMessage(requestString);

      assertTrue(response.contains("HTTP/1.1 404 Not Found"));
    });
  }

  @Test
  void testReturnsFileContentsRequested() {
    TempFilesHelper.withTempDirectory(directory -> {
      Path fileOne = TempFilesHelper.createTempFile(directory, "/test1.txt");
      TempFilesHelper.createContents("Hello World", fileOne);
      HttpServer httpServer = configureWithDirectory(directory.toString(), "0");
      startOnNewThread(httpServer);
      connectClient(httpServer, client);

      requestString = requestBuilder.setMethod("GET").setPath("/test1.txt").build();
      List<String> response = client.sendMessage(requestString);
      assertTrue(response.contains("Content-Type: text/plain"));
      assertTrue(response.contains("Hello World"));
    });
  }

  @Test
  void testCreatesFileWithPostRequest() {
    TempFilesHelper.withTempDirectory(directory -> {
      TempFilesHelper.createTempFile(directory, "/test1.txt");
      HttpServer httpServer = configureWithDirectory(directory.toString(), "0");
      startOnNewThread(httpServer);
      connectClient(httpServer, client);

      requestString = requestBuilder.setMethod("POST")
              .setPath("/testmore.txt")
              .setHeader("Content-Length", "3")
              .setBody("hey")
              .setHeader("Authorization", "Basic YWRtaW46Y2hpY2FnbzMy")
              .build();
      List<String> response = client.sendMessage(requestString);

      assertTrue(response.contains("HTTP/1.1 201 Created"));
    });
  }

  @Test
  void testPostFollowedByGetRequest() {
    TempFilesHelper.withTempDirectory(directory -> {
      HttpServer httpServer = configureWithDirectory(directory.toString(), "0");
      ClientHelper client2 = new ClientHelper();
      startOnNewThread(httpServer);
      connectTwoClients(httpServer, client2);

      requestString = requestBuilder.setMethod("POST")
              .setPath("/testmore.txt")
              .setHeader("Content-Length", "3")
              .setHeader("Authorization", "Basic YWRtaW46Y2hpY2FnbzMy")
              .setBody("hey")
              .build();
      String secondRequest = requestBuilder.setMethod("GET").setPath("/testmore.txt").build();
      List<String> postResponse = client.sendMessage(requestString);
      List<String> getResponse = client2.sendMessage(secondRequest);

      assertTrue(postResponse.contains("HTTP/1.1 201 Created"));
      assertTrue(getResponse.contains("hey") && getResponse.contains("HTTP/1.1 200 OK"));
    });
  }

  @Test
  void sends204ForPutRequestForExistingResource() {
    TempFilesHelper.withTempDirectory(directory -> {
      Path file = TempFilesHelper.createTempFile(directory, "/test1.txt");
      TempFilesHelper.createContents("Hello", file);
      HttpServer httpServer = configureWithDirectory(directory.toString(), "0");
      ClientHelper client2 = new ClientHelper();
      startOnNewThread(httpServer);
      connectTwoClients(httpServer, client2);

      requestString = requestBuilder.setMethod("PUT")
              .setPath("/test1.txt")
              .setHeader("Content-Length", "11")
              .setHeader("Authorization", "Basic YWRtaW46Y2hpY2FnbzMy")
              .setBody("Hello World")
              .build();
      String secondRequest = requestBuilder.setMethod("GET").setPath("/test1.txt").build();
      List<String> response = client.sendMessage(requestString);
      List<String> getResponse = client2.sendMessage(secondRequest);

      assertTrue(response.contains("HTTP/1.1 204 No Content"));
      assertTrue(getResponse.contains("Hello World"));
    });
  }

  @Test
  void test404ReturnedForAttemptedDirectoryTraversal() {
    TempFilesHelper.withTempDirectory(directory -> {
      Path fileOne = TempFilesHelper.createTempFile(directory, "/test1.txt");
      TempFilesHelper.createContents("Hello World", fileOne);
      HttpServer httpServer = configureWithDirectory(directory.toString(), "0");
      startOnNewThread(httpServer);
      connectClient(httpServer, client);

      requestString = requestBuilder.setMethod("GET").setPath("/../").build();
      List<String> response = client.sendMessage(requestString);

      assertTrue(response.contains("HTTP/1.1 404 Not Found"));
    });
  }

  @Test
  void test204ResponseForDeleteRequest() {
    TempFilesHelper.withTempDirectory(directory -> {
      TempFilesHelper.createTempFile(directory, "/test1.txt");
      HttpServer httpServer = configureWithDirectory(directory.toString(), "0");
      startOnNewThread(httpServer);
      connectClient(httpServer, client);

      requestString = requestBuilder.setMethod("DELETE").setPath("/test1.txt").setHeader("Authorization", "Basic YWRtaW46Y2hpY2FnbzMy").build();
      List<String> response = client.sendMessage(requestString);

      assertTrue(response.contains("HTTP/1.1 204 No Content"));
    });
  }

  @Test
  void test404ForGetRequestAfterResourceDeleted() {
    TempFilesHelper.withTempDirectory(directory ->  {
      Path file = TempFilesHelper.createTempFile(directory, "/test1.txt");
      TempFilesHelper.createContents("Hello", file);
      HttpServer httpServer = configureWithDirectory(directory.toString(), "0");
      ClientHelper client2 = new ClientHelper();
      startOnNewThread(httpServer);
      connectTwoClients(httpServer, client2);

      requestString = requestBuilder.setMethod("DELETE").setPath("/test1.txt").setHeader("Authorization", "Basic YWRtaW46Y2hpY2FnbzMy").build();
      String secondRequest = requestBuilder.setMethod("GET").setPath("/test1.txt").build();
      List<String> response = client.sendMessage(requestString);
      List<String> getResponse = client2.sendMessage(secondRequest);

      assertTrue(response.contains("HTTP/1.1 204 No Content"));
      assertTrue(getResponse.contains("HTTP/1.1 404 Not Found"));
    });
  }

  @Test
  void whenNoDirectoryIsGivenTheApplicationHasNoPublicDirectory() throws InterruptedException {
    HttpServer httpServer = configureWithNoDirectory("0");
    startOnNewThread(httpServer);
    client.connectWithTry("127.0.0.1", httpServer);

    List<String> response = client.sendMessage(basicGetRequest());

    assertTrue(response.contains("Hello World"));
  }

  @Test
  void whenDirectoryArgumentIsPassedApplicationIsConfiguredWithPublicDirectory() {
    TempFilesHelper.withTempDirectory(directory -> {
      Path file = TempFilesHelper.createTempFile(directory, "/test1.txt");
      TempFilesHelper.createContents("Hello There From The Test Suite!", file);
      HttpServer httpServer = configureWithDirectory(directory.toString(), "0");
      startOnNewThread(httpServer);
      connectClient(httpServer, client);

      requestString = requestBuilder.setMethod("GET").setPath("/test1.txt").build();
      List<String> response = client.sendMessage(requestString);

      assertTrue(response.contains("Hello There From The Test Suite!"));
    });
  }

  @Test
  void testServesHtmlFileForDirectoryWhenPresent() {
    TempFilesHelper.withTempDirectory(directory -> {
      Path file = TempFilesHelper.createTempFile(directory, "/index.html");
      TempFilesHelper.createContents("<p>Hello!</p>", file);
      HttpServer httpServer = configureWithDirectory(directory.toString(), "0");
      startOnNewThread(httpServer);
      connectClient(httpServer, client);

      List<String> response = client.sendMessage(basicGetRequest());

      assertTrue(response.contains("<p>Hello!</p>"));
    });
  }

  @Test
  void testQueryParamsParsedWhenGiven() {
    HttpServer httpServer = configureWithNoDirectory("0");
    startOnNewThread(httpServer);
    connectClient(httpServer, client);

    requestString = requestBuilder.setMethod("GET").setPath("/parse?hello=world").build();
    List<String> response = client.sendMessage(requestString);

    assertTrue(response.get(4).contains("<dt>hello</dt><dd>world</dd>"));
  }

  @Test
  void test401ForUnauthorizedUser() {
    TempFilesHelper.withTempDirectory(directory -> {
      TempFilesHelper.createTempFile(directory, "/test1");
      HttpServer httpServer = configureWithDirectory(directory.toString(), "0");
      startOnNewThread(httpServer);
      connectClient(httpServer, client);

      requestString = requestBuilder.setMethod("DELETE").setPath("/test1.txt").build();
      List<String> response = client.sendMessage(requestString);
      assertTrue(response.contains("HTTP/1.1 401 Unauthorized"));
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
    BasicAuthorizer basicAuthorizer = new BasicAuthorizer("admin", "chicago32");
    Map<String, String> args = new HashMap<>();
    args.put("directory", directory);
    config.setPort(port);
    config.setHandler(new AuthorizedApplicationFactory().create(args, basicAuthorizer));
    return new HttpServer(config, new ConnectionHandler(), new RequestReaderFactory());
  }

  private HttpServer configureWithNoDirectory(String port) {
    ServerConfiguration config = new ServerConfiguration();
    BasicAuthorizer basicAuthorizer = new BasicAuthorizer("admin", "chicago32");
    Map<String, String> args = new HashMap<>();
    config.setPort(port);
    config.setHandler(new AuthorizedApplicationFactory().create(args, basicAuthorizer));
    return new HttpServer(config, new ConnectionHandler(), new RequestReaderFactory());
  }

  private void startOnNewThread(HttpServer httpServer) {
    ExecutorService executorService = Executors.newSingleThreadExecutor();
    executorService.submit(httpServer::start);
  }

  private void connectClient(HttpServer httpServer, ClientHelper client) {
    try {
      client.connectWithTry("127.0.0.1", httpServer);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  private void connectTwoClients(HttpServer httpServer, ClientHelper client2) {
    try {
      client.connectWithTry("127.0.0.1", httpServer);
      client2.connectWithTry("127.0.0.1", httpServer);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  private String basicGetRequest() {
    return requestBuilder.setMethod("GET").setPath("/").build();
  }

  private void sleep() {
    try {
      Thread.sleep(50);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
