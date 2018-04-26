package me.karakelley.http.server.http.responses;

import me.karakelley.http.server.http.Response;

public class Conflict extends Response {

  public String getStatus() {
    return "409 Conflict";
  }
}
