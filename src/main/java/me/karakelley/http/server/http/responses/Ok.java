package me.karakelley.http.server.http.responses;

import me.karakelley.http.server.http.Response;

public class Ok extends Response {
  public String getStatus() {
    return "200 OK";
  }
}
