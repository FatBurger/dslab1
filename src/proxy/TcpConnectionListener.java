package proxy;

import java.io.IOException;
import java.net.Socket;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import protocols.MessageFileProtocol;
import proxy.userHandling.UserManager;

/**
 * Listens for client connections.
 * 
 * @author RaphM
 */
public class TcpConnectionListener implements Runnable
{
   /**
    * Lock to avoid access from multiple threads to the internal
    * vector of connections.
    */
   private final Lock lock = new ReentrantLock();
   
   /**
    * The encapsulated server socket.
    */
   private final TcpServerConnectionPoint tcpServer;
   
   /**
    * User manager reference.
    */
   private final UserManager userManager;
   
   /**
    * Stores all active connections.
    */
   private Vector<TcpConnection> activeConnections = new Vector<TcpConnection>();
   
   private Boolean isRunning;
   
   /**
    * Initializes this instance.
    * 
    * @param connection The client connection object.
    * @param userManager User manager reference.
    */
   public TcpConnectionListener(TcpServerConnectionPoint connection, UserManager userManager)
   {
      this.tcpServer = connection;
      this.userManager = userManager;
   }
   
   /**
    * Executed as a thread
    */
   public void run()
   {
      isRunning = true;
      
      // create a new cached thread pool
      ExecutorService threadPool = Executors.newCachedThreadPool();
      
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
            threadPool.execute(new TcpConnectionHandler(connection, userManager));
         }
         catch (IOException e)
         {
            isRunning = false;
            threadPool.shutdown();
            System.out.println("<ClientConnectionListener Thread>: Server socket was closed, terminating!");
         }
      }    
   }
   
   /**
    * Closes all active connections.
    */
   public void CloseAllConnections()
   {  
      lock.lock();
      for (TcpConnection connection : activeConnections)
      {
         // force the clients to perform a logoff
         MessageFileProtocol protocol = new MessageFileProtocol(connection.getOutputStream());
         protocol.SendForceLogoff();
         
         // also close the local connection sockets
         connection.Disconnect();
      }
      
      activeConnections.clear();
      lock.unlock();
   }
}
