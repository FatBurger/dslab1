package client.commands;

import java.io.*;

import protocols.MessageFileProtocol;

import commandHandling.ICommand;

/**
 * Sends commands to the proxy via a TCP connection.
 * 
 * @author RaphM
 */
public class ProxyCommand implements ICommand
{
   /**
    * The command delimiter.
    */
   private final String COMMAND_DELIMITER = " ";

   /**
    * The protocol object.
    */
   private final MessageFileProtocol protocol;
   
   /**
    * Creates a new ProxyCommand instance.
    * 
    * @param outputStream
    *           The stream that will be used for proxy communication.
    */
   public ProxyCommand(OutputStream outputStream)
   {
      protocol = new MessageFileProtocol(outputStream);
   }

   /**
    * Executes this instance.
    * 
    * @param parameters
    *           Raw string parameters (if present).
    */
   public void Execute(String[] parameters)
   {
      if (parameters != null && parameters.length > 0)
      {
         // rebuild the tokenized string
         StringBuilder command = new StringBuilder();

         for (String token : parameters)
         {
            command.append(token);
            command.append(COMMAND_DELIMITER);
         }

         // send the command to the output stream, trim the last
         // COMMAND_DELIMITER
         String restoredCommand = command.toString().substring(0,
                  command.length() - 1);
         protocol.writeText(restoredCommand);
      }
   }

}
