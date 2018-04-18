package me.karakelley.http.handlers;

import me.karakelley.http.HttpMethod;
import me.karakelley.http.Request;
import me.karakelley.http.Response;
import me.karakelley.http.filesystem.PublicDirectory;
import me.karakelley.http.helpers.TempFilesHelper;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class UpdateResourceHandlerTest {

  @Test
  void test204ResponseForUpdatingExistingResource() {
    TempFilesHelper.withTempDirectory(directory -> {
      Path file = TempFilesHelper.createTempFile(directory, "/test1");
      TempFilesHelper.createContents("Hello", file);
      PublicDirectory publicDirectory = PublicDirectory.create(directory.toString());
      Handler handler = new UpdateResourceHandler(publicDirectory);
      HashMap<String, String> headers = new HashMap<>();
      headers.put("Content-Length", "11");
      Response response = handler.respond(new Request(HttpMethod.PUT, "/test1.txt", "HTTP/1.1", headers, "Hello World".getBytes(), 0));
      assertEquals("204 No Content", response.getStatus());
    });
  }

  @Test
  void test201ResponseForNewResource() {
    TempFilesHelper.withTempDirectory(directory -> {
      PublicDirectory publicDirectory = PublicDirectory.create(directory.toString());
      Handler handler = new UpdateResourceHandler(publicDirectory);
      HashMap<String, String> headers = new HashMap<>();
      headers.put("Content-Length", "11");
      Response response = handler.respond(new Request(HttpMethod.PUT, "/test1.txt", "HTTP/1.1", headers, "Hello World".getBytes(), 0));
      assertEquals("201 Created", response.getStatus());
    });
  }
}