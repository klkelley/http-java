package me.karakelley.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Response {

  private String status;
  private Map<String, String> headers = new HashMap<>();
  private byte[] body;
  private final static String PROTOCOL = "HTTP/1.1 ";
  private final static String CRLF = "\r\n";

  public void setStatus(String status) {
    this.status = status;
  }

  public void setHeaders(String header, String value) {
    headers.put(header, value);
  }

  public void setBody(byte[] body) {
    this.body = body;
  }

  public void setBody(String body) {
    this.body = body.getBytes();
  }

  public byte[] getBody() {
    if (body != null) {
      return body;
    } else return "".getBytes();
  }

  public byte[] convertToBytes() {
    try {
      ByteArrayOutputStream out = new ByteArrayOutputStream();

      out.write(formatStatusLine(status).getBytes());
      out.write(formatHeaders(headers).getBytes());
      out.write(CRLF.getBytes());
      out.write(getBody());

      out.close();
      return out.toByteArray();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private String formatHeaders(Map<String, String> headers) {
    return headers.entrySet()
            .stream()
            .map(entry -> entry.getKey() + ": " + entry.getValue() + CRLF)
            .collect(Collectors.joining());
  }

  private String formatStatusLine(String statusLine) {
    return PROTOCOL + statusLine + CRLF;
  }
}
