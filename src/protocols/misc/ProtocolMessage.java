package protocols.misc;

/**
 * Contains possible read results the client may get.
 * 
 * @author RaphM
 */
public class ProtocolMessage
{
   /**
    * The received textual content.
    */
   private String content;
   
   /**
    * The received filename if this instance is a file.
    */
   private String fileName;
   
   /**
    * The result type.
    */
   private MessageType resultType;
   
   /**
    * Creates a ClientReadResult that represents a file.
    * 
    * @param fileName The file name.
    * @param content The file content.
    */
   public ProtocolMessage(String fileName, String content)
   {
      // init a file result
      this.content = content;
      this.fileName = fileName;
      this.resultType = MessageType.File;
   }
   
   /**
    * Creates a ClientReadResult that represents console output.
    * 
    * @param text The output text.
    */
   public ProtocolMessage(String text)
   {
      this.content = text;
      this.resultType = MessageType.Console;
   }
   
   /**
    * Creates a ClientReadResult that represents another type.
    * 
    * @param type The custom message tpy.e
    */
   public ProtocolMessage(MessageType type)
   {
      this.resultType = type;
   }
   
   /**
    * @return The received textual content.
    */
   public String getContent()
   {
      return content;
   }
   
   /**
    * @return The received filename if this instance is a file.
    */
   public String getFileName()
   {
      return fileName;
   }
   
   /**
    * @return The result type.
    */
   public MessageType getResultType()
   {
      return resultType;
   }
}
