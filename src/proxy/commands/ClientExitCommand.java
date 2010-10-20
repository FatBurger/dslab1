package proxy.commands;

import protocols.MessageFileProtocol;
import proxy.userHandling.UserData;
import proxy.userHandling.UserManager;
import tcpConnections.TcpConnection;
import commandHandling.ICommand;

/**
 * Implementation for the proxy's exit received from clients
 * 
 * @author RaphM
 */
public class ClientExitCommand implements ICommand
{
   /**
    * Command identifier of this instance.
    */
   private final String COMMAND = "!exit";

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
    * Creates a new client exit command.
    * 
    * @param userManager
    *           Reference to user manager.
    * @param responseProtocol
    *           Reference to response protocol object.
    * @param connection
    *           The used connection.
    */
   public ClientExitCommand(UserManager userManager,
            MessageFileProtocol responseProtocol, TcpConnection connection)
   {
      this.userManager = userManager;
      this.responseProtocol = responseProtocol;
      this.connection = connection;
   }

   /**
    * Performs the command logic
    */
   public void Execute(String[] parameters)
   {
      if (parameters.length == 0)
      {
         // check if a user is logged on via this connection
         UserData user = userManager.FindUserByConnection(connection);

         // send a force logoff request
         responseProtocol.SendForceLogoff();

         if (user != null)
         {
            // user was logged in - log off
            user.Logout();
            System.out.println("<TcpConnectionHandler Thread>: User "
                     + user.getName() + " logged off!");
         }
         else
         {
            // user was not logged in, but close the connection anyway
            connection.Disconnect();
            System.out
                     .println("<TcpConnectionHandler Thread>: Unknown user logged off!");
         }
      }
      else
      {
         responseProtocol.WriteText("Wrong parameters - Usage: !exit");
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
