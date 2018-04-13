package me.karakelley.http.responses;

import me.karakelley.http.Response;

public class NoContent extends Response {
  public String getStatus() {
    return "204 No Content";
  }
}
