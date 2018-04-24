package me.karakelley.http.http;


public interface Authorization {
  boolean isAuthorized(Request request);
}
