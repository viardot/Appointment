import java.time.ZoneId;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
	DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    long start = toEpoch(appointment.getStartDateTime());
    long end   = toEpoch(appointment.getEndDateTime());
	
	String sql = "INSERT INTO Appointments VALUES('" + appointment.getSubject() + "', '" + start + "', '" + end + "')";
	
	return sql;
	  
  }
}
