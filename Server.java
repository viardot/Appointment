import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.SelectionKey;

import java.io.IOException;

import java.net.ServerSocket;
import java.net.InetSocketAddress;

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
      byte[] byteArray = null;
      while (socket.isConnected()) {
	
        Message<Appointment> message = Util.readMessageFromSocket(socket);
        Appointment request = message.<Appointment>getMessageObject();
	switch (message.getEvent()) {
	  case "getFirstSlot":
            if (request.getSubject() == null && request.getStartDateTime() == null && request.getEndDateTime() == null) {
	      try {
	        message.setMessageObjects(db.getFirstSlot());
	        message.setMessage("OK");
	        message.setCode(100);
	      } catch (SQLException | ClassNotFoundException e) {
                message.setMessage("NOK");
		message.setCode(400);
	      }
            }
	    break;

          case "getAppointmentsBySubject":
            if (request.getSubject() != null && request.getStartDateTime() == null && request.getEndDateTime() == null) {
	      try { 
	        message.setMessageObjects(db.getAppointmentsBySubject(request.getSubject()));
	        message.setMessage("OK");
		message.setCode(100);
	      } catch (SQLException | ClassNotFoundException e) {
                message.setMessage("NOK");
		message.setCode(400);
	      }
            }
	    break;
      
	  case "getAppointmentsByPeriod":
            if (request.getSubject() == null && request.getStartDateTime() != null && request.getEndDateTime() != null) {  
	      try {
	        message.setMessageObjects(db.getAppointmentsByPeriod(request.getStartDateTime(), request.getEndDateTime()));
	        message.setMessage("OK");
		message.setCode(100);
	      } catch (SQLException | ClassNotFoundException e) {
                message.setMessage("NOK");
		message.setCode(400);
	      }
            }
	    break;

	  case "setAppointment":
            if (request.getSubject() != null && request.getStartDateTime() != null && request.getEndDateTime() != null) {
	      Set<Appointment> appointments = null;
	      try {
                String sql = Util.toSQLInsert(request);
                appointments = db.setAppointment(sql);
	        message.setMessage("OK");
		message.setCode(100);
	      } catch (SQLException | ClassNotFoundException e) {
                message.setMessage("NOK");
		message.setCode(400);
	      }
            }
	    break;

	  default:
	    message.setMessage("The event " +  message.getEvent() + " does not exist.");
	    message.setCode(400);
	    Util.writeMessageToSocket(socket, message);
	}
	Util.writeMessageToSocket(socket, message);
	socket.close();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
