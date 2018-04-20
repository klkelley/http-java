package me.karakelley.http.handlers;

import me.karakelley.http.http.HttpMethod;
import me.karakelley.http.http.Response;
import me.karakelley.http.http.Request;
import me.karakelley.http.server.Handler;
import me.karakelley.http.server.ResponseFormatter;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class RedirectHandlerTest {

  @Test
  void testRedirect() {
    Handler handler = new RedirectHandler();
    Response response = handler.respond(new Request(HttpMethod.GET, "/redirectme", "HTTP/1.1", new HashMap<>(),  null, 4000));
    assertEquals(new String(new ResponseFormatter(response).convertToBytes()), "HTTP/1.1 301 Moved Permanently\r\nLocation: http://localhost:4000/\r\n\r\n");
  }
}
