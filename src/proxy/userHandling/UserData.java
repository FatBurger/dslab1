package proxy.userHandling;

import tcpConnections.TcpConnection;

/**
 * Holds data for users.
 * 
 * @author RaphM
 */
public class UserData
{
   /**
    * Name of the user.
    */
   private String name;
   
   /**
    * Password of the user.
    */
   private String password;
   
   /**
    * Credits of the user.
    */
   private int credits;
   
   /**
    * Indicates if this user is currently logged in.
    */
   private boolean isLoggedIn;
   
   /**
    * Socket object that represents the users connection if logged in.
    */
   private TcpConnection connection;
   
   /**
    * Creates a new UserData object.
    * 
    * @param name Name of the user.
    * @param password Password of the user.
    * @param credits Initial credit value of the user.
    */
   public UserData(String name, String password, int credits)
   {
      this.name = name;
      this.password = password;
      this.credits = credits;
   }
   
   /**
    * Name of the user.
    */
   public String getName()
   {
      return name;
   }
   
   /**
    * Password of the user.
    */
   public String getPassword()
   {
      return password;
   }
   
   /**
    * Credits of the user.
    */
   public int getCredits()
   {
      return credits;
   }
   
   /**
    * Gets the connection.
    * 
    * @return The connection.
    */
   public TcpConnection getConnection()
   {
      return connection;
   }
   
   /**
    * Indicates if this user is currently logged in.
    */
   public Boolean isLoggedIn()
   {
      return isLoggedIn;
   }
   
   /**
    * Indicates if this user is currently authenticated via a
    * specific connection.
    * 
    * @param connection The TcpConnection to check.
    */
   public Boolean isAuthenticated(TcpConnection connection)
   {
      return isLoggedIn && this.connection == connection;
   }
   
   /**
    * Adds a certain amount of credits.
    * 
    * @param credits Amount of credits to add.
    */
   public void AddCredits(int credits)
   {
      this.credits += credits;
   }
   
   /**
    * Removes a certain amount of credits.
    * 
    * @param credits Amount of credits to remove.
    */
   public void RemoveCredits(int credits)
   {
      this.credits -= credits;
   }
   
   /**
    * Sets this user into logged in state and remembers
    * the used connection socket.
    */
   public boolean Login(TcpConnection connection, String password)
   {
      if (this.password.equals(password))
      {
         this.connection = connection;
         isLoggedIn = true;
      }
      
      return isLoggedIn;
   }
   
   /**
    * Logs this user out.
    */
   public void Logout()
   {
      isLoggedIn = false;
      connection.Disconnect(); 
      connection = null;
   }
}
