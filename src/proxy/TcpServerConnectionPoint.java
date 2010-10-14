package proxy;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

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
    */
   public TcpServerConnectionPoint(int port)
   {
      try
      {
         serverSocket = new ServerSocket(port);
      }
      catch (IOException e)
      {
         System.out.println("Could not get I/O on port " + port + "!");
         StopListening();
         System.exit(1);
      }
      
      System.out.println("Listening on TCP port " + port + "!");
   }
   
   /**
    * Stops listening for new connections.
    */
   public void StopListening()
   {
      try
      {
         if (!serverSocket.isClosed())
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
