package client;

import java.io.*;
import java.net.*;

/**
 * Encapsulates a TCP connection via a Socket.
 * 
 * @author RaphM
 */
public class ProxyConnection
{
   /**
    * The connection socket.
    */
   private Socket connection;

   /**
    * The input stream associated with the socket.
    */
   private InputStream inputStream;

   /**
    * The output stream associated with the socket.
    */
   private OutputStream outputStream;

   /**
    * Tries to establish a connection to the given hostname/port.
    * 
    * @param hostname
    *           The target hostname.
    * @param port
    *           The target port.
    */
   public ProxyConnection(String hostname, int port)
   {
      // try to create the socket
      try
      {
         connection = new Socket(hostname, port);
      }
      catch (UnknownHostException e)
      {
         System.out.println("Could not create socket for host " + hostname
                  + " on port " + port + "!");
         e.printStackTrace();
         System.exit(1);
      }
      catch (IOException e)
      {
         System.out.println("Could not get I/O for host " + hostname
                  + " on port " + port + "!");
         e.printStackTrace();
         System.exit(1);
      }

      // try to get input and output streams
      try
      {
         inputStream = connection.getInputStream();
      }
      catch (IOException e)
      {
         System.out.println("Could not get input stream for host " + hostname
                  + " on port " + port + "!");
         e.printStackTrace();
         System.exit(1);
      }

      try
      {
         outputStream = connection.getOutputStream();
      }
      catch (IOException e)
      {
         System.out.println("Could not get output stream for host " + hostname
                  + " on port " + port + "!");
         e.printStackTrace();
         System.exit(1);
      }
   }

   /**
    * Gets the input stream of the connection.
    * 
    * @return The input stream.
    */
   public InputStream GetInputStream()
   {
      return inputStream;
   }

   /**
    * Gets the output stream of the connection.
    * 
    * @return The output stream.
    */
   public OutputStream GetOutputStream()
   {
      return outputStream;
   }

   /**
    * Closes all connection resources
    */
   public void Disconnect()
   {
      try
      {
         if (!connection.isClosed())
         {
            outputStream.close();
            inputStream.close();
            connection.close();
            
            System.out.println("Closed connection resources succesfully!");
         }
      }
      catch (IOException e)
      {
         System.out.println("Could not close resources!");
         e.printStackTrace();
         System.exit(1);
      }
   }
}
