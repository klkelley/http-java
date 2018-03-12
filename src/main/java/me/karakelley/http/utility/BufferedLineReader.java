package me.karakelley.http.utility;

import java.io.BufferedReader;
import java.io.Reader;

public class BufferedLineReader extends BufferedReader implements LineReader {
  public BufferedLineReader(Reader in) {
    super(in);
  }
}
