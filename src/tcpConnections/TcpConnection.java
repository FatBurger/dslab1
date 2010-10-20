package tcpConnections;

import java.io.*;
import java.net.Socket;

/**
 * Encapsulates a TCP connection via a Socket.
 * 
 * @author RaphM
 */
public class TcpConnection
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
    * Stores the socket object and extracts input
    * and output stream.
    * 
    * @param socket The socket.
    */
   public TcpConnection(Socket socket)
   {
      // try to create the socket
      connection = socket;

      // try to get input and output streams
      try
      {
         inputStream = connection.getInputStream();
      }
      catch (IOException e)
      {
         System.out.println("Could not get input stream for host " 
                  + socket.getInetAddress().getCanonicalHostName()
                  + " on port " + socket.getPort() + "!");
         Disconnect();
         System.exit(1);
      }

      try
      {
         outputStream = connection.getOutputStream();
      }
      catch (IOException e)
      {
         System.out.println("Could not get output stream for host "
                  + socket.getInetAddress().getCanonicalHostName()
                  + " on port " + socket.getPort() + "!");
         Disconnect();
         System.exit(1);
      }

      System.out.println("<TcpConnectionListener Thread>: Incoming connection from "
               + socket.getInetAddress().getCanonicalHostName()+ ":" + socket.getPort() + "!");
   }

   /**
    * Gets the input stream of the connection.
    * 
    * @return The input stream.
    */
   public InputStream getInputStream()
   {
      return inputStream;
   }

   /**
    * Gets the output stream of the connection.
    * 
    * @return The output stream.
    */
   public OutputStream getOutputStream()
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
         System.out.println("<TcpConnectionListener Thread>: Could not close TCP connection output stream!");
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
         System.out.println("<TcpConnectionListener Thread>: Could not close TCP connection input stream!");
      }
      
      try
      {
         if (connection != null && !connection.isClosed())
         {
            connection.close();
            System.out.println("<TcpConnectionListener Thread>: Closed connection resources succesfully!");
         }
      }
      catch (IOException e)
      {
         System.out.println("<TcpConnectionListener Thread>: Could not close TCP connection socket!");
      }
   }
}
