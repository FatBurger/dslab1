package commandHandling;

/**
 * Interface for a class that is able to execute commands.
 * 
 * @author RaphM
 */
public interface ICommand
{  
   /**
    * Executes this instance.
    * 
    * @param parameters Raw string parameters (if present).
    */
   void Execute(String[] parameters);
}
