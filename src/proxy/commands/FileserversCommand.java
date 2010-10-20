package proxy.commands;

import java.util.Collection;

import proxy.serverHandling.ServerData;
import proxy.serverHandling.ServerManager;
import commandHandling.ICommand;

public class FileserversCommand implements ICommand
{
   /**
    * Command identifier of this instance.
    */
   private final String COMMAND = "!fileservers";

   /**
    * Servermanager reference.
    */
   private final ServerManager serverManager;

   /**
    * Initializes a new FileserversCommand instance.
    * 
    * @param serverManager
    *           Servermanager reference.
    */
   public FileserversCommand(ServerManager serverManager)
   {
      this.serverManager = serverManager;
   }

   /**
    * Performs the command logic
    */
   public void Execute(String[] parameters)
   {
      if (parameters.length == 0)
      {
         Collection<ServerData> servers = serverManager.getAllServers();

         if (servers != null && servers.size() > 0)
         {
            // print out data for each server
            for (ServerData server : servers)
            {
               String onlineStatus = server.isOnline() ? "online" : "offline";
               System.out.println("IP:" + server.getAddress() + " Port:"
                        + server.getTcpPort() + " " + onlineStatus + " Usage: "
                        + server.getLoad());
            }
         }
         else
         {
            System.out.println("No servers registered!");
         }
      }
      else
      {
         System.out.println("Wrong parameters - Usage: !fileservers");
      }

   }

   /**
    * Gets the command identifier of this instance.
    * 
    * @return Command identifier.
    */
   public String getIdentifier()
   {
      return COMMAND;
   }
}
