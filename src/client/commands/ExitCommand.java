package client.commands;


import protocols.MessageFileProtocol;

import client.ProxyConnection;
import commandHandling.ICommand;
import commandHandling.ICommandHandler;

/**
 * The clients exit command implementation.
 * 
 * @author RaphM
 */
public class ExitCommand implements ICommand
{
   /**
    * Command identifier of this instance.
    */
   private final String COMMAND = "!exit";
   
   /**
    * Command handler reference.
    */
   private final ICommandHandler commandHandler;
   
   /**
    * Proxy connection reference.
    */
   private final ProxyConnection connection;
   
   /**
    * The protocol object.
    */
   private final MessageFileProtocol protocol;
   
   /**
    * Constructor.
    * 
    * @param commandHandler Command handler that will be stopped when this
    *                       instance is executed.
    *                      
    * @param connection     The connection that will be closed when this instance
    *                       is executed.
    */
   public ExitCommand(ICommandHandler commandHandler, ProxyConnection connection)
   {
      this.commandHandler = commandHandler;
      this.connection = connection;
      protocol = new MessageFileProtocol(connection.GetOutputStream());
      
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
   
   /**
    * Executes this instance.
    * 
    * @param parameters Raw string parameters (if present).
    */
   public void Execute(String[] parameters)
   {
      if (parameters.length == 0)
      {
         // send exit to proxy to indicate the user is offline now
         SendExitMessage();
         
         // now stop command handling and close the proxy connection
         commandHandler.StopListening();
         connection.Disconnect();
         System.out.println("Exit success!");
      }
      else
      {
         System.out.println("Wrong parameters - Usage: !exit");
      }
   }
   
   /**
    * Sends an exit message to the proxy.
    */
   private void SendExitMessage()
   {
      protocol.writeText(COMMAND);
   }
}
