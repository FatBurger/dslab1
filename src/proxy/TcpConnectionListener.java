package proxy;

import java.io.IOException;
import java.net.Socket;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import protocols.MessageFileProtocol;
import proxy.serverHandling.ServerManager;
import proxy.userHandling.UserManager;
import tcpConnections.TcpConnection;
import tcpConnections.TcpServerConnectionPoint;

/**
 * Listens for client connections.
 * 
 * @author RaphM
 */
public class TcpConnectionListener implements Runnable
{
   /**
    * Lock to avoid access from multiple threads to the internal vector of
    * connections.
    */
   private final Lock lock = new ReentrantLock();

   /**
    * The encapsulated server socket.
    */
   private final TcpServerConnectionPoint tcpServer;

   /**
    * Thread pool that provides threads that run incoming requests.
    */
   private final ExecutorService threadPool = Executors.newCachedThreadPool();

   /**
    * User manager reference.
    */
   private final UserManager userManager;

   /**
    * Server manager reference.
    */
   private final ServerManager serverManager;

   /**
    * Stores all active connections.
    */
   private Vector<TcpConnection> activeConnections = new Vector<TcpConnection>();

   /**
    * Indicates if this instance is currently running.
    */
   private Boolean isRunning;

   /**
    * Initializes this instance.
    * 
    * @param connection
    *           The client connection object.
    * @param userManager
    *           User manager reference.
    * @param serverManager
    *           Server manager reference.
    */
   public TcpConnectionListener(TcpServerConnectionPoint connection,
            UserManager userManager, ServerManager serverManager)
   {
      this.tcpServer = connection;
      this.userManager = userManager;
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
         try
         {
            Socket incomingSocket = tcpServer.WaitForConnection();
            TcpConnection connection = new TcpConnection(incomingSocket);

            lock.lock();
            activeConnections.add(connection);
            lock.unlock();

            // handle connection communication in a separate thread
            threadPool.execute(new TcpConnectionHandler(connection,
                     userManager, serverManager, this));
         }
         catch (IOException e)
         {
            isRunning = false;
            CloseAllConnections();
            System.out
                     .println("<TcpConnectionListener Thread>: Server socket was closed, terminating!");
         }
      }
   }

   /**
    * Removes a specific connection from the internal list.
    * 
    * @param connection
    *           The connection.
    */
   public void RemoveConnection(TcpConnection connection)
   {
      lock.lock();
      if (activeConnections.contains(connection))
      {
         activeConnections.remove(connection);
      }
      lock.unlock();
   }

   /**
    * Closes all active connections.
    */
   public void CloseAllConnections()
   {
      threadPool.shutdown();
      isRunning = false;
      lock.lock();
      for (TcpConnection connection : activeConnections)
      {
         // force the clients to perform a logoff
         MessageFileProtocol protocol = new MessageFileProtocol(connection
                  .getOutputStream());
         protocol.sendForceLogoff();

         // also close the local connection sockets
         connection.Disconnect();
      }

      activeConnections.clear();
      lock.unlock();
   }
}
