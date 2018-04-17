package me.karakelley.http.handlers;

import me.karakelley.http.http.HttpMethod;
import me.karakelley.http.http.Request;
import me.karakelley.http.http.Response;
import me.karakelley.http.http.responses.MethodNotAllowed;
import me.karakelley.http.http.responses.NotFound;
import me.karakelley.http.server.Handler;

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
    HashMap<HttpMethod, Handler> predefinedRoute = predefinedRoutes.get(request.getPath());

    if (predefinedRoute == null) {
      return staticFileRoutes(request);
    }
    return predefinedRoutes(predefinedRoute, request);
  }

  private Response predefinedRoutes(HashMap<HttpMethod, Handler> predefinedRoute, Request request) {
    Handler handler = predefinedRoute.get(request.getMethod());
    return handler == null ? new MethodNotAllowed() : handler.respond(request);
  }

  private Response staticFileRoutes(Request request) {
    Handler handler = staticFileRoutes.get(request.getMethod());
    return staticFileRoutes.get(request.getMethod()) == null ? new NotFound() : handler.respond(request);
  }
}
