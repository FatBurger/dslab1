package proxy.commands;

import java.util.Collection;

import proxy.userHandling.UserData;
import proxy.userHandling.UserManager;
import commandHandling.ICommand;

public class UserCommand implements ICommand
{
   /**
    * Command identifier of this instance.
    */
   private final String COMMAND = "!users";

   /**
    * User manager reference.
    */
   private final UserManager userManager;

   /**
    * Creates a new UserCommand instance.
    * 
    * @param userManager
    *           User manager reference.
    */
   public UserCommand(UserManager userManager)
   {
      this.userManager = userManager;
   }

   /**
    * Performs the command logic
    */
   public void Execute(String[] parameters)
   {
      if (parameters.length == 0)
      {
         Collection<UserData> users = userManager.getAllUsers();

         if (users != null && users.size() > 0)
         {
            // print out data for each user
            for (UserData user : users)
            {
               String onlineStatus = user.isLoggedIn() ? "online" : "offline";
               System.out.println(user.getName() + " " + onlineStatus
                        + " Credits: " + user.getCredits());
            }
         }
         else
         {
            System.out.println("No users found!");
         }
      }
      else
      {
         System.out.println("Wrong parameters - Usage: !users");
      }
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
