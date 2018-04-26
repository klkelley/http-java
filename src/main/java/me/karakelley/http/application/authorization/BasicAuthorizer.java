package me.karakelley.http.application.authorization;

import me.karakelley.http.server.Authorization;
import me.karakelley.http.server.http.HttpMethod;
import me.karakelley.http.server.http.Request;

import java.util.Base64;

public class BasicAuthorizer implements Authorization {

  private final String user;
  private final String password;

  public BasicAuthorizer(String user, String password) {
    this.user = user;
    this.password = password;
  }

  @Override
  public boolean isAuthorized(Request request) {
    String credentials = request.getHeaders().get("Authorization");
    if (HttpMethod.modifiableMethods.contains(request.getMethod())) {
      return hasCredentials(credentials);
    } else {
      return true;
    }
  }

  private boolean hasCredentials(String credentials) {
    return credentials != null && requestIsAuthorized(credentials);
  }

  private boolean requestIsAuthorized(String credentials) {
    return encodeAuthorization().equals(parseRequestAuthorization(credentials));
  }

  private String encodeAuthorization() {
    String validAuthorization = user + ":" + password;
    return Base64.getEncoder().encodeToString(validAuthorization.getBytes());
  }

  private String parseRequestAuthorization(String credentials) {
    return credentials.split(" ")[1];
  }
}
