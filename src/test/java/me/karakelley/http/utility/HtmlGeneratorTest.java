package me.karakelley.http.utility;

import me.karakelley.http.contentpresenter.HtmlPresenter;
import me.karakelley.http.filesystem.PublicDirectory;
import me.karakelley.http.helpers.TempFilesHelper;
import org.junit.jupiter.api.Test;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;

class HtmlGeneratorTest {

  @Test
  void testDisplayDirectory() {
    HtmlPresenter htmlGenerator = new HtmlPresenter();
    TempFilesHelper.withTempDirectory(directory -> {
      Path pathOne = TempFilesHelper.createTempFile(directory, "/test1");
      PublicDirectory publicDirectory = PublicDirectory.create(directory.toString());

      assertEquals(htmlGenerator.displayResources(pathOne.toFile(), publicDirectory), "<p><a href=\"/test1.txt\">test1.txt</a></p>");
    });
  }
}