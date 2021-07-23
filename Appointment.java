import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

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

}
