package me.karakelley.http.handlers;

import me.karakelley.http.http.Request;
import me.karakelley.http.http.Response;
import me.karakelley.http.filesystem.PublicDirectory;
import me.karakelley.http.helpers.TempFilesHelper;
import me.karakelley.http.http.HttpMethod;
import me.karakelley.http.server.Handler;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class NewResourceHandlerTest {

  @Test
  void test201ResponseForPostContainsLocationHeader() {
    TempFilesHelper.withTempDirectory(directory -> {

      PublicDirectory publicDirectory = PublicDirectory.create(directory.toString());
      Handler handler = new NewResourceHandler(publicDirectory);
      HashMap<String, String> headers = new HashMap<>();
      headers.put("Content-Length", "9");
      Response response = handler.respond(new Request(HttpMethod.POST, "/test1.txt", "HTTP/1.1", headers, "123456789".getBytes(), 0));
      assertEquals("./src/testing/test1.txt", response.getHeaders().get("Location"));
    });
  }

  @Test
  void testPostToRootDirectoryWithoutFile() {
    PublicDirectory publicDirectory = PublicDirectory.create("/");
    Handler handler = new NewResourceHandler(publicDirectory);
    Response response = handler.respond(new Request(HttpMethod.POST, "/", "HTTP/1.1", null, null, 0));
    assertEquals(response.getStatus(), "409 Conflict");
  }

  @Test
  void test201ResponseForNewDirectory() {
    TempFilesHelper.withTempDirectory(directory ->  {
      PublicDirectory publicDirectory = PublicDirectory.create(directory.toString());
      Handler handler = new NewResourceHandler(publicDirectory);
      Response response = handler.respond(new Request(HttpMethod.POST, "/newpath/", "HTTP/1.1", null, null,  0));

      assertEquals(response.getStatus(), "201 Created");
    });
  }

  @Test
  void test409ResponseForFileThatAlreadyExists() {
    TempFilesHelper.withTempDirectory(directory -> {
      Path file = TempFilesHelper.createTempFile(directory, "/testing1");
      PublicDirectory publicDirectory = PublicDirectory.create(directory.toString());
      Handler handler = new NewResourceHandler(publicDirectory);
      HashMap<String, String> headers = new HashMap<>();
      headers.put("Content-Length", "9");
      Response response = handler.respond(new Request(HttpMethod.POST, "/testing1.txt", "HTTP/1.1", headers, null, 0));
      assertEquals("409 Conflict", response.getStatus());
    });
  }
}