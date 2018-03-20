package me.karakelley.http.controllers;

import me.karakelley.http.Request;
import me.karakelley.http.Response;

public interface Controller {
  Response respond(Request request);
}
