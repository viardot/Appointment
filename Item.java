import java.util.UUID;

public abstract class Item  {

  abstract public UUID getUUID();
  abstract public void setUUID (UUID uuid);
  abstract public boolean getActive();
  abstract public void setActive(boolean isActive);
  abstract public String createTable();
  abstract public String toSQLInsert();
}
