package me.karakelley.http.handlers;

import me.karakelley.http.Request;
import me.karakelley.http.Response;

import java.util.*;

public class Router implements Handler {
  private final Map<String, HashMap<String, Handler>> mappings = new HashMap<>();

  public Router route(String method, String path, Handler handler) {
    mappings.put(path, new HashMap<String, Handler>() {{
      put(method, handler);
    }});
    return this;
  }

  @Override
  public Response respond(Request request) {
    return getController(request).respond(request);
  }

  private Handler getController(Request request) {
    HashMap<String, Handler> resource = mappings.get(request.getPath());
    if (resource == null) {
      return new InvalidRequestHandler();
    } else
      return resource.getOrDefault(request.getMethod(), new InvalidRequestHandler());
  }
}
