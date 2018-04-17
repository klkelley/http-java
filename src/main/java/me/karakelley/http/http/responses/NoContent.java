package me.karakelley.http.http.responses;

import me.karakelley.http.http.Response;

public class NoContent extends Response {
  public String getStatus() {
    return "204 No Content";
  }
}
