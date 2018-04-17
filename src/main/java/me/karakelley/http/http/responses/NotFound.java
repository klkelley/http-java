package me.karakelley.http.http.responses;

import me.karakelley.http.http.Response;

public class NotFound extends Response {
  public String getStatus() {
    return "404 Not Found";
  }
}
