package me.karakelley.http;

import me.karakelley.http.filesystem.PublicDirectory;
import me.karakelley.http.handlers.Application;
import me.karakelley.http.handlers.Handler;

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
