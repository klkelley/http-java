package me.karakelley.http.http.responses;

import me.karakelley.http.http.Response;

public class Created extends Response {
  public String getStatus() {
    return "201 Created";
  }
}
