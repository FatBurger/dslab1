package client.commands;

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
         // stop command handling
         commandHandler.StopListening();
         connection.Disconnect();
         System.out.println("Exit success!");
      }
      else
      {
         System.out.println("Wrong parameters - Usage: !exit");
      }
   }
}
