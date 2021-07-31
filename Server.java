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

	String sql = null;

	switch (message.getEvent()) {
	  case "setItem":
              sql = request.toSQLInsert();
	      updateItem(message, sql);
	    break;
	  
	  case "deleteByUUID":
	    if (request.getUUID() != null) {
              sql = request.deleteByUUID();
	      updateItem(message, sql);
	    }
	    break;
	  
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
	      sql = ((Appointment)request).getBySubject();
	      getAppointments(message, request, sql);
            }
	    break;
          
	  case "getAppointmentByUUID":
	    if (request.getUUID() != null) {
	      sql = request.getByUUID();
              getAppointments(message, request, sql);
            }
	    break;
	  
	  case "getAppointmentsByPeriod":
            if (((Appointment)request).getSubject() == null && ((Appointment)request).getStartDateTime() != null && ((Appointment)request).getEndDateTime() != null) {  
              sql = ((Appointment)request).getByPeriod();
              getAppointments(message, request, sql);
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
   	      sql = ((Assignment)request).getBySubject();
              getAssignments(message, request, sql);
	    }
	    break;
          
	  case "getAssignmentByUUID":
            if (request.getUUID() != null) {
	      sql = request.getByUUID();
              getAssignments(message, request, sql);
            }
	    break;
	  
	  case "getAssignmentsByDueDateTime":
            if (((Assignment)request).getSubject() == null && ((Assignment)request).getDueDateTime() != null) {
	      sql = ((Assignment)request).getByDueDateTime();
              getAssignments(message, request,  sql);
	    }
	    break;
	  
	  case "getAppointmentByAssignmentUUID":
            if (((Appointment_Assignment)request).getAppointmentUUID() == null && ((Appointment_Assignment)request).getAssignmentUUID() != null) {
	      sql = ((Appointment_Assignment)request).getAppointmentByAssignmentUUID();
	      Appointment appointment = new Appointment();
	      try {    
		ResultSet rs = db.getItem(sql);
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
	      sql = ((Appointment_Assignment)request).getAssignmentByAppointmentUUID();
	      Assignment assignment = new Assignment();
	      try {    
		ResultSet rs = db.getItem(sql);
		message.setMessageObjects(assignment.returnItems(rs));
	        message.setMessage("OK");
		message.setCode(100);
	      } catch (SQLException | ClassNotFoundException e) {
                message.setMessage("NOK");
		message.setCode(500);
	      }
	    }
	    break;
	  
	  case "updateAppointmentSubject":
	    if (((Appointment)request).getSubject() != null) {
	      sql = ((Appointment)request).updateSubject();
	      updateItem(message, sql);
	    }
	    break;
	  
	  case "updateApointmentStartDateTime":
	    if (((Appointment)request).getStartDateTime() != null) {
	      sql = ((Appointment)request).updateStartDateTime();
	      updateItem(message, sql);
	    }
	    break;
	  case "updateAppointmentEndDateTime":
	    if (((Appointment)request).getEndDateTime() != null) {
	      sql = ((Appointment)request).updateEndDateTime();
	      updateItem(message, sql);
	    }
	    break;
	  case "updateAssignmentSubject":
	    //if() {
	    //}
	    break;
	  case "updateAssignmentDueDateTime":
	    //if() {
	    //}
	    break;
	  case "updateActive":
            sql = request.updateActive();
	    updateItem(message, sql);
	    break;

	  case "deleteAppointmentBySubject":
	    if (((Appointment)request).getSubject() != null) {
              sql = ((Appointment)request).deleteBySubject();
	      updateItem(message, sql);
	    }
	    break;
	  
	  case "deleteAppointmentByPeriod":
	    if (((Appointment)request).getStartDateTime() != null && ((Appointment)request).getEndDateTime() != null) {
	      sql = ((Appointment)request).deleteByPeriod();
	      updateItem(message, sql);
	    }
	    break;

	  case "deleteAssignmentBySubject":
	    if (((Assignment)request).getSubject() != null) {
              sql = ((Assignment)request).deleteBySubject();
	      updateItem(message, sql);
	    }
	    break;
	  
	  case "deleteAssignmentByDueDateTime":
	    if (((Assignment)request).getDueDateTime() != null) {
              sql = ((Assignment)request).deleteByDueDateTime();
	      updateItem(message, sql);
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

  private static void getAssignments(Message message, Item request, String sql) {
      try {    
	ResultSet rs = db.getItem(sql);
        message.setMessageObjects(request.<Assignment>returnItems(rs));
        message.setMessage("OK");
        message.setCode(100);
      } catch (SQLException | ClassNotFoundException e) {
        message.setMessage("NOK");
        message.setCode(500);
      }
  }

  private static void getAppointments(Message message, Item request, String sql) {
    try {
      ResultSet rs = db.getItem(sql);
      message.setMessageObjects(request.<Appointment>returnItems(rs));
      message.setMessage("OK");
      message.setCode(100);
    } catch (SQLException | ClassNotFoundException e) {
      message.setMessage("NOK");
      message.setCode(500);
    }
  }

  private static void updateItem(Message message, String sql) {
    try {
      db.updateItem(sql);
      message.setMessage("OK");
      message.setCode(100);
    } catch (SQLException | ClassNotFoundException e) {
      message.setMessage("NOK");
      message.setCode(500);
    }
  }
}
