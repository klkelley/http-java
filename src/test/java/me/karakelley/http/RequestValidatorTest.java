package me.karakelley.http;

import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class RequestValidatorTest {

  @Test
  void inValidHTTPProtocol() {
    Request request = new Request("GET", "/", "HTTP", new HashMap<>(), 0);
    RequestValidator requestValidator = new RequestValidator();
    assertFalse(requestValidator.isValid(request));
  }

  @Test
  void inValidRequestMethod() {
    Request request = new Request("GIT", "/", "HTTP/1.1", new HashMap<>(), 0);
    RequestValidator requestValidator = new RequestValidator();
    assertFalse(requestValidator.isValid(request));
  }

  @Test
  void testMissingRequestPath() {
    Request request = new Request("GIT", null, "HTTP/1.1", new HashMap<>(), 0);
    RequestValidator requestValidator = new RequestValidator();
    assertFalse(requestValidator.isValid(request));
  }

  @Test
  void testIsValid() {
    Request request = new Request("GET", "/", "HTTP/1.1", new HashMap<>(), 0);
    RequestValidator requestValidator = new RequestValidator();
    assertTrue(requestValidator.isValid(request));
  }
}