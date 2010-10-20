package proxy.serverHandling;

import java.util.Collection;
import java.util.Date;
import java.util.TimerTask;

/**
 * Performs the check if servers are still online.
 * 
 * @author RaphM
 *
 */
public class ServerOnlineTimerTask extends TimerTask
{
   /**
    * The timeout after which a server is considered as offline.
    */
   private final int serverOfflineTimeout;
   
   /**
    * The collection of servers to check.
    */
   private final Collection<ServerData> serversToCheck;
   
   /**
    * Initializes a new ServerOnlineTimerTask
    * 
    * @param serverOfflineTimeout The timeout after which a server is considered as offline.
    * @param serversToCheck The collection of servers to check.
    */
   public ServerOnlineTimerTask(int serverOfflineTimeout, Collection<ServerData> serversToCheck)
   {
      this.serverOfflineTimeout = serverOfflineTimeout;
      this.serversToCheck = serversToCheck;
   }

   
   /**
    * Executed as a thread.
    */
   public void run()
   {
      // use the time when this method was called as "base" timestamp
      Date currentTime = new Date();
      
      for (ServerData server : serversToCheck)
      {
         // check current time against the servers last activity timestamp
         Date lastServerActivity = server.getLastActivityTimestamp();
         
         long elapsedTime = currentTime.getTime() - lastServerActivity.getTime();
         
         if (elapsedTime > serverOfflineTimeout)
         {
            // set the server to offline
            server.SetOffline();
            System.out.println("Server is offline!");
         }
         else
         {
            // set the server to online
            server.SetOnline();
            System.out.println("Server is online!");
         }
      }
      
   }
}
