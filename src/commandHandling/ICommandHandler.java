package commandHandling;

/**
 * Interface for a command handler.
 * 
 * @author RaphM
 */
public interface ICommandHandler
{

   /**
    * Registers a command that will be executed when
    * a keyword is recognized on input stream.
    * 
    * @param keyword The keyword to register.
    * @param command The command to register.
    */
   void RegisterCommand(String keyword, ICommand command);
   
   /**
    * Registers an (optional) default command.
    * 
    * @param command The default command.
    */
   void RegisterDefaultCommand(ICommand command);

   /**
    * Starts listening to registered commands
    * on input stream.
    */
   void StartListening();

   /**
    * Stops listening to new commands - will only work if StartListening() was called.
    * The currently executed command will be finished.
    */
   public void StopListening();

}
