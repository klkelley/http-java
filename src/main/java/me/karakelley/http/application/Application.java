package me.karakelley.http.application;

import me.karakelley.http.application.handlers.HelloWorldHandler;
import me.karakelley.http.application.handlers.QueryParametersHandler;
import me.karakelley.http.application.handlers.RedirectHandler;
import me.karakelley.http.server.handlers.Router;
import me.karakelley.http.server.handlers.StaticFilesHandler;
import me.karakelley.http.application.presenters.HtmlFilePresenter;
import me.karakelley.http.application.presenters.HtmlListPresenter;
import me.karakelley.http.server.http.HttpMethod;
import me.karakelley.http.server.filesystem.PublicDirectory;
import me.karakelley.http.server.http.Request;
import me.karakelley.http.server.http.Response;
import me.karakelley.http.application.requestmatchers.AlwaysMatchesMatcher;
import me.karakelley.http.application.requestmatchers.MethodAndExactPathMatcher;
import me.karakelley.http.server.Handler;

public class Application implements Handler {
  private final Router router;

  public Application() {
    this.router = new Router()
            .route(new MethodAndExactPathMatcher(HttpMethod.GET, "/redirectme"), new RedirectHandler())
            .route(new MethodAndExactPathMatcher(HttpMethod.GET, "/parse"), new QueryParametersHandler(new HtmlListPresenter()))
            .route(new MethodAndExactPathMatcher(HttpMethod.GET, "/"), new HelloWorldHandler());
  }

  public Application(PublicDirectory publicDirectory) {
    this.router = new Router()
            .route(new MethodAndExactPathMatcher(HttpMethod.GET, "/redirectme"), new RedirectHandler())
            .route(new MethodAndExactPathMatcher(HttpMethod.GET, "/parse"), new QueryParametersHandler(new HtmlListPresenter()))
            .route(new AlwaysMatchesMatcher(), new StaticFilesHandler(publicDirectory, new HtmlFilePresenter(publicDirectory)));
  }

  @Override
  public Response respond(Request request) {
    return router.respond(request);
  }

}

