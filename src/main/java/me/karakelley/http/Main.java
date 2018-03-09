package me.karakelley.http;

class Main {
  public static void main(String[] args) {
    Integer port = new ServerConfiguration(args, new ServerLogger("ServerConfiguration")).getPort();
    Server server = new Server(port, new ServerLogger("Server"));
    server.start();
  }
}
