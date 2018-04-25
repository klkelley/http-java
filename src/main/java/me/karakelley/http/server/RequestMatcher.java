package me.karakelley.http.server;

import me.karakelley.http.server.http.Request;

public interface RequestMatcher {
  boolean methodMatches(Request req);
  boolean pathMatches(Request req);
}
