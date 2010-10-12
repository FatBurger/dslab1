package proxy;

import java.io.IOException;

import commandHandling.CommandHandler;
import commandHandling.ICommandHandler;

import protocols.MessageFileProtocol;
import protocols.misc.ProtocolMessage;
import protocols.misc.MessageType;
import proxy.commands.ClientExitCommand;
import proxy.userHandling.UserManager;

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
    */
   public TcpConnectionHandler(TcpConnection connection, UserManager userManager)
   {
      incomingProtocol = new MessageFileProtocol(connection.getInputStream());
      outgoingProtocol = new MessageFileProtocol(connection.getOutputStream());
      this.connection = connection;
      this.userManager = userManager;
      
      RegisterCommands();
   }
   
   /**
    * Registers all commands that will be triggered via TCP/IP messages.
    */
   private void RegisterCommands()
   {
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
            // close resources and let the thread time out
            isRunning = false;
            System.out.println("<TcpConnectionHandler Thread>: Input stream was closed, terminating!");
         }
      }
   }

}
