package me.karakelley.http.server.http;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RequestTest {
  Request request;

  @Test
  void testGetMethod() {
    request = basicGetRequest();

    assertEquals(HttpMethod.GET, request.getMethod());
  }

  @Test
  void testGetPath() {
    request = basicGetRequest();

    assertEquals("/", request.getPath());
  }

  @Test
  void testGetProtocol() {
    request = basicGetRequest();

    assertEquals("HTTP/1.1", request.getProtocol());
  }

  @Test
  void testGetHeaders() {
    Map<String, String> headers = setHeaders();
    request = basicGetRequest();

    assertEquals(headers, request.getHeaders());
  }

  @Test
  void testOneHeader() {
    request = basicGetRequest();

    assertEquals("localhost:5000", request.getHeader("Host"));
  }

  @Test
  void testBadRequest() {
    try {
      request = new Request.Builder().setMethod(HttpMethod.GET).setPath("/").setPort(0).build();
      } catch (Exception e) {
      assertEquals("java.lang.ArrayIndexOutOfBoundsException: 2", e.getMessage());
    }
  }

  @Test
  void testGetHostAndPort() {
    request = basicGetRequest();
    assertEquals(request.getHostAndPort(), "localhost:5000" );
  }

  @Test
  void testGetHostAndPortWithHostDefined() {
    HashMap<String, String> headers = new HashMap<>();
    headers.put("Host", "test.com");
    request = basicGetRequest(headers, 4000);
    assertEquals(request.getHostAndPort(), "test.com:4000" );
  }

  @Test
  void testPostBody() {
    HashMap<String, String> headers = new HashMap<>();
    headers.put("Content-Length", "9");
    Request request = new Request.Builder()
            .setMethod(HttpMethod.POST)
            .setPath("/test1.txt")
            .setProtocol("HTTP/1.1")
            .setHeaders(headers)
            .setBody("txt=99999".getBytes())
            .setPort(0)
            .build();

    assertEquals(new String(request.getBody()), "txt=99999");
  }

  @Test
  void testGetPathWithoutQueryParams() {
    request = new Request.Builder()
            .setMethod(HttpMethod.GET)
            .setPath("/parse?hey=hola")
            .setPort(0)
            .build();

    assertEquals("/parse", request.getPath());
  }

  private Map<String, String> setHeaders() {
    Map<String, String> headers = new HashMap<>();
    headers.put("Host", "localhost:5000");
    headers.put("Connection", "keep-alive");
    return headers;
  }

  private Request basicGetRequest() {
    return basicGetRequest(setHeaders(), 0);
  }

  private Request basicGetRequest(Map<String, String> headers, int port) {
    return new Request.Builder()
            .setMethod(HttpMethod.GET)
            .setPath("/")
            .setProtocol("HTTP/1.1")
            .setHeaders(headers)
            .setPort(port)
            .build();
  }

}