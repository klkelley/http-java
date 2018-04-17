package me.karakelley.http;

import me.karakelley.http.handlers.ApplicationFactory;
import me.karakelley.http.server.*;
import me.karakelley.http.exit.SystemExit;

import java.util.Map;

class Main {
  public static void main(String[] args) {
    Map<String, String> argsHash = new CommandLineArguments().parse(args, new SystemExit());
    ServerConfiguration serverConfiguration = new ServerConfiguration();
    serverConfiguration.setPort(argsHash.get("port"));
    serverConfiguration.setHandler(new ApplicationFactory().create(argsHash));
    HttpServer server = new HttpServer(serverConfiguration, new ConnectionHandler(), new RequestReaderFactory());

    server.start();
  }
}
