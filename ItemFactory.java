public class ItemFactory {

  public static Item getItem(String itemType) {
    if (itemType.equals("Appointment")) {
      return new Appointment();
    } else if (itemType.equals("Assignment")) {
      return new Assignment();
    } else if (itemType.equals("Appointment_Assignment")) {
      return new Appointment_Assignment();
    }
    return null;
  }

  public static Item castItem(Message message) {
    if(message.getMessageObjectType().equals("Appointment")) {
      return message.<Appointment>getMessageObject();
    } else if (message.getMessageObjectType().equals("Assignment")) {
      return message.<Assignment>getMessageObject();
    } else if (message.getMessageObjectType().equals("Appointment_Assignment")) {
      return message.<Appointment_Assignment>getMessageObject();
    }
    return null;
  }
}
