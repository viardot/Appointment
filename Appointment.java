import java.io.Serializable;
import java.time.LocalDateTime;

public class Appointment implements Serializable{

  private static final long serialVersionUID = 4484460051818968173L;
  private LocalDateTime startDateTime;
  private LocalDateTime endDateTime;
  private String subject;

  public Appointment () {
    //Do Nothing
  }

  public Appointment(String subject, LocalDateTime startDateTime, LocalDateTime endDateTime) {
    this.startDateTime  = startDateTime;
    this.endDateTime = endDateTime;
    this.subject = subject;
  }

  public String getSubject() {
    return subject;
  }

  public LocalDateTime getStartDateTime() {
    return startDateTime;
  }

  public LocalDateTime getEndDateTime() {
    return endDateTime;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  public void setStartDateTime(LocalDateTime startDateTime) {
    this.startDateTime = startDateTime;
  }

  public void setEndDateTime(LocalDateTime endDateTime) {
    this.endDateTime = endDateTime;
  }

}