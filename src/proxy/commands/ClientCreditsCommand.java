package proxy.commands;

import protocols.MessageFileProtocol;
import proxy.userHandling.UserData;
import proxy.userHandling.UserManager;
import tcpConnections.TcpConnection;
import commandHandling.ICommand;

/**
 * Performs logic for a client credits command.
 * 
 * @author RaphM
 */
public class ClientCreditsCommand implements ICommand
{
   /**
    * Command identifier of this instance.
    */
   private final String COMMAND = "!credits";

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
    * Creates a new client credits command.
    * 
    * @param userManager
    *           Reference to user manager.
    * @param responseProtocol
    *           Reference to response protocol object.
    * @param connection
    *           The used connection.
    */
   public ClientCreditsCommand(UserManager userManager,
            MessageFileProtocol responseProtocol, TcpConnection connection)
   {
      this.userManager = userManager;
      this.responseProtocol = responseProtocol;
      this.connection = connection;
   }

   /**
    * Performs client credits logic
    */
   public void Execute(String[] parameters)
   {
      if (parameters.length == 0)
      {
         UserData user = userManager.FindUserByConnection(connection);

         if (user != null && user.isLoggedIn())
         {
            // get the amount of credits
            responseProtocol.WriteText("You have " + user.getCredits()
                     + " credits left.");
         }
         else
         {
            responseProtocol.WriteText("User not authenticated!");
         }
      }
      else
      {
         responseProtocol.WriteText("Wrong parameters - Usage: !credits");
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
