import java.net.Socket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;

import java.util.Set;

public class ConnectToRemoteHost {
	
  // needs to come form config file for the client
  private static final String hostName = "localhost";
  private static final Integer portNumber = 4444;  

  public static void setData(Appointment request) {

    try (Socket socket = new Socket(hostName, portNumber);
	     ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
         ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())){

      oos.writeObject(request);
      oos.flush();
     
    } catch (IOException e) {
      System.err.println("Error read/write client socket");
      e.printStackTrace();
    }
   
  }

  public static Set<Appointment> getData(Appointment request) {
  
    Set<Appointment> result = null;

    try (Socket socket = new Socket(hostName, portNumber);
	     ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
         ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())){

      oos.writeObject(request);
      oos.flush();

      while(socket.isConnected()) {
        Set<Appointment> appointments = (Set<Appointment>)ois.readObject();
	    return appointments;
	  }
      
    } catch (IOException | ClassNotFoundException e) {
      System.err.println("Error read/write client socket");
      e.printStackTrace();
    }
    return result;    
  }
}
