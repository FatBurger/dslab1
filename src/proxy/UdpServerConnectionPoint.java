package proxy;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import common.InitFailedException;

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
    *           
    * @throws InitFailedException Exception that gets thrown when the
    *                             UDP connection point cannot be established.
    */
   public UdpServerConnectionPoint(int port) throws InitFailedException
   {
      try
      {
         serverSocket = new DatagramSocket(port);
      }
      catch (IOException e)
      {
         System.out.println("Could not get I/O on UDP port " + port + "!");
         StopListening();
         throw new InitFailedException();
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
