package me.karakelley.http.handlers;

import me.karakelley.http.*;
import me.karakelley.http.filesystem.PublicDirectory;
import me.karakelley.http.helpers.TempFilesHelper;
import me.karakelley.http.HttpMethod;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ApplicationTest {

  @Test
  void testRootRouteNoDirectory() {
    Handler handler = new Application();
    Response response = handler.respond(new Request(HttpMethod.GET, "/", "HTTP/1.1", new HashMap<>(), null,0));
    assertEquals(new String(new ResponseFormatter(response).convertToBytes()), "HTTP/1.1 200 OK\r\nContent-Length: 11\r\nContent-Type: text/plain\r\n\r\nHello World");
  }

  @Test
  void testRootRouteWithDirectory() {
    TempFilesHelper.withTempDirectory(directory ->  {
      Path fileOne = TempFilesHelper.createTempFile(directory, "/test1");
      Path fileTwo = TempFilesHelper.createTempFile(directory, "/test2");

      Handler handler = new Application(PublicDirectory.create(directory.toString()));
      Response response = handler.respond(new Request(HttpMethod.GET, "/", "HTTP/1.1", new HashMap<>(), null, 0));

      assertTrue(new String(response.getBody()).split("", 2).length == 2);
    });
  }

  @Test
  void testRedirectMeRoute() {
    Handler handler = new Application();
    Response response = handler.respond(new Request(HttpMethod.GET, "/redirectme", "HTTP/1.1", new HashMap<>(), null,  4000));
    assertEquals(new String(new ResponseFormatter(response).convertToBytes()), "HTTP/1.1 301 Moved Permanently\r\nLocation: http://localhost:4000/\r\n\r\n");
  }

  @Test
  void testRedirectMeRouteWithPost() {
    Handler handler = new Application();
    Response response = handler.respond(new Request(HttpMethod.POST, "/redirectme", "HTTP/1.1", new HashMap<>(), null, 4000));
    assertEquals(new String(new ResponseFormatter(response).convertToBytes()), "HTTP/1.1 405 Method Not Allowed\r\n\r\n");
  }
}