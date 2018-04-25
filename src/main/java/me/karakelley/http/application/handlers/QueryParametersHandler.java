package me.karakelley.http.application.handlers;

import me.karakelley.http.server.http.InvalidRequestException;
import me.karakelley.http.server.http.Request;
import me.karakelley.http.server.http.Response;
import me.karakelley.http.server.http.responses.BadRequest;
import me.karakelley.http.server.http.responses.Ok;
import me.karakelley.http.server.Handler;
import me.karakelley.http.server.ListPresenter;

import java.util.*;

public class QueryParametersHandler implements Handler {
  private final String CONTENT_TYPE = "Content-Type";
  private final String TEXT_HTML = "text/html";
  private ListPresenter listPresenter;

  public QueryParametersHandler(ListPresenter listPresenter) {
    this.listPresenter = listPresenter;
  }

  @Override
  public Response respond(Request request) {
    Response response = new Ok();
    try {
      return serveQueryResponse(response, request);
    } catch (InvalidRequestException e) {
      return new BadRequest();
    }
  }

  private Response serveQueryResponse(Response response, Request request) {
    Map<String, List<String>> query = request.getQueryParams();
    if (!query.isEmpty()) {
      response.setBody(listPresenter.displayList(query));
    }
    response.setHeaders(CONTENT_TYPE, TEXT_HTML);
    return response;
  }
}
