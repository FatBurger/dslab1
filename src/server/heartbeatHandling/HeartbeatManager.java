package server.heartbeatHandling;

import java.io.IOException;
import java.net.DatagramSocket;
import java.util.Timer;
import java.util.TimerTask;

import common.InitFailedException;

public class HeartbeatManager
{
   /**
    * The UDP socket used to send alive messages.
    */
   private DatagramSocket localSocket;
   
   /**
    * The timer that will execute sending of the alive packets.
    */
   private final Timer aliveTimer = new Timer();
   
   /**
    * Initializes a new heartbeat manager.
    * 
    * @param proxyhostName Target hostname.
    * @param proxyUdpPort Target UDP port of the proxy.
    * @param alivePeriod Period for sending alive packets
    * 
    * @throws InitFailedException Gets thrown when no UDP port for sending heartbeats
    *                             is available.
    */
   public HeartbeatManager(String proxyhostName, int proxyUdpPort, int alivePeriod, int localTcpPort) throws InitFailedException
   {
      try
      {
         // initialize the local UDP socket
         localSocket = new DatagramSocket();
      }
      catch (IOException e)
      {
         System.out.println("Could not get UDP port for sending alive Messages!");
         StopAliveMessages();
         throw new InitFailedException();
      }
      
      // start sending alive packets check timer for fileServers
      TimerTask task = new AliveMessageTimerTask(localSocket, proxyhostName, proxyUdpPort, localTcpPort);
      aliveTimer.scheduleAtFixedRate(task, 0, alivePeriod);

      System.out.println("Started sending alive packets to target: " + proxyhostName + ":" + proxyUdpPort);
   }
   
   /**
    * Stops sending alive messages.
    * Closes the local UDP socket.
    */
   public void StopAliveMessages()
   {
      aliveTimer.cancel();
      localSocket.close();
      System.out.println("Stopped sending alive packets!");
   }
}
