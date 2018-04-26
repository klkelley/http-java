package me.karakelley.http.server.http.responses;

import me.karakelley.http.server.http.Response;

public class NotFound extends Response {
  public String getStatus() {
    return "404 Not Found";
  }
}
