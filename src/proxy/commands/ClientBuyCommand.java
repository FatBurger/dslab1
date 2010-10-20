package proxy.commands;

import protocols.MessageFileProtocol;
import proxy.userHandling.UserData;
import proxy.userHandling.UserManager;
import tcpConnections.TcpConnection;
import commandHandling.ICommand;

/**
 * Performs logic for a client buy command.
 * 
 * @author RaphM
 */
public class ClientBuyCommand implements ICommand
{
   /**
    * Command identifier of this instance.
    */
   private final String COMMAND = "!buy";

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
    * Creates a new client buy command.
    * 
    * @param userManager
    *           Reference to user manager.
    * @param responseProtocol
    *           Reference to response protocol object.
    * @param connection
    *           The used connection.
    */
   public ClientBuyCommand(UserManager userManager,
            MessageFileProtocol responseProtocol, TcpConnection connection)
   {
      this.userManager = userManager;
      this.responseProtocol = responseProtocol;
      this.connection = connection;
   }

   /**
    * Performs client buy logic
    */
   public void Execute(String[] parameters)
   {
      if (parameters.length == 1)
      {
         try
         {
            int creditsToAdd = Integer.valueOf(parameters[0]).intValue();
            
            if (creditsToAdd > 0)
            {
               UserData user = userManager.FindUserByConnection(connection);
               
               if (user != null && user.isLoggedIn())
               {
                  // finally add the credits and respond to client
                  user.AddCredits(creditsToAdd);
                  responseProtocol.writeText("You now have " + user.getCredits() + " credits.");
               }
               else
               {
                  responseProtocol.writeText("User not authenticated!");
               }
            }
            else
            {
               responseProtocol.writeText("Supplied credit number is zero or less!");
            }
         }
         catch (NumberFormatException e)
         {
            responseProtocol.writeText("Supplied credit number has wrong format!");
         }
      }
      else
      {
         responseProtocol.writeText("Wrong parameters - Usage: !buy <credits>");
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
