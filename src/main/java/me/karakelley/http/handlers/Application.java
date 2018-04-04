package me.karakelley.http.handlers;

import me.karakelley.http.filesystem.PublicDirectory;
import me.karakelley.http.Request;
import me.karakelley.http.Response;
import me.karakelley.http.contentpresenter.HtmlPresenter;

public class Application implements Handler {
  private final Router router = new Router();
  private PublicDirectory publicDirectory = null;

  public Application() {
    router.route("GET", "/", new HelloWorldHandler());
    setupCommonRoutes();
  }

  public Application(PublicDirectory publicDirectory) {
    this.publicDirectory = publicDirectory;
    router.route("GET", "/",new StaticFilesHandler(publicDirectory, new HtmlPresenter()));
    setupCommonRoutes();
  }

  @Override
  public Response respond(Request request) {
    if (theRequestedResourceExists(request)) {
      return new StaticFilesHandler(publicDirectory, new HtmlPresenter()).respond(request);
    } else return router.respond(request);
  }

  private void setupCommonRoutes() {
    router.route("GET", "/redirectme", new RedirectHandler());
  }

  private boolean theRequestedResourceExists(Request request) {
    return publicDirectory != null && publicDirectory.resourceExists(request.getPath());
  }
}
