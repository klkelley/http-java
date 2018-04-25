package me.karakelley.http.server;

import java.io.File;
import java.util.List;

public interface FilePresenter {
  String displayFiles(List<File> paths);
}
