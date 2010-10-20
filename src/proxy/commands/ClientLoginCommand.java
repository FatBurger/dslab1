package proxy.commands;

import protocols.MessageFileProtocol;
import proxy.userHandling.UserData;
import proxy.userHandling.UserManager;
import tcpConnections.TcpConnection;
import commandHandling.ICommand;

/**
 * Performs logic for a client login.
 * 
 * @author RaphM
 */
public class ClientLoginCommand implements ICommand
{
   /**
    * Command identifier of this instance.
    */
   private final String COMMAND = "!login";
   
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
    * Creates a new client login command.
    * 
    * @param userManager Reference to user manager.
    * @param responseProtocol Reference to response protocol object.
    * @param connection The used connection.
    */
   public ClientLoginCommand(UserManager userManager, MessageFileProtocol responseProtocol, TcpConnection connection)
   {
      this.userManager = userManager;
      this.responseProtocol = responseProtocol;
      this.connection = connection;
   }
   
   /**
    * Performs client login logic
    */
   public void Execute(String[] parameters)
   {
      if (parameters.length == 2)
      {
         String userName = parameters[0];
         
         UserData user = userManager.FindUserByName(userName);
         
         if (user != null)
         {
            if (user.isLoggedIn())
            {
               responseProtocol.writeText("User already authenticated!");
               System.out.println("<TcpConnectionHandler Thread>: Received login attempt for already authenticated user:  " + user.getName()); 
            }
            else
            {
               String password = parameters[1];
            
               // try to authenticate the user
               boolean authenticated = user.Login(connection, password );
            
               if (authenticated)
               {
                  responseProtocol.writeText("Authentication succesful!");
                  System.out.println("<TcpConnectionHandler Thread>: User " + user.getName() + " succesfully authenticated!"); 
               }
               else
               {
               responseProtocol.writeText("Wrong password!");
                  System.out.println("<TcpConnectionHandler Thread>: Received wrong authentication for user " + user.getName());
               }
            }
         }
         else
         {
            responseProtocol.writeText("Could not find user!");
            System.out.println("<TcpConnectionHandler Thread>: Received login command for unknown user: " + userName);
         }
      }
      else
      {
         responseProtocol.writeText("Wrong parameters - Usage: !login <username> <password>");
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
