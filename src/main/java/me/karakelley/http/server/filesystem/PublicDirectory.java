package me.karakelley.http.server.filesystem;
import java.io.*;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class PublicDirectory {

  public static PublicDirectory create(String path) {
    if (!Files.exists(Paths.get(path)))
      throw new PublicDirectoryMissingException("Directory does not exist!");

    if (!Files.isDirectory(Paths.get(path)))
      throw new PublicDirectoryNotADirectoryException("Not a directory!");

    return new PublicDirectory(path);
  }

  private File documentRoot;
  private String path;
  private HashMap<String, String> mimeTypes = new HashMap<>();

  private PublicDirectory(String path) {
    this.documentRoot = Paths.get(path).toFile();
    this.path = path;
    setUpMimeTypes();
  }

  public List<File> getDirectoriesAndFiles(String requestedResource) {
    File directory = normalizeFullPath(requestedResource);
    return Arrays.stream(directory.listFiles())
            .filter(file -> file.isDirectory() || file.isFile())
            .collect(Collectors.toList());
  }

  public boolean resourceExists(String path) {
    File resource = null;
    try {
      resource = Paths.get(normalizedDocumentRoot() + path).toFile().getCanonicalFile();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return isValidPath(path) && (resource.exists()) && resource.isDirectory() || resource.isFile();
  }

  public String relativePath(File file) {
    return documentRoot.toURI().relativize(file.toURI()).getPath();
  }

  public String getMimeType(String requestedResource){
    File path = normalizeFullPath(requestedResource);
    String ext = "";
    int i = path.getName().lastIndexOf('.');
    if (i > 0) {
      ext = path.getName().substring(i+1);
    }
    return mimeTypes.getOrDefault(ext, URLConnection.guessContentTypeFromName(path.getName()));
  }

  public boolean isFile(String requestedResource) {
    return normalizeFullPath(requestedResource).isFile();
  }

  public byte[] getFileContents(String requestedResource) {
    Path file = normalizeFullPath(requestedResource).toPath();
    try {
      return Files.readAllBytes(file);
    } catch (IOException e) {
      return "".getBytes();
    }
  }

  public void createFile(String path, byte[] contents) throws IOException {
    File newFile = normalizeFullPath(path);
    if (newFile.exists() && (newFile.isFile() || newFile.isDirectory())) throw new IOException();

    new File(newFile.getParentFile().getAbsolutePath()).mkdirs();

    if (contents != null) {
      try (FileOutputStream outputStream = new FileOutputStream(newFile)) {
        outputStream.write(contents);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

  public Path getPath(String requestedResource) {
    return Paths.get(documentRoot + requestedResource);
  }

  public void deleteResource(String path) {
    Path newFile = normalizeFullPath(path).toPath();
    try {
      Files.deleteIfExists(newFile);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void updateFileContents(String path, byte[] contents) {
    File newFile = normalizeFullPath(path);
    try (FileOutputStream outputStream = new FileOutputStream(newFile, false)) {
      outputStream.write(contents);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private boolean isValidPath(String requestedPath) {
    String canonicalPath = normalizeFullPath(requestedPath).toString();

    try {
      return canonicalPath.startsWith(normalizedDocumentRoot().toFile().getCanonicalFile().toString());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private File normalizeFullPath(String requestPath) {
    try {
      return Paths.get(normalizedDocumentRoot() + requestPath).toFile().getCanonicalFile();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private Path normalizedDocumentRoot() {
    return documentRoot.toPath().normalize();
  }

  private void setUpMimeTypes() {
    mimeTypes.put("css", "text/css");
    mimeTypes.put("txt", "text/plain");
    mimeTypes.put("text", "text/plain");
    mimeTypes.put("svg", "image/svg+xml");
    mimeTypes.put("html", "text/html");
    mimeTypes.put("pdf", "application/pdf");
    mimeTypes.put("png", "image/png");
    mimeTypes.put("doc", "application/msword");
    mimeTypes.put("gif", "image/gif");
  }
}

