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
         ProtocolMessage result = incomingProtocol.readMessage();

         if (result.getResultType() == MessageType.Console)
         {
            messageCommandHandler.HandleCommand(result.getContent());
         }
         else if (result.getResultType() == MessageType.FileNamesRequest)
         {
            SendFileNames();
         }
         else if (result.getResultType() == MessageType.DownloadInfoRequest)
         {
            SendDownloadInfo(result.getFileName());
            
            // wait for an eventual download request, otherwise the client closes the connection
            // and we get an IOException
            ProtocolMessage downloadRequest = incomingProtocol.readMessage();
            if (downloadRequest.getResultType() == MessageType.DownloadRequest)
            {
               SendFile(result.getFileName(), fileManager.getFileContent(result.getFileName()));
            }
            
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
    * Sends file names to the connection initiator
    */
   private void SendFileNames()
   {
      outgoingProtocol.sendFileNames(fileManager.listFiles());
   }
   
   /**
    * Sends download info data to the connection initiator
    */
   private void SendDownloadInfo(String fileName)
   {
      long fileSize = fileManager.getFileSize(fileName);
      outgoingProtocol.sendFileSizeInfo(fileSize);
   }
   
   /**
    * Sends a file to the proxy.
    * 
    * @param fileName The file name.
    * @param fileContent The file content.
    */
   private void SendFile(String fileName, String fileContent)
   {
      outgoingProtocol.sendFile(fileName, fileContent);
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
