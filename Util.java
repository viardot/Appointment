import java.time.ZoneId;
import java.time.Instant;
import java.time.LocalDateTime;

import javax.crypto.Cipher;
import javax.crypto.SealedObject;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.BadPaddingException;

import java.security.*;
import java.security.cert.CertificateException;

import java.nio.channels.SocketChannel;
import java.nio.ByteBuffer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.io.InputStream;

import java.util.Set;

public class Util {

  private static final String AES_CIPHER_ALG = "AES/CBC/PKCS5PADDING";

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
    long start = toEpoch(appointment.getStartDateTime());
    long end   = toEpoch(appointment.getEndDateTime());
	
    String sql = "INSERT INTO Appointments VALUES('" + appointment.getSubject() + "', '" + start + "', '" + end + "')";
	
    return sql;  
  }

  public static <T extends Serializable> void writeMessageToSocket(SocketChannel socket, Message<T> message) {

    try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
         ObjectOutputStream oos = new ObjectOutputStream(baos)) {
      
      oos.writeObject(message);
      oos.flush();
      ByteBuffer buffer = ByteBuffer.wrap(baos.toByteArray());
      
      socket.write(buffer);
      
    } catch (IOException e) {
       e.printStackTrace();
    }
  }

  public static <T extends Serializable> Message readMessageFromSocket(SocketChannel socket) {
    
    byte[] bytes = readSocket(socket);
      
    try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
         ObjectInputStream ois = new ObjectInputStream(bais)) {

      Message<T> message = (Message<T>)ois.readObject();
      return message;

    } catch (IOException | ClassNotFoundException e) {
      e.printStackTrace();
    }
    return null;
  }

  private static byte[] readSocket (SocketChannel socket) {

    try {
      Integer read, i;
      ByteBuffer buffer = ByteBuffer.allocate(64);
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      
      while ((read = socket.read(buffer)) > 0) {
        byte[] byteArray = new byte[buffer.limit()];
        i = -1;
        buffer.flip();
        while (buffer.hasRemaining()) {
          byteArray[++i] = buffer.get();
        }
        baos.write(byteArray);
        buffer.clear();
      }

      return baos.toByteArray();
    
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }
  
  public static <T extends Serializable> SealedObject SealObject(T t, byte[]  secretKey, byte[] initializationVector) {
    try {
      Cipher encryptCipher = Cipher.getInstance(AES_CIPHER_ALG);
      IvParameterSpec ivParameterSpec = new IvParameterSpec(initializationVector);
      encryptCipher.init(Cipher.ENCRYPT_MODE, decryptSecretKey(secretKey), ivParameterSpec);
      return new SealedObject(t, encryptCipher);
    } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException | InvalidAlgorithmParameterException | IllegalBlockSizeException | IOException  e) {
      System.err.println("Error Sealing object");
      e.printStackTrace();
    }
    return null;
  }

  public static <T extends Serializable> T UnSealObject(SealedObject sealedObject, byte[] secretKey, byte[] initializationVector) {
    try {
      Cipher decriptCipher = Cipher.getInstance(AES_CIPHER_ALG);
      IvParameterSpec ivParameterSpec = new IvParameterSpec(initializationVector);
      decriptCipher.init(Cipher.DECRYPT_MODE, decryptSecretKey(secretKey), ivParameterSpec);
      return (T)sealedObject.getObject(decriptCipher);
    } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException | ClassNotFoundException | IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  private static KeyPair getKeyPair (String keyStorePath, String keyAlias, String keyStoreType, char[] keyStorePassword, char[] keyEntryPassword) {
    //keytool -genkeypair -alias mykey -storepass password1 -keypass password1 -keyalg RSA -keystore keystore.ks -deststoretype pkcs12
    try {
      InputStream ins = Util.class.getResourceAsStream(keyStorePath);
      KeyStore keyStore = KeyStore.getInstance(keyStoreType);
      keyStore.load(ins, "password1".toCharArray());
      KeyStore.PasswordProtection keyPassword = new KeyStore.PasswordProtection("password1".toCharArray());
      KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry)keyStore.getEntry(keyAlias, keyPassword);
      java.security.cert.Certificate cert = keyStore.getCertificate(keyAlias);
      PublicKey publicKey = cert.getPublicKey();
      PrivateKey privateKey = privateKeyEntry.getPrivateKey();
      
      return new KeyPair(publicKey, privateKey);
    
    } catch (CertificateException | IOException | KeyStoreException | NoSuchAlgorithmException | UnrecoverableEntryException e) {
      e.printStackTrace();
    }
    System.err.println("Cannot retrieve key pair from keystore.");
    return null;
  }

  private static SecretKey createSecretKey() {
    SecureRandom secureRandom = new SecureRandom();
    try {
      KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
      keyGenerator.init(256, secureRandom);
      SecretKey secretKey= keyGenerator.generateKey();
      return secretKey;

    } catch (NoSuchAlgorithmException e) {
      System.err.println("Error creating a secret key");
      e.printStackTrace();
    }
    return null;
  }

  public static byte[] createInitializationVector () {
    byte[] initializationVector = new byte[16];
    SecureRandom secureRandom = new SecureRandom();
    secureRandom.nextBytes(initializationVector);
    return initializationVector;
  }

  public static byte[] encryptSecretKey() {
    SecretKey secretKey = createSecretKey();
    KeyPair keyPair = getKeyPair("resources/keystore.jks", "mykey", "pkcs12", "password1".toCharArray(), "password1".toCharArray());
    try {
      Cipher encryptCipher = Cipher.getInstance("RSA");
      encryptCipher.init(Cipher.ENCRYPT_MODE, keyPair.getPublic());
      byte[] cipherKey = encryptCipher.doFinal(secretKey.getEncoded());
      return cipherKey;
    } catch (NoSuchAlgorithmException | IllegalBlockSizeException | NoSuchPaddingException | InvalidKeyException | BadPaddingException e) {
      System.err.println("Error encypting Secret Key");
      e.printStackTrace();
    }
    return null;
  }
  
  public static SecretKey decryptSecretKey(byte[] secretKey) {
    KeyPair keyPair = getKeyPair("resources/keystore.jks", "mykey", "pkcs12", "password1".toCharArray(), "password1".toCharArray());
    try {
      Cipher decryptCipher = Cipher.getInstance("RSA");
      decryptCipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
      byte[] sk = decryptCipher.doFinal(secretKey);
      SecretKeySpec plainSecretKey = new SecretKeySpec(sk, 0, sk.length, "AES" );
      return plainSecretKey;
    } catch (NoSuchAlgorithmException | InvalidKeyException | IllegalBlockSizeException | NoSuchPaddingException | BadPaddingException e) {
      e.printStackTrace();
    }
    return null;
  }
}
