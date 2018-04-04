package me.karakelley.http.responses;

import me.karakelley.http.Response;

public class NotFound extends Response {
  public String getStatus() {
    return "404 Not Found";
  }
}
