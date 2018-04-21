package me.karakelley.http.requestmatchers;

import me.karakelley.http.handlers.RequestMatcher;
import me.karakelley.http.http.Request;

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
