package me.karakelley.http;

import me.karakelley.http.exceptions.PublicDirectoryMissingException;
import me.karakelley.http.exceptions.PublicDirectoryNotADirectoryException;
import me.karakelley.http.filesystem.FileFinderCache;
import me.karakelley.http.filesystem.PublicDirectory;
import me.karakelley.http.filesystem.RealFileFinder;
import me.karakelley.http.helpers.TempFilesHelper;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PublicDirectoryTest {
  @Test
  void testMissingDirectory() {
    assertThrows(PublicDirectoryMissingException.class, () -> {
      PublicDirectory.create(UUID.randomUUID().toString(), new FileFinderCache(new RealFileFinder()));
    });
  }

  @Test
  void testPathPointsToAFile() {
    TempFilesHelper.withTempDirectory(directory -> {
      Path file = TempFilesHelper.createTempFile(directory, "/test1");

      assertThrows(PublicDirectoryNotADirectoryException.class, () -> {
        PublicDirectory.create(file.toString(), new FileFinderCache(new RealFileFinder()));
      });
    });
  }

  @Test
  void testEmptyDirectory() {
    TempFilesHelper.withTempDirectory(directory1 -> {
      PublicDirectory publicDirectory = PublicDirectory.create(directory1.toString(), new FileFinderCache(new RealFileFinder()));
      List<File> fileNames = publicDirectory.getDirectoriesAndFiles("/");

      assertTrue(fileNames.size() == 0);
    });
  }

  @Test
  void testListFiles() {
    TempFilesHelper.withTempDirectory(directory -> {
      Path fileOne = TempFilesHelper.createTempFile(directory, "/test1");
      Path fileTwo = TempFilesHelper.createTempFile(directory, "/test2");
      PublicDirectory publicDirectory = PublicDirectory.create(directory.toString(), new FileFinderCache(new RealFileFinder()));
      List<File> fileNames = publicDirectory.getDirectoriesAndFiles("/");

      assertTrue(fileNames.size() == 2);
    });
  }

  @Test
  void testResourceExists() {
    TempFilesHelper.withTempDirectory(directory -> {
      Path fileOne = TempFilesHelper.createTempFile(directory, "/test1");
      PublicDirectory publicDirectory = PublicDirectory.create(directory.toString(), new FileFinderCache(new RealFileFinder()));

      assertTrue(publicDirectory.resourceExists("/" + String.valueOf(fileOne.getFileName())));
    });
  }

  @Test
  void testResourceDoesntExist() {
    PublicDirectory publicDirectory = PublicDirectory.create("/", new FileFinderCache(new RealFileFinder()));

    assertFalse(publicDirectory.resourceExists("/someBadPath"));
  }

  @Test
  void testNoFilesInDirectory() {
    PublicDirectory publicDirectory = PublicDirectory.create("/", new FileFinderCache(new RealFileFinder()));

    assertEquals(new ArrayList<>(), publicDirectory.getDirectoriesAndFiles("/somewhere"));
  }

  @Test
  void testGetMimeType() {
    TempFilesHelper.withTempDirectory(directory -> {
      PublicDirectory publicDirectory = PublicDirectory.create(directory.toString(), new FileFinderCache(new RealFileFinder()));

      assertEquals("text/plain", publicDirectory.getMimeType("/text1.txt"));
    });
  }

  @Test
  void testIsFile() {
    TempFilesHelper.withTempDirectory(directory -> {
      Path file = TempFilesHelper.createTempFile(directory, "/test1");
      PublicDirectory publicDirectory = PublicDirectory.create(directory.toString(), new FileFinderCache(new RealFileFinder()));

      assertEquals(true, publicDirectory.isFile("/test1.txt"));
    });
  }

  @Test
  void testIsNotAFile() {
    TempFilesHelper.withTempDirectory(directory ->  {
      PublicDirectory publicDirectory = PublicDirectory.create(directory.toString(), new FileFinderCache(new RealFileFinder()));

      assertEquals(false, publicDirectory.isFile(directory.toString()));
    });
  }

  @Test
  void testGetFileContentsForEmptyFile() {
    TempFilesHelper.withTempDirectory(directory -> {
      Path file = TempFilesHelper.createTempFile(directory, "/test1");
      PublicDirectory publicDirectory = PublicDirectory.create(directory.toString(), new FileFinderCache(new RealFileFinder()));

      assertEquals("", new String(publicDirectory.getFileContents("/test1.txt")));
    });
  }

  @Test
  void getFileContents() {
    TempFilesHelper.withTempDirectory(directory -> {
      Path file = TempFilesHelper.createTempFile(directory, "/test1");
      TempFilesHelper.createContents("Hello World", file);

      PublicDirectory publicDirectory = PublicDirectory.create(directory.toString(), new FileFinderCache(new RealFileFinder()));

      assertEquals("Hello World", new String(publicDirectory.getFileContents("/test1.txt")));
    });
  }
}