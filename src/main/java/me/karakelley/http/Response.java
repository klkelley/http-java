package me.karakelley.http;

public class Response {

  private final Request request;
  private final String ROOT = "/";
  private final String PROTOCOL = "HTTP/1.1 ";
  private final String CONTENT_TYPE = "Content-Type: text/plain\r\n";
  private final String OK = "200 OK\r\n";
  private final String BAD = "400 Bad Request\r\n";
  private final String BLANK_LINE = "\r\n";

  public Response(Request request) {
    this.request = request;
  }

  public String getResponse() {
    if (request.validRequestLine() && request.getPath().equals(ROOT)) {
      return PROTOCOL + OK + CONTENT_TYPE + BLANK_LINE + "Hello World";
    } else return PROTOCOL + BAD + BLANK_LINE;
  }
}
