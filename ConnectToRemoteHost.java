import java.nio.channels.SocketChannel;
import java.net.InetSocketAddress;
import java.io.IOException;
import java.util.Set;

public class ConnectToRemoteHost {

  // needs to come from config file for the client
  private static String hostName = "localhost";
  private static Integer portNumber = 4444;
  
  public static void setData(Appointment request) {

    try {
      SocketChannel socket = SocketChannel.open(new InetSocketAddress(hostName, portNumber));
      
      Util.writeAppointmentToSocket(socket, request);
      
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static Set<Appointment> getData(Appointment request) {

    try {
      SocketChannel socket = SocketChannel.open(new InetSocketAddress(hostName, portNumber));

      Util.writeAppointmentToSocket(socket, request);
      
      Set<Appointment> appointments = Util.readAppointmentsFromSocket(socket);
      socket.close();
      return appointments;

    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }
}
