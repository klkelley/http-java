package me.karakelley.http.controllers;

import me.karakelley.http.Request;
import me.karakelley.http.Response;
import me.karakelley.http.utility.BufferedLineReader;
import me.karakelley.http.utility.LineReader;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;

import static org.junit.jupiter.api.Assertions.*;

class RedirectControllerTest {

  @Test
  void testRedirect() {
    Controller controller = new RedirectController();
    Response response = controller.respond(new Request(newBufferedReader("GET /redirectme HTTP/1.1\r\n"), 4000));
    assertEquals(new String(response.convertToBytes()), "HTTP/1.1 301 Moved Permanently\r\nLocation: http://localhost:4000/\r\n\r\n");
  }

  @Test
  void testRedirectGivenInvalidMethod() {
    Controller controller = new RedirectController();
    Response response = controller.respond(new Request(newBufferedReader("POST /redirectme HTTP/1.1\r\n"), 0));
    assertEquals(new String(response.convertToBytes()), "HTTP/1.1 404 Not Found\r\n\r\n");
  }

  private LineReader newBufferedReader(String request) {
    ByteArrayInputStream inputStream = new ByteArrayInputStream(request.getBytes());
    return new BufferedLineReader(new InputStreamReader(inputStream));
  }
}