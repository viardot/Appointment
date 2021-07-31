import java.io.Serializable;
import java.time.LocalDateTime;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.HashSet;

public class Appointment extends Item implements Serializable  {

  private static final long serialVersionUID = 4484460051818968173L;
  private UUID uuid = UUID.randomUUID();
  private LocalDateTime startDateTime;
  private LocalDateTime endDateTime;
  private String subject;
  private boolean isActive = true;

  public Appointment () {
    //Do Nothing
  }

  public Appointment(String subject, LocalDateTime startDateTime, LocalDateTime endDateTime) {
    this.startDateTime  = startDateTime;
    this.endDateTime = endDateTime;
    this.subject = subject;
  }

  public String getSubject()              { return subject; }
  public LocalDateTime getStartDateTime() { return startDateTime; }
  public LocalDateTime getEndDateTime()   { return endDateTime; }
  public UUID getUUID()                   { return uuid; }
  public boolean getActive()              { return isActive; }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  public void setStartDateTime(LocalDateTime startDateTime) {
    this.startDateTime = startDateTime;
  }

  public void setEndDateTime(LocalDateTime endDateTime) {
    this.endDateTime = endDateTime;
  }

  public void setActive(boolean isActive) {
    this.isActive = isActive;
  }

  public void setUUID(UUID uuid) {
    this.uuid = uuid;
  }

  public String createTable() {
    String sql = " CREATE TABLE IF NOT EXISTS Appointments ("
	    + "UUID longvarchar,"
	    + "Subject longvarchar,"
	    + "startDateTime bigint,"
	    + "endDateTime bigint,"
	    + "isActive boolean)";
    return sql;
  }

  public String toSQLInsert() {
    long start = Util.toEpoch(startDateTime);
    long end   = Util.toEpoch(endDateTime);
    
    String sql = "INSERT INTO Appointments VALUES('" + uuid.toString() + "', '" + subject + "', '" + start + "', '" + end + "', '" + isActive + "')";

    return sql;
  }

  public String getByUUID() {
    String sql = "SELECT * FROM Appointments WHERE UUID = '" + uuid.toString() + "' and isActive = true";
    return sql;
  }

  public String getBySubject () {
    String sql = "SELECT * FROM Appointments WHERE subject = '" + subject + "' and isActive = true";
    return sql;  
  }

  public String getByPeriod () {
    long start = Util.toEpoch(startDateTime);
    long end   = Util.toEpoch(endDateTime);
    String sql = "SELECT * FROM Appointments WHERE startDateTime >= " + start + " and endDateTime <= " + end + " and isActive= true";
    return sql;
  }

  public String updateActive() {
    String sql = "UPDATE Appointments SET isActive = " + isActive + " WHERE UUID = '" + uuid.toString() + "'";
    return sql;
  }

  public String updateSubject() {
    String sql = "UPDATE Appointments SET subject = '" + subject + "' WHERE UUID = '" + uuid.toString() + "'";
    return sql;
  }

  public String updateStartDateTime() {
    long start = Util.toEpoch(startDateTime);
    String sql = "UPDATE Appointments SET startDateTime = '" + start + "' WHERE UUID = '" + uuid.toString() + "'";
    return sql;
  }

  public String updateEndDateTime() {
    long end = Util.toEpoch(endDateTime);
    String sql = "UPDATE Appointments SET startDateTime = '" + end + "' WHERE UUID = '" + uuid.toString() + "'";
    return sql;
  }

  public String deleteByUUID() {
    String sql = "DELETE FROM Appointments WHERE UUID = '" + uuid.toString() + "' and isActive = false";
    return sql;
  }

  public String deleteBySubject() {
    String sql = "DELETE FROM Appointments WHERE subject = '" + subject + "' and isActive = false";
    return sql;
  }

  public String deleteByPeriod() {
    long start = Util.toEpoch(startDateTime);
    long end = Util.toEpoch(endDateTime);
    String sql = "DELETE FROM Appointments WHERE startDateTime >= " + start + " and endDateTime <= " + end + " and isActive = false";
    return sql;
  }

  public  HashSet<Appointment> returnItems (ResultSet rs) throws SQLException {
    HashSet<Appointment> appointments = new HashSet<Appointment>();

    while (rs.next()) {
      String mySubject = rs.getString("subject");
      LocalDateTime startDateTime = Util.toLocalDateTime(rs.getLong("startDateTime"));
      LocalDateTime endDateTime = Util.toLocalDateTime(rs.getLong("endDateTime"));
      Appointment appointment = new Appointment(mySubject, startDateTime, endDateTime);
      appointment.setUUID(UUID.fromString(rs.getString("UUID")));
      appointments.add(appointment);
    }
    return appointments;
  }
}
