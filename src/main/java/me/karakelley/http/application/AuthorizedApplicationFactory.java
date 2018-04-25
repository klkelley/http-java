package me.karakelley.http.application;

import me.karakelley.http.application.authorization.AlwaysAuthorized;
import me.karakelley.http.application.handlers.AuthorizedHandler;
import me.karakelley.http.server.filesystem.PublicDirectory;
import me.karakelley.http.server.Authorization;
import me.karakelley.http.server.Handler;

import java.util.Map;

public class AuthorizedApplicationFactory {

  public Handler create(Map<String, String> argsHash, Authorization basicAuth) {
    if (argsHash.containsKey("directory")) {
      return new AuthorizedHandler(new Application(PublicDirectory.create(argsHash.get("directory"))), basicAuth);
    } else {
      return new AuthorizedHandler(new Application(), new AlwaysAuthorized());
    }
  }
}
