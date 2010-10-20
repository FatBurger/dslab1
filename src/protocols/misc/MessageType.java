package protocols.misc;

/**
 * Contains possible results for read operations from the client.
 * 
 * @author RaphM
 */
public enum MessageType
{
   /**
    * Read message is for console output
    */
   Console,
   
   /**
    * Read message is a file
    */
   File,
   
   /**
    * Read message is a force logoff message
    */
   ForceLogoff,
   
   /**
    * Result of a list files operation
    */
   FileList,
   
   /**
    * Request for a file names list.
    */
   FileNamesRequest,
   
   /**
    * Request for download info.
    */
   DownloadInfoRequest,
   
   /**
    * File size info message
    */
   FileSizeInfo,
   
   /**
    * Download request message.
    */
   DownloadRequest,
}
