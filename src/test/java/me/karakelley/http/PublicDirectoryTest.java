package me.karakelley.http;


import me.karakelley.http.helpers.TempFilesHelper;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PublicDirectoryTest {
  @Test
  void testMissingDirectory() {
    assertThrows(PublicDirectory.PublicDirectoryMissingException.class, () -> PublicDirectory.create(UUID.randomUUID().toString()));
  }

  @Test
  void testPathPointsToAFile() {
    TempFilesHelper.withTempDirectory(directory -> {
      Path file = TempFilesHelper.createTempFile(directory);

      assertThrows(PublicDirectory.PublicDirectoryNotADirectoryException.class, () -> PublicDirectory.create(file.toString()));
    });
  }

  @Test
  void testEmptyDirectory() {
    TempFilesHelper.withTempDirectory(directory -> {
      PublicDirectory publicDirectory = PublicDirectory.create(directory.toString());

      List<String> fileNames = publicDirectory.getFiles();

      assertTrue(fileNames.size() == 0);
    });
  }

  @Test
  void testListFiles() {
    TempFilesHelper.withTempDirectory(directory -> {
      Path fileOne = TempFilesHelper.createTempFile(directory);
      Path fileTwo = TempFilesHelper.createTempFile(directory);
      PublicDirectory publicDirectory = PublicDirectory.create(directory.toString());

      List<String> fileNames = publicDirectory.getFiles();

      assertTrue(fileNames.size() == 2);
      assertTrue(fileNames.contains(fileOne.getFileName().toString()));
      assertTrue(fileNames.contains(fileTwo.getFileName().toString()));
    });
  }
}