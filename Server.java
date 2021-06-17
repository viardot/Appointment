import java.net.ServerSocket;

import java.io.IOException;

public class Server {

  public static void main(String[] args){

    if (args.length != 1) {
      System.out.println("Usage: Server <portnumber>");
      return;
    }

    Integer port = 0;
    try {
      port = Integer.parseInt(args[0]);
    } catch (Exception e) {
      System.err.println("Error converting port number");
      e.printStackTrace();
    }

    Database db = new Database();

    try (ServerSocket serverSocket = new ServerSocket(port)) {
      while (true) {
	System.out.println("Connection made: " + serverSocket.getLocalSocketAddress());
        new SocketServer(serverSocket.accept(), db).start();
      }  
    } catch (IOException e) {
       System.err.println("Error listen on socket port");
       e.printStackTrace();
    }
  }
}
