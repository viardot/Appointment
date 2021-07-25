import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.SelectionKey;

import java.io.IOException;

import java.net.ServerSocket;
import java.net.InetSocketAddress;

import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.Set;
import java.util.Iterator;

public class Server {

  private static Selector selector = null;
  private static String hostName = "localhost";
  private static Database db = null;

  public static void main(String[] args) {

    if (args.length != 1) {
      System.out.println("Usage: Server <port number>");
      return;
    }

    Integer port = 0;
    try {
      port = Integer.parseInt(args[0]);
    } catch (Exception e) {
      e.printStackTrace();
    }

    db = new Database();

    try { 
      selector = Selector.open(); 
      ServerSocketChannel socket = ServerSocketChannel.open();
      ServerSocket serverSocket = socket.socket();
      serverSocket.bind(new InetSocketAddress(hostName, port));
      socket.configureBlocking(false);
      Integer ops = socket.validOps();
      socket.register(selector, ops, null);
      while (true) {
        selector.select();
	Set<SelectionKey> selectedKeys = selector.selectedKeys();
        Iterator<SelectionKey> it = selectedKeys.iterator();

	while(it.hasNext()) {
          SelectionKey key = it.next();

	  if (key.isAcceptable()) {
            handleAccept(socket, key);
	  } else if (key.isReadable()) {
            handleRead(key);
	  }
	  it.remove();
	}
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static void handleAccept(ServerSocketChannel mySocket, SelectionKey key) throws IOException {

    SocketChannel client = mySocket.accept();
    client.configureBlocking(false);

    client.register(selector, SelectionKey.OP_READ);
  }

  private static void handleRead(SelectionKey key) throws IOException {
 
    SocketChannel socket = (SocketChannel) key.channel();
    
    try {
      while (socket.isConnected()) {
	
        Message message = Util.readMessageFromSocket(socket);
	
	Item request;
	message.setMessage("NOK");
	message.setCode(400);

	switch (message.getEvent()) {
	  case "getFirstSlot":
	    request = (Appointment)message.<Appointment>getMessageObject();
	    if (((Appointment)request).getSubject() == null && ((Appointment)request).getStartDateTime() == null && ((Appointment)request).getEndDateTime() == null) {
	      try {
	        message.setMessageObjects(db.getFirstSlot());
	        message.setMessage("OK");
	        message.setCode(100);
	      } catch (SQLException | ClassNotFoundException e) {
                message.setMessage("NOK");
	        message.setCode(500);
	      }
            }
            break;

          case "getAppointmentsBySubject":
	    request = (Appointment)message.<Appointment>getMessageObject();
            if (((Appointment)request).getSubject() != null && ((Appointment)request).getStartDateTime() == null && ((Appointment)request).getEndDateTime() == null) {
	      try { 
		String sql = ((Appointment)request).getBySubject();
		ResultSet rs = db.getItem(sql);
                message.setMessageObjects(((Appointment)request).returnAppointments(rs));
	        message.setMessage("OK");
	        message.setCode(100);
	      } catch (SQLException | ClassNotFoundException e) {
                message.setMessage("NOK");
	        message.setCode(500);
	      }
            }
	    break;
      
	  case "getAppointmentsByPeriod":
	    request = (Appointment)message.<Appointment>getMessageObject();
            if (((Appointment)request).getSubject() == null && ((Appointment)request).getStartDateTime() != null && ((Appointment)request).getEndDateTime() != null) {  
              try {
		String sql = ((Appointment)request).getByPeriod();
		ResultSet rs = db.getItem(sql);
		message.setMessageObjects(((Appointment)request).returnAppointments(rs));
	        message.setMessage("OK");
	        message.setCode(100);
	      } catch (SQLException | ClassNotFoundException e) {
                message.setMessage("NOK");
	        message.setCode(500);
	      }
            }
	    break;

	  case "setAppointment":
	    request = (Appointment)message.<Appointment>getMessageObject();
            if (((Appointment)request).getSubject() != null && ((Appointment)request).getStartDateTime() != null && ((Appointment)request).getEndDateTime() != null) {
	      Set<Appointment> appointments = null;
	      try {
                String sql = ((Appointment)request).toSQLInsert();
	        appointments = db.<Appointment>setItem(sql);
	        message.setMessage("OK");
	        message.setCode(100);
	      } catch (SQLException | ClassNotFoundException e) {
                message.setMessage("NOK");
	        message.setCode(500);
	      }
            }
	    break;

          case "getNextAssignment":
	    request = (Assignment)message.<Assignment>getMessageObject();
            if (((Assignment)request).getSubject() == null && ((Assignment)request).getDueDateTime() == null){
              try {
	        message.setMessageObjects(db.getNextAssignment());
	        message.setMessage("OK");
	        message.setCode(100);
	      } catch (SQLException | ClassNotFoundException e) {
                  message.setMessage("NOK - next aasignment");
		  message.setCode(500);
	      }
	    }
            break;
          case "getAssignmentsBySubject":
	    request = (Assignment)message.<Assignment>getMessageObject();
            if (((Assignment)request).getSubject() != null && ((Assignment)request).getDueDateTime() == null) {
	      try {    
   	        String sql = ((Assignment)request).getBySubject();
		ResultSet rs = db.getItem(sql);
		message.setMessageObjects(((Assignment)request).returnAssignments(rs));
	        message.setMessage("OK");
  	        message.setCode(100);
	      } catch (SQLException | ClassNotFoundException e) {
                message.setMessage("NOK");
  	        message.setCode(500);
	      }
	    }
	    break;
	  case "getAssignmentsByDueDateTime":
	    request = (Assignment)message.<Assignment>getMessageObject();
            if (((Assignment)request).getSubject() == null && ((Assignment)request).getDueDateTime() != null) {
	      try {    
		String sql = ((Assignment)request).getByDueDateTime();
		ResultSet rs = db.getItem(sql);
		message.setMessageObjects(((Assignment)request).returnAssignments(rs));
	        message.setMessage("OK");
		message.setCode(100);
	      } catch (SQLException | ClassNotFoundException e) {
                message.setMessage("NOK");
		message.setCode(500);
	      }
	    }
	    break;
	  case "setAssignment":
	    request = (Assignment)message.<Assignment>getMessageObject();
	    if (((Assignment)request).getSubject() != null && ((Assignment)request).getDueDateTime() != null) {
	      Set<Assignment> assignments = null;
	      try {
	        String sql = ((Assignment)request).toSQLInsert();
		assignments = db.<Assignment>setItem(sql);
	        message.setMessage("OK");
	        message.setCode(100);
              } catch (SQLException | ClassNotFoundException e) {
                message.setMessage("NOK");
		message.setCode(500);
	      }
	    }
	    break;
          default:
            message.setMessage("The event " + message.getEvent() + " does not exist.");
            message.setCode(400);
	}
	Util.writeMessageToSocket(socket, message);
	socket.close();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
