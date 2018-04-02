package me.karakelley.http;

import java.util.Map;

public class RequestBuilder {

  public static Request build(String requestMethod, String requestPath, String requestProtocol, Map<String, String> headers, int port) {
    return new Request(requestMethod, requestPath, requestProtocol, headers, port);
  }
}
