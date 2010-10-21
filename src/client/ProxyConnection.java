package client;

import java.io.*;
import java.net.*;

import common.InitFailedException;

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
    *           
    * @throws InitFailedException Exception that gets thrown when the connection cannot be
    *                             initialized.
    */
   public ProxyConnection(String hostname, int port) throws InitFailedException
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
         Disconnect();
         throw new InitFailedException();
      }
      catch (IOException e)
      {
         System.out.println("Could not get I/O for host " + hostname
                  + " on port " + port + "!");
         Disconnect();
         throw new InitFailedException();
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
         Disconnect();
         throw new InitFailedException();
      }

      try
      {
         outputStream = connection.getOutputStream();
      }
      catch (IOException e)
      {
         System.out.println("Could not get output stream for host " + hostname
                  + " on port " + port + "!");
         Disconnect();
         throw new InitFailedException();
      }

      System.out.println("Connected to " + hostname + ":" + port
               + ", local port: " + connection.getLocalPort() + "!");
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
         if (outputStream != null)
         {
            outputStream.close();
         }
      }
      catch (IOException e)
      {
         System.out.println("Could not close proxy connection output stream!");
      }
      
      try
      {
         if (inputStream != null)
         {
            inputStream.close();
         }
      }
      catch (IOException e)
      {
         System.out.println("Could not close proxy connection input stream!");
      }
      
      try
      {
         if (connection != null && !connection.isClosed())
         {
            connection.close();
            System.out.println("Closed proxy connection resources succesfully!");
         }
      }
      catch (IOException e)
      {
         System.out.println("Could not close proxy connection socket!");
      }
   }
}
