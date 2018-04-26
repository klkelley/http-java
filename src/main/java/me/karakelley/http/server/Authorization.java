package me.karakelley.http.server;

import me.karakelley.http.server.http.Request;

public interface Authorization {
  boolean isAuthorized(Request request);
}
