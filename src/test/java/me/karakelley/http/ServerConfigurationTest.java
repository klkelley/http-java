package me.karakelley.http;

import ch.qos.logback.classic.Logger;
import org.junit.jupiter.api.*;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

class ServerConfigurationTest {
  ServerConfiguration serverConfig;
  private static Logger rootLogger;
  private static Appender appender;

  @BeforeEach
  void setUp() {
    rootLogger = (Logger) LoggerFactory.getLogger("ROOT");
    appender = (Appender) rootLogger.getAppender("appender");
    appender.setPrefix("test");
    appender.start();
  }

  @AfterEach
  void tearDown() {
    appender.stop();
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

    assertTrue(appender.getEvents().contains("[INFO] Not enough arguments"));
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

    assertTrue(appender.getEvents().contains("[INFO] java.lang.NumberFormatException: For input string: \"badinput\""));
  }

  @Test
  void testTooManyArguments() {
    serverConfig = new ServerConfiguration(new String[]{"-p", "badinput", "otherstuff"});
    serverConfig.setPort();

    assertTrue(appender.getEvents().contains("[INFO] Not enough arguments"));
  }
}