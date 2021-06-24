import java.time.ZoneId;
import java.time.Instant;
import java.time.LocalDateTime;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;

import java.util.Set;

public class Util {

  public static long toEpoch(LocalDateTime ldt) {
    ZoneId zoneId = ZoneId.of("UTC");
    long epoch = ldt.atZone(zoneId).toEpochSecond();
    return epoch;
  }

  public static LocalDateTime toLocalDateTime(long epoch){
    ZoneId zoneId = ZoneId.of("UTC");
    LocalDateTime ldt = LocalDateTime.ofInstant(Instant.ofEpochSecond(epoch), zoneId);
    return ldt;
  }
  
  public static String toSQLInsert(Appointment appointment) {
    long start = toEpoch(appointment.getStartDateTime());
    long end   = toEpoch(appointment.getEndDateTime());
	
    String sql = "INSERT INTO Appointments VALUES('" + appointment.getSubject() + "', '" + start + "', '" + end + "')";
	
    return sql;  
  }

  public static void writeAppointmentToSocket(SocketChannel socket, Appointment appointment) {

    try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
         ObjectOutputStream oos = new ObjectOutputStream(baos)) {
      
      oos.writeObject(appointment);
      oos.flush();
      ByteBuffer buffer = ByteBuffer.wrap(baos.toByteArray());
      
      socket.write(buffer);
      
    } catch (IOException e) {
       e.printStackTrace();
    }
  }
  
  public static void writeAppointmentsToSocket(SocketChannel socket, Set<Appointment> appointments) {
  
    try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
         ObjectOutputStream oos = new ObjectOutputStream(baos)) {
      
      oos.writeObject(appointments);
      oos.flush();
      ByteBuffer buffer = ByteBuffer.wrap(baos.toByteArray());
      
      socket.write(buffer);
      
    } catch (IOException e) {
       e.printStackTrace();
    }
  }

  public static Appointment readAppointmentFromSocket(SocketChannel socket) {
    
    byte[] bytes = readSocket(socket);
      
    try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
         ObjectInputStream ois = new ObjectInputStream(bais)) {

      Appointment appointment = (Appointment)ois.readObject();
      return appointment;

    } catch (IOException | ClassNotFoundException e) {
      e.printStackTrace();
    }
    return null;
  }

  
  public static Set<Appointment> readAppointmentsFromSocket(SocketChannel socket) {
    
    byte[] bytes = readSocket(socket);
      
    try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
         ObjectInputStream ois = new ObjectInputStream(bais)) {

      Set<Appointment> appointments = (Set<Appointment>)ois.readObject();
      return appointments;

    } catch (IOException | ClassNotFoundException e) {
      e.printStackTrace();
    }
    return null;
  }
  
  private static byte[] readSocket (SocketChannel socket) {

    try {
      Integer read, i;
      ByteBuffer buffer = ByteBuffer.allocate(64);
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      
      while ((read = socket.read(buffer)) > 0) {
        byte[] byteArray = new byte[buffer.limit()];
        i = -1;
        buffer.flip();
        while (buffer.hasRemaining()) {
          byteArray[++i] = buffer.get();
        }
        baos.write(byteArray);
        buffer.clear();
      }

      return baos.toByteArray();
    
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }
}
