package server.commands;

import java.util.Vector;

import protocols.MessageFileProtocol;
import server.fileHandling.FileManager;
import commandHandling.ICommand;

/**
 * Command that lists all files in the current directory.
 * 
 * @author RaphM
 */
public class ListCommand implements ICommand
{
   /**
    * Command identifier of this instance.
    */
   private final String COMMAND = "!list";
   
   /**
    * Response protocol.
    */
   private final MessageFileProtocol protocol;
   
   /**
    * File manager reference.
    */
   private final FileManager fileManager;
   
   /**
    * Initializes a new ListCommand instance.
    * 
    * @param ResponseProtocol Response protocol.
    * @param fileManager File manager reference.
    */
   public ListCommand(MessageFileProtocol responseProtocol, FileManager fileManager)
   {
      this.protocol = responseProtocol;
      this.fileManager = fileManager;
   }
   
   /**
    * Executes the logic of this command.
    */
   public void Execute(String[] parameters)
   {
      // get all filenames
      Vector<String> fileNames = fileManager.listFiles();
      
      // respond them to the requesting proxy
      protocol.SendFileNames(fileNames);  
   }

   /**
    * Gets the command identifier of this instance.
    * 
    * @return Command identifier.
    */
   public String getIdentifier()
   {
      return COMMAND;
   }
}
