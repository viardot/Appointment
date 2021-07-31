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
            setAppointment(args[1], "setItem", args[2], args[3], args[4]);
	    break;
	  case "Assignment":
	    if (args.length != 4) usageMessage();
	    setAssignment(args[1], "setItem", args[2], args[3]);
	    break;
	  case "Appointment_Assignment":
	    if (args.length != 4) usageMessage();
	    setAppointment_Assignment(args[1], "setItem", args[2], args[3]);
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
	if (args.length != 5) usageMessage();
	switch (args[1]) {
          case "Appointment":
	    switch (args[3]) {
	      case "-s":
		updateAppointmentSubject(args[1], "updateAppointmentSubject", args[2],  args[4]);
	        break;
	      case "-S":
		updateAppointmentStartDateTime(args[1],"updateAppointmentStartDateTime", args[2], args[4]);
		break;
              case "-T":
		updateAppointmentEndDateTime(args[1], "updateAppointmentEndDateTime", args[2], args[4]);
		break;
	      case "-a":
		updateActive(args[1], "updateActive", args[2], args[4]);
		break;
              default:
	        usageMessage();
	    }
            break;
          case "Assignment":
	    switch (args[3]) {
	      case "-s":
		updateAssignmentSubject(args[1], "updateAssignmentSubject", args[2], args[4]);
	        break;
	      case "-d":
		updateAssignmentDueDateTime(args[1], "updateAssignmentDueDateTime", args[2], args[4]);
		break;
	      case "-a":
		updateActive(args[1], args[2], "updateActive", args[4]);
		break;
              default:
	        usageMessage();
	    }
            break;
	  case "Appoinment_Assignment":
	    switch (args[3]) {
	      case "-a":
		updateActive(args[1], "updateActive", args[2], args[4]);
		break;
              default:
	        usageMessage();
	    }
            break;
          default:
            usageMessage();	    
	}
        break;
		
     case "-d":
	if (args.length < 4) usageMessage();
	switch (args[1]) {
	  case "Appointment":
	    switch (args[2]) {
	      case "-s":
                deleteAppointmentBySubject(args[1], "deleteAppointmentBySubject", args[3]);
	        break;
	      case "-p":
		if (args.length != 5) usageMessage();
		deleteAppointmentByPeriod(args[1], args[3], "deleteAppointmentByPeriod", args[4]);
		break;
	      case "-u":
		deleteByUUID(args[1], "deleteByUUID", args[3]);
		break;
	      default:
                usageMessage();
	    }
	    break;
	  case "Assignment":
	    switch (args[2]) {
	      case "-s":
                deleteAssignmentBySubject(args[1], "deleteAssignmentBySubject",args[3]);
	        break;
	      case "-d":
		deleteAssignmentByDueDateTime(args[1], "deleteAssignmentByDueDateTime", args[3]);
		break;
	      case "-u":
		deleteByUUID(args[1], "deleteByUUID", args[3]);
		break;
	      default:
                usageMessage();
	    }
	    break;
	  default:
	    usageMessage();
	}
        break;
     case "-t":
	
	System.out.print("Create Appointment with UUID ");
        setAppointment("Appointment", "setItem", "testAppointment", "1970-01-01T01:00", "1970-01-01T02:00");
	System.out.print("Create Assesment with UUID ");
	setAssignment("Assignment", "setItem", "testAssignment", "1970-01-01T00:00");
	System.out.println("Get Appointment by subject");
	getAppointmentsBySubject("Appointment", "testAppointment");
	System.out.println("Get Assignment by subject");
	getAssignmentsBySubject("Assignment", "testAssignment");
	System.out.println("Get Appointment by period");
	getAppointmentsByPeriod("Appointment", "1970-01-01T01:00", "1970-01-01T02:00");
	System.out.println("Get Assignment by due date time");
	getAssignmentsByDueDateTime("Assignment", "1970-01-01T00:00");
	System.out.print("Create relation between Appointment and Assingment with UUID ");
	setAppointment_Assignment("Appointment_Assignment", "setItem",  testAppointmentUUID, testAssignmentUUID);
	System.out.println("Get Appointment by related Assingment");
	getAppointmentByAssignmentUUID("Appointment_Assignment",  testAssignmentUUID);
	System.out.println("Get Assignment by releated Appointment");
	getAssignmentByAppointmentUUID("Appointment_Assignment", testAppointmentUUID);
	updateActive("Appointment", "updateActive", testAppointmentUUID, "false");
	updateActive("Assignment", "updateActive", testAssignmentUUID, "false");
	System.out.println("Delete Appointment and Assignment");
	deleteByUUID("Appointment", "deleteByUUID", testAppointmentUUID);
	deleteByUUID("Assignment", "deleteByUUID", testAssignmentUUID);
	getAppointmentsBySubject("Appointment", "testAppointment");
	getAssignmentsBySubject("Assignment", "testAssignment");
	
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
    System.out.println("  -r Appointment -p <start-date-time> <end-date-time>\t search by period");
    System.out.println("  -u Appointment <UUID> -s <subject>                 \t update Appointment\'s subject with UUID");
    System.out.println("  -u Appointment <UUID> -S <start-date-time>         \t update Appointment\'s start date time with UUID");
    System.out.println("  -u Appointment <UUID> -T <end-date-time>           \t update Appointment\'s end date time with UUID");
    System.out.println("  -u Appointment <UUID> -a <isActive true | false>   \t update Appointment\'s Active state with UUID");
    System.out.println("  -d Appointment -s <subject>                        \t delete all appointments with <subject>");
    System.out.println("  -d Appointment -p <start-date-time> <end-date-time>\t delete all appointments in period");
    System.out.println("  -d Appointment -u <UUID>                           \t delete all appointments with <UUID>\n");
    System.out.println("  -c Assignment <subject> <due-date-time>            \t create an assignment");
    System.out.println("  -r Assignment                                      \t returns the next assignment");
    System.out.println("  -r Assignment -s <subject>                         \t search by subject");
    System.out.println("  -r Assignment -d <due-date-time>                   \t search by due date time");
    System.out.println("  -u Assignment <UUID> -s <subject>                  \t update Appointment\'s subject with UUID");
    System.out.println("  -u Assignment <UUID> -d <due-date-time>            \t update Appointment\'s start date time with UUID");
    System.out.println("  -u Assignment <UUID> -a <isActive true | false>    \t update Appointment\'s Active state with UUID");
    System.out.println("  -d Assignment -s <subject>                         \t delete all assignments with <subject>");
    System.out.println("  -d Assignment -d <due-date-time>                   \t delete all assignments in period");
    System.out.println("  -d Assignment -u <UUID>                            \t delete all assignments with <UUID>\n");
    System.out.println("  -c Appointment_Assignment <AppUUID> <AssUUID>      \t create relation between appointment and assignment");
    System.out.println("  -r Appointment_Assignment -A <AppUUID>             \t search by appointment UUID");
    System.out.println("  -r Appointment_Assignment -a <AssUUID>             \t search by assignment UUID");
    System.out.println("  -u Appointment_Assignment <UUID> -a <true | false> \t update Appointment_Assignment Active state with UUID");
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

  private static void setAppointment(String messageObjectType, String method, String subject, String start, String end) {
  
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
      sendMessage(messageObjectType, method, request);
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

  private static void setAssignment (String messageObjectType, String method, String subject, String due) {
  
    LocalDateTime dueDateTime = null;
    try {
      dueDateTime = LocalDateTime.parse(due);
      Assignment request = new Assignment(subject, dueDateTime);
      System.out.println(request.getUUID().toString());
      sendMessage(messageObjectType, method, request);
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

  private static void setAppointment_Assignment(String messageObjectType, String method, String appointmentUUID, String assignmentUUID) {
 
    Appointment_Assignment request = new Appointment_Assignment(appointmentUUID, assignmentUUID);
    System.out.println(request.getUUID());
    sendMessage(messageObjectType, method, request);
  }

  private static void updateAppointmentSubject(String messageObjectType, String method, String uuid, String subject){
    Appointment request = new Appointment(subject, null, null);
    request.setUUID(UUID.fromString(uuid));
    sendMessage(messageObjectType, method, request);
  }
  private static void updateAppointmentStartDateTime(String messageObjectType, String method, String uuid, String start){
    Appointment request = new Appointment(null, LocalDateTime.parse(start), null);
    request.setUUID(UUID.fromString(uuid));
    sendMessage(messageObjectType, method, request);
  }
  private static void updateAppointmentEndDateTime(String messageObjectType, String method, String uuid, String end){
    Appointment request = new Appointment(null, null, LocalDateTime.parse(end));
    request.setUUID(UUID.fromString(uuid));
    sendMessage(messageObjectType, method, request);
  }
  private static void updateAssignmentSubject(String messageObjectType, String method, String uuid, String subject){
    Assignment request = new Assignment(subject, null);
    request.setUUID(UUID.fromString(uuid));
    sendMessage(messageObjectType, method, request);
  }
  private static void updateAssignmentDueDateTime(String messageObjectType, String method, String uuid, String due){
    Assignment request = new Assignment(null, LocalDateTime.parse(due));
    request.setUUID(UUID.fromString(uuid));
    sendMessage(messageObjectType, method, request);
  }
  private static void updateActive(String messageObjectType, String method, String uuid, String isActive){
    Item request = ItemFactory.getItem(messageObjectType);
    request.setUUID(UUID.fromString(uuid));
    if (isActive.equals("true")) {
      request.setActive(true);
    } else {
      request.setActive(false);
    }
    sendMessage(messageObjectType, method, request);
  }

  private static void deleteAppointmentBySubject(String messageObjectType, String method, String subject){
    Appointment request = new Appointment();
    request.setSubject(subject);
    sendMessage(messageObjectType, method, request);
  }

  private static void deleteAppointmentByPeriod(String messageObjectType, String method, String start, String end){
    Appointment request = new Appointment();
    request.setStartDateTime(LocalDateTime.parse(start));
    request.setEndDateTime(LocalDateTime.parse(end));
    sendMessage(messageObjectType, method, request);
  }

  private static void deleteByUUID(String messageObjectType, String method, String uuid) {
    Item request = ItemFactory.getItem(messageObjectType);
    request.setUUID(UUID.fromString(uuid));
    sendMessage(messageObjectType, method, request);
  }

  private static void deleteAssignmentBySubject(String messageObjectType, String method, String subject){
    Assignment request = new Assignment();
    request.setSubject(subject);
    sendMessage(messageObjectType, method, request);
  }

  private static void deleteAssignmentByDueDateTime(String messageObjectType, String method, String due){
    Assignment request = new Assignment();
    request.setDueDateTime(LocalDateTime.parse(due));
    sendMessage(messageObjectType, method, request);
  }

  private static void sendMessage(String messageObjectType, String method, Item request) {
    Message message = new Message(messageObjectType, method, request);
    message = ConnectToRemoteHost.getData(message);
    if (message.getCode() != 100) {
      System.err.println(message.getMessage() + "\nStatus code " + message.getCode());
    }
  }
 
}
