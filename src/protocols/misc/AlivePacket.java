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
    * Current load of the originator.
    */
   private final int load;
   
   /**
    * Creates a new alive packet instance.
    * 
    * @param address Textual IP representation of the messages originator.
    * @param tcpPort TCP port on which the originator is listening.
    * @param load Current load of the originator.
    */
   public AlivePacket(String address, int tcpPort, int load)
   {
      this.address = address;
      this.tcpPort = tcpPort;
      this.load = load;
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
    * Current load of the originator.
    */
   public int getLoad()
   {
      return load;
   }
}
