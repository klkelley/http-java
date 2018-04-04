package me.karakelley.http;

import me.karakelley.http.exceptions.InvalidRequestException;
import me.karakelley.http.utility.BufferedLineReader;
import me.karakelley.http.utility.LineReader;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;

import static org.junit.jupiter.api.Assertions.*;

class RequestParserTest {

  @Test
  void testThrowsInvalidRequestException() {
    RequestParser requestParser = new RequestParser(new RequestValidator());

    assertThrows(InvalidRequestException.class, () -> {
      requestParser.parse(newBufferedReader("GET /\r\n")).buildRequest(0);
    });
  }

  @Test
  void testBuildRequest() throws InvalidRequestException {
    RequestParser requestParser = new RequestParser(new RequestValidator());
    RequestParser parsedRequest = requestParser.parse(newBufferedReader("GET / HTTP/1.1\r\n"));
    assertEquals(parsedRequest.buildRequest(0).getClass(), Request.class);
  }

  @Test
  void testThrowsExceptionIfStreamClosed() throws Exception {
    ByteArrayInputStream inputStream = new ByteArrayInputStream("GET / HTTP/1.1\r\n".getBytes());
    LineReader reader = new BufferedLineReader(new InputStreamReader(inputStream));
    reader.close();
    try {
      new RequestParser(new RequestValidator()).parse(reader);
    } catch (Exception e) {
      assertEquals("java.io.IOException: Stream closed", e.getMessage());
    }
  }

  @Test
  void testBadRequest() {
    try {
      RequestParser requestParser = new RequestParser(new RequestValidator());
      requestParser.parse(newBufferedReader("GET /"));
    } catch (Exception e) {
      assertEquals("java.lang.ArrayIndexOutOfBoundsException: 2", e.getMessage());
    }
  }

  @Test
  void testBadHeaders() {
    try {
      RequestParser requestParser = new RequestParser(new RequestValidator());
      requestParser.parse(newBufferedReader("GET / HTTP/1.1\r\nHOST=badrequest"));
    } catch (Exception e) {
      assertEquals("java.lang.ArrayIndexOutOfBoundsException: 1", e.getMessage());
    }
  }

  private LineReader newBufferedReader(String request) {
    ByteArrayInputStream inputStream = new ByteArrayInputStream(request.getBytes());
    return new BufferedLineReader(new InputStreamReader(inputStream));
  }
}