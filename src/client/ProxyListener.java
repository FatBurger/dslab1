package client;

import java.io.*;

import commandHandling.ICommandHandler;

/**
 * Listens to messages from the remote proxy and prints
 * them to the console.
 * 
 * @author RaphM
 *
 */
public class ProxyListener implements Runnable
{
   /**
    * The used buffered reader.
    */
   private final BufferedReader reader;
   
   /**
    * Command handler reference.
    */
   private final ICommandHandler commandHandler;
   
   /**
    * Proxy connection reference.
    */
   private final ProxyConnection connection;
   
   /**
    * Indicates if this instance is being run currently.
    */
   private Boolean running;
   
   /**
    * Creates a new proxy listener.
    * 
    * @param inputStream The stream to listen on.
    */
   /**
    * @param commandHandler The command handler.
    * @param connection The proxy connection.
    */
   public ProxyListener(ICommandHandler commandHandler, ProxyConnection connection)
   {
      this.commandHandler = commandHandler;
      this.connection = connection;
      reader = new BufferedReader(new InputStreamReader(connection.GetInputStream()));
   }

   /**
    * Starts listening to replies from the proxy.
    * 
    */
   public void run()
   {
      running = true;
      
      while (running)
      {
         try
         {
            System.out.println(reader.readLine());
         }
         catch (IOException e)
         {
            // close resources and let the thread time out
            System.out.println("<ProxyListener Thread>: Listening stream was closed, terminating!");
            commandHandler.StopListening();
            connection.Disconnect();
            running = false;
         }
      } 
   }
}
