package me.karakelley.http.handlers;

import me.karakelley.http.presenters.HtmlFilePresenter;
import me.karakelley.http.presenters.HtmlListPresenter;
import me.karakelley.http.http.HttpMethod;
import me.karakelley.http.filesystem.PublicDirectory;
import me.karakelley.http.http.Request;
import me.karakelley.http.http.Response;
import me.karakelley.http.server.Handler;

public class Application implements Handler {
  private final Router router = new Router();
  private PublicDirectory publicDirectory = null;

  public Application() {
    router.route(HttpMethod.GET, "/", new HelloWorldHandler());
    setupPredefinedRoutes();
  }

  public Application(PublicDirectory publicDirectory) {
    this.publicDirectory = publicDirectory;
    Handler staticFileHandler = new StaticFilesHandler(publicDirectory, new HtmlFilePresenter(publicDirectory));
    router.route(HttpMethod.GET, staticFileHandler);
    router.route(HttpMethod.POST, staticFileHandler);
    router.route(HttpMethod.PUT, staticFileHandler);
    router.route(HttpMethod.DELETE, staticFileHandler);
    setupPredefinedRoutes();
  }

  @Override
  public Response respond(Request request) {
    return router.respond(request);
  }

  private void setupPredefinedRoutes() {
    router.route(HttpMethod.GET, "/redirectme", new RedirectHandler());
    router.route(HttpMethod.GET, "/parse", new QueryParametersHandler(new HtmlListPresenter()));
  }
}

