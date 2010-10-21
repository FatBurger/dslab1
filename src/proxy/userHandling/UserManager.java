package proxy.userHandling;

import java.io.*;
import java.util.Collection;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import common.InitFailedException;

import tcpConnections.TcpConnection;

/**
 * Manages users and their status.
 * 
 * @author RaphM
 */
public class UserManager
{
   /**
    * Config file name.
    */
   private final String CONFIG_FILE = "user.properties";

   /**
    * Suffix for credits value in config file.
    */
   private final String CREDITS_SUFFIX = ".credits";

   /**
    * Map with user names and user data.
    */
   private final ConcurrentMap<String, UserData> userList = new ConcurrentHashMap<String, UserData>();

   /**
    * Creates a new UserManager.
    * 
    * @throws InitFailedException Exception that gets thrown when the
    *                             user.properties file cannot be located.
    */
   public UserManager() throws InitFailedException
   {
      LoadPropertiesFile();
   }

   /**
    * Tries to find a user that has a given name.
    * 
    * @param name The name to check.
    * @return The user object if found, null otherwise.
    */
   public UserData FindUserByName(String name)
   {
      UserData user = null;

      for (UserData curUser : userList.values())
      {
         if (curUser.getName().equals(name))
         {
            // user with that name found
            user = curUser;
            break;
         }
      }

      return user;
   }
   
   /**
    * Returns all present users.
    * 
    * @return Vector of UserData objects.
    */
   public Collection<UserData> getAllUsers()
   {
      return userList.values();
   }
   
   /**
    * Tries to find a user that uses the given connection.
    * 
    * @param connection
    *           The connection to check.
    * @return The user object if found, null otherwise.
    */
   public UserData FindUserByConnection(TcpConnection connection)
   {
      UserData user = null;

      for (UserData curUser : userList.values())
      {
         if (curUser.isLoggedIn() && curUser.getConnection() == connection)
         {
            // user with that connection found
            user = curUser;
            break;
         }
      }

      return user;
   }

   /**
    * Loads the properties file.
    * 
    * @throws InitFailedException Exception that gets thrown when the
    *                             user.properties file cannot be located.
    */
   private void LoadPropertiesFile() throws InitFailedException
   {
      try
      {
         // load the file and open it with a key / value reader
         InputStream configStream = ClassLoader
                  .getSystemResourceAsStream(CONFIG_FILE);
         Properties propertyReader = new Properties();

         if (configStream != null)
         {
            propertyReader.load(configStream);

            // read initial user data
            InitUsers(propertyReader);
         }
         else
         {
            System.out.println("Could not find config file: " + CONFIG_FILE
                     + " !");
            throw new InitFailedException();
         }
      }
      catch (FileNotFoundException e)
      {
         System.out
                  .println("Could not find config file: " + CONFIG_FILE + " !");
         throw new InitFailedException();
      }
      catch (IOException e)
      {
         System.out.println("IO error reading from config file: " + CONFIG_FILE
                  + " !");
         throw new InitFailedException();
      }
   }

   /**
    * Inits users from a key value property reader.
    * 
    * @param propertyReader
    *           The reader.
    */
   private void InitUsers(Properties propertyReader)
   {
      for (String property : propertyReader.stringPropertyNames())
      {
         // only process non-credit entries directly
         if (!property.endsWith(CREDITS_SUFFIX))
         {
            // found a user
            String user = property;

            // get the users password
            String password = propertyReader.getProperty(user);

            // try to find the credit entry for this user
            if (propertyReader.containsKey(user + CREDITS_SUFFIX))
            {
               try
               {
                  int credits = Integer.valueOf(
                           propertyReader.getProperty(user + CREDITS_SUFFIX))
                           .intValue();

                  // finally add the user
                  AddUser(user, password, credits);
               }
               catch (NumberFormatException e)
               {
                  System.out
                           .println("Could not parse credits for user " + user);
               }
            }
            else
            {
               System.out.println("Found no " + CREDITS_SUFFIX
                        + " entry for user " + user
                        + " - not adding him to internal database");
            }
         }
      }
   }

   /**
    * Adds a user to the internal collection.
    * 
    * @param name
    *           The user's name.
    * @param password
    *           The user's password.
    * @param credits
    *           The user's credits.
    */
   private void AddUser(String name, String password, int credits)
   {
      UserData user = new UserData(name, password, credits);

      if (!userList.containsKey(name))
      {
         userList.put(name, user);
      }
      else
      {
         System.out.println("User " + name + " already present in userlist!");
      }
   }
}
