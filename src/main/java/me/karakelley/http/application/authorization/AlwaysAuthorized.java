package me.karakelley.http.application.authorization;

import me.karakelley.http.server.Authorization;
import me.karakelley.http.server.http.Request;

public class AlwaysAuthorized implements Authorization {
  @Override
  public boolean isAuthorized(Request request) {
    return true;
  }
}
