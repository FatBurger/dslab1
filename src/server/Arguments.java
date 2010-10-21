package server;

import common.InitFailedException;

/**
 * Parses command line arguments for the server.
 * 
 * @author RaphM
 */
public class Arguments
{
   /**
    * The directory where files are located.
    */
   private String directory;
   
   /**
    * TCP port.
    */
   private int tcpPort;
   
   /**
    * Hostname of the proxy.
    */
   private String proxyHost;
   
   /**
    * UPD port.
    */
   private int proxyUdpPort;
   
   /**
    * File server alive interval.
    */
   private int alivePeriod;
   
   
   /**
    * Parses the given arguments.
    * 
    * @param arguments The argument array.
    * 
    * @throws InitFailedException Gets thrown when the arguments are
    *                             missing or in wrong format.
    */
   public Arguments(String[] arguments) throws InitFailedException
   {
      // validate command line arguments
      if (arguments == null || arguments.length != 5)
      {
         Usage();
      }
      
      try
      {
         directory = arguments[0];
         tcpPort = Integer.valueOf(arguments[1]).intValue();
         proxyHost = arguments[2];
         proxyUdpPort = Integer.valueOf(arguments[3]).intValue();
         alivePeriod = Integer.valueOf(arguments[4]).intValue();
      }
      catch (NumberFormatException e)
      {
         Usage();
      }
      
      if (tcpPort < 1 || tcpPort > 65535 || proxyUdpPort < 1 || proxyUdpPort > 65535 
                      || alivePeriod < 1)
      {
         Usage();
      }
   }
   
   /**
    * Prints out a usage message and terminates the program.
    * 
    * @throws InitFailedException Gets thrown when the arguments are
    *                             missing or in wrong format.
    */
   private void Usage() throws InitFailedException
   {
      System.out.println("Usage: FDS_Server <sharedFilesDir> <tcpPort> <proxyHost> <proxyUdpPort> <alivePeriod>");
      throw new InitFailedException();
   }
   
   /**
    * Gets the directory.
    * 
    * @return The directory.
    */
   public String getDirectory()
   {
      return directory;
   }
   
   /**
    * TCP port.
    */
   public int getTcpPort()
   {
      return tcpPort;
   }
   
   /**
    * Gets the proxy host.
    * 
    * @return The proxy host.
    */
   public String getProxyHost()
   {
      return proxyHost;
   }
   
   /**
    * Proxy UPD port.
    */
   public int getProxyUdpPort()
   {
      return proxyUdpPort;
   }
   
   /**
    * File server alive interval.
    */
   public int getalivePeriod()
   {
      return alivePeriod;
   }
}
