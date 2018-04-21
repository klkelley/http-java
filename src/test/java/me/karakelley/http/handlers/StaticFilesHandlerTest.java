package me.karakelley.http.handlers;

import me.karakelley.http.filesystem.PublicDirectory;
import me.karakelley.http.helpers.TempFilesHelper;
import me.karakelley.http.http.HttpMethod;
import me.karakelley.http.http.Request;
import me.karakelley.http.http.Response;
import me.karakelley.http.presenters.HtmlFilePresenter;
import me.karakelley.http.server.Handler;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class StaticFilesHandlerTest {

  @Nested
  class WhenRequestIsGet {
    @Test
    void testDisplaysFiles() {
      TempFilesHelper.withTempDirectory(directory -> {
        TempFilesHelper.createTempFile(directory, "/test1.txt");
        TempFilesHelper.createTempFile(directory, "/test2.txt");
        PublicDirectory publicDirectory = PublicDirectory.create(directory.toString());
        FilePresenter filePresenter = new HtmlFilePresenter(publicDirectory);
        Handler handler = new StaticFilesHandler(publicDirectory, filePresenter);

        Response response = handler.respond(basicGetRequest());

        assertTrue(new String(response.getBody()).split("", 2).length == 2);
      });
    }

    @Test
    void testCreatesLinksForDirectories() {
      TempFilesHelper.withTempDirectory(directory -> {
        TempFilesHelper.createTempFile(directory, "/test1.txt");
        PublicDirectory publicDirectory = PublicDirectory.create(directory.toString());
        FilePresenter filePresenter = new HtmlFilePresenter(publicDirectory);
        Handler handler = new StaticFilesHandler(publicDirectory, filePresenter);
        Response response = handler.respond(basicGetRequest());

        assertTrue(new String(response.getBody()).contains("<p><a href=\"/test1.txt\">test1.txt</a></p>"));
      });
    }

    @Test
    void testServesTextFile() {
      TempFilesHelper.withTempDirectory(directory -> {
        Path file = TempFilesHelper.createTempFile(directory, "/test1.txt");
        TempFilesHelper.createContents("Hello World", file);
        PublicDirectory publicDirectory = PublicDirectory.create(directory.toString());
        FilePresenter filePresenter = new HtmlFilePresenter(publicDirectory);
        Handler handler = new StaticFilesHandler(publicDirectory, filePresenter);
        Response response = handler.respond(new Request.Builder()
                .setMethod(HttpMethod.GET)
                .setPath("/test1.txt")
                .setProtocol("HTTP/1.1")
                .setPort(0)
                .build());

        assertEquals("Hello World", new String(response.getBody()));
      });
    }

    @Test
    void testServesIndexFileIfPresent() {
      TempFilesHelper.withTempDirectory(directory -> {
        Path file = TempFilesHelper.createTempFile(directory, "/index.html");
        TempFilesHelper.createContents("<p>Index File Present</p>", file);
        PublicDirectory publicDirectory = PublicDirectory.create(directory.toString());
        FilePresenter filePresenter = new HtmlFilePresenter(publicDirectory);
        Handler handler = new StaticFilesHandler(publicDirectory, filePresenter);
        Response response = handler.respond(new Request.Builder()
                .setMethod(HttpMethod.GET)
                .setPath("/")
                .setProtocol("HTTP/1.1")
                .setPort(0)
                .build());

        assertEquals("<p>Index File Present</p>", new String(response.getBody()));
      });
    }
  }

  @Nested
  class WhenRequestIsPost {
    @Test
    void testResponseContainsLocationHeaderForPostRequest() {
      TempFilesHelper.withTempDirectory(directory -> {
        PublicDirectory publicDirectory = PublicDirectory.create(directory.toString());
        FilePresenter filePresenter = new HtmlFilePresenter(publicDirectory);
        Handler handler = new StaticFilesHandler(publicDirectory, filePresenter);
        Response response = handler.respond(new Request.Builder()
                .setMethod(HttpMethod.POST)
                .setPath("/test1.txt")
                .setProtocol("HTTP/1.1")
                .setHeaders(setHeaders())
                .setBody("12345678911".getBytes())
                .setPort(0)
                .build());

        assertEquals("./src/testing/test1.txt", response.getHeaders().get("Location"));
      });
    }

    @Test
    void testPostToRootDirectoryWithoutFile() {
      PublicDirectory publicDirectory = PublicDirectory.create("/");
      FilePresenter filePresenter = new HtmlFilePresenter(publicDirectory);
      Handler handler = new StaticFilesHandler(publicDirectory, filePresenter);
      Response response = handler.respond(new Request.Builder()
              .setMethod(HttpMethod.POST)
              .setPath("/")
              .setProtocol("HTTP/1/1")
              .setPort(0)
              .build());

      assertEquals(response.getStatus(), "409 Conflict");
    }

    @Test
    void test201ResponseForNewDirectory() {
      TempFilesHelper.withTempDirectory(directory ->  {
        PublicDirectory publicDirectory = PublicDirectory.create(directory.toString());
        FilePresenter filePresenter = new HtmlFilePresenter(publicDirectory);
        Handler handler = new StaticFilesHandler(publicDirectory, filePresenter);
        Response response = handler.respond(new Request.Builder()
                .setMethod(HttpMethod.POST)
                .setPath("/newpath/")
                .setProtocol("HTTP/1.1")
                .setPort(0)
                .build());

        assertEquals(response.getStatus(), "201 Created");
      });
    }

    @Test
    void test409ResponseForFileThatAlreadyExists() {
      TempFilesHelper.withTempDirectory(directory -> {
        TempFilesHelper.createTempFile(directory, "/testing1.txt");
        PublicDirectory publicDirectory = PublicDirectory.create(directory.toString());
        FilePresenter filePresenter = new HtmlFilePresenter(publicDirectory);
        Handler handler = new StaticFilesHandler(publicDirectory, filePresenter);
        Response response = handler.respond(new Request.Builder()
                .setMethod(HttpMethod.POST)
                .setPath("/testing1.txt")
                .setProtocol("HTTP/1.1")
                .setHeaders(setHeaders())
                .setPort(0)
                .build());

        assertEquals("409 Conflict", response.getStatus());
      });
    }
  }

  @Nested
  class WhenRequestIsPut {
    @Test
    void test204ResponseForUpdatingExistingResource() {
      TempFilesHelper.withTempDirectory(directory -> {
        Path file = TempFilesHelper.createTempFile(directory, "/test1.txt");
        TempFilesHelper.createContents("Hello", file);
        PublicDirectory publicDirectory = PublicDirectory.create(directory.toString());
        FilePresenter filePresenter = new HtmlFilePresenter(publicDirectory);
        Handler handler = new StaticFilesHandler(publicDirectory, filePresenter);
        Response response = handler.respond(basicPutRequest());

        assertEquals("204 No Content", response.getStatus());
      });
    }


    @Test
    void test201ResponseForNewResource() {
      TempFilesHelper.withTempDirectory(directory -> {
        PublicDirectory publicDirectory = PublicDirectory.create(directory.toString());
        FilePresenter filePresenter = new HtmlFilePresenter(publicDirectory);
        Handler handler = new StaticFilesHandler(publicDirectory, filePresenter);
        Response response = handler.respond(basicPutRequest());

        assertEquals("201 Created", response.getStatus());
      });
    }
  }

  @Nested
  class WhenRequestIsDelete {
    @Test
    void testResourceIsDeleted() {
      TempFilesHelper.withTempDirectory(directory -> {
        PublicDirectory publicDirectory = PublicDirectory.create(directory.toString());
        TempFilesHelper.createTempFile(directory, "/test1.txt");
        FilePresenter filePresenter = new HtmlFilePresenter(publicDirectory);
        Handler handler = new StaticFilesHandler(publicDirectory, filePresenter);
        Response response = handler.respond(new Request.Builder()
                .setMethod(HttpMethod.DELETE)
                .setPath("/test1/.txt")
                .setProtocol("HTTP/1.1")
                .setPort(0)
                .build());

        assertEquals("204 No Content", response.getStatus());
      });
    }
  }


  private Request basicGetRequest() {
    return new Request.Builder()
            .setMethod(HttpMethod.GET)
            .setPath("/")
            .setProtocol("HTTP/1.1")
            .setHeaders(new HashMap<>())
            .setPort(0)
            .build();
  }

  private Request basicPutRequest() {
    return new Request.Builder()
            .setMethod(HttpMethod.PUT)
            .setPath("/test1.txt")
            .setProtocol("HTTP/1.1")
            .setHeaders(setHeaders())
            .setBody("Hello World".getBytes())
            .setPort(0)
            .build();
  }

  private Map<String, String> setHeaders() {
    HashMap<String, String> headers = new HashMap<>();
    headers.put("Content-Length", "11");
    return headers;
  }
}