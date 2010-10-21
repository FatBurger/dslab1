package proxy;

import common.InitFailedException;

/**
 * Parses command line arguments for the proxy.
 * 
 * @author RaphM
 */
public class Arguments
{
   /**
    * TCP port.
    */
   private int tcpPort;
   
   /**
    * UPD port.
    */
   private int udpPort;
   
   /**
    * File server timeout interval.
    */
   private int fileserverTimeout;
   
   /**
    * File server check period
    */
   private int checkPeriod;
   
   
   /**
    * Parses the given arguments.
    * 
    * @param arguments The argument array.
    * 
    * @throws InitFailedException Exception that gets thrown when the arguments
    *                             cannot be parsed.
    */
   public Arguments(String[] arguments) throws InitFailedException
   {
      // validate command line arguments
      if (arguments == null || arguments.length != 4)
      {
         Usage();
      }
      
      try
      {
         tcpPort = Integer.valueOf(arguments[0]).intValue();
         udpPort = Integer.valueOf(arguments[1]).intValue();
         fileserverTimeout = Integer.valueOf(arguments[2]).intValue();
         checkPeriod = Integer.valueOf(arguments[3]).intValue();
      }
      catch (NumberFormatException e)
      {
         Usage();
      }
      
      if (tcpPort < 1 || tcpPort > 65535 || udpPort < 1 || udpPort > 65535 
                      || fileserverTimeout < 1 || checkPeriod < 1)
      {
         Usage();
      }
   }
   
   /**
    * Prints out a usage message and terminates the program.
    * 
    * @throws InitFailedException Exception that gets thrown when the arguments
    *                             cannot be parsed.
    */
   private void Usage() throws InitFailedException
   {
      System.out.println("Usage: FDS_Proxy <tcpPort> <udpPort> <fileserverTimeout> <checkPeriod>");
      throw new InitFailedException();
   }
   
   /**
    * TCP port.
    */
   public int getTcpPort()
   {
      return tcpPort;
   }
   
   /**
    * UPD port.
    */
   public int getUdpPort()
   {
      return udpPort;
   }
   
   /**
    * File server timeout interval.
    */
   public int getFileserverTimeout()
   {
      return fileserverTimeout;
   }
   
   /**
    * File server check period
    */
   public int getCheckPeriod()
   {
      return checkPeriod;
   }
}
