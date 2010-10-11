package client.commands;

import java.io.*;

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
    * The output printer.
    */
   private final PrintWriter outputPrinter;

   /**
    * Creates a new ProxyCommand instance.
    * 
    * @param outputStream
    *           The stream that will be used for proxy communication.
    */
   public ProxyCommand(OutputStream outputStream)
   {
      outputPrinter = new PrintWriter(outputStream, true);
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
         outputPrinter.println(restoredCommand);
      }
   }

}
