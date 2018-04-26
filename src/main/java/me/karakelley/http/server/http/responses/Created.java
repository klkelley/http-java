package me.karakelley.http.server.http.responses;

import me.karakelley.http.server.http.Response;

public class Created extends Response {
  public String getStatus() {
    return "201 Created";
  }
}
