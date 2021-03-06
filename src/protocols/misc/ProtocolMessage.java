package protocols.misc;

import java.util.Vector;

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
    * List of files.
    */
   private Vector<String> fileList;
   
   /**
    * The file size.
    */
   private long fileSize;
   
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
   public ProtocolMessage(Vector<String> fileList)
   {
      this.resultType = MessageType.FileList;
      this.fileList = fileList;
   }
   
   /**
    * Creates a ClientReadResult that represents another type.
    * 
    * @param type The custom message tpye.
    */
   public ProtocolMessage(MessageType type)
   {
      this.resultType = type;
   }
   
   /**
    * Creates a ClientReadResult that represents another type.
    * Also sets the filename
    * 
    * @param type The custom message type.
    */
   public ProtocolMessage(MessageType type, String filename)
   {
      this.resultType = type;
      this.fileName = filename;
   }
   
   /**
    * Creates a ClientReadResult that represents another type.
    * Also sets the file size
    * 
    * @param type The custom message type.
    */
   public ProtocolMessage(MessageType type, long size)
   {
      this.resultType = type;
      this.fileSize = size;
   }
   
   /**
    * @return The received textual content.
    */
   public String getContent()
   {
      return content;
   }
   
   /**
    * @return The received filelist.
    */
   public Vector<String> getFileList()
   {
      return fileList;
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
   
   /**
    * Gets the filesize.
    * 
    * @return The stored file size.
    */
   public long getFileSize()
   {
      return fileSize;
   }
}
