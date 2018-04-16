package me.karakelley.http.handlers;

import me.karakelley.http.filesystem.PublicDirectory;
import me.karakelley.http.helpers.TempFilesHelper;
import me.karakelley.http.http.HttpMethod;
import me.karakelley.http.http.Response;
import me.karakelley.http.http.Request;
import me.karakelley.http.server.Handler;
import me.karakelley.http.server.ResponseFormatter;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ApplicationTest {

  @Test
  void testRootRouteNoDirectory() {
    Handler handler = new Application();
    Response response = handler.respond(basicGetRequest());
    assertEquals(new String(new ResponseFormatter(response).convertToBytes()), "HTTP/1.1 200 OK\r\nContent-Length: 11\r\nContent-Type: text/plain\r\n\r\nHello World");
  }

  @Test
  void testRootRouteWithDirectory() {
    TempFilesHelper.withTempDirectory(directory ->  {
      TempFilesHelper.createTempFile(directory, "/test1");
      TempFilesHelper.createTempFile(directory, "/test2");

      Handler handler = new Application(PublicDirectory.create(directory.toString()));
      Response response = handler.respond(basicGetRequest());

      assertTrue(new String(response.getBody()).split("", 2).length == 2);
    });
  }

  @Test
  void testRedirectMeRoute() {
    Handler handler = new Application();
    Response response = handler.respond(new Request.Builder()
            .setMethod(HttpMethod.GET)
            .setPath("/redirectme")
            .setProtocol("HTTP/1.1")
            .setHeaders(new HashMap<>())
            .setPort(4000)
            .build());

    assertEquals(new String(new ResponseFormatter(response).convertToBytes()), "HTTP/1.1 301 Moved Permanently\r\nLocation: http://localhost:4000/\r\n\r\n");
  }

  @Test
  void testRedirectMeRouteWithPost() {
    Handler handler = new Application();
    Response response = handler.respond(new Request.Builder()
            .setMethod(HttpMethod.POST)
            .setPath("/redirectme")
            .setProtocol("HTTP/1.1")
            .setHeaders(new HashMap<>())
            .setPort(0)
            .build());

    assertEquals(new String(new ResponseFormatter(response).convertToBytes()), "HTTP/1.1 405 Method Not Allowed\r\n\r\n");
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
}