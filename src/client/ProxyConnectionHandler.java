package client;

import java.io.*;

import protocols.MessageFileProtocol;
import protocols.misc.ProtocolMessage;
import protocols.misc.MessageType;

import commandHandling.ICommandHandler;

/**
 * Listens to messages from the remote proxy and prints
 * them to the console.
 * 
 * @author RaphM
 *
 */
public class ProxyConnectionHandler implements Runnable
{  
   /**
    * Command handler reference.
    */
   private final ICommandHandler commandHandler;
   
   /**
    * Proxy connection reference.
    */
   private final ProxyConnection connection;
   
   /**
    * Communication object between client and proxy.
    */
   private final MessageFileProtocol protocol;
   
   /**
    * Indicates if this instance is being run currently.
    */
   private Boolean isRunning;
   
   /**
    * Creates a new proxy listener.
    * 
    * @param inputStream The stream to listen on.
    */
   /**
    * @param commandHandler The command handler.
    * @param connection The proxy connection.
    */
   public ProxyConnectionHandler(ICommandHandler commandHandler, ProxyConnection connection)
   {
      this.commandHandler = commandHandler;
      this.connection = connection;
      protocol = new MessageFileProtocol(connection.GetInputStream());
   }

   /**
    * Starts listening to replies from the proxy.
    * 
    */
   public void run()
   {
      isRunning = true;
      
      while (isRunning)
      {
         try
         {
            // read next protocol element
            ProtocolMessage readResult = protocol.ReadMessage();
            MessageType type = readResult.getResultType();
            
            // perform action depending on result type
            if (type == MessageType.Console)
            {
               System.out.println(readResult.getContent());
            }
            else if (type == MessageType.File)
            {
               WriteToFile(readResult.getFileName(), readResult.getContent());
            }
            else if (type == MessageType.ForceLogoff)
            {
               // close resources and let the thread time out
               connection.Disconnect();
               commandHandler.StopListening();
               isRunning = false;
               System.out.println("<ProxyConnectionHandler Thread>: Received force logoff message, terminating!");
            }
         }
         catch (IOException e)
         {
            // close resources and let the thread time out
            connection.Disconnect();
            commandHandler.StopListening();
            isRunning = false;
            System.out.println("<ProxyConnectionHandler Thread>: Input stream was closed, terminating!");
         }
      } 
   }
   
   /**
    * Writes a new file
    * 
    * @param fileName The file name.
    * @param fileContent The file content.
    */
   private void WriteToFile(String fileName, String fileContent)
   {
      System.out.println("write to file " + fileName + " content " + fileContent);
   }
}
