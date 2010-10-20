package server.fileHandling;

import java.io.File;
import java.util.Vector;

/**
 * Manages files.
 * 
 * @author RaphM
 */
public class FileManager
{
   /**
    * The directory where files are located.
    */
   private final File fileLocation;
   
   /**
    * Initializes a new file manager.
    * 
    * @param directory The directory where files are located.
    */
   public FileManager(String directory)
   {    
      fileLocation = new File(directory);
      
      // check if the directory actually exits
      if (fileLocation.isDirectory())
      {
         System.out.println("Providing files from directory: " + fileLocation.getAbsolutePath());
      }
      else
      {
         System.out.println("Cannot find directory: " + fileLocation.getAbsolutePath());
         System.exit(1);
      }
   }
   
   /**
    * Returns a collection of all filenames in the current directory.
    * 
    * @return Collection of filenames
    */
   public Vector<String> listFiles()
   {
      Vector<String> fileNames = new Vector<String>();
      
      File[] files = fileLocation.listFiles();
      
      for (File file : files)
      {
         fileNames.add(file.getName());
      }
      
      return fileNames;
   }
}
