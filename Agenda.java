import java.time.LocalDateTime;
import java.time.DateTimeException;

import java.util.Set;
import java.util.HashSet;
import java.util.UUID;

public class Agenda {
  
  //for test
  private static String testAppointmentUUID;
  private static String testAssignmentUUID;
  //

  public static void main (String[] args) {

    if (args.length < 2) {
      usageMessage();
    }

    switch (args[0]) {
      case "-c":
        switch (args[1]) {
	  case "Appointment":
            if(args.length != 5) usageMessage();
            setAppointment(args[1], args[2], args[3], args[4]);
	    break;
	  case "Assignment":
	    if (args.length != 4) usageMessage();
	    setAssignment(args[1], args[2], args[3]);
	    break;
	  case "Appointment_Assignment":
	    if (args.length != 4) usageMessage();
	    setAppointment_Assignment(args[1], args[2], args[3]);
	  default:
	    usageMessage();
	}
	break;
    
      case "-r": 
	switch (args[1]) {
          case "Appointment":
	    switch (args.length) {
              case  2:
	        getFirstSlot(args[1]);
		break;
	      case  4:
                if (args[2].equals("-s")) {
		  getAppointmentsBySubject(args[1], args[3]);
		} else {
		  usageMessage();
		}
		break;
	      case 5:
                if (args[2].equals("-p")) {
                 getAppointmentsByPeriod(args[1], args[3], args[4]);
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
	      getNextAssignment(args[1]);	  
	    } else {
	      switch (args[2]) {
                case "-s":
                  getAssignmentsBySubject(args[1], args[3]);
	          break;
	        case "-d":
		  getAssignmentsByDueDateTime(args[1], args[3]);
	  	  break;
	        default:
		  usageMessage();
	      }
	    }
	    break;
	  case "Appointment_Assignment":
	    if (args.length !=3) usageMessage();
	    switch (args[2]) { 
              case "-A":
		System.out.println("not implemented yet");
                break;
	      case "-a":
		System.out.println("not implemented yet");
		break;
	      default:
		usageMessage();
	    }
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
     
     case "-t":
        setAppointment("Appointment", "testAppointment", "1970-01-01T01:00", "1970-01-01T02:00");
	setAssignment("Assignment", "testAssignment", "1970-01-01T00:00");
	getAppointmentsBySubject("Appointment", "testAppointment");
	getAssignmentsBySubject("Assignment", "testAssignment");
	setAppointment_Assignment("Appointment_Assignment", testAppointmentUUID, testAssignmentUUID);
	getAppointmentByAssignmentUUID("Appointment_Assignment", testAssignmentUUID);
	getAssignmentByAppointmentUUID("Appointment_Assignment", testAppointmentUUID);
	break;
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
    System.out.println("  -c Appointment_Assignment <AppUUID> <AssUUID>      \t create relation between Appointment and Assignment");
    System.out.println("  -r Appointment_Assignment -A <AppUUID>             \t search by Appointment UUID");
    System.out.println("  -r Appointment_Assignment -a <AssUUID>             \t search by Assignment UUID\n");
    System.exit(0);
  }

  private static void getFirstSlot(String messageObjectType) {

    Appointment request = new Appointment(null, null, null);
    Message message = new Message(messageObjectType, "getFirstSlot", request);
    message = ConnectToRemoteHost.getData(message);
    
    if (message.getCode() == 100) {
      HashSet<Appointment> appointments = message.getMessageObjects();
      for(Appointment a : appointments) {
        System.out.println(a.getSubject());
        System.out.println(a.getStartDateTime());
        System.out.println(a.getEndDateTime() + "\n");
      }
    } else {
      System.err.println(message.getMessage() + "\nStatus code " + message.getCode());
    }
  }

  public static void getAppointmentsBySubject(String messageObjectType, String subject) {
  	  
    Appointment request = new Appointment(subject, null, null);
    Message message = new Message(messageObjectType, "getAppointmentsBySubject", request);
    message = ConnectToRemoteHost.getData(message);
    
    if (message.getCode() == 100) {
      HashSet<Appointment> appointments = message.getMessageObjects();
      for(Appointment a : appointments) {
	//for test
	testAppointmentUUID = a.getUUID().toString();
	//
        System.out.println(a.getUUID().toString());
	System.out.println(a.getSubject());
        System.out.println(a.getStartDateTime());
        System.out.println(a.getEndDateTime() + "\n");
      }
    } else {
      System.err.println(message.getMessage() + "\nStatus code " + message.getCode());
    }
  }

  public static void getAppointmentsByPeriod(String messageObjectType, String start, String end) {

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
      Message message = new Message(messageObjectType, "getAppointmentsByPeriod", request);
      message = ConnectToRemoteHost.getData(message);

      if (message.getCode() ==  100) {
        HashSet<Appointment> appointments = message.getMessageObjects();
        for(Appointment a : appointments) {
          System.out.println(a.getUUID().toString());
	  System.out.println(a.getSubject());
	  System.out.println(a.getStartDateTime());
	  System.out.println(a.getEndDateTime() + "\n");
        }
      } else { 
        System.err.println(message.getMessage() + "\nStatus code " + message.getCode());
      }
    } catch (DateTimeException e) {
      e.printStackTrace();
    }
  }

  private static void getAppointmentByUUID(String messageObjectType, String uuid) {
      Appointment request = new Appointment();
      request.setUUID(UUID.fromString(uuid));
      Message message = new Message(messageObjectType, "getAppointmentByUUID", request);
      message = ConnectToRemoteHost.getData(message);

      if (message.getCode() ==  100) {
        HashSet<Appointment> appointments = message.getMessageObjects();
        for(Appointment a : appointments) {
          System.out.println(a.getUUID().toString());
	  System.out.println(a.getSubject());
	  System.out.println(a.getStartDateTime());
	  System.out.println(a.getEndDateTime() + "\n");
        }
      } else { 
        System.err.println(message.getMessage() + "\nStatus code " + message.getCode());
      }
  }

  private static void setAppointment(String messageObjectType, String subject, String start, String end) {
  
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
      Message message = new Message(messageObjectType, "setAppointment", request); 
      message = ConnectToRemoteHost.getData(message);
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

  public static void getNextAssignment(String messageObjectType) {
    Assignment request = new Assignment(null, null);
    Message message = new Message(messageObjectType, "getNextAssignment", request);
    message = ConnectToRemoteHost.getData(message);
    
    if (message.getCode() == 100) {
      HashSet<Assignment> assignments = message.getMessageObjects();
      for(Assignment a : assignments) {
        System.out.println(a.getSubject());
        System.out.println(a.getDueDateTime() + "\n");
      }
    } else {
      System.err.println(message.getMessage() + "\nStatus code " + message.getCode());
    }
  }

  public static void getAssignmentsBySubject(String messageObjectType, String subject) {
    Assignment request = new Assignment(subject, null);
    Message message = new Message(messageObjectType, "getAssignmentsBySubject", request);
    message = ConnectToRemoteHost.getData(message);
    if (message.getCode() == 100) {
      HashSet<Assignment> assignments = message.getMessageObjects();
      for(Assignment a : assignments) {
        //for test
	testAssignmentUUID = a.getUUID().toString();
	//
        System.out.println(a.getUUID().toString());
	System.out.println(a.getSubject());
        System.out.println(a.getDueDateTime() + "\n");
      }
    } else {
      System.err.println(message.getMessage() + "\nStatus code " + message.getCode());
    }
  }

  public static void getAssignmentsByDueDateTime (String messageObjectType, String due) {
    
    LocalDateTime dueDateTime = null;
    try {
      dueDateTime = LocalDateTime.parse(due);
      Assignment request = new Assignment(null, dueDateTime);
      Message message = new Message(messageObjectType, "getAssignmentsByDueDateTime", request);
      message = ConnectToRemoteHost.getData(message);
      if (message.getCode() == 100) {
        HashSet<Assignment> assignments = message.getMessageObjects();
        for(Assignment a : assignments) {
	  System.out.println(a.getUUID().toString());
          System.out.println(a.getSubject());
          System.out.println(a.getDueDateTime() + "\n");
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

  private static void getAssignmentByUUID (String messageObjectType, String uuid) {
      Assignment request = new Assignment();
      request.setUUID(UUID.fromString(uuid));
      Message message = new Message(messageObjectType, "getAssignmentByUUID", request);
      message = ConnectToRemoteHost.getData(message);

      if (message.getCode() ==  100) {
        HashSet<Assignment> assignment = message.getMessageObjects();
        for(Assignment a : assignment) {
          System.out.println(a.getUUID().toString());
	  System.out.println(a.getSubject());
	  System.out.println(a.getDueDateTime() + "\n" );
        }
      } else { 
        System.err.println(message.getMessage() + "\nStatus code " + message.getCode());
      }

  }

  private static void setAssignment (String messageObjectType, String subject, String due) {
  
    LocalDateTime dueDateTime = null;
    try {
      dueDateTime = LocalDateTime.parse(due);
      Assignment request = new Assignment(subject, dueDateTime);
      System.out.println(request.getUUID().toString());
      Message message = new Message(messageObjectType, "setAssignment", request);
      message = ConnectToRemoteHost.getData(message);
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
 
  private static void getAssignmentByAppointmentUUID(String messageObjectType, String appointmentUUID) {
  
    Appointment_Assignment request = new Appointment_Assignment();
    request.setAppointmentUUID(appointmentUUID);
    Message message = new Message(messageObjectType, "getAssignmentByAppointmentUUID", request);
    message = ConnectToRemoteHost.getData(message);
    if (message.getCode() == 100) {
        HashSet<Assignment> set = message.getMessageObjects();
        for(Assignment a : set) {
          System.out.println(a.getUUID().toString());
	  System.out.println(a.getSubject());
	  System.out.println(a.getDueDateTime() + "\n");
	}
    } else {
       System.err.println(message.getMessage() + "\nStatus code " + message.getCode());
    }
  }

  private static void getAppointmentByAssignmentUUID(String messageObjectType, String assignmentUUID) {
  
    Appointment_Assignment request = new Appointment_Assignment();
    request.setAssignmentUUID(assignmentUUID);
    Message message = new Message(messageObjectType, "getAppointmentByAssignmentUUID", request);
    message = ConnectToRemoteHost.getData(message);
    if (message.getCode() == 100) {
        HashSet<Appointment> set = message.getMessageObjects();
        for(Appointment a : set) {
          System.out.println(a.getUUID().toString());
	  System.out.println(a.getSubject());
	  System.out.println(a.getStartDateTime());
	  System.out.println(a.getEndDateTime() + "\n");
        }
    } else {
       System.err.println(message.getMessage() + "\nStatus code " + message.getCode());
    }
  }

  private static void setAppointment_Assignment(String messageObjectType, String appointmentUUID, String assignmentUUID) {
 
    Appointment_Assignment request = new Appointment_Assignment(appointmentUUID, assignmentUUID);
    System.out.println(request.getUUID());
    Message message = new Message(messageObjectType, "setAppointment_Assignment", request);
    message = ConnectToRemoteHost.getData(message);
    if (message.getCode() != 100) {
      System.err.println(message.getMessage() + "\nStatus code " + message.getCode());
    }
  }
}
