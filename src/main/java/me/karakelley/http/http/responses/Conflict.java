package me.karakelley.http.http.responses;

import me.karakelley.http.http.Response;

public class Conflict extends Response {

  public String getStatus() {
    return "409 Conflict";
  }
}
