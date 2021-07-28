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
	
	Item request = ItemFactory.getItem(message.getMessageObjectType());
	request = ItemFactory.castItem(message);

	message.setMessage("NOK");
	message.setCode(400);

	switch (message.getEvent()) {
	  case "getFirstSlot":
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
	    if (((Appointment)request).getSubject() != null && ((Appointment)request).getStartDateTime() == null && ((Appointment)request).getEndDateTime() == null) {
	      try { 
		String sql = ((Appointment)request).getBySubject();
		ResultSet rs = db.getItem(sql);
	        message.setMessageObjects(request.<Appointment>returnItems(rs));
		message.setMessage("OK");
	        message.setCode(100);
	      } catch (SQLException | ClassNotFoundException e) {
                message.setMessage("NOK");
	        message.setCode(500);
	      }
            }
	    break;
          case "getAppointmentByUUID":
            if (request.getUUID() != null) {
              try {
		String sql = request.getByUUID();
		ResultSet rs = db.getItem(sql);
	        message.setMessageObjects(request.<Appointment>returnItems(rs));
	        message.setMessage("OK");
	        message.setCode(100);
	      } catch (SQLException | ClassNotFoundException e) {
                message.setMessage("NOK");
	        message.setCode(500);
	      }
            }
	    break;
	  case "getAppointmentsByPeriod":
            if (((Appointment)request).getSubject() == null && ((Appointment)request).getStartDateTime() != null && ((Appointment)request).getEndDateTime() != null) {  
              try {
		String sql = ((Appointment)request).getByPeriod();
		ResultSet rs = db.getItem(sql);
	        message.setMessageObjects(request.<Appointment>returnItems(rs));
	        message.setMessage("OK");
	        message.setCode(100);
	      } catch (SQLException | ClassNotFoundException e) {
                message.setMessage("NOK");
	        message.setCode(500);
	      }
            }
	    break;

	  case "setAppointment":
            if (((Appointment)request).getSubject() != null && ((Appointment)request).getStartDateTime() != null && ((Appointment)request).getEndDateTime() != null) {
	      Set<Appointment> appointments = null;
	      try {
                String sql = request.toSQLInsert();
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
            if (((Assignment)request).getSubject() != null && ((Assignment)request).getDueDateTime() == null) {
	      try {    
   	        String sql = ((Assignment)request).getBySubject();
		ResultSet rs = db.getItem(sql);
	        message.setMessageObjects(request.<Assignment>returnItems(rs));
	        message.setMessage("OK");
  	        message.setCode(100);
	      } catch (SQLException | ClassNotFoundException e) {
                message.setMessage("NOK");
  	        message.setCode(500);
	      }
	    }
	    break;
          case "getAssignmentByUUID":
            if (request.getUUID() != null) {
              try {
		String sql = request.getByUUID();
		ResultSet rs = db.getItem(sql);
	        message.setMessageObjects(request.<Assignment>returnItems(rs));
	        message.setMessage("OK");
	        message.setCode(100);
	      } catch (SQLException | ClassNotFoundException e) {
                message.setMessage("NOK");
	        message.setCode(500);
	      }
            }
	    break;
	  case "getAssignmentsByDueDateTime":
            if (((Assignment)request).getSubject() == null && ((Assignment)request).getDueDateTime() != null) {
	      try {    
		String sql = ((Assignment)request).getByDueDateTime();
		ResultSet rs = db.getItem(sql);
	        message.setMessageObjects(request.<Assignment>returnItems(rs));
	        message.setMessage("OK");
		message.setCode(100);
	      } catch (SQLException | ClassNotFoundException e) {
                message.setMessage("NOK");
		message.setCode(500);
	      }
	    }
	    break;
	  case "setAssignment":
	    if (((Assignment)request).getSubject() != null && ((Assignment)request).getDueDateTime() != null) {
	      Set<Assignment> assignments = null;
	      try {
	        String sql = request.toSQLInsert();
		assignments = db.<Assignment>setItem(sql);
	        message.setMessage("OK");
	        message.setCode(100);
              } catch (SQLException | ClassNotFoundException e) {
                message.setMessage("NOK");
		message.setCode(500);
	      }
	    }
	    break;
	  case "getAppointmentByAssignmentUUID":
            if (((Appointment_Assignment)request).getAppointmentUUID() == null && ((Appointment_Assignment)request).getAssignmentUUID() != null) {
	      try {    
		String sql = ((Appointment_Assignment)request).getAppointmentByAssignmentUUID();
		ResultSet rs = db.getItem(sql);
		Appointment appointment = new Appointment();
		message.setMessageObjects(appointment.returnItems(rs));
		message.setMessage("OK");
		message.setCode(100);
	      } catch (SQLException | ClassNotFoundException e) {
                message.setMessage("NOK");
		message.setCode(500);
		e.printStackTrace();
	      }
	    }
	    break;
	  case "getAssignmentByAppointmentUUID":
            if (((Appointment_Assignment)request).getAppointmentUUID() != null && ((Appointment_Assignment)request).getAssignmentUUID() == null) {
	      try {    
		String sql = ((Appointment_Assignment)request).getAssignmentByAppointmentUUID();
		ResultSet rs = db.getItem(sql);
	        Assignment assignment = new Assignment();
		message.setMessageObjects(assignment.returnItems(rs));
	        message.setMessage("OK");
		message.setCode(100);
	      } catch (SQLException | ClassNotFoundException e) {
                message.setMessage("NOK");
		message.setCode(500);
	      }
	    }
	    break;
	  case "setAppointment_Assignment":
	    if (((Appointment_Assignment)request).getAppointmentUUID() != null && ((Appointment_Assignment)request).getAssignmentUUID() != null) {
	      Set<Appointment_Assignment> set = null;
	      try {
                String sql = request.toSQLInsert();
                set = db.<Appointment_Assignment>setItem(sql);
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
