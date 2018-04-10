package me.karakelley.http;

import ch.qos.logback.classic.Logger;
import me.karakelley.http.mocks.ExitMock;
import me.karakelley.http.utility.CommandLineArguments;
import me.karakelley.http.utility.Exit;
import me.karakelley.http.utility.InMemoryAppender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

class CommandLineArgumentsTest {

  private Exit exitMock;

  @BeforeEach
  void setUp() {
    exitMock = new ExitMock();
  }

  @AfterEach
  void tearDown() {
    CommandLineArguments.parse(new String[]{}, exitMock);
  }

  @Test
  void testInvalidPortThrowsException() {
    List<String> events = withCapturedLogging(() ->  {
      CommandLineArguments.parse(new String[]{"-p", "badinput"}, exitMock);
    });
    assertTrue(events.contains("java.lang.NumberFormatException: For input string: \"badinput\""));
  }

  @Test
  void testThrowsExceptionWithTooManyArguments() {
    List<String> events = withCapturedLogging(() ->  {
      CommandLineArguments.parse(new String[]{"-p", "badinput", "otherstuff"}, exitMock);
    });
    assertTrue(events.contains("java.lang.RuntimeException: Invalid number of arguments"));
  }

  @Test
  void testThrowsExceptionWithTooLittleArguments() {
    List<String> events = withCapturedLogging(() ->  {
      CommandLineArguments.parse(new String[]{"5000"}, exitMock);
    });
    assertTrue(events.contains("java.lang.RuntimeException: Invalid number of arguments"));
  }

  @Test
  void testContainsArgument() {
    Map<String, String> args = CommandLineArguments.parse(new String[]{"-d", "../path"}, exitMock);

    assertTrue(args.containsKey("directory"));
  }

  @Test
  void testGetArgument() {
    Map<String, String> args = CommandLineArguments.parse(new String[]{"-p", "5000"}, exitMock);

    assertEquals(args.get("port"), "5000");
  }

  @Test
  void testGetArgsHash() {
    Map<String, String> argsHash = CommandLineArguments.parse(new String[]{"--port", "5000"}, exitMock);
    Map<String, String> args = new HashMap<>();
    args.put("port", "5000");
    args.put("directory", "nodirectory");
    assertEquals(args, argsHash);
  }

  @Test
  void givenInvalidDirectoryProgramExits() {
    CommandLineArguments.parse(new String[]{"-p", "5000", "-d", "./funstuff/"}, exitMock);
    assertTrue(ExitMock.exitCalled > 0);
  }

  @Test
  void givenInvalidPortProgramExits() {
    CommandLineArguments.parse(new String[]{"-p", "badport", "-d", "./badpath/"}, exitMock);
    assertTrue(ExitMock.exitCalled > 0);
  }

  private List<String> withCapturedLogging(Runnable runnable) {
    Logger logger = (Logger) LoggerFactory.getLogger("ROOT");
    InMemoryAppender inMemoryAppender = (InMemoryAppender) logger.getAppender("InMemoryAppender");
    inMemoryAppender.setPrefix("test");
    inMemoryAppender.start();
    runnable.run();
    inMemoryAppender.stop();
    return inMemoryAppender.getEvents();
  }
}