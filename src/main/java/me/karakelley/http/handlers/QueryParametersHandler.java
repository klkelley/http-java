package me.karakelley.http.handlers;

import me.karakelley.http.UriQueryParser;
import me.karakelley.http.http.InvalidRequestException;
import me.karakelley.http.http.Request;
import me.karakelley.http.http.Response;
import me.karakelley.http.http.responses.BadRequest;
import me.karakelley.http.http.responses.Ok;
import me.karakelley.http.server.Handler;

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
    Map<String, List<String>> query = UriQueryParser.parse(request.getQueryParams());
    if (!query.isEmpty()) {
      response.setBody(listPresenter.displayList(query));
    }
    response.setHeaders(CONTENT_TYPE, TEXT_HTML);
    return response;
  }
}
