package proxy.serverHandling;

import java.util.Date;

/**
 * Stores data for servers.
 * 
 * @author RaphM
 */
public class ServerData
{
   /**
    * String representation of the servers address.
    */
   private final String address;
   
   /**
    * TCP port of the server.
    */
   private final int tcpPort;
   
   /**
    * Current load of the server.
    */
   private long load;
   
   /**
    * Date when this instance was created.
    */
   private Date lastActivityTimestamp;
   
   /**
    * Online status of the server.
    */
   private boolean online;
   
   /**
    * Initializes a new server data object.
    * 
    * @param address String representation of the servers address.
    * @param tcpPort TCP port of the server.
    * @param load Current load of the server.
    */
   public ServerData(String address, int tcpPort)
   {
      this.address = address;
      this.tcpPort = tcpPort;
      this.lastActivityTimestamp = new Date();
   }
   
   /**
    * String representation of the servers address.
    */
   public String getAddress()
   {
      return address;
   }
   
   /**
    * TCP port of the server.
    */
   public int getTcpPort()
   {
      return tcpPort;
   }
   
   /**
    * Adds a certain amount to load.
    * 
    * @param load the amount to add.
    */
   public void addLoad(long load)
   {
      this.load += load;
   }
   
   /**
    * Current load of the server.
    */
   public long getLoad()
   {
      return load;
   }
   
   /**
    * Online status of the server.
    */
   public boolean isOnline()
   {
      return online;
   }
   
   /**
    * Gets the timestamp of the create date of this instance.
    * 
    * @return Timestamp.
    */
   public Date getLastActivityTimestamp()
   {
      return lastActivityTimestamp;
   }
   
   /**
    * Sets this server into online status.
    */
   public void SetOnline()
   {
      online = true;
   }
   
   /**
    * Sets this server into offline status.
    */
   public void SetOffline()
   {
      online = false;
   }
   
   /**
    * Sets the load of this server.
    * 
    * @param newLoad The new load.
    */
   public void updateLoad(int newLoad)
   {
      load = newLoad;
   }
   
   /**
    * Updates the activity timestamp to the current date.
    */
   public void renewActivityTimestamp()
   {
      lastActivityTimestamp = new Date();
   }
   
   /**
    * Gets a unique server id (address + port)
    * 
    * @return Unique server id.
    */
   public String getServerIdentifier()
   {
      return address + ":" + tcpPort;
   }
}
