package me.karakelley.http.responses;

import me.karakelley.http.Response;

public class Created extends Response {
  public String getStatus() {
    return "201 Created";
  }
}
