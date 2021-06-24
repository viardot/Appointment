import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.SelectionKey;

import java.io.IOException;

import java.net.ServerSocket;
import java.net.InetSocketAddress;

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
	
        Appointment request = Util.readAppointmentFromSocket(socket);
        
        if (request.getSubject() == null && request.getStartDateTime() == null && request.getEndDateTime() == null) {
          Set<Appointment> appointments = db.getFirstSlot();
          Util.writeAppointmentsToSocket(socket, appointments);
        }

        if (request.getSubject() != null && request.getStartDateTime() == null && request.getEndDateTime() == null) {
          Set<Appointment> appointments = db.getAppointmentsBySubject(request.getSubject());
          Util.writeAppointmentsToSocket(socket, appointments);
        }
     
        if (request.getSubject() == null && request.getStartDateTime() != null && request.getEndDateTime() != null) {  
          Set<Appointment> appointments = db.getAppointmentsByPeriod(request.getStartDateTime(), request.getEndDateTime());
          Util.writeAppointmentsToSocket(socket, appointments);
        }

        if (request.getSubject() != null && request.getStartDateTime() != null && request.getEndDateTime() != null) {
          String sql = Util.toSQLInsert(request);
          db.writeDB(sql);
        }
	socket.close();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
