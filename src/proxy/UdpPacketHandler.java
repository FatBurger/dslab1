package proxy;

import java.net.DatagramPacket;

import protocols.AliveProtocol;
import protocols.misc.AlivePacket;
import proxy.serverHandling.ServerData;
import proxy.serverHandling.ServerManager;

/**
 * Class that handles incoming UDP packets.
 * 
 * @author RaphM
 */
public class UdpPacketHandler implements Runnable
{
   /**
    * Reference to the received datagram packet.
    */
   private final DatagramPacket packet;

   /**
    * Protocol used to process the incoming packet.
    */
   private final AliveProtocol protocol = new AliveProtocol();

   /**
    * Server manager reference.
    */
   private final ServerManager serverManager;

   /**
    * Initializes a new UdpPacketHandler.
    * 
    * @param incomingPacket
    *           The incoming datagram packet to handle.
    * @param serverManager
    *           Server manager reference.
    */
   public UdpPacketHandler(DatagramPacket incomingPacket,
            ServerManager serverManager)
   {
      this.packet = incomingPacket;
      this.serverManager = serverManager;
   }

   /**
    * Executed as a thread.
    */
   public void run()
   {
      // use protocol to extract packet data
      AlivePacket convertedPacket = protocol.ExtractPacketData(packet);

      // get the unique identifier of the packets originator
      String serverIdentifier = convertedPacket.getServerIdentifier();

      ServerData serverData = serverManager.getServerById(serverIdentifier);

      if (serverData == null)
      {
         // create server data object with the received data
         serverData = new ServerData(convertedPacket.getAddress(), convertedPacket.getTcpPort());
         
         // add the new data object
         serverManager.addServer(serverData);
      }
      else
      {
         // update the existing object with the new load and update the timestamp
         serverData.renewActivityTimestamp();
      }
   }

}
