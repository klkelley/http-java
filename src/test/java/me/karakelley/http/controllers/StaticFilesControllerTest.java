package me.karakelley.http.controllers;

import me.karakelley.http.*;
import me.karakelley.http.ContentGeneration.ContentGenerator;
import me.karakelley.http.ContentGeneration.HtmlGenerator;
import me.karakelley.http.FileSystem.FileFinderCache;
import me.karakelley.http.FileSystem.PublicDirectory;
import me.karakelley.http.FileSystem.RealFileFinder;
import me.karakelley.http.helpers.TempFilesHelper;
import me.karakelley.http.utility.BufferedLineReader;
import me.karakelley.http.utility.LineReader;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class StaticFilesControllerTest {

  @Test
  void testGetDisplayFiles() {
    TempFilesHelper.withTempDirectory(directory -> {
      Path fileOne = TempFilesHelper.createTempFile(directory, "/test1");
      Path fileTwo = TempFilesHelper.createTempFile(directory, "/test2");
      PublicDirectory publicDirectory = PublicDirectory.create(directory.toString(), new FileFinderCache(new RealFileFinder()));

      ContentGenerator contentGenerator = new HtmlGenerator();
      Controller controller = new StaticFilesController(publicDirectory, contentGenerator);
      Response response = controller.respond(new Request(newBufferedReader("GET / HTTP/1.1\r\n"), 0));
      assertTrue(new String(response.getBody()).split("", 2).length == 2);
    });
  }

  @Test
  void testPostDisplayFile() {
    PublicDirectory publicDirectory = PublicDirectory.create("/", new FileFinderCache(new RealFileFinder()));
    Controller controller = new StaticFilesController(publicDirectory, new HtmlGenerator());
    Response response = controller.respond(new Request(newBufferedReader("POST / HTTP/1.1\r\n"), 0));
    assertEquals(new String(response.convertToBytes()), "HTTP/1.1 404 Not Found\r\n\r\n");
  }

  @Test
  void testCreatesLinksForDirectories() {
    TempFilesHelper.withTempDirectory(directory -> {
      Path file = TempFilesHelper.createTempFile(directory, "/test1");
      PublicDirectory publicDirectory = PublicDirectory.create(directory.toString(), new FileFinderCache(new RealFileFinder()));
      ContentGenerator contentGenerator = new HtmlGenerator();
      Controller controller = new StaticFilesController(publicDirectory, contentGenerator);
      Response response = controller.respond(new Request(newBufferedReader("GET / HTTP/1.1\r\n"), 0));

      assertTrue(new String(response.getBody()).contains("<p><a href=\"/test1.txt\">test1.txt</a></p>"));
    });
  }

  @Test
  void testServesTextFile() {
    TempFilesHelper.withTempDirectory(directory -> {
      Path file = TempFilesHelper.createTempFile(directory, "/test1");
      writeToFile("Hello World", file);

      PublicDirectory publicDirectory = PublicDirectory.create(directory.toString(), new FileFinderCache(new RealFileFinder()));
      ContentGenerator contentGenerator = new HtmlGenerator();
      Controller controller = new StaticFilesController(publicDirectory, contentGenerator);
      Response response = controller.respond(new Request(newBufferedReader("GET /test1.txt HTTP/1.1\r\n"), 0));
      assertEquals("Hello World", new String(response.getBody()));
    });
  }

  private LineReader newBufferedReader(String request) {
    ByteArrayInputStream inputStream = new ByteArrayInputStream(request.getBytes());
    return new BufferedLineReader(new InputStreamReader(inputStream));
  }

  private void writeToFile(String text, Path file) {
    try {
      BufferedWriter writer = new BufferedWriter(new FileWriter(String.valueOf(file)));
      writer.write(text);
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}

