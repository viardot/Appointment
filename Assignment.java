import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

public class Assignment extends Item implements Serializable {

  private static final long serialVersionUID = -299403410729778598L;
  private UUID uuid = UUID.randomUUID();
  private LocalDateTime dueDateTime;
  private String subject;
  private boolean isActive = true;

  public Assignment () {
    // Do Nothing
  }

  public Assignment (String subject, LocalDateTime dueDateTime) {
    
    this.dueDateTime = dueDateTime;
    this.subject = subject;
  }

  public String getSubject()            { return subject; }
  public LocalDateTime getDueDateTime() { return dueDateTime; }
  public UUID getUUID()                 { return uuid; }
  public boolean getActive()            { return isActive; }

  public void setSubject (String subject) {
    this.subject = subject;
  }

  public void setDueDateTime (LocalDateTime dueDateTime) {
    this.dueDateTime = dueDateTime;
  }

  public void setActive (boolean isActive) {
    this.isActive = isActive;
  }

  public void setUUID (UUID uuid) {
    this.uuid = uuid;
  }

  public String createTable() {
    String sql = " CREATE TABLE IF NOT EXISTS Assignments ("
	    + "UUID longvarchar,"
	    + "Subject longvarchar,"
	    + "dueDateTime bigint,"
	    + "isActive boolean)";
    return sql;
  }

  public String toSQLInsert() {
    long due = Util.toEpoch(dueDateTime);

    String sql = "INSERT INTO Assignments VALUES('" + uuid.toString() + "', '" + subject + "', '" + due + "', '" + isActive + "')";
    return sql;
  }

}
