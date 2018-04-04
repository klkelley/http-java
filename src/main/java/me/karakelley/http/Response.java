package me.karakelley.http;

import java.util.HashMap;
import java.util.Map;

public class Response {

  private String status;
  private Map<String, String> headers = new HashMap<>();
  private byte[] body;

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

  public String getStatus() {
    return status;
  }

  public Map<String, String> getHeaders() {
    return headers;
  }
}
