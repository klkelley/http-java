package me.karakelley.http.handlers;

import me.karakelley.http.Request;
import me.karakelley.http.Response;

public interface Handler {
  Response respond(Request request);
}
