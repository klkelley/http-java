package me.karakelley.http;

import me.karakelley.http.controllers.Controller;
import me.karakelley.http.controllers.InvalidRequestController;
import me.karakelley.http.controllers.RedirectController;
import me.karakelley.http.controllers.RootController;

import java.util.HashMap;
import java.util.Map;

public class Router {

  public static Controller dispatch(Request request) {
    Map<String, Controller> routes = new HashMap();
    routes.put("/redirectme", new RedirectController(request));
    routes.put("/", new RootController(request));

    if (routes.get(request.getPath()) == null) {
      return new InvalidRequestController();
    }
    return routes.get(request.getPath());
  }
}
