package me.karakelley.http.handlers;

import me.karakelley.http.presenters.HtmlFilePresenter;
import me.karakelley.http.filesystem.PublicDirectory;
import me.karakelley.http.helpers.TempFilesHelper;
import me.karakelley.http.http.HttpMethod;
import me.karakelley.http.http.Response;
import me.karakelley.http.http.Request;
import me.karakelley.http.server.Handler;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class StaticFilesHandlerTest {

  @Test
  void testGetDisplayFiles() {
    TempFilesHelper.withTempDirectory(directory -> {
      TempFilesHelper.createTempFile(directory, "/test1.txt");
      TempFilesHelper.createTempFile(directory, "/test2.txt");
      PublicDirectory publicDirectory = PublicDirectory.create(directory.toString());
      Handler handler = new StaticFilesHandler(publicDirectory, new HtmlFilePresenter(publicDirectory));
      Response response = handler.respond(new Request.Builder()
              .setMethod(HttpMethod.GET)
              .setPath("/")
              .setProtocol("HTTP/1.1")
              .setPort(0)
              .build());

      assertTrue(new String(response.getBody()).split("", 2).length == 2);
    });
  }

  @Test
  void testCreatesLinksForDirectories() {
    TempFilesHelper.withTempDirectory(directory -> {
      TempFilesHelper.createTempFile(directory, "/test1.txt");
      PublicDirectory publicDirectory = PublicDirectory.create(directory.toString());
      Handler handler = new StaticFilesHandler(publicDirectory, new HtmlFilePresenter(publicDirectory));
      Response response = handler.respond(new Request.Builder()
              .setMethod(HttpMethod.GET)
              .setPath("/")
              .setProtocol("HTTP/1.1")
              .setPort(0)
              .build());

      assertTrue(new String(response.getBody()).contains("<p><a href=\"/test1.txt\">test1.txt</a></p>"));
    });
  }

  @Test
  void testServesTextFile() {
    TempFilesHelper.withTempDirectory(directory -> {
      Path file = TempFilesHelper.createTempFile(directory, "/test1.txt");
      TempFilesHelper.createContents("Hello World", file);

      PublicDirectory publicDirectory = PublicDirectory.create(directory.toString());
      Handler handler = new StaticFilesHandler(publicDirectory, new HtmlFilePresenter(publicDirectory));
      Response response = handler.respond(new Request.Builder()
              .setMethod(HttpMethod.GET)
              .setPath("/test1.txt")
              .setProtocol("HTTP/1.1")
              .setPort(0)
              .build());

      assertEquals("Hello World", new String(response.getBody()));
    });
  }

  @Test
  void testServesIndexFileIfPresent() {
    TempFilesHelper.withTempDirectory(directory -> {
      Path file = TempFilesHelper.createTempFile(directory, "/index.html");
      TempFilesHelper.createContents("<p>Index File Present</p>", file);
      PublicDirectory publicDirectory = PublicDirectory.create(directory.toString());
      Handler handler = new StaticFilesHandler(publicDirectory, new HtmlFilePresenter(publicDirectory));
      Response response = handler.respond(new Request.Builder()
              .setMethod(HttpMethod.GET)
              .setPath("/")
              .setProtocol("HTTP/1.1")
              .setPort(0)
              .build());

      assertEquals("<p>Index File Present</p>", new String(response.getBody()));
    });
  }
}

