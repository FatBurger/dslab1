package server.commands;

import server.TcpConnectionListener;
import server.heartbeatHandling.HeartbeatManager;
import tcpConnections.TcpServerConnectionPoint;

import commandHandling.ICommand;
import commandHandling.ICommandHandler;

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
    * Server connection reference.
    */
   private final TcpServerConnectionPoint connection;
   
   /**
    * The Tcp connection listener object.
    */
   private final TcpConnectionListener listener;
   
   /**
    * The heartbeat manager reference.
    */
   private final HeartbeatManager heartbeatManager;

   /**
    * Creates a new exit command.
    * 
    * @param commandHandler
    *           Console command handler that should be ended when this command
    *           is executed.
    * @param connection
    *           ServerConnection that should be closed when this command is
    *           executed.
    * @param listener The tcp connection listener object
    * @param heartbeatManager Heartbeat manager reference, used to stop the recurring timertask.
    */
   public ExitCommand(ICommandHandler commandHandler,
            TcpServerConnectionPoint connection,
            TcpConnectionListener listener,
            HeartbeatManager heartbeatManager)
   {
      this.commandHandler = commandHandler;
      this.connection = connection;
      this.listener = listener;
      this.heartbeatManager = heartbeatManager;
   }

   /**
    * Performs the command logic
    */
   public void Execute(String[] parameters)
   {
      if (parameters.length == 0)
      {
         // stop command handling and close the server port as well
         // as all open client connections
         listener.CloseAllConnections();
         connection.CloseServerSocket();
         heartbeatManager.StopAliveMessages();
         commandHandler.StopListening();
         System.out.println("Exit success!");
      }
      else
      {
         System.out.println("Wrong parameters - Usage: !exit");
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