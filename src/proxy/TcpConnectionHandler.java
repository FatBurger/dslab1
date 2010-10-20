package proxy;

import java.io.IOException;

import commandHandling.CommandHandler;
import commandHandling.ICommandHandler;

import protocols.MessageFileProtocol;
import protocols.misc.ProtocolMessage;
import protocols.misc.MessageType;
import proxy.commands.ClientBuyCommand;
import proxy.commands.ClientCreditsCommand;
import proxy.commands.ClientExitCommand;
import proxy.commands.ClientLoginCommand;
import proxy.commands.ClientUnknownCommand;
import proxy.userHandling.UserData;
import proxy.userHandling.UserManager;
import tcpConnections.TcpConnection;

/**
 * Handles client connections.
 * 
 * @author RaphM
 */
public class TcpConnectionHandler implements Runnable
{
   /**
    * Communication object between client and proxy.
    */
   private final MessageFileProtocol incomingProtocol;
   
   /**
    * Communication object between client and proxy.
    */
   private final MessageFileProtocol outgoingProtocol;
   
   /**
    * The connection object that is being handled.
    */
   private final TcpConnection connection;
   
   /**
    * The TCP connection listener.
    */
   private final TcpConnectionListener listener;
   
   /**
    * User manager reference.
    */
   private final UserManager userManager;
   
   /**
    * Command handler for incoming TCP commands.
    */
   private final ICommandHandler messageCommandHandler = new CommandHandler();

   /**
    * Indicates if this instance is running currently.
    */
   private Boolean isRunning;

   /**
    * Handles a new client connection.
    * 
    * @param connection The connection object.
    * @param userManager User manager reference.
    * @param listener Connection listener reference.
    */
   public TcpConnectionHandler(TcpConnection connection, UserManager userManager, TcpConnectionListener listener)
   {
      incomingProtocol = new MessageFileProtocol(connection.getInputStream());
      outgoingProtocol = new MessageFileProtocol(connection.getOutputStream());
      this.connection = connection;
      this.userManager = userManager;
      this.listener = listener;
      
      RegisterCommands();
   }
   
   /**
    * Registers all commands that will be triggered via TCP/IP messages.
    */
   private void RegisterCommands()
   {
      // register the unknown command as default
      ClientUnknownCommand unknownCommand = new ClientUnknownCommand(outgoingProtocol);
      messageCommandHandler.RegisterDefaultCommand(unknownCommand);
      
      // register the login command
      ClientLoginCommand loginCommand = new ClientLoginCommand(userManager, outgoingProtocol, connection);
      messageCommandHandler.RegisterCommand(loginCommand.getIdentifier(), loginCommand);
      
      // register the credits command
      ClientCreditsCommand creditsCommand = new ClientCreditsCommand(userManager, outgoingProtocol, connection);
      messageCommandHandler.RegisterCommand(creditsCommand.getIdentifier(), creditsCommand);
      
      // register the buy command
      ClientBuyCommand buyCommand = new ClientBuyCommand(userManager, outgoingProtocol, connection);
      messageCommandHandler.RegisterCommand(buyCommand.getIdentifier(), buyCommand);
      
      // register the exit command
      ClientExitCommand exitCommand = new ClientExitCommand(userManager, outgoingProtocol, connection);
      messageCommandHandler.RegisterCommand(exitCommand.getIdentifier(), exitCommand);
   }

   /**
    * Executed as a thread.
    */
   public void run()
   {
      isRunning = true;

      while (isRunning)
      {
         try
         {
            ProtocolMessage result = incomingProtocol.ReadMessage();
            
            if (result.getResultType() == MessageType.Console)
            {
               messageCommandHandler.HandleCommand(result.getContent());
            }
            else
            {
               System.out.println("<TcpConnectionHandler Thread>: Received unknown protocol message type: " + result.getResultType());
            }
         }
         catch (IOException e)
         {
            // close the connection
            connection.Disconnect();
            
            // try to log off the user (if authenticated over this connection)
            LogoffUser();
            
            // remove from the listeners list
            RemoveFromConnectionList();
            
            // close resources and let the thread time out
            isRunning = false;
            System.out.println("<TcpConnectionHandler Thread>: Input stream was closed, terminating!");
         }
      }
   }

   /**
    * Tries to log off the user handled by this instance (if logged in)
    */
   private void LogoffUser()
   {
      // check if a user is logged on via this connection
      UserData user = userManager.FindUserByConnection(connection);
      
      if (user != null)
      {
         // user was logged in - log off
         user.Logout();
         System.out.println("<TcpConnectionHandler Thread>: User " + user.getName() + " logged off!");
      }
   }
   
   /**
    * Removes the connection from this instance from
    * the listeners list of activeConnections.
    */
   private void RemoveFromConnectionList()
   {
      listener.RemoveConnection(connection);
   }
}
