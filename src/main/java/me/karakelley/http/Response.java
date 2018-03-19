package me.karakelley.http;

import java.util.HashMap;
import java.util.Map;

public class Response {

  private String status;
  private Map<String, String> headers = new HashMap<>();
  private String body;

  public void setStatus(String status) {
    this.status = status;
  }

  public void setHeaders(String header, String value) {
    headers.put(header, value);
  }

  public void setBody(String body) {
    this.body = body;
  }

  public String getBody() {
    if (body != null) {
      return body;
    } else return "";
  }

  public String deliver() {
    return ResponseFormatter.build(status, headers, getBody());
  }
}
