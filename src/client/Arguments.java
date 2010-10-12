package client;

/**
 * Parses command line arguments for the client.
 * 
 * @author RaphM
 */
public class Arguments
{
   /**
    * Download directory.
    */
   private String downloadDir;
   
   /**
    * Proxy hostname.
    */
   private String hostname;
   
   /**
    * Proxy port.
    */
   private int port;
   
   
   /**
    * Parses the given arguments.
    * 
    * @param arguments The argument array.
    */
   public Arguments(String[] arguments)
   {
      // validate command line arguments
      if (arguments == null || arguments.length != 3)
      {
         Usage();
      }
      
      downloadDir = arguments[0];
      hostname = arguments[1];
      
      try
      {
         port = Integer.valueOf(arguments[2]).intValue();
      }
      catch (NumberFormatException e)
      {
         Usage();
      }
      
      if (port < 1 || port > 65535)
      {
         Usage();
      }
   }
   
   /**
    * Prints out a usage message and terminates the program.
    */
   private void Usage()
   {
      System.out.println("Usage: FDS_Client <downloadDir> <proxyHost> <proxyTCPPort>");
      System.exit(1);
   }
   
   /**
    * Gets the download directory.
    * 
    * @return The download directory.
    */
   public String getDownloadDir()
   {
      return downloadDir;
   }
   
   /**
    * Gets the hostname.
    * 
    * @return The hostname.
    */
   public String getHostname()
   {
      return hostname;
   }
   
   /**
    * Gets the port.
    * 
    * @return The port.
    */
   public int getPort()
   {
      return port;
   }
}
