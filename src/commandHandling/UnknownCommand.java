package commandHandling;

public class UnknownCommand implements ICommand
{
   /**
    * The command delimiter.
    */
   private final String COMMAND_DELIMITER = " ";
   
   /**
    * Executes the logic when an unknown command is received.
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

         // print command
         System.out.println("Unknown command: " + restoredCommand);

      }
   }

}
