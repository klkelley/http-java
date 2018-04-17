package me.karakelley.http.http;

import java.util.Map;

public class Request {
  private int port;
  private HttpMethod method;
  private String path = "";
  private String protocol = "";
  private byte[] body;
  private Map<String, String> headers;

  public Request(HttpMethod requestMethod, String requestPath, String requestProtocol, Map<String, String> headers, byte[] body, int port) {
    this.port = port;
    this.headers = headers;
    this.protocol = requestProtocol;
    this.method = requestMethod;
    this.path = requestPath;
    this.body = body;
  }

  public HttpMethod getMethod() {
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


  public byte[] getBody() {
    return body;
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

  public static class Builder {
    private int port;
    private HttpMethod method;
    private String path = "";
    private String protocol = "";
    private byte[] body;
    private Map<String, String> headers;

    public Builder setMethod(HttpMethod method) {
      this.method = method;
      return this;
    }

    public Builder setPath(String path) {
      this.path = path;
      return this;
    }

    public Builder setProtocol(String protocol) {
      this.protocol = protocol;
      return this;
    }

    public Builder setBody(byte[] body) {
      this.body = body;
      return this;
    }

    public Builder setPort(int port) {
      this.port = port;
      return this;
    }

    public Builder setHeaders(Map<String, String> headers) {
      this.headers = headers;
      return this;
    }

    public Request build() {
      return new Request(method, path, protocol, headers, body, port);
    }
  }
}
