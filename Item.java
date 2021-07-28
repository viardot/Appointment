import java.util.UUID;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;

public abstract class Item  {
  
  abstract public UUID getUUID();
  abstract public void setUUID (UUID uuid);
  abstract public boolean getActive();
  abstract public void setActive(boolean isActive);
  abstract public String createTable();
  abstract public String toSQLInsert();
  abstract public String getByUUID();
  abstract public <T> HashSet<T> returnItems(ResultSet rs) throws SQLException;
}
