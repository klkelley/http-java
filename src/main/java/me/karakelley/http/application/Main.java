package me.karakelley.http.application;

import me.karakelley.http.application.authorization.BasicAuthorizer;
import me.karakelley.http.server.*;
import me.karakelley.http.application.exit.SystemExit;

import java.util.Map;

class Main {
  public static void main(String[] args) {
    Map<String, String> argsHash = new CommandLineArguments().parse(args, new SystemExit());
    ServerConfiguration serverConfiguration = new ServerConfiguration();
    serverConfiguration.setPort(argsHash.get("port"));
    BasicAuthorizer basicAuthorizer = new BasicAuthorizer("admin", "chicago32");
    AuthorizedApplicationFactory appFactory = new AuthorizedApplicationFactory();
    serverConfiguration.setHandler(appFactory.create(argsHash, basicAuthorizer));
    HttpServer server = new HttpServer(serverConfiguration, new ConnectionHandler(), new RequestReaderFactory());

    server.start();
  }
}
