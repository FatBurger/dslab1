package proxy.commands;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import protocols.MessageFileProtocol;
import protocols.misc.MessageType;
import protocols.misc.ProtocolMessage;
import proxy.serverHandling.ServerData;
import proxy.serverHandling.ServerManager;
import proxy.userHandling.UserData;
import proxy.userHandling.UserManager;
import tcpConnections.TcpConnection;
import commandHandling.ICommand;


/**
 * Performs logic for a client download command.
 * 
 * @author RaphM
 */
public class ClientDownloadCommand implements ICommand
{
   /**
    * Command identifier of this instance.
    */
   private final String COMMAND = "!download";
   
   /**
    * User manager reference.
    */
   private final UserManager userManager;

   /**
    * Response protocol reference.
    */
   private final MessageFileProtocol responseProtocol;

   /**
    * The used connection object.
    */
   private final TcpConnection connection;
   
   /**
    * Server manager reference.
    */
   private final ServerManager serverManager;
   
   /**
    * Creates a new client download command.
    * 
    * @param userManager
    *           Reference to user manager.
    * @param serverManager
    *           Reference to server manager.
    * @param responseProtocol
    *           Reference to response protocol object.
    * @param connection
    *           The used connection.
    */
   public ClientDownloadCommand(UserManager userManager,
            MessageFileProtocol responseProtocol, ServerManager serverManager, TcpConnection connection)
   {
      this.userManager = userManager;
      this.responseProtocol = responseProtocol;
      this.serverManager = serverManager;
      this.connection = connection;
   }
   
   /**
    * Performs client download logic
    */
   public void Execute(String[] parameters)
   {
      if (parameters.length == 1)
      {
         // check if user is logged in
         UserData user = userManager.FindUserByConnection(connection);

         if (user != null && user.isLoggedIn())
         {
            ServerData leastUsedServer = serverManager.getLeastUsedServer();
            
            if (leastUsedServer != null)
            {
               // server available, forward the request
               SendRequestToServer(leastUsedServer.getAddress(), leastUsedServer.getTcpPort(), parameters[0], user, leastUsedServer);
            }
            else
            {
               responseProtocol.writeText("No fileservers available!");
            }
         }
         else
         {
            responseProtocol.writeText("User not authenticated!");
         }
      }
      else
      {
         responseProtocol.writeText("Wrong parameters - Usage: !download <filename>");
      }
   }
   
   /**
    * Sends a download request to a specific fileserver.
    * 
    * @param serverAddress Fileserver address.
    * @param serverPort Fileserver port.
    * @param fileName The requested fileName
    * @param user User data object
    * @param server Server data object.
    */
   private void SendRequestToServer(String serverAddress, int serverPort, String fileName, UserData user, ServerData server)
   {
      try
      {
         Socket serverConnection = new Socket(serverAddress, serverPort);
         MessageFileProtocol outgoingServerProtocol = new MessageFileProtocol(serverConnection.getOutputStream());
         MessageFileProtocol incomingServerProtocol = new MessageFileProtocol(serverConnection.getInputStream());
         
         // request info about the wanted file
         outgoingServerProtocol.sendDownloadInfoRequest(fileName);
         
         // now listen for the response
         ProtocolMessage message = incomingServerProtocol.readMessage();
         
         if (message != null && message.getResultType() == MessageType.FileSizeInfo)
         {          
            long size = message.getFileSize();
            
            if (size > -1)
            {
               // check credits
               if (user.getCredits() > size)
               {
                  // finally request the file and update server and user objects if everything went ok
                  DownloadFile(outgoingServerProtocol, incomingServerProtocol, size, user, server);
               }
               else
               {
                  // error, not enough credits
                  responseProtocol.writeText("Not enough credits (you have " + user.getCredits() + ", filesize is " + size);    
               }
            }
            else
            {
               // error, received malformed response
               responseProtocol.writeText("File not found on server!");  
            }
         }
         else
         {
            // error, received malformed response
            responseProtocol.writeText("Received unexpected message from fileserver: " + message.getResultType());         
         }
         
         serverConnection.close();
      }
      catch (UnknownHostException e)
      {
         System.out.println("Could not find host " + serverAddress);
      }
      catch (IOException e)
      {
         System.out.println("Socket was closed: " + serverAddress);
      }
   }

   /**
    * Downloads a file from the server.
    * 
    * @param outgoingServerProtocol Protocol object to use.
    * @param incomingServerProtocol Incoming protocol object.
    * @param size File size.
    * @param user User that requests the file.
    * @param server Server that provides the file.
    * @throws IOException 
    */
   private void DownloadFile(MessageFileProtocol outgoingServerProtocol, MessageFileProtocol incomingServerProtocol,
            long size, UserData user, ServerData server) throws IOException
   {
      // send the download request
      outgoingServerProtocol.sendDownloadRequest();
      
      // wait for the file response
      ProtocolMessage response = incomingServerProtocol.readMessage();
      
      if (response != null && response.getResultType() == MessageType.File)
      {
         // update credits and load
         user.RemoveCredits(size);
         server.addLoad(size);
         
         // everything ok, forward file to client
         responseProtocol.sendFile(response.getFileName(), response.getContent());
      }
      else
      {
         responseProtocol.writeText("Received unexpected message type from fileserver: " + response.getResultType());
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
