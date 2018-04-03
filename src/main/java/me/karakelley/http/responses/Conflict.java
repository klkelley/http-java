package me.karakelley.http.responses;

import me.karakelley.http.Response;

public class Conflict extends Response {

  public String getStatus() {
    return "409 Conflict";
  }
}
