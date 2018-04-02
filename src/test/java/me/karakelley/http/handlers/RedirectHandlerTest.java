package me.karakelley.http.handlers;

import me.karakelley.http.*;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class RedirectHandlerTest {

  @Test
  void testRedirect() {
    Handler handler = new RedirectHandler();
    Response response = handler.respond(new Request("GET", "/redirectme", "HTTP/1.1", new HashMap<>(), 4000));
    assertEquals(new String(new ResponseFormatter(response).convertToBytes()), "HTTP/1.1 301 Moved Permanently\r\nLocation: http://localhost:4000/\r\n\r\n");
  }
}
