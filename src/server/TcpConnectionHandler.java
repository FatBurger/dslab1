package server;

import java.io.IOException;

import commandHandling.CommandHandler;
import commandHandling.ICommandHandler;

import protocols.MessageFileProtocol;
import protocols.misc.ProtocolMessage;
import protocols.misc.MessageType;
import server.commands.ListCommand;
import server.fileHandling.FileManager;
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
    * File manager reference.
    */
   private final FileManager fileManager;

   /**
    * Command handler for incoming TCP commands.
    */
   private final ICommandHandler messageCommandHandler = new CommandHandler();

   /**
    * Handles a new client connection.
    * 
    * @param connection
    *           The connection object.
    * @param fileManager
    *           File manager reference.
    * @param listener
    *           Connection listener reference.
    */
   public TcpConnectionHandler(TcpConnection connection,
            FileManager fileManager, TcpConnectionListener listener)
   {
      incomingProtocol = new MessageFileProtocol(connection.getInputStream());
      outgoingProtocol = new MessageFileProtocol(connection.getOutputStream());
      this.connection = connection;
      this.fileManager = fileManager;
      this.listener = listener;

      RegisterCommands();
   }

   /**
    * Registers all commands that will be triggered via TCP/IP messages.
    */
   private void RegisterCommands()
   {
      ListCommand listCommand = new ListCommand(outgoingProtocol, fileManager);
      messageCommandHandler.RegisterCommand(listCommand.getIdentifier(), listCommand);
      
   }

   /**
    * Executed as a thread.
    */
   public void run()
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
            System.out
                     .println("<TcpConnectionHandler Thread>: Received unknown protocol message type: "
                              + result.getResultType());
         }
      }
      catch (IOException e)
      {
         // close resources and let the thread time out
         System.out
                  .println("<TcpConnectionHandler Thread>: Input stream was closed, terminating!");
      }
      finally
      {
         // close the connection
         connection.Disconnect();

         // remove from the listeners list
         RemoveFromConnectionList();
      }
   }

   /**
    * Removes the connection from this instance from the listeners list of
    * activeConnections.
    */
   private void RemoveFromConnectionList()
   {
      listener.RemoveConnection(connection);
   }
}
