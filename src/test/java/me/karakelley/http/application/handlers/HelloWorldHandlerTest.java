package me.karakelley.http.application.handlers;

import me.karakelley.http.server.http.HttpMethod;
import me.karakelley.http.server.http.Request;
import me.karakelley.http.server.http.Response;
import me.karakelley.http.server.Handler;
import me.karakelley.http.server.ResponseFormatter;
import org.junit.jupiter.api.Test;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HelloWorldHandlerTest {

  @Test
  void testHelloWorld() {
    Handler handler = new HelloWorldHandler();
    Response response = handler.respond(new Request.Builder()
            .setMethod(HttpMethod.GET)
            .setPath("/")
            .setProtocol("HTTP/1.1")
            .setHeaders(new HashMap<>())
            .setPort(0)
            .build());

    assertEquals(new String(new ResponseFormatter(response).convertToBytes()), "HTTP/1.1 200 OK\r\nContent-Length: 11\r\nContent-Type: text/plain\r\n\r\nHello World");
  }
}
