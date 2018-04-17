package me.karakelley.http.handlers;

import me.karakelley.http.filesystem.PublicDirectory;
import me.karakelley.http.server.Handler;

import java.util.Map;

public class ApplicationFactory {

  public Handler create(Map<String, String> argsHash) {

    if (argsHash.containsKey("directory")) {
      return new Application(PublicDirectory.create(argsHash.get("directory")));
    } else {
      return new Application();
    }
  }
}
