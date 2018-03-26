package me.karakelley.http.controllers;

import me.karakelley.http.FileSystem.PublicDirectory;
import me.karakelley.http.Request;
import me.karakelley.http.Response;
import me.karakelley.http.ContentGeneration.HtmlGenerator;

public class Application implements Controller {
  private final Router router = new Router();
  private PublicDirectory publicDirectory = null;

  public Application() {
    router.route("/", new HelloWorldController());
    setupCommonRoutes();
  }

  public Application(PublicDirectory publicDirectory) {
    this.publicDirectory = publicDirectory;
    router.route("/", new StaticFilesController(publicDirectory, new HtmlGenerator()));
    setupCommonRoutes();
  }

  @Override
  public Response respond(Request request) {
    if (theRequestedResourceExists(request)) {
      return new StaticFilesController(publicDirectory, new HtmlGenerator()).respond(request);
    } else return router.respond(request);
  }

  private void setupCommonRoutes() {
    router.route("/redirectme", new RedirectController());
  }

  private boolean theRequestedResourceExists(Request request) {
    return publicDirectory != null && publicDirectory.resourceExists(request.getPath());
  }
}
