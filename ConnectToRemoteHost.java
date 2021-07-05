import java.nio.channels.SocketChannel;
import java.net.InetSocketAddress;

import java.io.Serializable;
import java.io.IOException;

import java.util.Set;

public class ConnectToRemoteHost {

  // needs to come from config file for the client
  private static String hostName = "localhost";
  private static Integer portNumber = 4444;
  
  public static <T extends Serializable> Message getData(Message<T> message) {

    try {
      SocketChannel socket = SocketChannel.open(new InetSocketAddress(hostName, portNumber));

      Util.writeMessageToSocket(socket, message);
      
      message = Util.readMessageFromSocket(socket);
      socket.close();
      return message;

    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }
}
