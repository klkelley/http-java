package me.karakelley.http.server;

import me.karakelley.http.server.http.HttpMethod;
import me.karakelley.http.server.http.Request;
import me.karakelley.http.server.http.InvalidRequestException;
import me.karakelley.http.helpers.RequestStringBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class HttpRequestReaderTest {
  private RequestStringBuilder requestBuilder;
  private String requestString;

  @BeforeEach
  void setUp() {
    requestBuilder = new RequestStringBuilder();
  }

  @Test
  void testRequestObjectIsNotNull() {
    requestString = requestBuilder.setMethod("GET").setPath("/").build();
    Request request = parse(requestString);

    assertTrue(request != null);
  }

  @Test
  void testRequestHasGetMethod() {
    requestString = requestBuilder.setMethod("GET").setPath("/").build();
    Request request = parse(requestString);

    assertEquals(HttpMethod.GET, request.getMethod());
  }

  @Test
  void testRequestHasPostMethod() {
    Request request = parse(basicPostRequest());

    assertEquals(HttpMethod.POST, request.getMethod());
  }

  @Test
  void testRequestHasAPathOfRoot() {
    Request request = parse(basicPostRequest());

    assertEquals("/", request.getPath());
  }

  @Test
  void testRequestHasAPathOfRedirectme() {
    requestString = requestBuilder.setMethod("POST").setPath("/redirectme").setHeader("Authorization", "Basic YWRtaW46Y2hpY2FnbzMy").build();
    Request request = parse(requestString);

    assertEquals("/redirectme", request.getPath());
  }

  @Test
  void testRequestHasAProtocol() {
    Request request = parse(basicPostRequest());

    assertEquals("HTTP/1.1", request.getProtocol());
  }

  @Test
  void testASyntacticallyInvalidRequestMethod() {
    requestString = requestBuilder.setMethod("GIT").setPath("/").build();

    assertThrows(InvalidRequestException.class, () -> parse(requestString));
  }

  @Test
  void testInvalidHttpProtocol() {
    assertThrows(InvalidRequestException.class, () -> parse("GET / HTTP\r\n\r\n"));
  }

  @Test
  void testRequestHasMultipleHeaders() {
    Request request = parse(postRequestWithBody());
    Map<String, String> headers = new HashMap<>();
    headers.put("Content-Type", "text/plain");
    headers.put("Content-Length", "3");
    headers.put("Authorization", "Basic YWRtaW46Y2hpY2FnbzMy");

    assertEquals(headers, request.getHeaders());
  }

  @Test
  void testWhenABodyIsPresent() {
    Request request = parse(postRequestWithBody());

    assertEquals("hey", new String(request.getBody()));
  }

  private Request parse(String request) {
    ByteArrayInputStream inputStream = new ByteArrayInputStream(request.getBytes());
    HttpRequestReader requestReader = new HttpRequestReader(inputStream);
    return requestReader.read(0);
  }

  private String basicPostRequest() {
    return requestBuilder.setMethod("POST").setPath("/")
            .setHeader("Authorization", "Basic YWRtaW46Y2hpY2FnbzMy")
            .build();
  }

  private String postRequestWithBody() {
    return requestBuilder.setMethod("POST")
            .setPath("/hey.txt")
            .setHeader("Content-Type", "text/plain")
            .setHeader("Content-Length", "3")
            .setHeader("Authorization", "Basic YWRtaW46Y2hpY2FnbzMy")
            .setBody("hey")
            .build();
  }
}
