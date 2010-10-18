package proxy;

import commandHandling.*;

import proxy.Arguments;
import proxy.commands.ExitCommand;
import proxy.serverHandling.ServerManager;
import proxy.userHandling.UserManager;

/**
 * The main class for the File Distribution System (FDS) Proxy.
 * 
 * @author Raphael Mader, 0826052
 */
public class FDS_Proxy
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
    * UDP server connection point.
    */
   private static UdpServerConnectionPoint udpServer;
   
   /**
    * UDP packet listener.
    */
   private static UdpPacketListener udpListener;
   
   /**
    * Command handler that will read from System.in
    */
   private static ICommandHandler consoleCommandHandler = new CommandHandler(System.in);
   
   /**
    * The user manager.
    */
   private static final UserManager userManager = new UserManager();
   
   /**
    * The server manager.
    */
   private static final ServerManager serverManager = new ServerManager();

   /**
    * Main entry point
    * 
    * @param args The command line arguments
    */
   public static void main(String[] args)
   {
      // parse command line arguments
      Arguments parsedArguments = new Arguments(args);
      
      // initialize the TCP server port
      tcpServer = new TcpServerConnectionPoint(parsedArguments.getTcpPort());
      
      // start listening for messages from TCP clients
      StartTcpListener();
      
      // initialize the UDP server port
      udpServer = new UdpServerConnectionPoint(parsedArguments.getUdpPort());
      
      // start listening for UDP packets
      StartUdpListener();
      
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
      tcpListener = new TcpConnectionListener(tcpServer, userManager);
      
      // run as a thread
      Thread listenerThread = new Thread(tcpListener);
      listenerThread.start();
   }
   
   /**
    * Listens for packets from UDP clients.
    */
   private static void StartUdpListener()
   {
      // create a new udp packet listener
      udpListener = new UdpPacketListener(udpServer, serverManager);
      
      // run as a thread
      Thread listenerThread = new Thread(udpListener);
      listenerThread.start();
   }
   
   /**
    * Registers known console commands.
    */
   private static void RegisterCommands()
   {
      // register the exit command
      ExitCommand exitCommand = new ExitCommand(consoleCommandHandler, tcpServer, tcpListener, udpServer);
      consoleCommandHandler.RegisterCommand(exitCommand.getIdentifier(), exitCommand);
   }
}
