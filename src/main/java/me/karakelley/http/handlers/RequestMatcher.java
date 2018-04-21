package me.karakelley.http.handlers;

import me.karakelley.http.http.Request;

public interface RequestMatcher {
  boolean methodMatches(Request req);
  boolean pathMatches(Request req);
}
