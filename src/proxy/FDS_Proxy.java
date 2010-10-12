package proxy;

import commandHandling.*;

import proxy.Arguments;
import proxy.commands.ExitCommand;
import proxy.userHandling.UserManager;

/**
 * The main class for the File Distribution System (FDS) Proxy.
 * 
 * @author Raphael Mader, 0826052
 */
public class FDS_Proxy
{
   /**
    * TCP server point.
    */
   private static TcpServerConnectionPoint tcpServer;
   
   /**
    * TCP connection listener.
    */
   private static TcpConnectionListener listener;
   
   /**
    * Command handler that will read from System.in
    */
   private static ICommandHandler consoleCommandHandler = new CommandHandler(System.in);
   
   /**
    * The user manager.
    */
   private static UserManager userManager;

   /**
    * Main entry point
    * 
    * @param args The command line arguments
    */
   public static void main(String[] args)
   {
      // parse command line arguments
      Arguments parsedArguments = new Arguments(args);
      
      // initialize the user manager
      userManager = new UserManager();
      
      // initialize the TCP server port
      tcpServer = new TcpServerConnectionPoint(parsedArguments.getTcpPort());
      
      // start listening for messages from TCP clients
      StartTcpListener();
      
      // register known console commands and start listening for them
      RegisterCommands();
      consoleCommandHandler.StartListening();
   }
   
   /**
    * Listens for messages from TCP clients.
    */
   private static void StartTcpListener()
   {
      // create a new client connection listener
      listener = new TcpConnectionListener(tcpServer, userManager);
      
      // run as a thread
      Thread listenerThread = new Thread(listener);
      listenerThread.start();
   }
   
   /**
    * Registers known console commands.
    */
   private static void RegisterCommands()
   {
      // register the exit command
      ExitCommand exitCommand = new ExitCommand(consoleCommandHandler, tcpServer, listener);
      consoleCommandHandler.RegisterCommand(exitCommand.getIdentifier(), exitCommand);
   }
}
