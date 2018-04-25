package me.karakelley.http.server.http.responses;

import me.karakelley.http.server.http.Response;

public class NoContent extends Response {
  public String getStatus() {
    return "204 No Content";
  }
}
