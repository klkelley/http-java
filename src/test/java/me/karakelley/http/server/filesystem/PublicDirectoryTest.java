package me.karakelley.http.server.filesystem;

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
      PublicDirectory.create(UUID.randomUUID().toString());
    });
  }

  @Test
  void testPathPointsToAFile() {
    TempFilesHelper.withTempDirectory(directory -> {
      Path file = TempFilesHelper.createTempFile(directory, "/test1.txt");

      assertThrows(PublicDirectoryNotADirectoryException.class, () -> {
        PublicDirectory.create(file.toString());
      });
    });
  }

  @Test
  void testEmptyDirectory() {
    TempFilesHelper.withTempDirectory(directory1 -> {
      PublicDirectory publicDirectory = PublicDirectory.create(directory1.toString());
      List<File> fileNames = publicDirectory.getDirectoriesAndFiles("/");

      assertTrue(fileNames.size() == 0);
    });
  }

  @Test
  void testListFiles() {
    TempFilesHelper.withTempDirectory(directory -> {
      Path fileOne = TempFilesHelper.createTempFile(directory, "/test1.txt");
      Path fileTwo = TempFilesHelper.createTempFile(directory, "/test2.txt");
      PublicDirectory publicDirectory = PublicDirectory.create(directory.toString());
      List<File> fileNames = publicDirectory.getDirectoriesAndFiles("/");

      assertTrue(fileNames.size() == 2);
    });
  }

  @Test
  void testResourceExists() {
    TempFilesHelper.withTempDirectory(directory -> {
      Path fileOne = TempFilesHelper.createTempFile(directory, "/test1.txt");
      PublicDirectory publicDirectory = PublicDirectory.create(directory.toString());

      assertTrue(publicDirectory.resourceExists("/" + String.valueOf(fileOne.getFileName())));
    });
  }

  @Test
  void testResourceDoesNotExist() {
    PublicDirectory publicDirectory = PublicDirectory.create("/");

    assertFalse(publicDirectory.resourceExists("/someBadPath"));
  }

  @Test
  void testGetMimeType() {
    TempFilesHelper.withTempDirectory(directory -> {
      PublicDirectory publicDirectory = PublicDirectory.create(directory.toString());
      Path file = TempFilesHelper.createTempFile(directory, "/test1.ss");
      assertEquals("text/css", publicDirectory.getMimeType("/test1.css"));
    });
  }

  @Test
  void testIsFile() {
    TempFilesHelper.withTempDirectory(directory -> {
      Path file = TempFilesHelper.createTempFile(directory, "/test1.txt");
      PublicDirectory publicDirectory = PublicDirectory.create(directory.toString());

      assertEquals(true, publicDirectory.isFile("/test1.txt"));
    });
  }

  @Test
  void testIsNotAFile() {
    TempFilesHelper.withTempDirectory(directory ->  {
      PublicDirectory publicDirectory = PublicDirectory.create(directory.toString());

      assertEquals(false, publicDirectory.isFile(directory.toString()));
    });
  }

  @Test
  void testGetFileContentsForEmptyFile() {
    TempFilesHelper.withTempDirectory(directory -> {
      Path file = TempFilesHelper.createTempFile(directory, "/test1.txt");
      PublicDirectory publicDirectory = PublicDirectory.create(directory.toString());

      assertEquals("", new String(publicDirectory.getFileContents("/test1.txt")));
    });
  }

  @Test
  void getFileContents() {
    TempFilesHelper.withTempDirectory(directory -> {
      Path file = TempFilesHelper.createTempFile(directory, "/test1.txt");
      TempFilesHelper.createContents("Hello World", file);

      PublicDirectory publicDirectory = PublicDirectory.create(directory.toString());

      assertEquals("Hello World", new String(publicDirectory.getFileContents("/test1.txt")));
    });
  }

  @Test
  void testCreateNewFile() {
    TempFilesHelper.withTempDirectory(directory ->  {
      PublicDirectory publicDirectory = PublicDirectory.create(directory.toString());
      try {
        publicDirectory.createFile("/testing123.txt", "hello world".getBytes());
      } catch (IOException e) {
        e.printStackTrace();
      }
      assertTrue(publicDirectory.resourceExists("/testing123.txt"));
    });
  }

  @Test
  void testCreateNewFileWithNewDirectory() {
    TempFilesHelper.withTempDirectory(directory ->  {
      PublicDirectory publicDirectory = PublicDirectory.create(directory.toString());
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
      PublicDirectory publicDirectory = PublicDirectory.create(directory.toString());
      Path file = TempFilesHelper.createTempFile(directory, "/test1.txt");

      byte[] bytes = new byte[0];
      try {
        bytes = Files.readAllBytes(file);
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
      Path file = TempFilesHelper.createTempFile(directory, "/test1.txt");
      TempFilesHelper.createContents("Hello", file);
      PublicDirectory publicDirectory = PublicDirectory.create(directory.toString());
      publicDirectory.updateFileContents("/test1.txt", "Hello World".getBytes());

      assertEquals("Hello World", new String(publicDirectory.getFileContents("/test1.txt")));
    });
  }

  @Test
  void testValidPath() {
    TempFilesHelper.withTempDirectory(directory ->  {
      PublicDirectory publicDirectory = PublicDirectory.create(directory.toString());

      assertEquals(true, publicDirectory.resourceExists("testing/../testing"));
    });
  }

  @Test
  void testInvalidPath() {
    TempFilesHelper.withTempDirectory(directory ->  {
      PublicDirectory publicDirectory = PublicDirectory.create(directory.toString());

      assertFalse(publicDirectory.resourceExists("/testing/../../"));
    });
  }
}
