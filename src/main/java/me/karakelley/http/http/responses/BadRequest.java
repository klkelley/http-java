package me.karakelley.http.http.responses;

import me.karakelley.http.http.Response;

public class BadRequest extends Response {

  public String getStatus() {
    return "400 Bad Request";
  }
}
