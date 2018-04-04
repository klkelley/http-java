package me.karakelley.http;

import org.junit.jupiter.api.Test;
import java.util.HashMap;
import static org.junit.jupiter.api.Assertions.assertEquals;

class RequestTest {
  Request request;

  @Test
  void testGetMethod() {
    request = new Request("GET", null, null, null, 0);
    assertEquals("GET", request.getMethod());
  }

  @Test
  void testGetPath() {
    request = new Request(null, "/", null, null, 0);
    assertEquals("/", request.getPath());
  }

  @Test
  void testGetProtocol() {
    request = new Request(null, null, "HTTP/1.1", null, 0);
    assertEquals("HTTP/1.1", request.getProtocol());
  }

  @Test
  void testGetHeaders() {
    HashMap headers = new HashMap();
    headers.put("Host", "localhost:5000");
    headers.put("Connection", "keep-alive");

    request = new Request("GET", "/", "HTTP/1.1", headers, 0);
    assertEquals(headers, request.getHeaders());
  }

  @Test
  void testOneHeader() {
    HashMap headers = new HashMap();
    headers.put("Host", "localhost:5000");
    headers.put("Connection", "keep-alive");
    request = new Request(null, null, null, headers, 0);
    assertEquals("localhost:5000", request.getHeader("Host"));
  }

  @Test
  void testGetHostAndPort() {
    request = new Request("GET", "/",  "HTTP/1.1", new HashMap<>(), 4000);
    assertEquals(request.getHostAndPort(), "localhost:4000" );
  }

  @Test
  void testGetHostAndPortWithHostDefined() {
    HashMap headers = new HashMap();
    headers.put("Host", "test.com");
    request = new Request("GET", "/",  "HTTP/1.1", headers, 4000);
    assertEquals(request.getHostAndPort(), "test.com:4000" );
  }
}