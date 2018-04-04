package me.karakelley.http.responses;

import me.karakelley.http.Response;

public class BadRequest extends Response {

  public String getStatus() {
    return "400 Bad Request";
  }
}
