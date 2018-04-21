package me.karakelley.http.requestmatchers;

import me.karakelley.http.handlers.RequestMatcher;
import me.karakelley.http.http.HttpMethod;
import me.karakelley.http.http.Request;

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
