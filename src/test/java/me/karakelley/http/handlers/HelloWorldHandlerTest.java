package me.karakelley.http.handlers;

import me.karakelley.http.*;
import me.karakelley.http.HttpMethod;
import org.junit.jupiter.api.Test;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class HelloWorldHandlerTest {

  @Test
  void testHelloWorld() {
    Handler handler = new HelloWorldHandler();
    Response response = handler.respond(new Request(HttpMethod.GET, "/", "HTTP/1.1", new HashMap<>(), null, 0));
    assertEquals(new String(new ResponseFormatter(response).convertToBytes()), "HTTP/1.1 200 OK\r\nContent-Length: 11\r\nContent-Type: text/plain\r\n\r\nHello World");
  }
}