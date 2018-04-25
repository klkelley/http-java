package me.karakelley.http.server.http.responses;

import me.karakelley.http.server.http.Response;

public class BadRequest extends Response {

  public String getStatus() {
    return "400 Bad Request";
  }
}
