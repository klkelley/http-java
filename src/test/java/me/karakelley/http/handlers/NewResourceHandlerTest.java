package me.karakelley.http.handlers;

import me.karakelley.http.http.Request;
import me.karakelley.http.http.Response;
import me.karakelley.http.filesystem.PublicDirectory;
import me.karakelley.http.helpers.TempFilesHelper;
import me.karakelley.http.http.HttpMethod;
import me.karakelley.http.server.Handler;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class NewResourceHandlerTest {

  @Test
  void test201ResponseForPostContainsLocationHeader() {
    TempFilesHelper.withTempDirectory(directory -> {
      PublicDirectory publicDirectory = PublicDirectory.create(directory.toString());
      Handler handler = new NewResourceHandler(publicDirectory);
      Response response = handler.respond(new Request.Builder()
              .setMethod(HttpMethod.POST)
              .setPath("/test1.txt")
              .setProtocol("HTTP/1.1")
              .setHeaders(setHeaders())
              .setBody("123456789".getBytes())
              .setPort(0)
              .build());

      assertEquals("./src/testing/test1.txt", response.getHeaders().get("Location"));
    });
  }

  @Test
  void testPostToRootDirectoryWithoutFile() {
    PublicDirectory publicDirectory = PublicDirectory.create("/");
    Handler handler = new NewResourceHandler(publicDirectory);
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
      Handler handler = new NewResourceHandler(publicDirectory);
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
      TempFilesHelper.createTempFile(directory, "/testing1");
      PublicDirectory publicDirectory = PublicDirectory.create(directory.toString());
      Handler handler = new NewResourceHandler(publicDirectory);
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

  private Map<String, String> setHeaders() {
    HashMap<String, String> headers = new HashMap<>();
    headers.put("Content-Length", "9");
    return headers;
  }
}