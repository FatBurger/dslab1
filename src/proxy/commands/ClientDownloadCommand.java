package proxy.commands;

import protocols.MessageFileProtocol;
import proxy.serverHandling.ServerData;
import proxy.serverHandling.ServerManager;
import proxy.userHandling.UserData;
import proxy.userHandling.UserManager;
import tcpConnections.TcpConnection;
import commandHandling.ICommand;

/**
 * Performs logic for a client download command.
 * 
 * @author RaphM
 */
public class ClientDownloadCommand implements ICommand
{
   /**
    * Command identifier of this instance.
    */
   private final String COMMAND = "!download";
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
    * Creates a new client download command.
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
   public ClientDownloadCommand(UserManager userManager,
            MessageFileProtocol responseProtocol, ServerManager serverManager,
            TcpConnection connection)
   {
      this.userManager = userManager;
      this.responseProtocol = responseProtocol;
      this.serverManager = serverManager;
      this.connection = connection;
   }

   /**
    * Performs client download logic
    */
   public void Execute(String[] parameters)
   {
      if (parameters.length == 1)
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
                        leastUsedServer.getTcpPort(), parameters[0], user,
                        leastUsedServer);
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
         responseProtocol
                  .writeText("Wrong parameters - Usage: !download <filename>");
      }
   }

   /**
    * Sends a download request to a specific fileserver.
    * 
    * @param serverAddress
    *           Fileserver address.
    * @param serverPort
    *           Fileserver port.
    * @param fileName
    *           The requested fileName
    * @param user
    *           User data object
    * @param server
    *           Server data object.
    */
   private void SendRequestToServer(String serverAddress, int serverPort,
            String fileName, UserData user, ServerData server)
   {
      // create a new workflow runner and execute the rest of the communication
      // in a separate thread to avoid
      // blocking the client from performing other commands meanwhile
      DownloadCommandWorkflowRunner runner = new DownloadCommandWorkflowRunner(
               responseProtocol, serverAddress, serverPort, fileName, user,
               server);
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
