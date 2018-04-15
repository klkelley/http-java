package me.karakelley.http.authorization;

import me.karakelley.http.http.Authorization;
import me.karakelley.http.http.Request;

public class AlwaysAuthorized implements Authorization {
  @Override
  public boolean isAuthorized(Request request) {
    return true;
  }
}
