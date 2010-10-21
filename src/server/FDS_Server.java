package server;

import commandHandling.CommandHandler;
import commandHandling.ICommandHandler;
import common.InitFailedException;

import server.Arguments;
import server.TcpConnectionListener;
import server.commands.ExitCommand;
import server.fileHandling.FileManager;
import server.heartbeatHandling.HeartbeatManager;
import tcpConnections.TcpServerConnectionPoint;

/**
 * The main class for the File Distribution System (FDS) Server.
 * 
 * @author Raphael Mader, 0826052
 */
public class FDS_Server
{
   /**
    * TCP server connection point.
    */
   private static TcpServerConnectionPoint tcpServer;

   /**
    * TCP connection listener.
    */
   private static TcpConnectionListener tcpListener;

   /**
    * The file manager.
    */
   private static FileManager fileManager;

   /**
    * Command handler that will read from System.in
    */
   private static ICommandHandler consoleCommandHandler = new CommandHandler(
            System.in);

   /**
    * The heartbeat manager.
    */
   private static HeartbeatManager heartbeatManager;

   /**
    * Main entry point
    * 
    * @param args
    *           The command line arguments
    */
   public static void main(String[] args)
   {
      try
      {
         // parse command line arguments
         Arguments parsedArguments = new Arguments(args);

         // initializes the file manager
         fileManager = new FileManager(parsedArguments.getDirectory());

         // initialize the TCP server port
         tcpServer = new TcpServerConnectionPoint(parsedArguments.getTcpPort());

         // start listening for messages from TCP clients
         StartTcpListener();

         // initialize and start the heartbeat manager
         heartbeatManager = new HeartbeatManager(
                  parsedArguments.getProxyHost(), parsedArguments
                           .getProxyUdpPort(),
                  parsedArguments.getalivePeriod(), parsedArguments
                           .getTcpPort());

         // register known console commands and start listening for them
         RegisterCommands();
         consoleCommandHandler.StartListening();
      }
      catch (InitFailedException e)
      {
         System.out.println("Fileserver initialization failed - terminating!");
      }
      finally
      {
         tcpServer.CloseServerSocket();
         tcpListener.CloseAllConnections();
         heartbeatManager.StopAliveMessages();
         consoleCommandHandler.StopListening();
      }
   }

   /**
    * Listens for messages from TCP clients.
    */
   private static void StartTcpListener()
   {
      // create a new client connection listener
      tcpListener = new TcpConnectionListener(tcpServer, fileManager);

      // run as a thread
      Thread listenerThread = new Thread(tcpListener);
      listenerThread.start();
   }

   /**
    * Registers known console commands.
    */
   private static void RegisterCommands()
   {
      // register the exit command
      ExitCommand exitCommand = new ExitCommand(consoleCommandHandler,
               tcpServer, tcpListener, heartbeatManager);
      consoleCommandHandler.RegisterCommand(exitCommand.getIdentifier(),
               exitCommand);
   }
}
