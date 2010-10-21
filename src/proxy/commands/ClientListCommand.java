package proxy.commands;

import protocols.MessageFileProtocol;
import proxy.serverHandling.ServerData;
import proxy.serverHandling.ServerManager;
import proxy.userHandling.UserData;
import proxy.userHandling.UserManager;
import tcpConnections.TcpConnection;
import commandHandling.ICommand;

/**
 * Performs logic for a client list command.
 * 
 * @author RaphM
 */
public class ClientListCommand implements ICommand
{
   /**
    * Command identifier of this instance.
    */
   private final String COMMAND = "!list";
   /**
    * User manager reference.
    */
   private final UserManager userManager;
   /**
    * Response protocol reference.
    */
   private final MessageFileProtocol responseProtocol;
   /**
    * The used connection object.
    */
   private final TcpConnection connection;
   /**
    * Server manager reference.
    */
   private final ServerManager serverManager;

   /**
    * Creates a new client list command.
    * 
    * @param userManager
    *           Reference to user manager.
    * @param serverManager
    *           Reference to server manager.
    * @param responseProtocol
    *           Reference to response protocol object.
    * @param connection
    *           The used connection.
    */
   public ClientListCommand(UserManager userManager,
            MessageFileProtocol responseProtocol, ServerManager serverManager,
            TcpConnection connection)
   {
      this.userManager = userManager;
      this.responseProtocol = responseProtocol;
      this.serverManager = serverManager;
      this.connection = connection;
   }

   /**
    * Performs client list logic
    */
   public void Execute(String[] parameters)
   {
      if (parameters.length == 0)
      {
         // check if user is logged in
         UserData user = userManager.FindUserByConnection(connection);
         if (user != null && user.isLoggedIn())
         {
            ServerData leastUsedServer = serverManager.getLeastUsedServer();
            if (leastUsedServer != null)
            {
               // server available, forward the request
               SendRequestToServer(leastUsedServer.getAddress(),
                        leastUsedServer.getTcpPort());
            }
            else
            {
               responseProtocol.writeText("No fileservers available!");
            }
         }
         else
         {
            responseProtocol.writeText("User not authenticated!");
         }
      }
      else
      {
         responseProtocol.writeText("Wrong parameters - Usage: !list");
      }
   }

   /**
    * Sends a list request to a specific fileserver.
    * 
    * @param serverAddress
    *           Fileserver address.
    * @param serverPort
    *           Fileserver port.
    */
   private void SendRequestToServer(String serverAddress, int serverPort)
   {
      // create a new workflow runner and execute the rest of the communication
      // in a separate thread to avoid
      // blocking the client from performing other commands meanwhile
      ListCommandWorkflowRunner runner = new ListCommandWorkflowRunner(
               responseProtocol, serverAddress, serverPort);
      // run as a thread
      Thread downloadThread = new Thread(runner);
      downloadThread.start();
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
