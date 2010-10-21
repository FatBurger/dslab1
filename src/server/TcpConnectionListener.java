package server;

import java.io.IOException;
import java.net.Socket;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import common.InitFailedException;

import server.fileHandling.FileManager;
import tcpConnections.TcpConnection;
import tcpConnections.TcpServerConnectionPoint;

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
    * Thread pool that provides threads that run incoming requests.
    */
   private final ExecutorService threadPool = Executors.newCachedThreadPool();
   
   /**
    * File manager reference.
    */
   private final FileManager fileManager;
   
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
    * @param connection The client connection object.
    * @param fileManager File manager reference.
    */
   public TcpConnectionListener(TcpServerConnectionPoint connection, FileManager fileManager)
   {
      this.tcpServer = connection;
      this.fileManager = fileManager;
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
            threadPool.execute(new TcpConnectionHandler(connection, fileManager, this));
         }
         catch (InitFailedException e)
         {
            System.out
            .println("<TcpConnectionListener Thread>: Failed to get inbound TCP connection socket!");
         }
         catch (IOException e)
         {
            isRunning = false;
            CloseAllConnections();
            System.out.println("<TcpConnectionListener Thread>: Server socket was closed, terminating!");
         }
      }    
   }
   
   /**
    * Removes a specific connection from the internal list.
    * 
    * @param connection The connection.
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
         // close the local connection sockets
         connection.Disconnect();
      }
      
      activeConnections.clear();
      lock.unlock();
   }
}
