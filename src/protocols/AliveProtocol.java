package protocols;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import protocols.misc.AlivePacket;

/**
 * Contains methods for convenient UDP communication between Fileservers and
 * proxy.
 * 
 * @author RaphM
 */
public class AliveProtocol
{
   /**
    * The size of our UDP packets.
    */
   private final int BUF_SIZE = 256;
   
   /**
    * Marker element for protocol information.
    */
   private final String PROTOCOL_PLAIN_MARKER = "!";
   
   /**
    * Marker element for the case that the plain marker element
    * is present in plain text. 
    */
   private final String PROTOCOL_ENCODE_MARKER = PROTOCOL_PLAIN_MARKER + "_";
   
   /**
    * Marker element for begin of the load section.
    */
   private final String LOAD_SEPARATOR = PROTOCOL_PLAIN_MARKER + "LOAD";

   /**
    * Sends an alive packet to target.
    * 
    * @param hostname
    *           Target hostname.
    * @param port
    *           Target port.
    */
   public void SendAlivePacket(String hostname, int port, int localPort, int localLoad)
   {
      try
      {  
         DatagramSocket localSocket = new DatagramSocket();
         
         // create the data content for the packet
         byte[] packetData = CreateProtocolMessage(localPort, localLoad);

         if (packetData.length > BUF_SIZE)
         {
            System.out
                     .println("Could not send outgoing alive packet - string representation of data is to long!");
         }
         else
         {
            try
            {
               InetAddress targetAddress = InetAddress.getByName(hostname);
               DatagramPacket packet = new DatagramPacket(packetData,
                        BUF_SIZE, targetAddress, port);

               try
               {
                  localSocket.send(packet);
               }
               catch (IOException e)
               {
                  System.out.println("Could not send outgoing alive packet!");
               }
            }
            catch (UnknownHostException e)
            {
               System.out.println("Could not get InetAddress for host: "
                        + hostname);
            }
         }
      }
      catch (SocketException e)
      {
         System.out
                  .println("Could not get socket for sending of outgoing alive packet!");
      }
   }

   /**
    * Waits for incoming alive packets (blocking).
    * 
    * @param socket
    *           The UDP socket to wait on.
    * 
    * @return The content of the alive packet.
    * @throws IOException If an IO error occurs.
    */
   public DatagramPacket ReceivePacket(DatagramSocket socket) throws IOException
   {
      byte[] incomingBuffer = new byte[BUF_SIZE];

      // prepare the incoming packet
      DatagramPacket incomingPacket = new DatagramPacket(incomingBuffer, incomingBuffer.length);
      
      // wait for incoming packets
      socket.receive(incomingPacket);
      
      // return the extracted packet data
      return incomingPacket;
   }
   
   /**
    * Extracts the relevant data from an incoming datagram alive packet.
    * 
    * @param incomingPacket The incoming packet.
    * @return Created AlivePacket instance.
    */
   public AlivePacket ExtractPacketData(DatagramPacket incomingPacket)
   {
      AlivePacket returnValue = null;
      
      // extract the raw data as a string
      String incomingData = new String(incomingPacket.getData());
      
      String[] extractedData = incomingData.split(LOAD_SEPARATOR);
      
      if (extractedData.length == 2)
      {
         int remoteTcpPort = Integer.valueOf(Decode(extractedData[0])).intValue();
         int remoteLoad = Integer.valueOf(Decode(extractedData[1])).intValue();
         
         returnValue = new AlivePacket(incomingPacket.getAddress().getHostAddress(),remoteTcpPort, remoteLoad);
      }
      else
      {
         System.out.println("Received malformed UDP packet!");
      }
      
      return returnValue;
   }

   /**
    * Creates a new protocol message including the encoded data.
    * 
    * @param localPort
    * @param localLoad
    * @return
    */
   private byte[] CreateProtocolMessage(int localPort, int localLoad)
   {
      // create the textual message representation
      String message = Encode(String.valueOf(localPort)) + LOAD_SEPARATOR + Encode(String.valueOf(localLoad));
      
      // return as byte array
      return message.getBytes();
   }
   
   /**
    * Encodes text to avoid
    * conflicting with Protocol constants.
    * 
    * @param encodedText The plain text.
    * @return The encoded text.
    */
   private String Encode(String plainText)
   {
      return plainText.replaceAll(PROTOCOL_PLAIN_MARKER, PROTOCOL_ENCODE_MARKER);
   }
   
   /**
    * Decodes text that was encoded to avoid
    * conflicting with Protocol constants.
    * 
    * @param encodedText The encoded text.
    * @return The decoded text.
    */
   private String Decode(String encodedText)
   {
      return encodedText.replaceAll(PROTOCOL_ENCODE_MARKER, PROTOCOL_PLAIN_MARKER);
   }
}
