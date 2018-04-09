package me.karakelley.http.publicdirectory;

import me.karakelley.http.exceptions.PublicDirectoryMissingException;
import me.karakelley.http.exceptions.PublicDirectoryNotADirectoryException;
import me.karakelley.http.filesystem.FileFinderCache;
import me.karakelley.http.filesystem.PublicDirectory;
import me.karakelley.http.filesystem.RealFileFinder;
import me.karakelley.http.helpers.TempFilesHelper;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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

  @Test
  void testCreateNewFile() {
    TempFilesHelper.withTempDirectory(directory ->  {
      PublicDirectory publicDirectory = PublicDirectory.create(directory.toString(), new FileFinderCache(new RealFileFinder()));
      try {
        publicDirectory.createFile("/testing123.txt", "hello world".getBytes());
      } catch (IOException e) {
        e.printStackTrace();
      }
      assertTrue(publicDirectory.resourceExists("/testing123.txt"));
    });
  }

  @Test
  void testCreateNewFileWithNewdirectory() {
    TempFilesHelper.withTempDirectory(directory ->  {
      PublicDirectory publicDirectory = PublicDirectory.create(directory.toString(), new FileFinderCache(new RealFileFinder()));
      try {
        publicDirectory.createFile("/newpath/testing123.txt", "hello world".getBytes());
      } catch (IOException e) {
        e.printStackTrace();
      }
      assertTrue(publicDirectory.resourceExists("/newpath/testing123.txt"));
    });
  }

  @Test
  void testBytesAreEqual() {
    TempFilesHelper.withTempDirectory(directory ->  {
      PublicDirectory publicDirectory = PublicDirectory.create(directory.toString(), new FileFinderCache(new RealFileFinder()));
      Path file = TempFilesHelper.createTempFile(directory, "/test1");

      byte[] bytes = new byte[0];
      try {
        bytes = Files.readAllBytes(file);
      } catch (IOException e) {
        e.printStackTrace();
      }

      try {
        publicDirectory.createFile(String.valueOf(file.toAbsolutePath()), "hello world".getBytes());
      } catch (IOException e) {
        e.printStackTrace();
      }

      byte[] otherBytes = publicDirectory.getFileContents("/test1");
      assertEquals(bytes.length, otherBytes.length);
    });
  }

  @Test
  void testUpdateContentsOfFile() {
    TempFilesHelper.withTempDirectory(directory -> {
      Path file = TempFilesHelper.createTempFile(directory, "/test1");
      TempFilesHelper.createContents("Hello", file);
      PublicDirectory publicDirectory = PublicDirectory.create(directory.toString(), new FileFinderCache(new RealFileFinder()));

      publicDirectory.updateFileContents("/test1.txt", "Hello World".getBytes());
      assertEquals("Hello World", new String(publicDirectory.getFileContents("/test1.txt")));
    });
  }

  @Test
  void testValidPath() {
    TempFilesHelper.withTempDirectory(directory ->  {
      PublicDirectory publicDirectory = PublicDirectory.create(directory.toString(), new FileFinderCache(new RealFileFinder()));
      assertEquals(true, publicDirectory.resourceExists("testing/../testing"));
    });
  }

  @Test
  void testInvalidPath() {
    TempFilesHelper.withTempDirectory(directory ->  {
      PublicDirectory publicDirectory = PublicDirectory.create(directory.toString(), new FileFinderCache(new RealFileFinder()));
      assertFalse(publicDirectory.resourceExists("/testing/../../"));
    });
  }
}