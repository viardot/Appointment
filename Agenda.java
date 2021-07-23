import java.time.LocalDateTime;
import java.time.DateTimeException;

import java.util.Set;
import java.util.HashSet;

public class Agenda {

  public static void main (String[] args) {

    if (args.length < 2) {
      usageMessage();
    }

    switch (args[0]) {
      case "-c":
        switch (args[1]) {
	  case "Appointment":
            if(args.length != 5) usageMessage();
            setAppointment(args[2], args[3], args[4]);
	    break;
	  case "Assignment":
	    if (args.length != 4) usageMessage();
	    setAssignment(args[2], args[3]);
	    break;
	  default:
	    usageMessage();
	}
	break;
    
      case "-r": 
	switch (args[1]) {
          case "Appointment":
	    switch (args.length) {
              case  2:
	        getFirstSlot();
		break;
	      case  4:
                if (args[2].equals("-s")) {
		  getAppointmentsBySubject(args[3]);
		} else {
		  usageMessage();
		}
		break;
	      case 5:
                if (args[2].equals("-p")) {
                 getAppointmentsByPeriod(args[3], args[4]);
		} else {
                  usageMessage();
		}
		break;
	      default:
	        usageMessage();
	    }
	    break;
	  case "Assignment":
	    if (args.length == 2) {
	      getNextAssignment();	  
	    } else {
	      switch (args[2]) {
                case "-s":
                  getAssignmentsBySubject(args[3]);
	          break;
	        case "-d":
		  getAssignmentsByDueDateTime(args[3]);
	  	  break;
	        default:
		  usageMessage();
	      }
	    }
	    break;
	  default:
	    usageMessage();
	}
	break;
	
      case "-u":
        System.out.println("not implemented yet");
        return;
        //break;
		
     case "-d":
        System.out.println("not implemented yet");
	return;
        //break;
      
     default:
	usageMessage();
    }
  }
  
  private static void usageMessage() {
    
    System.out.println(" Agenda [crud] [item] [option]\n");
    System.out.println("  -c create");
    System.out.println("  -r read");
    System.out.println("  -u update");
    System.out.println("  -d delete\n");
    System.out.println("  -c Appointment <subject> <start> <end>             \t create an appointment");
    System.out.println("  -r Appointment                                     \t returns the first available slot");
    System.out.println("  -r Appointment -s <subject>                        \t search by subject");
    System.out.println("  -r Appointment -p <start-date-time> <end-date-time>\t search by period\n");
    System.out.println("  -c Assignment <subject> <due-date-time>            \t create an assignment");
    System.out.println("  -r Assignment                                      \t returns the next assignment");
    System.out.println("  -r Assignment -s <subject>                         \t search by subject");
    System.out.println("  -r Assignment -d <due-date-time>                   \t search by due date time\n");
    System.exit(0);
  }

  public static void getFirstSlot() {

    Appointment request = new Appointment(null, null, null);
    Message<Appointment> message = new Message("getFirstSlot", request);
    message = ConnectToRemoteHost.<Appointment>getData(message);
    
    if (message.getCode() == 100) {
      HashSet<Appointment> appointments = message.getMessageObjects();
      for(Appointment a : appointments) {
        System.out.println(a.getSubject());
        System.out.println(a.getStartDateTime());
        System.out.println(a.getEndDateTime());
      }
    } else {
      System.err.println(message.getMessage() + "\nStatus code " + message.getCode());
    }
  }

  public static void getAppointmentsBySubject(String subject) {
  	  
    Appointment request = new Appointment(subject, null, null);
    Message<Appointment> message = new Message("getAppointmentsBySubject", request);
    message = ConnectToRemoteHost.<Appointment>getData(message);
    
    if (message.getCode() == 100) {
      HashSet<Appointment> appointments = message.getMessageObjects();
      for(Appointment a : appointments) {
        System.out.println(a.getUUID().toString());
	System.out.println(a.getSubject());
        System.out.println(a.getStartDateTime());
        System.out.println(a.getEndDateTime());
      }
    } else {
      System.err.println(message.getMessage() + "\nStatus code " + message.getCode());
    }
  }
  
  public static void getAppointmentsByPeriod(String start, String end) {

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
      Message<Appointment> message = new Message("getAppointmentsByPeriod", request);
      message = ConnectToRemoteHost.<Appointment>getData(message);

      if (message.getCode() ==  100) {
        HashSet<Appointment> appointments = message.getMessageObjects();
        for(Appointment a : appointments) {
          System.out.println(a.getUUID().toString());
	  System.out.println(a.getSubject());
	  System.out.println(a.getStartDateTime());
	  System.out.println(a.getEndDateTime());
        }
      } else { 
        System.err.println(message.getMessage() + "\nStatus code " + message.getCode());
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
      System.out.println(request.getUUID().toString()); 
      Message<Appointment> message = new Message("setAppointment", request); 
      message = ConnectToRemoteHost.<Appointment>getData(message);
      if (message.getCode() != 100) {
        System.err.println(message.getMessage() + "\nStatus code " + message.getCode());
      }
    } catch (DateTimeException e) {
      System.err.println("Error processing date time input");
      System.err.println("Expected date time format: yyyy-mm-ddTHH:mm:ss");
      System.err.println("Example: Agenda -c Appointment Meeting 2021-01-15T08:00:00 2021-01-15T09:00:00");
      e.printStackTrace();
    }
  }

  public static void getNextAssignment() {
    Assignment request = new Assignment(null, null);
    Message<Assignment> message = new Message("getNextAssignment", request);
    message = ConnectToRemoteHost.<Assignment>getData(message);
    
    if (message.getCode() == 100) {
      HashSet<Assignment> assignments = message.getMessageObjects();
      for(Assignment a : assignments) {
        System.out.println(a.getSubject());
        System.out.println(a.getDueDateTime());
      }
    } else {
      System.err.println(message.getMessage() + "\nStatus code " + message.getCode());
    }
  }

  public static void getAssignmentsBySubject(String subject) {
    Assignment request = new Assignment(subject, null);
    Message<Assignment> message = new Message<Assignment>("getAssignmentsBySubject", request);
    message = ConnectToRemoteHost.<Assignment>getData(message);
    if (message.getCode() == 100) {
      HashSet<Assignment> assignments = message.getMessageObjects();
      for(Assignment a : assignments) {
        System.out.println(a.getUUID().toString());
	System.out.println(a.getSubject());
        System.out.println(a.getDueDateTime());
      }
    } else {
      System.err.println(message.getMessage() + "\nStatus code " + message.getCode());
    }
  }

  public static void getAssignmentsByDueDateTime(String due) {
    
    LocalDateTime dueDateTime = null;
    try {
      dueDateTime = LocalDateTime.parse(due);
      Assignment request = new Assignment(null, dueDateTime);
      Message<Assignment> message = new Message<Assignment>("getAssignmentsByDueDateTime", request);
      message = ConnectToRemoteHost.<Assignment>getData(message);
      if (message.getCode() == 100) {
        HashSet<Assignment> assignments = message.getMessageObjects();
        for(Assignment a : assignments) {
	  System.out.println(a.getUUID().toString());
          System.out.println(a.getSubject());
          System.out.println(a.getDueDateTime());
        }
      } else {
        System.err.println(message.getMessage() + "\nStatus code " + message.getCode());
      }
    } catch (DateTimeException e) {
      System.err.println("Error processing date time input");
      System.err.println("Expected date time format: yyyy-mm-ddTHH:mm:ss");
      System.err.println("Example: Agenda -r Assignment -d  2021-01-15T09:00:00");
      e.printStackTrace();
    }
  }

  public static void setAssignment(String subject, String due) {
  
    LocalDateTime dueDateTime = null;
    try {
      dueDateTime = LocalDateTime.parse(due);
      Assignment request = new Assignment(subject, dueDateTime);
      System.out.println(request.getUUID().toString());
      Message<Assignment> message = new Message("setAssignment", request);
      message = ConnectToRemoteHost.<Assignment>getData(message);
      if (message.getCode() != 100) {
        System.err.println(message.getMessage() + "\nStatus code " + message.getCode());
      }
    } catch (DateTimeException e) {
      System.err.println("Error processing date time input");
      System.err.println("Expected date time format: yyyy-mm-ddTHH:mm:ss");
      System.err.println("Example: Agenda -c Assignment \"Fill in pappers\" 2021-01-15T09:00:00");
      e.printStackTrace();
    }
  }
}
