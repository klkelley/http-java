package me.karakelley.http.server.http.responses;

import me.karakelley.http.server.http.Response;

import java.util.HashMap;
import java.util.Map;

public class Unauthorized extends Response {

  public String getStatus() {
    return "401 Unauthorized";
  }

  public Map<String, String> getHeaders() {
    Map<String, String> headers = new HashMap<>();
    headers.put("WWW-Authenticate", "Basic realm=My Server");
    return headers;
  }
}
