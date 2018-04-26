package me.karakelley.http.application.requestmatchers;

import me.karakelley.http.server.http.Request;
import me.karakelley.http.server.RequestMatcher;

public class AlwaysMatchesMatcher implements RequestMatcher {
  @Override
  public boolean methodMatches(Request req) {
    return true;
  }

  @Override
  public boolean pathMatches(Request req) {
    return true;
  }
}
