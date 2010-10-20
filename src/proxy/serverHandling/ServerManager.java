package proxy.serverHandling;

import java.util.Collection;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Manages registered servers.
 * 
 * @author RaphM
 */
public class ServerManager
{
   /**
    * Map with server names and server data.
    */
   private final ConcurrentMap<String, ServerData> serverList = new ConcurrentHashMap<String, ServerData>();
   
   /**
    * The timer that will execute the server online / offline checks.
    */
   private final Timer checkTimer = new Timer();
   
   /**
    * Creates a new ServerManager object.
    * 
    * @param fileServerTimeout The timeout value for server online / offline status
    * @param fileServerCheckInterval The interval in which online / offline
    *                            status of registered fileservers will be re-checked
    */
   public ServerManager(int fileServerTimeout, int fileServerCheckInterval)
   {   
      // start the online check timer for fileServers
      TimerTask task = new ServerOnlineTimerTask(fileServerTimeout, serverList.values());
      checkTimer.scheduleAtFixedRate(task, 0, fileServerCheckInterval);
      
      System.out.println("Started recurring online check for registered fileservers!");
   }
   
   /**
    * Adds a new server to the internal map
    * 
    * @param server
    */
   public void addServer(ServerData server)
   {
      serverList.put(server.getServerIdentifier(), server);
   }
   
   /**
    * Tries to find a server object for a given Identifier.
    * 
    * @param name The server identifier
    * @return The server object if found.
    */
   public ServerData getServerById(String id)
   {
      return serverList.get(id);
   }
   
   /**
    * Gets the server with the lowest current load.
    * 
    * @return SeverData object if found, null otherwise.
    */
   public ServerData getLeastUsedServer()
   {
      ServerData leastUsedServer = null;
      
      // get the least used server
      for (ServerData server : serverList.values())
      {
         if (server.isOnline() && (leastUsedServer == null || leastUsedServer.getLoad() > server.getLoad()))
         {
            leastUsedServer = server;
         }
      }
      
      return leastUsedServer;
   }
   
   /**
    * Returns a collection of all present servers.
    * 
    * @return Collection of all present servers.
    */
   public Collection<ServerData> getAllServers()
   {
      return serverList.values();
   }
   
   /**
    * Stops updating the online status of fileservers.
    */
   public void StopOnlineCheck()
   {
      checkTimer.cancel();
      
      System.out.println("Stopped recurring online check for registered fileservers!");
   }
}
