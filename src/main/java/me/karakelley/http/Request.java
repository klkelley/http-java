package me.karakelley.http;

import java.util.Map;

public class Request {
  private final int port;
  private String method;
  private String path;
  private String protocol;
  private Map<String, String> headers;

  public Request(String requestMethod, String requestPath, String requestProtocol, Map<String, String> headers, int port) {
    this.port = port;
    this.headers = headers;
    this.protocol = requestProtocol;
    this.method = requestMethod;
    this.path = requestPath;
  }

  public String getMethod() {
    return method;
  }

  public String getPath() {
    return path;
  }

  public String getProtocol() {
    return protocol;
  }

  public Map<String, String> getHeaders() {
    return headers;
  }

  public String getHeader(String header) {
    return headers.get(header);
  }

  public int getPort() {
    return port;
  }

  public String getHostAndPort() {
    String host = getHost();
    if (!host.contains(":")) {
      host += ":" + getPort();
    }
    return host;
  }

  private String getHost() {
    String host = getHeaders().get("Host");
    if (host == null) {
      host = "localhost";
    }
    return host;
  }
}
