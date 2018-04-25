package me.karakelley.http.application.requestmatchers;

import me.karakelley.http.server.http.HttpMethod;
import me.karakelley.http.server.http.Request;
import me.karakelley.http.server.RequestMatcher;

public class MethodAndExactPathMatcher implements RequestMatcher {
  private HttpMethod method;
  private String path;

  public MethodAndExactPathMatcher(HttpMethod method, String path) {
    this.method = method;
    this.path = path;
  }

  @Override
  public boolean methodMatches(Request req) {
    return req.getMethod().equals(method);
  }

  @Override
  public boolean pathMatches(Request req) {
    return req.getPath().equals(path);
  }
}
