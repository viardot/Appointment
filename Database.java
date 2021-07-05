import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.time.LocalDateTime;

import java.util.HashSet;
import java.util.Set;

public class Database {

  public Database () {

    try (Connection con = connectDB()) {

      if (con != null) {
        Statement stmt = con.createStatement();
	String sql = "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.SYSTEM_TABLES WHERE TABLE_NAME = 'Appointments'";

	ResultSet rs = stmt.executeQuery(sql);
        
	if (!rs.next()) {
          initDatabase(stmt);
	}
	rs.close();

      }
    } catch (Exception e) {
      System.err.println("Error connection db");
      e.printStackTrace();
    }
  }

  private final Connection connectDB () throws ClassNotFoundException, SQLException {

    final String JDBCDriver = "org.hsqldb.jdbc.JDBCDriver";
    final String Connection = "jdbc:hsqldb:file:./data/agenda";
    final String UserName = "SA";
    final String Password = "";

    Class.forName(JDBCDriver);
    return DriverManager.getConnection(Connection, UserName, Password);
  }

  private final void initDatabase (Statement stmt) throws SQLException {
    
    String sql = " CREATE TABLE IF NOT EXISTS Appointments ("
	    + "Subject longvarchar,"
	    + "startDateTime bigint,"
	    + "endDateTime bigint)";

    stmt.executeUpdate(sql);
  }

  public HashSet<Appointment> setAppointment (String sql) throws SQLException, ClassNotFoundException {

    Connection con = connectDB();
    Statement stmt = con.createStatement();
    stmt.executeUpdate(sql);
    return null;
  }

  public HashSet<Appointment> getAppointmentsByPeriod(LocalDateTime startDateTime, LocalDateTime endDateTime) throws SQLException, ClassNotFoundException {
    
    HashSet<Appointment> set = new HashSet<>();

    long start = Util.toEpoch(startDateTime);
    long end = Util.toEpoch(endDateTime);
    
    Connection con =  connectDB();
    Statement stmt = con.createStatement();
    String sql = "SELECT * FROM Appointments WHERE StartDateTime >= '"+ start + "' and EndDateTime <= '" + end + "'";
    ResultSet rs = stmt.executeQuery(sql);
    while(rs.next()){
        Appointment appointment = new Appointment(rs.getString("subject")
	       	                               ,Util.toLocalDateTime(rs.getLong("startDateTime"))
					       ,Util.toLocalDateTime(rs.getLong("endDateTime")));
        set.add(appointment);  
    }
    return set;
  }

  public HashSet<Appointment> getAppointmentsBySubject(String subject) throws SQLException, ClassNotFoundException {

    String mySubject = null;
    LocalDateTime startDateTime = null;
    LocalDateTime endDateTime = null;

    HashSet<Appointment> appointments = new HashSet<>();
    
    Connection con = connectDB();
    Statement stmt = con.createStatement();
    String sql = "SELECT * FROM Appointments WHERE subject = '" + subject + "'";
    ResultSet rs = stmt.executeQuery(sql);
    while (rs.next()){
	    mySubject     = rs.getString("subject");
	    startDateTime = Util.toLocalDateTime(rs.getLong("startDateTime"));
	    endDateTime   = Util.toLocalDateTime(rs.getLong("endDateTime"));
	    Appointment appointment = new Appointment(mySubject, startDateTime, endDateTime);
	    appointments.add(appointment);
    }
    return appointments;
  }
  
  public HashSet<Appointment> getFirstSlot() throws SQLException, ClassNotFoundException {
	  
    HashSet<Appointment> set = new HashSet<>();
	
    LocalDateTime ldt = LocalDateTime.now();
	Long epochLdt = Util.toEpoch(ldt);
	
    Appointment firstSlot = new Appointment();
	
    Connection con = connectDB();
    Statement stmt = con.createStatement();

    while (firstSlot.getStartDateTime() == null) {

      String sql = "SELECT MIN(startDateTime) AS startDateTime, endDateTime FROM (SELECT * FROM Appointments WHERE startDateTime > '" + epochLdt + "') GROUP BY endDateTime";
      ResultSet rs = stmt.executeQuery(sql);
      Appointment a = new Appointment();
      while (rs.next()){
        a.setStartDateTime(Util.toLocalDateTime(rs.getLong("startDateTime")));
        a.setEndDateTime(Util.toLocalDateTime(rs.getLong("endDateTime")));
      }

      sql = "SELECT startDateTime, MIN(endDateTime) AS endDateTime FROM (SELECT * FROM Appointments WHERE endDateTime > '" + epochLdt + "') GROUP BY startDateTime";
      rs = stmt.executeQuery(sql);
      Appointment b = new Appointment();		
      while (rs.next()){
        b.setStartDateTime(Util.toLocalDateTime(rs.getLong("startDateTime")));
        b.setEndDateTime(Util.toLocalDateTime(rs.getLong("endDateTime")));
      }

      if (a.getStartDateTime() == null && b.getEndDateTime() == null) {
        firstSlot.setSubject("There is no future appointment.");
        firstSlot.setStartDateTime(Util.toLocalDateTime(epochLdt));
        firstSlot.setEndDateTime(null);
      } else {

        if (a.getStartDateTime().isBefore(b.getEndDateTime())) {
          firstSlot.setSubject("First free slot for an appointment.");
          firstSlot.setStartDateTime(Util.toLocalDateTime(epochLdt));
          firstSlot.setEndDateTime(a.getStartDateTime());
        }
		
        if (a.getStartDateTime().isAfter(b.getEndDateTime())) {
          epochLdt = Util.toEpoch(b.getEndDateTime());
        }
		
        if (a.getStartDateTime().isEqual(b.getEndDateTime())) {
          epochLdt = Util.toEpoch(a.getEndDateTime());
        }
      }
    }	  
    set.add(firstSlot);
    return set;
  }
}
