package me.karakelley.http;

import me.karakelley.http.http.HttpMethod;
import me.karakelley.http.http.Request;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
import static org.junit.jupiter.api.Assertions.assertEquals;

class RequestTest {
  Request request;

  @Test
  void testGetMethod() {
    request = new Request(HttpMethod.GET, null, null, null, "".getBytes(), 0);
    assertEquals(HttpMethod.GET, request.getMethod());
  }

  @Test
  void testGetPath() {
    request = new Request(null, "/", null, null, "".getBytes(), 0);
    assertEquals("/", request.getPath());
  }

  @Test
  void testGetProtocol() {
    request = new Request(null, null, "HTTP/1.1", null, "".getBytes(), 0);
    assertEquals("HTTP/1.1", request.getProtocol());
  }

  @Test
  void testGetHeaders() {
    HashMap headers = new HashMap();
    headers.put("Host", "localhost:5000");
    headers.put("Connection", "keep-alive");

    request = new Request(HttpMethod.GET, "/", "HTTP/1.1", headers, "".getBytes(), 0);
    assertEquals(headers, request.getHeaders());
  }

  @Test
  void testOneHeader() {
    HashMap headers = new HashMap();
    headers.put("Host", "localhost:5000");
    headers.put("Connection", "keep-alive");
    request = new Request(null, null, null, headers, "".getBytes(), 0);
    assertEquals("localhost:5000", request.getHeader("Host"));
  }

  @Test
  void testBadRequest() {
    try {
      request = new Request(HttpMethod.GET, "/", null, null, "".getBytes(), 0);
    } catch (Exception e) {
      assertEquals("java.lang.ArrayIndexOutOfBoundsException: 2", e.getMessage());
    }
  }

  @Test
  void testGetHostAndPort() {
    request = new Request(HttpMethod.GET, "/",  "HTTP/1.1", new HashMap<>(), "".getBytes(),  4000);
    assertEquals(request.getHostAndPort(), "localhost:4000" );
  }

  @Test
  void testGetHostAndPortWithHostDefined() {
    HashMap headers = new HashMap();
    headers.put("Host", "test.com");
    request = new Request(HttpMethod.GET, "/",  "HTTP/1.1", headers, "".getBytes(), 4000);
    assertEquals(request.getHostAndPort(), "test.com:4000" );
  }

  @Test
  void testgetBody() throws IOException {
    HashMap headers = new HashMap();
    headers.put("Content-Length", 9);
    Request request = new Request(HttpMethod.POST, "/test1.txt", "HTTP/1.1", headers, "txt=99999".getBytes(), 0);
    assertEquals(new String(request.getBody()), "txt=99999");
  }
}