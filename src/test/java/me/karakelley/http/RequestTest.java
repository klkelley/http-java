package me.karakelley.http;

import me.karakelley.http.utility.BufferedLineReader;
import me.karakelley.http.utility.LineReader;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class RequestTest {
  Request request;
  final String basicGetRequest = "GET / HTTP/1.1\r\nHost: localhost:5000\r\n";
  final String requestWithHeaders = "GET / HTTP/1.1\r\nHost: localhost:5000\r\nConnection: keep-alive\r\n";

  @Test
  void testGetMethod() {
    request = new Request(newBufferedReader(basicGetRequest), 0);
    assertEquals("GET", request.getMethod());
  }

  @Test
  void testGetPath() {
    request = new Request(newBufferedReader(basicGetRequest), 0);
    assertEquals("/", request.getPath());
  }

  @Test
  void testGetProtocol() {
    request = new Request(newBufferedReader(basicGetRequest), 0);
    assertEquals("HTTP/1.1", request.getProtocol());
  }

  @Test
  void testGetHeaders() {
    request = new Request(newBufferedReader(requestWithHeaders), 0);
    HashMap headers = new HashMap();
    headers.put("Host", "localhost:5000");
    headers.put("Connection", "keep-alive");
    assertEquals(headers, request.getHeaders());
  }

  @Test
  void testOneHeader() {
    request = new Request(newBufferedReader(basicGetRequest), 0);
    assertEquals("localhost:5000", request.getHeader("Host"));
  }

  @Test
  void testBadRequest() {
    try {
      request = new Request(newBufferedReader("GET /"), 0);
    } catch (Exception e) {
      assertEquals("java.lang.ArrayIndexOutOfBoundsException: 2", e.getMessage());
    }
  }

  @Test
  void testBadHeaders() {
    try {
      request = new Request(newBufferedReader("GET / HTTP/1.1\r\nHOST=badrequest"), 0);
    } catch (Exception e) {
      assertEquals("java.lang.ArrayIndexOutOfBoundsException: 1", e.getMessage());
    }
  }

  @Test
  void testInValidRequest() {
    request = new Request(newBufferedReader("GET /"), 0);
    assertFalse(request.validRequestLine());
  }

  @Test
  void testGetHostAndPort() {
    request = new Request(newBufferedReader("GET / HTTP/1.1\r\n"), 4000);
    assertEquals(request.getHostAndPort(), "localhost:4000" );
  }

  @Test
  void testGetHostAndPortWithHostDefined() {
    request = new Request(newBufferedReader("GET / HTTP/1.1\r\nHost: test.com\r\n"), 4000);
    assertEquals(request.getHostAndPort(), "test.com:4000" );
  }

  @Test
  void something() throws Exception {
    ByteArrayInputStream inputStream = new ByteArrayInputStream("GET / HTTP/1.1\r\n".getBytes());
    LineReader reader = new BufferedLineReader(new InputStreamReader(inputStream));
    reader.close();
    try {
      new Request(reader, 0);
    } catch (Exception e) {
      assertEquals("java.io.IOException: Stream closed", e.getMessage());
    }
  }

  private LineReader newBufferedReader(String request) {
    ByteArrayInputStream inputStream = new ByteArrayInputStream(request.getBytes());
    return new BufferedLineReader(new InputStreamReader(inputStream));
  }
}