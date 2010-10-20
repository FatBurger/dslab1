package proxy.commands;

import protocols.MessageFileProtocol;
import commandHandling.ICommand;

/**
 * Performs logic for unknown client commands.
 * 
 * @author RaphM
 */
public class ClientUnknownCommand implements ICommand
{
   /**
    * The command delimiter.
    */
   private final String COMMAND_DELIMITER = " ";

   /**
    * Response protocol reference.
    */
   private final MessageFileProtocol responseProtocol;

   /**
    * Initializes this command.
    * 
    * @param responseProtocol
    *           The protocol that will be used to respond to the client.
    */
   public ClientUnknownCommand(MessageFileProtocol responseProtocol)
   {
      this.responseProtocol = responseProtocol;
   }

   /**
    * Executes the logic when an unknown client command is received.
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

         // trim the last
         // COMMAND_DELIMITER
         String restoredCommand = command.toString().substring(0,
                  command.length() - 1);

         // responds to client
         responseProtocol.writeText("Unknown command: "
                  + restoredCommand);

      }
   }
}
