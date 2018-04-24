package me.karakelley.http.handlers;

import me.karakelley.http.http.Authorization;
import me.karakelley.http.authorization.BasicAuthorizer;
import me.karakelley.http.filesystem.PublicDirectory;
import me.karakelley.http.helpers.TempFilesHelper;
import me.karakelley.http.http.HttpMethod;
import me.karakelley.http.http.Request;
import me.karakelley.http.server.Handler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AuthorizedHandlerTest {
  private Handler handler;
  private Authorization authorizer;

  @BeforeEach
  void setUp() {
    authorizer = new BasicAuthorizer("admin", "chicago32");
  }

  @Test
  void requestIsNotAuthorized() {
    TempFilesHelper.withTempDirectory(directory ->  {
      handler = new AuthorizedHandler(new Application(PublicDirectory.create(directory.toString())), authorizer);
      Request request = new Request.Builder()
              .setMethod(HttpMethod.POST)
              .setPath("/test1.txt")
              .setHeaders(setInvalidBasicAuthHeaders())
              .setProtocol("HTTP/1.1")
              .setPort(0)
              .build();

      assertEquals("401 Unauthorized", handler.respond(request).getStatus());
    });
  }


  @Test
  void requestIsAuthorized() {
    TempFilesHelper.withTempDirectory(directory ->  {
      handler = new AuthorizedHandler(new Application(PublicDirectory.create(directory.toString())), authorizer);
      Request request = new Request.Builder()
              .setMethod(HttpMethod.POST)
              .setPath("/test1.txt")
              .setHeaders(setBasicAuthHeaders())
              .setProtocol("HTTP/1.1")
              .setPort(0)
              .build();

      assertEquals("201 Created", handler.respond(request).getStatus());
    });
  }

  @Test
  void testWithoutCredentialsUsersItNotAuthorized() {
    TempFilesHelper.withTempDirectory(directory -> {
      handler = new AuthorizedHandler(new Application(PublicDirectory.create(directory.toString())), authorizer);
      Request request = new Request.Builder()
              .setMethod(HttpMethod.POST)
              .setPath("/test1.txt")
              .setHeaders(new HashMap<>())
              .setProtocol("HTTP/1.1")
              .setPort(0)
              .build();

      assertEquals("401 Unauthorized", handler.respond(request).getStatus());
    });
  }

  private Map<String, String> setBasicAuthHeaders() {
    HashMap<String, String> headers = new HashMap<>();
    headers.put("Authorization", "Basic YWRtaW46Y2hpY2FnbzMy");
    return headers;
  }


  private Map<String, String> setInvalidBasicAuthHeaders() {
    HashMap<String, String> headers = new HashMap<>();
    headers.put("Authorization", "Basic YWRtaW46Y2hp");
    return headers;
  }


}