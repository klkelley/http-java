package me.karakelley.http.controllers;

import me.karakelley.http.PublicDirectory;
import me.karakelley.http.Request;
import me.karakelley.http.Response;
import me.karakelley.http.helpers.TempFilesHelper;
import me.karakelley.http.utility.BufferedLineReader;
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
      PublicDirectory.create(directory.toString());

      Controller controller = new StaticFilesController();
      Response response = controller.respond(new Request(newBufferedReader("GET / HTTP/1.1\r\n"), 0));
      assertTrue(response.getBody().split("", 2).length == 2);
    });
  }

  @Test
  void testPostDisplayFile() {
    Controller controller = new StaticFilesController();
    Response response = controller.respond(new Request(newBufferedReader("POST / HTTP/1.1\r\n"), 0));
    assertEquals(response.deliver(), "HTTP/1.1 404 Not Found\r\n\r\n");
  }

  private LineReader newBufferedReader(String request) {
    ByteArrayInputStream inputStream = new ByteArrayInputStream(request.getBytes());
    return new BufferedLineReader(new InputStreamReader(inputStream));
  }
}

