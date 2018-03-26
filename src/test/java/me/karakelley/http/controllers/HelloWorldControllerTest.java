package me.karakelley.http.controllers;

import me.karakelley.http.Request;
import me.karakelley.http.Response;
import me.karakelley.http.utility.BufferedLineReader;
import me.karakelley.http.utility.LineReader;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;

import static org.junit.jupiter.api.Assertions.*;

class HelloWorldControllerTest {

  @Test
  void testHelloWorld() {
    Controller controller = new HelloWorldController();
    Response response = controller.respond(new Request(newBufferedReader("GET / HTTP/1.1\r\n"), 0));
    assertEquals(new String(response.convertToBytes()), "HTTP/1.1 200 OK\r\nContent-Length: 11\r\nContent-Type: text/plain\r\n\r\nHello World");
  }

  @Test
  void testInvalidRequest() {
    Controller controller = new HelloWorldController();
    Response response = controller.respond(new Request(newBufferedReader("POST / HTTP/1.1\r\n"), 0));
    assertEquals(new String(response.convertToBytes()), "HTTP/1.1 404 Not Found\r\n\r\n");
  }

  private LineReader newBufferedReader(String request) {
    ByteArrayInputStream inputStream = new ByteArrayInputStream(request.getBytes());
    return new BufferedLineReader(new InputStreamReader(inputStream));
  }
}