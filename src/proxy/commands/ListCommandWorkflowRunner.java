package proxy.commands;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import protocols.MessageFileProtocol;
import protocols.misc.MessageType;
import protocols.misc.ProtocolMessage;

/**
 * Handles the list-command workflow towards the fileserver
 * 
 * @author RaphM
 */
public class ListCommandWorkflowRunner implements Runnable
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
    * Initializes a new ListCommandWorkflowRunner.
    * 
    * @param clientResponseProtocol Protocol object to communicate with the client.
    * @param serverAddress Address of the target fileserver.
    * @param serverPort Port of the target fileserver
    */
   public ListCommandWorkflowRunner(MessageFileProtocol clientResponseProtocol, String serverAddress, int serverPort)
   {
      this.clientResponseProtocol = clientResponseProtocol;
      this.serverAddress = serverAddress;
      this.serverPort = serverPort;
   }
   
   /**
    * Executed as a thread.
    */
   public void run()
   {
      try
      {
         Socket serverConnection = new Socket(serverAddress, serverPort);   
         MessageFileProtocol outgoingServerProtocol = new MessageFileProtocol(serverConnection.getOutputStream());
         MessageFileProtocol incomingServerProtocol = new MessageFileProtocol(serverConnection.getInputStream());
         
         // send file names request to server
         outgoingServerProtocol.sendFileNamesRequest();
         
         // now listen for the response
         ProtocolMessage message = incomingServerProtocol.readMessage();
         
         if (message != null && message.getResultType() == MessageType.FileList)
         {
            // success, forward filenames to client
            for (String fileName : message.getFileList())
            {
               clientResponseProtocol.writeText(fileName);
            }
         }
         else
         {
            // error, received malformed response
            clientResponseProtocol.writeText("Received unexpected message from fileserver: " + message.getResultType());         
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
}
