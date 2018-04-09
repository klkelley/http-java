package me.karakelley.http.server;

import me.karakelley.http.HttpMethod;
import me.karakelley.http.Request;
import me.karakelley.http.exceptions.InvalidRequestException;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class HttpRequestReaderTest {

  @Test
  void testRequestObjectIsNotNull() {
    Request request = parse("GET / HTTP/1.1\r\n\r\n");
    assertTrue(request != null);
  }

  @Test
  void testRequestHasGetMethod() {
    Request request = parse("GET / HTTP/1.1\r\n\r\n");
    assertEquals(HttpMethod.GET, request.getMethod());
  }

  @Test
  void testRequestHasPostMethod() {
    Request request = parse("POST / HTTP/1.1\r\n\r\n");
    assertEquals(HttpMethod.POST, request.getMethod());
  }

  @Test
  void testRequestHasAPathofRoot() {
    Request request = parse("POST / HTTP/1.1\r\n\r\n");
    assertEquals("/", request.getPath());
  }

  @Test
  void testRequestHasAPathOfRedirectme() {
    Request request = parse("POST /redirectme HTTP/1.1\r\n\r\n");
    assertEquals("/redirectme", request.getPath());
  }

  @Test
  void testRequestHasAProtocol() {
    Request request = parse("POST / HTTP/1.1\r\n\r\n");
    assertEquals("HTTP/1.1", request.getProtocol());
  }

  @Test
  void testASyntacticallyInvalidRequestMethod() {
    assertThrows(InvalidRequestException.class, () -> parse("GIT / HTTP/1.1\r\n\r\n"));
  }

  @Test
  void testInvalidHttpProtocol() {
    assertThrows(InvalidRequestException.class, () -> parse("GET / HTTP\r\n\r\n"));
  }

  @Test
  void testRequestHasHeaders() {
    Request request = parse("POST /hey.txt HTTP/1.1\r\nContent-Type: text/plain\r\n\r\n");
    Map<String, String> headers = new HashMap<>();
    headers.put("Content-Type", "text/plain");
    assertEquals(headers, request.getHeaders());
  }

  @Test
  void testRequestHasMultipleHeaders() {
    Request request = parse("POST /hey.txt HTTP/1.1\r\nContent-Type: text/plain\r\nContent-Length: 3\r\n\r\nhey");
    Map<String, String> headers = new HashMap<>();
    headers.put("Content-Type", "text/plain");
    headers.put("Content-Length", "3");
    assertEquals(headers, request.getHeaders());
  }

  @Test
  void testWhenABodyIsPresent() {
    Request request = parse("POST /hey.txt HTTP/1.1\r\nContent-Type: text/plain\r\nContent-Length: 3\r\n\r\nhey");
    assertEquals("hey", new String(request.getBody()));
  }

  private Request parse(String request) {
    ByteArrayInputStream inputStream = new ByteArrayInputStream(request.getBytes());
    HttpRequestReader requestReader = new HttpRequestReader(inputStream);
    return requestReader.read(0);
  }
}