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
    * Encapsulates the stream to listen on for commands.
    */
   private BufferedReader inputReader;
   
   /**
    * Input stream reference.
    */
   private InputStream inputStream;
   
   /**
    * Indicates that listening to commands takes place currently.
    */
   private Boolean isRunning = false;
   
   /**
    * Indicates if this instance is currently blocked (waiting for input).
    */
   private boolean blocked;
   
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
      inputReader = new BufferedReader(new InputStreamReader(inputStream));
   }
   
   /**
    * Creates a new CommandHandler that does not
    * listen on a stream.
    */
   public CommandHandler()
   {
   }
   
   /**
    * Starts listening to registered
    * on input stream.
    */
   public void StartListening()
   {
      isRunning = true;
      
      // execute until the program gets terminated externally via StopListening()
      while (isRunning)
      {
         try
         {
            // read one line from stream, split along COMMAND_DELIMITER
            blocked = true;
            String inputLine = inputReader.readLine();
            blocked = false;
            if (inputLine != null)
            {
               String[] tokens = inputLine.split(COMMAND_DELIMITER);
         
               // validate the parsed input
               validateInput(tokens);
            }
         }
         catch (IOException e)
         {
            // close resources and let the thread time out
            System.out.println("<CommandHandler>: Input stream was closed, stopping to read!");
            isRunning = false;
         }
      }
   }
   
   /**
    * Handles a command with having to read from a stream;
    * 
    * @param command The command to handle.
    */
   public void HandleCommand(String command)
   {
      // split the command up into its tokens
      String[] tokens = command.split(COMMAND_DELIMITER);
      
      // validate the parsed input
      validateInput(tokens);
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
         else
         {
            // execute the generic unknown command
            UnknownCommand unknownCommand = new UnknownCommand();
            unknownCommand.Execute(tokens);
         }
      }
   }
   
   /**
    * Stops listening to new commands - will only work if StartListening() was called.
    * The currently executed command will be finished.
    */
   public void StopListening()
   {
      // close the input stream to trigger an IOException in the reading loop
      try
      {
         if (inputStream == System.in && blocked)
         {
            // system.in cannot be closed, therefore we have no way
            // to trigger an IOException - we have to rely on the user
            // to unblock the "inputReader.readLine()" by pressing enter
            System.out.println("Press <ENTER> to terminate!");
         }
         else
         {
            inputStream.close();
         }
      }
      catch (IOException e)
      {
         System.out.println("Could not close input stream!");
         e.printStackTrace();
      }
      isRunning = false;
   }
}
