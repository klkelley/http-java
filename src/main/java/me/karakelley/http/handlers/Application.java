package me.karakelley.http.handlers;

import me.karakelley.http.presenters.HtmlFilePresenter;
import me.karakelley.http.presenters.HtmlListPresenter;
import me.karakelley.http.http.HttpMethod;
import me.karakelley.http.filesystem.PublicDirectory;
import me.karakelley.http.http.Request;
import me.karakelley.http.http.Response;
import me.karakelley.http.requestmatchers.AlwaysMatchesMatcher;
import me.karakelley.http.requestmatchers.MethodAndExactPathMatcher;
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

