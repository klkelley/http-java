package me.karakelley.http.controllers;

import me.karakelley.http.PublicDirectory;
import me.karakelley.http.Request;
import me.karakelley.http.Response;

public class Application implements Controller {
  private final Router router = new Router();

  public Application() {
    router.route("/", new HelloWorldController());
    setupCommonRoutes();
  }

  public Application(PublicDirectory publicDirectory) {
    router.route("/", new StaticFilesController());
    setupCommonRoutes();
  }

  @Override
  public Response respond(Request request) {
    return router.respond(request);
  }

  private void setupCommonRoutes() {
    router.route("/redirectme", new RedirectController());
  }
}
