import java.net.Socket;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;

import java.util.Set;

public class SocketServer extends Thread {

  private Socket socket = null;
  private Database db = null;

  public SocketServer(Socket socket, Database db) {
    super("SocketServer");
    this.socket = socket;
    this.db = db;
  }

  public void run() {

    try (ObjectInputStream ois  = new ObjectInputStream(socket.getInputStream());
         ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream())) {
      
      Appointment request;

      while(socket.isConnected()) {

		request = (Appointment)ois.readObject();

		if(request.getSubject() == null && request.getStartDateTime() == null && request.getEndDateTime() == null) {
		  Set<Appointment> appointments = db.getFirstSlot();
	      oos.writeObject(appointments);
		}
		
		if(request.getSubject() != null && request.getStartDateTime() == null && request.getEndDateTime() == null) {
		  Set<Appointment> appointments = db.getAppointmentsBySubject(request.getSubject());
	      oos.writeObject(appointments);
		}

		if(request.getSubject() == null && request.getStartDateTime() != null && request.getEndDateTime() != null) {
          //Reserved for getting appointments in a period
		}
		
	    if(request.getSubject() != null && request.getStartDateTime() != null && request.getEndDateTime() != null) {
		  String sql = Util.toSQLInsert(request);
		  db.writeDB(sql);
		}
		
      }
	  socket.close();
    } catch (IOException | ClassNotFoundException e) {
      System.err.println("Error server socket read/write");
      e.printStackTrace();
    }
  }
}
