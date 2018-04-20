package me.karakelley.http.handlers;

import me.karakelley.http.http.HttpMethod;
import me.karakelley.http.http.Request;
import me.karakelley.http.http.Response;
import me.karakelley.http.filesystem.PublicDirectory;
import me.karakelley.http.helpers.TempFilesHelper;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class DeleteResourceHandlerTest {

  @Test
  void testResourceIsDeleted() {
    TempFilesHelper.withTempDirectory(directory -> {
      Path file = TempFilesHelper.createTempFile(directory, "/test1");
      DeleteResourceHandler handler = new DeleteResourceHandler(PublicDirectory.create(directory.toString()));
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