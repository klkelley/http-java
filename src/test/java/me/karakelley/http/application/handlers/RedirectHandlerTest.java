package me.karakelley.http.application.handlers;

import me.karakelley.http.server.http.HttpMethod;
import me.karakelley.http.server.http.Request;
import me.karakelley.http.server.http.Response;
import me.karakelley.http.server.Handler;
import me.karakelley.http.server.ResponseFormatter;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RedirectHandlerTest {

  @Test
  void testRedirect() {
    Handler handler = new RedirectHandler();
    Response response = handler.respond(new Request.Builder()
                                        .setMethod(HttpMethod.GET)
                                        .setPath("/redirectme")
                                        .setProtocol("HTTP/1.1")
                                        .setHeaders(new HashMap<>())
                                        .setPort(4000)
                                        .build());

    assertEquals(new String(new ResponseFormatter(response).convertToBytes()), "HTTP/1.1 301 Moved Permanently\r\nLocation: http://localhost:4000/\r\n\r\n");
  }
}
