package me.karakelley.http;

import java.nio.charset.StandardCharsets;

public class Response {

  private final Request request;
  private final String ROOT = "/";
  private final String PROTOCOL = "HTTP/1.1 ";
  private final String CONTENT_TYPE = "Content-Type: text/plain\r\n";
  private final String BLANK_LINE = "\r\n";
  private final String CONTENT_LENGTH = "Content-Length: ";
  private final String DEFAULT_RESPONSE = "Hello World";

  public Response(Request request) {
    this.request = request;
  }

  public String getResponse() {
    if (request.validRequestLine() && request.getPath().equals(ROOT)) {
      byte[] responseLength = DEFAULT_RESPONSE.getBytes(StandardCharsets.UTF_8);
      return PROTOCOL + Status.OK + CONTENT_TYPE + CONTENT_LENGTH + responseLength.length + BLANK_LINE + BLANK_LINE + DEFAULT_RESPONSE;
    } else return PROTOCOL + Status.NOT_FOUND + BLANK_LINE;
  }
}
