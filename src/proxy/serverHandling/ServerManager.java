package proxy.serverHandling;

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
    * Adds a new server to the internal map
    * 
    * @param server
    */
   public void updateServerStatus(ServerData server)
   {
      serverList.put(server.getServerIdentifier(), server);
   }
   
   /**
    * Tries to find a server object for a given name.
    * 
    * @param name
    * @return
    */
   public ServerData getServerByName(String name)
   {
      return serverList.get(name);
   }
}
