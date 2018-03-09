package me.karakelley.http.mocks;

import me.karakelley.http.ServerLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class MockLogger extends ServerLogger {
  final Logger logger;
  final String name;
  final List messages = new ArrayList();

  public MockLogger(String name) {
    super(name);
    this.name = name;
    this.logger = LoggerFactory.getLogger(name);
  }

  public List getMessages() {
    return messages;
  }

  public void info(String msg) {
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    PrintStream out = new PrintStream(output);
    PrintStream originalOut = System.out;
    System.setOut(out);
    logger.info(msg);
    System.out.flush();
    System.setOut(originalOut);

    messages.add(output.toString());
  }
}
