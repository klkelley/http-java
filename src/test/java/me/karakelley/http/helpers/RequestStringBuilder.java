package me.karakelley.http.helpers;

import java.util.HashMap;

import static java.util.stream.Collectors.joining;

public class RequestStringBuilder {
  private String method;
  private String path;
  private String protocol;
  private String body = "";
  private HashMap<String, String> headers = new HashMap<>();
  private static final String CRLF = "\r\n";

  public RequestStringBuilder setMethod(String method) {
    this.method = method;
    return this;
  }

  public RequestStringBuilder setPath(String path) {
    this.path = path;
    return this;
  }

  public RequestStringBuilder setProtocol(String protocol) {
    this.protocol = protocol;
    return this;
  }

  public RequestStringBuilder setBody(String body) {
    this.body = body;
    return this;
  }

  public RequestStringBuilder setHeaders(HashMap<String, String> headers) {
    this.headers = headers;
    return this;
  }

  public RequestStringBuilder setHeader(String key, String value) {
    headers.put(key, value);
    return this;
  }

  public String build() {
    StringBuilder requestString = new StringBuilder();
    requestString.append(method + " ");
    requestString.append(path + " ");
    requestString.append("HTTP/1.1");
    requestString.append(CRLF);

    if (!headers.isEmpty()) {
      requestString.append(formatHeaders());
      requestString.append(CRLF);
    } else {
      requestString.append(CRLF);
    }

    if (!body.isEmpty()) {
      requestString.append(body);
    }
    return requestString.toString();
  }

  private String formatHeaders() {
    return headers.entrySet()
            .stream()
            .map(entry -> entry.getKey() + ": " + entry.getValue() + CRLF)
            .collect(joining());
  }
}
