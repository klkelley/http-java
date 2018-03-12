package me.karakelley.http;

import ch.qos.logback.classic.Logger;
import me.karakelley.http.utility.InMemoryAppender;
import org.junit.jupiter.api.*;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

class HttpServerConfigurationTest {
  ServerConfiguration serverConfig;
  private static Logger rootLogger;
  private static InMemoryAppender inMemoryAppender;

  @BeforeEach
  void setUp() {
    rootLogger = (Logger) LoggerFactory.getLogger("ROOT");
    inMemoryAppender = (InMemoryAppender) rootLogger.getAppender("InMemoryAppender");
    inMemoryAppender.setPrefix("test");
    inMemoryAppender.start();
  }

  @AfterEach
  void tearDown() {
    inMemoryAppender.stop();
  }

  @Test
  void testNoPortGiven() {
    serverConfig = new ServerConfiguration(new String[]{});
    serverConfig.setPort();

    assertEquals(0, serverConfig.getPort());
  }

  @Test
  void testTooLittleArguments() {
    serverConfig = new ServerConfiguration(new String[]{"5000"});
    serverConfig.setPort();

    assertTrue(inMemoryAppender.getEvents().contains("Not enough arguments"));
  }

  @Test
  void testGivenAPort() {
    serverConfig = new ServerConfiguration(new String[]{"-p", "5000"});
    serverConfig.setPort();

    assertEquals(5000, serverConfig.getPort());
  }

  @Test
  void testInvalidPort() {
    serverConfig = new ServerConfiguration(new String[]{"-p", "badinput"});
    serverConfig.setPort();

    assertTrue(inMemoryAppender.getEvents().contains("java.lang.NumberFormatException: For input string: \"badinput\""));
  }

  @Test
  void testTooManyArguments() {
    serverConfig = new ServerConfiguration(new String[]{"-p", "badinput", "otherstuff"});
    serverConfig.setPort();

    assertTrue(inMemoryAppender.getEvents().contains("Not enough arguments"));
  }
}