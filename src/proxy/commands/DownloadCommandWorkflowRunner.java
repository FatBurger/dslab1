package proxy.commands;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import protocols.MessageFileProtocol;
import protocols.misc.MessageType;
import protocols.misc.ProtocolMessage;
import proxy.serverHandling.ServerData;
import proxy.userHandling.UserData;

/**
 * Handles the download-command workflow towards the fileserver
 * 
 * @author RaphM
 */
public class DownloadCommandWorkflowRunner implements Runnable
{
   /**
    * Protocol object to communicate with the client.
    */
   private final MessageFileProtocol clientResponseProtocol;
   /**
    * Address of the target fileserver.
    */
   private final String serverAddress;
   /**
    * Port of the target fileserver
    */
   private final int serverPort;
   /**
    * The requested file.
    */
   private final String fileName;
   /**
    * The user that requested the file.
    */
   private final UserData user;
   /**
    * The server that provides the file.
    */
   private final ServerData server;

   /**
    * Initializes a new DownloadCommandWorkflowRunner.
    * 
    * @param clientResponseProtocol
    *           Protocol object to communicate with the client.
    * @param serverAddress
    *           Address of the target fileserver.
    * @param serverPort
    *           Port of the target fileserver
    * @param fileName
    *           The requested file.
    * @param user
    *           The user that requested the file.
    * @param server
    *           The server that provides the file.
    */
   public DownloadCommandWorkflowRunner(
            MessageFileProtocol clientResponseProtocol, String serverAddress,
            int serverPort, String fileName, UserData user, ServerData server)
   {
      this.clientResponseProtocol = clientResponseProtocol;
      this.serverAddress = serverAddress;
      this.serverPort = serverPort;
      this.fileName = fileName;
      this.user = user;
      this.server = server;
   }

   /**
    * Executed as a thread.
    */
   public void run()
   {
      try
      {
         Socket serverConnection = new Socket(serverAddress, serverPort);
         MessageFileProtocol outgoingServerProtocol = new MessageFileProtocol(
                  serverConnection.getOutputStream());
         MessageFileProtocol incomingServerProtocol = new MessageFileProtocol(
                  serverConnection.getInputStream());
         // request info about the wanted file
         outgoingServerProtocol.sendDownloadInfoRequest(fileName);
         // now listen for the response
         ProtocolMessage message = incomingServerProtocol.readMessage();
         if (message != null
                  && message.getResultType() == MessageType.FileSizeInfo)
         {
            long size = message.getFileSize();
            if (size > -1)
            {
               // check credits
               if (user.getCredits() > size)
               {
                  // finally request the file and update server and user objects
                  // if everything went ok
                  DownloadFile(outgoingServerProtocol, incomingServerProtocol, size);
               }
               else
               {
                  // error, not enough credits
                  clientResponseProtocol
                           .writeText("Not enough credits (you have "
                                    + user.getCredits() + ", filesize is "
                                    + size);
               }
            }
            else
            {
               // error, received malformed response
               clientResponseProtocol.writeText("File not found on server!");
            }
         }
         else
         {
            // error, received malformed response
            clientResponseProtocol
                     .writeText("Received unexpected message from fileserver: "
                              + message.getResultType());
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
    * @param outgoingServerProtocol
    *           Protocol object used for communication towards the server.
    * @param incomingServerProtocol
    *           Protocol object used for incoming communication from the server.
    * @param size
    *           The size of the file.
    * 
    * @throws IOException
    */
   private void DownloadFile(MessageFileProtocol outgoingServerProtocol,
            MessageFileProtocol incomingServerProtocol, long size)
            throws IOException
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
         clientResponseProtocol.sendFile(response.getFileName(), response
                  .getContent());
      }
      else
      {
         clientResponseProtocol
                  .writeText("Received unexpected message type from fileserver: "
                           + response.getResultType());
      }
   }
}
