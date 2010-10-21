package tcpConnections;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import common.InitFailedException;

/**
 * Encapsulates a TCP server socket.
 * 
 * @author RaphM
 */
public class TcpServerConnectionPoint
{
   /**
    * The server socket.
    */
   private ServerSocket serverSocket;
   
   /**
    * Initializes this connection point
    * 
    * @param port TCP port to listen on.
    * 
    * @throws InitFailedException Gets thrown when this connection point
    *                             fails to initialize.
    */
   public TcpServerConnectionPoint(int port) throws InitFailedException
   {
      try
      {
         serverSocket = new ServerSocket(port);
      }
      catch (IOException e)
      {
         System.out.println("Could not get I/O on TCP port " + port + "!");
         CloseServerSocket();
         throw new InitFailedException();
      }
      
      System.out.println("Listening on TCP port " + port + "!");
   }
   
   /**
    * Stops listening for new connections.
    */
   public void CloseServerSocket()
   {
      try
      {
         if (serverSocket != null && !serverSocket.isClosed())
         {
            serverSocket.close();

            System.out.println("Closed TCP server socket succesfully!");
         }
      }
      catch (IOException e)
      {
         System.out.println("Could not close TCP server socket!");
      }
   }
   
   /**
    * Exposes the accept() method of the internal server socket.
    * 
    * @return New socket if a connection is established.
    * @throws IOException
    */
   public Socket WaitForConnection() throws IOException
   {
      return serverSocket.accept();
   }
}
