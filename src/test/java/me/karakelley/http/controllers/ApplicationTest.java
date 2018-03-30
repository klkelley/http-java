package me.karakelley.http.controllers;

import me.karakelley.http.*;
import me.karakelley.http.FileSystem.FileFinderCache;
import me.karakelley.http.FileSystem.PublicDirectory;
import me.karakelley.http.FileSystem.RealFileFinder;
import me.karakelley.http.helpers.TempFilesHelper;
import me.karakelley.http.utility.BufferedLineReader;
import me.karakelley.http.utility.LineReader;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ApplicationTest {

  @Test
  void testRootRouteNoDirectory() {
    Controller controller = new Application();
    Response response = controller.respond(new Request(newBufferedReader("GET / HTTP/1.1\r\n"), 0));
    assertEquals(new String(response.convertToBytes()), "HTTP/1.1 200 OK\r\nContent-Length: 11\r\nContent-Type: text/plain\r\n\r\nHello World");
  }

  @Test
  void testRootRouteWithDirectory() {
    TempFilesHelper.withTempDirectory(directory ->  {
      Path fileOne = TempFilesHelper.createTempFile(directory, "/test1");
      Path fileTwo = TempFilesHelper.createTempFile(directory, "/test2");

      Controller controller = new Application(PublicDirectory.create(directory.toString(), new FileFinderCache(new RealFileFinder())));
      Response response = controller.respond(new Request(newBufferedReader("GET / HTTP/1.1\r\n"), 0));
      assertTrue(new String(response.getBody()).split("", 2).length == 2);
    });
  }

  @Test
  void testRedirectMeRoute() {
    Controller controller = new Application();
    Response response = controller.respond(new Request(newBufferedReader("GET /redirectme HTTP/1.1\r\n"), 4000));
    assertEquals(new String(response.convertToBytes()), "HTTP/1.1 301 Moved Permanently\r\nLocation: http://localhost:4000/\r\n\r\n");
  }

  private LineReader newBufferedReader(String request) {
    ByteArrayInputStream inputStream = new ByteArrayInputStream(request.getBytes());
    return new BufferedLineReader(new InputStreamReader(inputStream));
  }
}