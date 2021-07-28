import java.io.Serializable;
import java.util.UUID;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.HashSet;

public class Appointment_Assignment extends Item implements Serializable {

  private static final long serialVersionUID =1L;
  private UUID uuid = UUID.randomUUID();
  private String AssignmentUUID;
  private String AppointmentUUID;
  private boolean isActive = true;
  
  public Appointment_Assignment () {
    //Do Nothing
  }

  public Appointment_Assignment (String AppointmentUUID, String AssignmentUUID) {
    this.AppointmentUUID = AppointmentUUID;
    this.AssignmentUUID = AssignmentUUID;
  }

  public UUID getUUID() { return uuid; }
  public String getAppointmentUUID() { return AppointmentUUID; }
  public String getAssignmentUUID() { return AssignmentUUID; }
  public boolean getActive() { return isActive; }

  public void setUUID (UUID uuid) {
    this.uuid = uuid;
  }

  public void setAppointmentUUID (String AppointmentUUID) {
    this.AppointmentUUID = AppointmentUUID;
  }

  public void setAssignmentUUID (String AssignmentUUID) {
    this.AssignmentUUID = AssignmentUUID;
  } 

  public void setActive (boolean isActive) {
    this.isActive = isActive;
  }

  public String getAppointmentByAssignmentUUID() {
    String sql = "SELECT * FROM Appointments WHERE UUID = ("
	    + "SELECT TOP 1 AppointmentUUID "
	    + "FROM Appointment_Assignment "
	    + "WHERE AssignmentUUID = '" + AssignmentUUID + "' and isActive = true)";
    return sql;  
  }

  public String getAssignmentByAppointmentUUID() {
    String sql = "SELECT * FROM Assignments WHERE UUID = ("
	    + "SELECT TOP 1 AssignmentUUID " 
	    + "FROM Appointment_Assignment "
	    + "WHERE AppointmentUUID = '" + AppointmentUUID + "' and isActive = true)";
    return sql;  
  }
  
  public String createTable () {
    String sql = " CREATE TABLE IF NOT EXISTS Appointment_Assignment ("
	    + "UUID longvarchar,"
	    + "AppointmentUUID longvarchar,"
	    + "AssignmentUUID longvarchar,"
	    + "isActive boolean)";
     return sql;
  }

  public String toSQLInsert() {
    String sql = "INSERT INTO Appointment_Assignment VALUES('" + uuid.toString() + "', '" + AppointmentUUID + "', '" + AssignmentUUID + "', " + isActive +")";
    return sql;
  }

  public String getByUUID () {
    String sql = "SELECT * FROM Appointment_Assignment WHERE UUID = '" + uuid.toString() + "' and isActive = true";
    return sql;
  }

  public <T> HashSet<T> returnItems(ResultSet rs) throws SQLException {
    HashSet<Appointment_Assignment> relations = new HashSet<>();
    
    while (rs.next()) {
      String AppointmentUUID = rs.getString("AppointmentUUID");
      String AssignmentUUID = rs.getString("AssignmentUUID");
      Appointment_Assignment relation= new Appointment_Assignment(AppointmentUUID, AssignmentUUID);
      relation.setUUID(UUID.fromString(rs.getString("UUID")));
      relations.add(relation);
    }

    return (HashSet<T>)relations;
  }
}
