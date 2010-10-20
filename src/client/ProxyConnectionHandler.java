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
    * The file directory for downloaded files.
    */
   private final String fileDirectory;
   
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
    * @param directory The directory for downloaded files.
    */
   public ProxyConnectionHandler(ICommandHandler commandHandler, ProxyConnection connection, String directory)
   {
      this.commandHandler = commandHandler;
      this.connection = connection;
      this.fileDirectory = directory;
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
            ProtocolMessage readResult = protocol.readMessage();
            
            // perform action depending on result
            PerformAction(readResult);
         }
         catch (IOException e)
         {
            // close resources and let the thread time out
            System.out.println("<ProxyConnectionHandler Thread>: Input stream was closed, terminating!");
            connection.Disconnect();
            commandHandler.StopListening();
            isRunning = false;
         }
      } 
   }
   
   /**
    * Performs an action depending specific to the given protocol message.
    * 
    * @param message The protocol message.
    */
   private void PerformAction(ProtocolMessage message)
   {
      MessageType type = message.getResultType();
      
      if (type == MessageType.Console)
      {
         System.out.println(message.getContent());
      }
      else if (type == MessageType.File)
      {
         WriteToFile(message.getFileName(), message.getContent());
      }
      else if (type == MessageType.ForceLogoff)
      {
         // close resources and let the thread time out
         System.out.println("<ProxyConnectionHandler Thread>: Received force logoff message, terminating!");
         connection.Disconnect();
         commandHandler.StopListening();
         isRunning = false;
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
      // check if the directory exists
      File directory = new File(fileDirectory);
      
      if (!directory.exists())
      {
         System.out.println("<ProxyConnectionHandler Thread>: Directory does not exist, creating it: " + fileDirectory);
         directory.mkdir();
      }
      
      if (directory.isDirectory())
      {
         File fileToCreate = new File(fileDirectory + File.separator + fileName);
         
         if (fileToCreate.exists())
         {
            System.out.println("<ProxyConnectionHandler Thread>: File already exists, deleting the old one: " + fileToCreate);
            fileToCreate.delete();
         }
         
         try
         {
            fileToCreate.createNewFile();
            
            BufferedWriter out = new BufferedWriter(new FileWriter(fileToCreate));
            out.write(fileContent);
            out.close();
            
            System.out.println("<ProxyConnectionHandler Thread>: Succesfully downloaded file: " + fileToCreate);
         }
         catch (IOException e)
         {
            System.out.println("<ProxyConnectionHandler Thread>: Could not create file: " + fileToCreate);
         }
      }
      else
      {
         System.out.println("<ProxyConnectionHandler Thread>: Specified name is no directory: " + fileDirectory);
      }
   }
}
