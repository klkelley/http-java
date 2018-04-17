package me.karakelley.http.handlers;

import me.karakelley.http.HttpMethod;
import me.karakelley.http.Request;
import me.karakelley.http.Response;
import me.karakelley.http.responses.MethodNotAllowed;
import me.karakelley.http.responses.NotFound;

import java.util.*;

public class Router implements Handler {
  private final Map<String, HashMap<HttpMethod, Handler>> predefinedRoutes = new HashMap<>();
  private final Map<HttpMethod, Handler> staticFileRoutes = new HashMap<>();

  public Router route(HttpMethod method, String path, Handler handler) {
    predefinedRoutes.put(path, new HashMap<HttpMethod, Handler>() {{
      put(method, handler);
    }});
    return this;
  }

  public Router route(HttpMethod method, Handler handler) {
    staticFileRoutes.put(method, handler);
    return this;
  }

  @Override
  public Response respond(Request request) {
    return getController(request).respond(request);
  }

  private Handler getController(Request request) {
    HashMap<HttpMethod, Handler> resource = predefinedRoutes.get(request.getPath());

    if (resource == null) {
      return staticFileRoutes.getOrDefault(request.getMethod(), new InvalidRequestHandler(new NotFound()));
    }

    return resource.getOrDefault(request.getMethod(), new InvalidRequestHandler(new MethodNotAllowed()));
  }
}
