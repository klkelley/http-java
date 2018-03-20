package me.karakelley.http.controllers;

import me.karakelley.http.Request;
import me.karakelley.http.Response;

import java.util.HashMap;
import java.util.Map;

public class Router implements Controller {
  private final Map<String, Controller> mappings = new HashMap<>();

  public Router route(String path, Controller controller) {
    mappings.put(path, controller);
    return this;
  }

  @Override
  public Response respond(Request request) {
    return mappings.getOrDefault(request.getPath(), new InvalidRequestController()).respond(request);
  }
}
