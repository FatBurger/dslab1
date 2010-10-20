package server.fileHandling;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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
    * @param directory
    *           The directory where files are located.
    */
   public FileManager(String directory)
   {
      fileLocation = new File(directory);

      // check if the directory actually exits
      if (fileLocation.isDirectory())
      {
         System.out.println("Providing files from directory: "
                  + fileLocation.getAbsolutePath());
      }
      else
      {
         System.out.println("Cannot find directory: "
                  + fileLocation.getAbsolutePath());
         System.exit(1);
      }
   }

   public String getFileContent(String fileName)
   {
      byte[] bytes = null;

      File file = new File(fileLocation.getAbsolutePath() + File.separatorChar
               + fileName);

      if (file.isFile())
      {
         BufferedInputStream bis;
         try
         {
            bis = new BufferedInputStream(new FileInputStream(file));
            bytes = new byte[(int) file.length()];
            bis.read(bytes);
            bis.close();
         }
         catch (IOException e)
         {
            System.out.println("Error reading from file: " + file.getAbsolutePath());
         }
      }

      return new String(bytes, 0, bytes.length);
   }

   /**
    * Requests the size of a given file.
    * 
    * @param fileName
    *           The file to check.
    * 
    * @return size The filesize or -1 if file not found.
    */
   public long getFileSize(String fileName)
   {
      long size = -1;

      File file = new File(fileLocation.getAbsolutePath() + File.separatorChar
               + fileName);

      if (file.isFile())
      {
         size = file.length();
      }

      return size;
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
