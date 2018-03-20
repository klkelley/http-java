package me.karakelley.http.utility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class CommandLineArguments {
  private final static Logger logger = LoggerFactory.getLogger(CommandLineArguments.class);
  private static final String DEFAULT_PATH = "nodirectory";
  private static final int DEFAULT_PORT = 0;
  static Map<String, String> argsHash = new HashMap<>();

  public static Map<String, String> parse(String[] args, Exit systemExit) {
    try {
      if (!noArguments(args)) {
        convertArgsToMap(args);
        checkAndFormatArgs();
      }
      setPort(args);
      setDirectory(args);
    } catch (Exception e) {
      logger.info("Ouch!", e);
      systemExit.exit(0);
    }
    return argsHash;
  }

  private static void setDirectory(String[] args) {
    if (!noArguments(args) && containsArgument("directory")) {
      try {
        validPath();
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    } else argsHash.put("directory", DEFAULT_PATH);
  }

  private static void setPort(String[] args) {
    if (noArguments(args) || !containsArgument("port")) {
      argsHash.put("port", String.valueOf(DEFAULT_PORT));
    } else {
      validatePort();
    }
  }

  private static boolean containsArgument(String argument) {
    return argsHash.containsKey(argument);
  }

  private static boolean noArguments(String[] args) {
    return args.length == 0;
  }

  private static int validatePort() {
    try {
      return Integer.parseInt(argsHash.get("port"));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private static boolean validPath() {
    File file = new File(argsHash.get("directory"));
    if (file.isFile() || file.isDirectory()) {
      return true;
    } else {
      throw new RuntimeException("Invalid Directory");
    }
  }

  private static void checkAndFormatArgs() {
    if (argExists("-p", "--port")) {
      formatArgsHash("-p", "--port", "port");
    }
    if (argExists("-d", "--directory")) {
      formatArgsHash("-d", "--directory", "directory");
    }
  }

  private static void convertArgsToMap(String[] args) {
    try {
      invalidNumberOfArguments(args);

      for (int i = 0; i < args.length; i += 2) {
        argsHash.put(args[i], args[i + 1]);
      }
    } catch (RuntimeException ex) {
      throw new RuntimeException(ex);
    }
  }

  private static boolean argExists(String shortFlag, String fullFlag) {
    return argsHash.containsKey(shortFlag) || argsHash.containsKey(fullFlag);
  }

  private static void formatArgsHash(String shortFlag, String fullFlag, String newKey) {
    if (argsHash.containsKey(shortFlag)) {
      argsHash.put(newKey, argsHash.remove(shortFlag));
    } else if (argsHash.containsKey(fullFlag)) {
      argsHash.put(newKey, argsHash.remove(fullFlag));
    }
  }

  private static void invalidNumberOfArguments(String[] args) {
    if (args.length % 2 != 0 || args.length > 4) {
      throw new RuntimeException("Invalid number of arguments");
    }
  }
}
