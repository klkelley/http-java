package me.karakelley.http.handlers;

import me.karakelley.http.http.HttpMethod;
import me.karakelley.http.filesystem.PublicDirectory;
import me.karakelley.http.http.Request;
import me.karakelley.http.http.Response;
import me.karakelley.http.contentpresenter.HtmlPresenter;
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
    router.route(HttpMethod.POST, new NewResourceHandler(publicDirectory));
    router.route(HttpMethod.GET, new StaticFilesHandler(publicDirectory, new HtmlPresenter()));
    router.route(HttpMethod.PUT, new UpdateResourceHandler(publicDirectory));
    router.route(HttpMethod.DELETE, new DeleteResourceHandler(publicDirectory));
    setupPredefinedRoutes();
  }

  @Override
  public Response respond(Request request) {
    return router.respond(request);
  }

  private void setupPredefinedRoutes() {
    router.route(HttpMethod.GET, "/redirectme", new RedirectHandler());
  }
}

