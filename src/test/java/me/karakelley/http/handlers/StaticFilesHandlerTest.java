package me.karakelley.http.handlers;

import me.karakelley.http.*;
import me.karakelley.http.contentpresenter.ContentPresenter;
import me.karakelley.http.contentpresenter.HtmlPresenter;
import me.karakelley.http.filesystem.FileFinderCache;
import me.karakelley.http.filesystem.PublicDirectory;
import me.karakelley.http.filesystem.RealFileFinder;
import me.karakelley.http.helpers.TempFilesHelper;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class StaticFilesHandlerTest {

  @Test
  void testGetDisplayFiles() {
    TempFilesHelper.withTempDirectory(directory -> {
      Path fileOne = TempFilesHelper.createTempFile(directory, "/test1");
      Path fileTwo = TempFilesHelper.createTempFile(directory, "/test2");
      PublicDirectory publicDirectory = PublicDirectory.create(directory.toString(), new FileFinderCache(new RealFileFinder()));

      ContentPresenter contentPresenter = new HtmlPresenter();
      Handler handler = new StaticFilesHandler(publicDirectory, contentPresenter);
      Response response = handler.respond(new Request("GET", "/", "HTTP/1.1", null, 0));

      assertTrue(new String(response.getBody()).split("", 2).length == 2);
    });
  }

  @Test
  void testCreatesLinksForDirectories() {
    TempFilesHelper.withTempDirectory(directory -> {
      Path file = TempFilesHelper.createTempFile(directory, "/test1");
      PublicDirectory publicDirectory = PublicDirectory.create(directory.toString(), new FileFinderCache(new RealFileFinder()));
      ContentPresenter contentPresenter = new HtmlPresenter();
      Handler handler = new StaticFilesHandler(publicDirectory, contentPresenter);
      Response response = handler.respond(new Request("GET", "/", "HTTP/1.1", null, 0));

      assertTrue(new String(response.getBody()).contains("<p><a href=\"/test1.txt\">test1.txt</a></p>"));
    });
  }

  @Test
  void testServesTextFile() {
    TempFilesHelper.withTempDirectory(directory -> {
      Path file = TempFilesHelper.createTempFile(directory, "/test1");
      TempFilesHelper.createContents("Hello World", file);

      PublicDirectory publicDirectory = PublicDirectory.create(directory.toString(), new FileFinderCache(new RealFileFinder()));
      ContentPresenter contentPresenter = new HtmlPresenter();
      Handler handler = new StaticFilesHandler(publicDirectory, contentPresenter);
      Response response = handler.respond(new Request("GET", "/test1.txt",  "HTTP/1.1", null, 0));
      assertEquals("Hello World", new String(response.getBody()));
    });
  }
}

