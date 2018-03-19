package me.karakelley.http;

public class Response {

  private final Request request;
  private final String ROOT = "/";
  private final String PROTOCOL = "HTTP/1.1 ";
  private final String CONTENT_TYPE = "Content-Type: text/plain\r\n";
  private final String BLANK_LINE = "\r\n";

  public Response(Request request) {
    this.request = request;
  }

  public String getResponse() {
    if (request.validRequestLine() && request.getPath().equals(ROOT)) {
      return PROTOCOL + Status.OK + CONTENT_TYPE + BLANK_LINE + "Hello World";
    } else return PROTOCOL + Status.NOT_FOUND + BLANK_LINE;
  }
}
