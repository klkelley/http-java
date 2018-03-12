package me.karakelley.http.utility;

import java.io.IOException;

public interface LineReader extends AutoCloseable {
  String readLine() throws IOException;
}
