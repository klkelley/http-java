package me.karakelley.http.handlers;

import me.karakelley.http.filesystem.PublicDirectory;
import me.karakelley.http.http.HttpMethod;
import me.karakelley.http.http.Request;
import me.karakelley.http.http.Response;
import me.karakelley.http.server.Handler;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import me.karakelley.http.handlers.staticfilestrategies.*;


public class StaticFilesHandler implements Handler {
  private static Map<HttpMethod, Handler> fileStrategies;
  private static PublicDirectory publicDirectory = null;
  private static FilePresenter filePresenter = null;

  public StaticFilesHandler(PublicDirectory publicDirectory, FilePresenter filePresenter) {
    this.publicDirectory = publicDirectory;
    this.filePresenter = filePresenter;
    setupFileStrategies();
  }

  @Override
  public Response respond(Request request) {
    return fileStrategies.get(request.getMethod()).respond(request);
  }

  private static void setupFileStrategies() {
    fileStrategies = new HashMap<>();
    fileStrategies.put(HttpMethod.POST, new NewResourceStrategy(publicDirectory));
    fileStrategies.put(HttpMethod.GET, new RetrieveResourceStrategy(publicDirectory, filePresenter));
    fileStrategies.put(HttpMethod.DELETE, new DeleteResourceStrategy(publicDirectory));
    fileStrategies.put(HttpMethod.PUT, new UpdateResourceStrategy(publicDirectory));
    fileStrategies = Collections.unmodifiableMap(fileStrategies);
  }
}
