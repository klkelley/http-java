package me.karakelley.http.controllers;

import me.karakelley.http.*;
import me.karakelley.http.FileSystem.FileFinderCache;
import me.karakelley.http.FileSystem.PublicDirectory;
import me.karakelley.http.FileSystem.RealFileFinder;
import me.karakelley.http.helpers.TempFilesHelper;
import me.karakelley.http.utility.BufferedLineReader;
import me.karakelley.http.ContentGeneration.ContentGenerator;
import me.karakelley.http.ContentGeneration.HtmlGenerator;
import me.karakelley.http.utility.LineReader;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class StaticFilesControllerTest {

  @Test
  void testGetDisplayFiles() {
    TempFilesHelper.withTempDirectory(directory -> {
      Path fileOne = TempFilesHelper.createTempFile(directory);
      Path fileTwo = TempFilesHelper.createTempFile(directory);
      PublicDirectory publicDirectory = PublicDirectory.create(directory.toString(), new FileFinderCache(new RealFileFinder()));

      ContentGenerator contentGenerator = new HtmlGenerator();
      Controller controller = new StaticFilesController(publicDirectory, contentGenerator);
      Response response = controller.respond(new Request(newBufferedReader("GET / HTTP/1.1\r\n"), 0));
      assertTrue(response.getBody().split("", 2).length == 2);
    });
  }

  @Test
  void testPostDisplayFile() {
    ContentGenerator contentGenerator = new HtmlGenerator();
    PublicDirectory publicDirectory = PublicDirectory.create("/", new FileFinderCache(new RealFileFinder()));
    Controller controller = new StaticFilesController(publicDirectory, contentGenerator);
    Response response = controller.respond(new Request(newBufferedReader("POST / HTTP/1.1\r\n"), 0));
    assertEquals(response.deliver(), "HTTP/1.1 404 Not Found\r\n\r\n");
  }

  @Test
  void testCreatesLinksForDirectories() {
    PublicDirectory publicDirectory = PublicDirectory.create("./src/", new FileFinderCache(new RealFileFinder()));

    ContentGenerator contentGenerator = new HtmlGenerator();
    Controller controller = new StaticFilesController(publicDirectory, contentGenerator);
    Response response = controller.respond(new Request(newBufferedReader("GET /test HTTP/1.1\r\n"), 0));
    assertTrue(response.deliver().contains("<p><a href="));
  }

  private LineReader newBufferedReader(String request) {
    ByteArrayInputStream inputStream = new ByteArrayInputStream(request.getBytes());
    return new BufferedLineReader(new InputStreamReader(inputStream));
  }
}

