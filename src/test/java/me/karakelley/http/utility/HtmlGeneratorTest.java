package me.karakelley.http.utility;

import me.karakelley.http.ContentGeneration.HtmlGenerator;
import me.karakelley.http.FileSystem.FileFinderCache;
import me.karakelley.http.FileSystem.PublicDirectory;
import me.karakelley.http.FileSystem.RealFileFinder;
import me.karakelley.http.helpers.TempFilesHelper;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class HtmlGeneratorTest {

  @Test
  void testDisplayDirectory() {
    HtmlGenerator htmlGenerator = new HtmlGenerator();
    TempFilesHelper.withTempDirectory(directory -> {
      Path pathOne = TempFilesHelper.createTempFile(directory);

      assertTrue(htmlGenerator.displayDirectories(pathOne.toFile(), PublicDirectory.create("/", new FileFinderCache(new RealFileFinder()))).contains("<p><a href="));
      assertTrue(htmlGenerator.displayDirectories(pathOne.toFile(), PublicDirectory.create("/", new FileFinderCache(new RealFileFinder()))).contains("</a></p>"));
    });
  }

  @Test
  void testDisplayFile() {
    HtmlGenerator htmlGenerator = new HtmlGenerator();
    assertEquals("<p>testFile</p>", htmlGenerator.displayFiles("testFile"));
  }
}