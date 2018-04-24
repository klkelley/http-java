package me.karakelley.http.handlers;

import me.karakelley.http.authorization.AlwaysAuthorized;
import me.karakelley.http.filesystem.PublicDirectory;
import me.karakelley.http.http.Authorization;
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
