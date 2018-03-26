package me.karakelley.http;

import me.karakelley.http.FileSystem.FileFinderCache;
import me.karakelley.http.FileSystem.PublicDirectory;
import me.karakelley.http.FileSystem.RealFileFinder;
import me.karakelley.http.controllers.Application;
import me.karakelley.http.utility.CommandLineArguments;
import me.karakelley.http.utility.SystemExit;

import java.util.Map;
import java.util.function.BiConsumer;

class Main {
  public static void main(String[] args) {
    main(args, (argsHash1, serverConfiguration1) -> {
      if (!argsHash1.containsValue("nodirectory")) {
        serverConfiguration1.setController(new Application(PublicDirectory.create(argsHash1.get("directory"), new FileFinderCache(new RealFileFinder()))));
      } else serverConfiguration1.setController(new Application());
    });
  }

  public static void main(String[] args, BiConsumer<Map<String, String>, ServerConfiguration> configurationBiConsumer) {
    Map<String, String> argsHash = CommandLineArguments.parse(args, new SystemExit());
    ServerConfiguration serverConfiguration = new ServerConfiguration();
    serverConfiguration.setPort(argsHash.get("port"));

    configurationBiConsumer.accept(argsHash, serverConfiguration);

    HttpServer server = new HttpServer(serverConfiguration);
    server.start();
  }
}
