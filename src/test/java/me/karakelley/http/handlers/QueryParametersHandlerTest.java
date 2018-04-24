package me.karakelley.http.handlers;

import me.karakelley.http.presenters.HtmlListPresenter;
import me.karakelley.http.http.HttpMethod;
import me.karakelley.http.http.Request;
import me.karakelley.http.http.Response;
import me.karakelley.http.server.Handler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QueryParametersHandlerTest {

  private Handler handler;

  @BeforeEach
  void setUp() {
    handler = new QueryParametersHandler(new HtmlListPresenter());
  }

  @Test
  void testParsesSimpleQuery() {
    Response response = handler.respond(newParseRequest("?hello=world"));

    assertTrue(new String(response.getBody()).contains("<dt>hello</dt><dd>world</dd>"));
  }

  @Test
  void testParsesQueryWithMultipleParameters() {
    Response response = handler.respond(newParseRequest("?hello=world&hola=mundo,hey"));

    assertTrue(new String(response.getBody()).contains("<dt>hello</dt><dd>world</dd><dt>hola</dt><dd>mundo,hey</dd>"));
  }

  @Test
  void testWithNoQueryParamsPageIsBlank() {
    Response response = handler.respond(newParseRequest(""));

    assertEquals("", new String(response.getBody()));
  }

  @Test
  void testEncodedQueryParams() {
    Response response = handler.respond(newParseRequest("?value=%3Ddecoding+all+types+of+stuff%21%3F"));

    assertTrue(new String(response.getBody()).contains("<dt>value</dt><dd>=decoding all types of stuff!?</dd>"));
  }

  @Test
  void testMalformedSyntaxForQueryParamsResultsIn400() {
    Response response = handler.respond(newParseRequest("?key1=value+1&key2=value%40%21%242&key3=value%253%"));

    assertEquals("400 Bad Request", response.getStatus());
  }

  private Request newParseRequest(String query) {
    return new Request.Builder()
            .setMethod(HttpMethod.GET)
            .setPath("/parse" + query)
            .setProtocol("HTTP/1.1")
            .setPort(0)
            .build();
  }
}