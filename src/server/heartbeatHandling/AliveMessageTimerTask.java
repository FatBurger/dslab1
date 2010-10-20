package server.heartbeatHandling;

import java.net.DatagramSocket;
import java.util.TimerTask;

import protocols.AliveProtocol;

/**
 * Sends alive messages.
 * 
 * @author RaphM
 */
public class AliveMessageTimerTask extends TimerTask
{
   /**
    * Protocol object for convenient communication.
    */
   private final AliveProtocol protocol = new AliveProtocol();
   
   /**
    * The outgoing socket used to send alive messages.
    */
   private final DatagramSocket outgoingSocket;
   
   /**
    * The target hostname.
    */
   private final String targetHostname;
   
   /**
    * The target udp port.
    */
   private final int targetUdpPort;
   
   /**
    * The local tcp port.
    */
   private final int localTcpPort;
   
   /**
    * Initializes a new AliveMessageTimerTask
    * 
    * @param outgoingSocket The outgoing socket to use.
    * @param targetHostname The target hostname.
    * @param targetUdpPort The target Udp Port.
    * @param localTcpPort The local tcp port.
    */
   public AliveMessageTimerTask(DatagramSocket outgoingSocket, String targetHostname, int targetUdpPort, int localTcpPort)
   {
      this.outgoingSocket = outgoingSocket;
      this.targetHostname = targetHostname;
      this.targetUdpPort = targetUdpPort;
      this.localTcpPort = localTcpPort;
   }
   
   /**
    * Executed as a thread.
    */
   public void run()
   {
      // send alive packet to target host
      protocol.SendAlivePacket(outgoingSocket, targetHostname, targetUdpPort, localTcpPort); 
   }
}
