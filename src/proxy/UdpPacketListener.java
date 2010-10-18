package proxy;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import proxy.serverHandling.ServerManager;

/**
 * Listens for UDP packets.
 * 
 * @author RaphM
 */
public class UdpPacketListener implements Runnable
{
   /**
    * The server connection point to listen on.
    */
   private final UdpServerConnectionPoint connection;

   /**
    * Reference to the server manager.
    */
   private final ServerManager serverManager;

   /**
    * Thread pool that provides threads that run incoming requests.
    */
   private final ExecutorService threadPool = Executors.newCachedThreadPool();;

   /**
    * Indicates if this instance is currently running.
    */
   private Boolean isRunning;

   /**
    * Initializes this instance.
    * 
    * @param connection
    *           The UDP connection to listen on.
    * @param serverManager
    *           The server manager object.
    */
   public UdpPacketListener(UdpServerConnectionPoint connection,
            ServerManager serverManager)
   {
      this.connection = connection;
      this.serverManager = serverManager;
   }

   /**
    * Executed as a thread
    */
   public void run()
   {
      isRunning = true;
      
      while (isRunning)
      {
         // wait for incoming isAlive packages
         try
         {
            DatagramPacket receivedPacket = connection.ReceiveAliveMessage();
            
            // handle connection communication in a separate thread
            threadPool.execute(new UdpPacketHandler(receivedPacket, serverManager));
         }
         catch (IOException e)
         {
            isRunning = false;
            threadPool.shutdown();
            connection.StopListening();
            System.out.println("<UdpPacketListener Thread>: Server socket was closed, terminating!");
         }
      }
      
   }
}
