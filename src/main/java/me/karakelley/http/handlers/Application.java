package me.karakelley.http.handlers;

import me.karakelley.http.HttpMethod;
import me.karakelley.http.filesystem.PublicDirectory;
import me.karakelley.http.Request;
import me.karakelley.http.Response;
import me.karakelley.http.contentpresenter.HtmlPresenter;

public class Application implements Handler {
  private final Router router = new Router();
  private PublicDirectory publicDirectory = null;

  public Application() {
    router.route(HttpMethod.GET, "/", new HelloWorldHandler());
    setupCommonRoutes();
  }

  public Application(PublicDirectory publicDirectory) {
    this.publicDirectory = publicDirectory;
    router.route(HttpMethod.POST, new NewResourceHandler(publicDirectory));
    router.route(HttpMethod.GET, new StaticFilesHandler(publicDirectory, new HtmlPresenter()));
    setupCommonRoutes();
  }

  @Override
  public Response respond(Request request) {
    return router.respond(request);
  }

  private void setupCommonRoutes() {
    router.route(HttpMethod.GET, "/redirectme", new RedirectHandler());
  }
}
