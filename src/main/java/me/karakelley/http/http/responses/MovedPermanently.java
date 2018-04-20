package me.karakelley.http.http.responses;

import me.karakelley.http.http.Response;

public class MovedPermanently extends Response {
  public String getStatus() {
    return "301 Moved Permanently";
  }
}
