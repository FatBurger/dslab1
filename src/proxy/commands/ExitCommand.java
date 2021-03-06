package proxy.commands;

import proxy.TcpConnectionListener;
import proxy.UdpPacketListener;
import proxy.UdpServerConnectionPoint;
import proxy.serverHandling.ServerManager;
import tcpConnections.TcpServerConnectionPoint;
import commandHandling.ICommand;
import commandHandling.ICommandHandler;

/**
 * Implementation for the proxy's exit command
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
    * Server connection reference.
    */
   private final TcpServerConnectionPoint connection;

   /**
    * TCP connection listener reference.
    */
   private final TcpConnectionListener tcpListener;
   
   /**
    * UDP packet listener reference.
    */
   private final UdpPacketListener udpListener;

   /**
    * The UDP connection point
    */
   private final UdpServerConnectionPoint udpConnectionPoint;
   
   /**
    * The server manager reference.
    */
   private final ServerManager serverManager;

   /**
    * Creates a new exit command.
    * 
    * @param commandHandler
    *           Console command handler that should be ended when this command
    *           is executed.
    * @param connection
    *           ServerConnection that should be closed when this command is
    *           executed.
    * @param listener
    *           TCP connection listeners, provides a method to close all active
    *           client connections.
    * @param udpListener UDP packet listener reference.
    * @param serverManager Server manager reference, used to stop the recurring timertask.
    */
   public ExitCommand(ICommandHandler commandHandler,
            TcpServerConnectionPoint connection,
            TcpConnectionListener listener,
            UdpPacketListener udpListener,
            UdpServerConnectionPoint udpConnectionPoint,
            ServerManager serverManager)
   {
      this.commandHandler = commandHandler;
      this.connection = connection;
      this.tcpListener = listener;
      this.udpListener = udpListener;
      this.udpConnectionPoint = udpConnectionPoint;
      this.serverManager = serverManager;
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
         tcpListener.CloseAllConnections();
         commandHandler.StopListening();
         connection.CloseServerSocket();
         udpConnectionPoint.CloseServerSocket();
         udpListener.StopListening();
         serverManager.StopOnlineCheck();
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
