package commandHandling;

import java.io.*;
import java.util.*;

/**
 * Class that performs general command execution
 * triggered by stream input.
 * 
 * @author RaphM
 */
public class CommandHandler implements ICommandHandler
{
   /**
    * Character that is used to delimit entered commands and their parameters.
    */
   private final String COMMAND_DELIMITER = " ";
   
   /**
    * The stream to listen on for commands.
    */
   private final InputStream inputStream;
   
   /**
    * Indicates that listening to commands takes place currently.
    */
   private Boolean isRunning = false;
   
   /**
    * Stores all registered keywords with their associated command executor object.
    */
   private Map<String, ICommand> registeredCommands = new HashMap<String, ICommand>();
   
   /**
    * Optional default command.
    */
   private ICommand defaultCommand = null;
   
   /**
    * Registers a command that will be executed when
    * a keyword is recognized on input stream.
    * 
    * @param keyword The keyword to register.
    * @param command The command to register.
    */
   public void RegisterCommand(String keyword, ICommand command)
   {
      // duplicate keywords will be overwritten
      registeredCommands.put(keyword, command);
   }
   
   /**
    * Registers an (optional) default command.
    * 
    * @param command The default command.
    */
   public void RegisterDefaultCommand(ICommand command)
   {
      defaultCommand = command;
   }
   
   /**
    * Creates a new CommandHandler that listens for
    * commands on a specific stream.
    * 
    * @param inputStream The stream to listen on.
    */
   public CommandHandler(InputStream inputStream)
   {
      this.inputStream = inputStream;
   }
   
   /**
    * Starts listening to registered
    * on input stream.
    */
   public void StartListening() throws IllegalStateException
   {
      isRunning = true;
      
      Scanner consoleInput = new Scanner(inputStream);
      
      // execute until the program gets terminated externally via StopListening()
      while (isRunning)
      {
         // read one line from stream, split along COMMAND_DELIMITER
         String inputLine = consoleInput.nextLine();
         String[] tokens = inputLine.split(COMMAND_DELIMITER);
         
         // validate the parsed input
         validateInput(tokens);
      }
   }

   /**
    * Checks if the parsed input can be matched to a registered command
    * and executes the command if possible.
    * 
    * @param tokens Parsed string tokens that will be used as command name
    *               and parameters.
    */
   private void validateInput(String[] tokens)
   {
      if (tokens != null && tokens.length > 0)
      {
         // extract the command and check if it is registered
         String command = tokens[0];
         
         if (registeredCommands.containsKey(command))
         {  
            // create parameters array and copy values into it (may be empty)
            String[] parameters = new String[tokens.length - 1];
            System.arraycopy(tokens, 1, parameters, 0, tokens.length - 1);
            
            // execute a registered command
            registeredCommands.get(command).Execute(parameters);
         }
         else if (defaultCommand != null)
         {
            // execute default command otherwise if present
            defaultCommand.Execute(tokens);       
         }
      }
   }
   
   /**
    * Stops listening to new commands - will only work if StartListening() was called.
    * The currently executed command will be finished.
    */
   public void StopListening()
   {
      isRunning = false;
   }
}
