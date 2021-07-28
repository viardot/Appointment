import java.io.Serializable;
import java.util.UUID;
import java.util.HashSet;

import javax.crypto.SealedObject;
import javax.crypto.SecretKey;

public class Message implements Serializable {

  private static final long serialVersionUID = 4231495197757798964L;	
  private UUID uuid = UUID.randomUUID();
  private byte[] secretKey;
  private byte[] initializationVector; 
  private String messageObjectType;
  private String event;
  private String message;
  private Integer code;
  private SealedObject messageObject;
  private SealedObject messageObjects;

  public Message () {
    this.secretKey = Util.encryptSecretKey();
    this.initializationVector = Util.createInitializationVector();
  }

  public <T extends Serializable> Message (String messageObjectType, String event, T messageObject) {
    this.secretKey = Util.encryptSecretKey();
    this.initializationVector = Util.createInitializationVector();
    this.messageObjectType = messageObjectType;
    this.event = event;
    this.messageObject = Util.SealObject(messageObject, secretKey, initializationVector);
  }

  public <T extends Serializable> Message (String message, Integer code, HashSet<T> messageObjects) {
    this.secretKey = Util.encryptSecretKey();
    this.initializationVector = Util.createInitializationVector();
    this.message = message;
    this.code = code;
    this.messageObjects = Util.SealObject(messageObjects, secretKey, initializationVector);
  }

  public UUID getUUID()                 { return this.uuid; }
  public String getMessageObjectType()  { return this.messageObjectType; }
  public String getEvent()              { return this.event; }
  public String getMessage()            { return this.message; }
  public Integer getCode()              { return this.code; }
  public <T extends Serializable> T getMessageObject()           { return Util.UnSealObject(this.messageObject, secretKey, initializationVector); }
  public <T extends Serializable>  HashSet<T> getMessageObjects() { return Util.UnSealObject(this.messageObjects, secretKey, initializationVector); }

  public void setMessageObjectType(String objectType)      { this.messageObjectType = objectType; }
  public void setEvent(String event)                       { this.event = event; }
  public void setMessage(String message)                   { this.message = message; }
  public void setCode(Integer code)                        { this.code = code; }
  public <T extends Serializable> void setMessageObject(T messageObject)            { this.messageObject = Util.SealObject(messageObject, secretKey, initializationVector); }
  public <T extends Serializable> void setMessageObjects(HashSet<T> messageObjects) { this.messageObjects = Util.SealObject(messageObjects, secretKey, initializationVector); }

}
