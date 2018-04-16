package me.karakelley.http.handlers;

import me.karakelley.http.http.HttpMethod;
import me.karakelley.http.http.Request;
import me.karakelley.http.http.Response;
import me.karakelley.http.filesystem.PublicDirectory;
import me.karakelley.http.helpers.TempFilesHelper;
import me.karakelley.http.server.Handler;
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
      Response response = handler.respond(basicPutRequest());

      assertEquals("204 No Content", response.getStatus());
    });
  }

  @Test
  void test201ResponseForNewResource() {
    TempFilesHelper.withTempDirectory(directory -> {
      PublicDirectory publicDirectory = PublicDirectory.create(directory.toString());
      Handler handler = new UpdateResourceHandler(publicDirectory);
      Response response = handler.respond(basicPutRequest());

      assertEquals("201 Created", response.getStatus());
    });
  }

  private HashMap<String, String> setHeaders() {
    HashMap<String, String> headers = new HashMap<>();
    headers.put("Content-Length", "11");
    return headers;
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
}