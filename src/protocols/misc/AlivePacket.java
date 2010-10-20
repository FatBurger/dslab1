package protocols.misc;

/**
 * Contains data of an alive packet.
 * 
 * @author RaphM
 */
public class AlivePacket
{
   /**
    * Textual IP representation of the messages originator.
    */
   private final String address;
   
   /**
    * TCP port on which the originator is listening.
    */
   private final int tcpPort;
   
   /**
    * Creates a new alive packet instance.
    * 
    * @param address Textual IP representation of the messages originator.
    * @param tcpPort TCP port on which the originator is listening.
    */
   public AlivePacket(String address, int tcpPort)
   {
      this.address = address;
      this.tcpPort = tcpPort;
   }
   
   /**
    * Textual IP representation of the messages originator.
    */
   public String getAddress()
   {
      return address;
   }
   
   /**
    * TCP port on which the originator is listening.
    */
   public int getTcpPort()
   {
      return tcpPort;
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
