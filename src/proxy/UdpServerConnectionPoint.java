package proxy;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import protocols.AliveProtocol;

/**
 * Encapsulates a UDP server socket.
 * 
 * @author RaphM
 */
public class UdpServerConnectionPoint
{
   /**
    * The UDP server socket
    */
   private DatagramSocket serverSocket;
   
   /**
    * The protocol used for incoming packets.
    */
   private final AliveProtocol protocol = new AliveProtocol();

   /**
    * Initializes this connection point
    * 
    * @param port
    *           UDP port to listen on.
    */
   public UdpServerConnectionPoint(int port)
   {
      try
      {
         serverSocket = new DatagramSocket(port);
      }
      catch (IOException e)
      {
         System.out.println("Could not get I/O on UDP port " + port + "!");
         StopListening();
         System.exit(1);
      }

      System.out.println("Listening on UDP port " + port + "!");
   }

   /**
    * Stops listening for new connections.
    */
   public void StopListening()
   {
      if (serverSocket != null && !serverSocket.isClosed())
      {
         serverSocket.close();

         System.out.println("Closed UDP server socket succesfully!");
      }
   }
   
   /**
    * Exposes the receive() method of the datagram socket.
    * 
    * @return The received packet content.
    * @throws IOException IO exception.
    */
   public DatagramPacket ReceiveAliveMessage() throws IOException
   {
      return protocol.ReceivePacket(serverSocket);
   }
}
