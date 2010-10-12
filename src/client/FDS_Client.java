package client;

import client.commands.*;
import commandHandling.*;

/**
 * The main class for the File Distribution System (FDS) Client.
 * 
 * @author Raphael Mader, 0826052
 */
public class FDS_Client
{
   /**
    * Helper class that handles commands received via console input.
    */
   private static ICommandHandler commandHandler = new CommandHandler(System.in);
   
   /**
    * Helper class that handles the socket connection.
    */
   private static ProxyConnection connection;
   
   /**
    * Main entry point
    * 
    * @param args The command line arguments
    */
   public static void main(String[] args)
   {
      // parse command line arguments
      Arguments parsedArguments = new Arguments(args);
      
      // initialize the proxy connection with values arguments
      connection = new ProxyConnection(parsedArguments.getHostname(), parsedArguments.getPort());
      
      // start listening for messages from the proxy
      StartProxyListener();
      
      // register known console commands and start command handling
      RegisterCommands();
      commandHandler.StartListening();
   }
   
   /**
    * Listens for messages from the remote proxy.
    */
   private static void StartProxyListener()
   {
      // create a new proxy listener
      ProxyConnectionHandler listener = new ProxyConnectionHandler(commandHandler, connection);
      
      // run as a thread
      Thread listenerThread = new Thread(listener);
      listenerThread.start();
   }
   
   /**
    * Registers known console commands.
    */
   private static void RegisterCommands()
   {
      // register proxy commands as default commands
      ProxyCommand proxyCommand = new ProxyCommand(connection.GetOutputStream());
      commandHandler.RegisterDefaultCommand(proxyCommand);
      
      // register the exit command - will perform special logic
      ExitCommand exitCommand = new ExitCommand(commandHandler, connection);
      commandHandler.RegisterCommand(exitCommand.getIdentifier(), exitCommand);
   }
}
