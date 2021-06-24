import java.time.LocalDateTime;
import java.time.DateTimeException;

import java.util.Set;

public class Agenda {
  public static void main (String[] args) {
    
    switch (args.length) {
      case 0:
        getFirstSlot();
	    break;
    
      case 1: 
	    getAppointmentsBySubject(args[0]);
	    break;
	
	 case 2:
	    getApointmenstByPeriod(args[0], args[1]);
        break;
		
     case 3:
        setAppointment(args[0], args[1], args[2]);
        break;
      
     default:
	System.out.println("Invalid input. Usage: ");
	System.out.println("1) Agenda");
	System.out.println("2) Agenda subject");
	System.out.println("3) Agenda startDateTime endDateTime (not implemented yet)");
	System.out.println("4) Agenda subject startDateTime endDateTime");
	return;
    }
  }
  
  public static void  getFirstSlot(){

    Appointment request = new Appointment(null, null, null);
    Set<Appointment> appointments = ConnectToRemoteHost.getData(request);
    for(Appointment a : appointments) {
      System.out.println(a.getSubject());
      System.out.println(a.getStartDateTime());
      System.out.println(a.getEndDateTime());
    }
  }

  public static void getAppointmentsBySubject(String subject){
  	  
    Appointment request = new Appointment(subject, null, null);

    Set<Appointment> appointments = ConnectToRemoteHost.getData(request);
    for(Appointment a : appointments) {
      System.out.println(a.getSubject());
      System.out.println(a.getStartDateTime());
      System.out.println(a.getEndDateTime());
    }
  }
  
  public static void getApointmenstByPeriod(String start, String end) {

    LocalDateTime startDateTime = null;
    LocalDateTime endDateTime = null;
    try {
      startDateTime = LocalDateTime.parse(start);
      endDateTime = LocalDateTime.parse(end);
      if (startDateTime.isAfter(endDateTime)) {
        System.out.println("Start date/time can not be after end date/time.");
        System.exit(1);
      }
      Appointment request = new Appointment(null, startDateTime, endDateTime);
      Set<Appointment> appointments = ConnectToRemoteHost.getData(request);
      for(Appointment a : appointments) {
        System.out.println(a.getSubject());
	System.out.println(a.getStartDateTime());
	System.out.println(a.getEndDateTime());
      }
    } catch (DateTimeException e) {
      e.printStackTrace();
    }
  }

  public static void setAppointment(String subject, String start, String end) {
  
    LocalDateTime startDateTime = null;
    LocalDateTime endDateTime = null;
    try {
      //should check and or correct time format to 08:00:00 (trailing zero's) for precision.
      startDateTime = LocalDateTime.parse(start);
      endDateTime = LocalDateTime.parse(end);
	  if (startDateTime.isAfter(endDateTime)){
	    System.out.println("Start date/time can not be after end date/time.");
	    System.exit(1);
	  }
      Appointment request =  new Appointment(subject, startDateTime, endDateTime); 
      ConnectToRemoteHost.setData(request);
    } catch (DateTimeException e) {
      System.err.println("Error processing date time input");
      System.err.println("Expected date time format: yyyy-mm-ddTHH:mm:ss");
      System.err.println("Example: Agenda Meeting 2021-01-15T08:00:00 2021-01-15T09:00:00");
      e.printStackTrace();
    }
  }
}
