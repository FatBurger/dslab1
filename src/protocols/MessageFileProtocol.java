package protocols;

import java.io.*;
import java.util.Vector;

import protocols.misc.MessageType;
import protocols.misc.ProtocolMessage;

/**
 * Provides methods for convenient TCP communication between
 * clients, proxy and fileservers.
 * 
 * @author RaphM
 */
public class MessageFileProtocol
{
   /**
    * Marker element for protocol information.
    */
   private final String PROTOCOL_PLAIN_MARKER = "!";
   
   /**
    * Marker element for the case that the plain marker element
    * is present in plain text. 
    */
   private final String PROTOCOL_ENCODE_MARKER = PROTOCOL_PLAIN_MARKER + "_";
   
   /**
    * First header element for file messages.
    */
   private final String PROT_FILE_NAME = PROTOCOL_PLAIN_MARKER + "FILE_NAME";
   
   /**
    * Second header element for file messages.
    */
   private final String PROT_FILE_CONTENT = PROTOCOL_PLAIN_MARKER + "FILE_CONTENT";
   
   /**
    * Trailer for file messages.
    */
   private final String PROT_TEXT_END = PROTOCOL_PLAIN_MARKER + "FILE_END";
   
   /**
    * Force logoff protocol message.
    */
   private final String PROT_FORCE_LOGOFF = PROTOCOL_PLAIN_MARKER + "FORCE_LOGOFF";
   
   /**
    * Indicates the start of a filenames list.
    */
   private final String PROT_FILENAMES_START = PROTOCOL_PLAIN_MARKER + "LISTRESULT";
   
   /**
    * Indicates the end of a filenames list.
    */
   private final String PROT_FILENAMES_END = PROTOCOL_PLAIN_MARKER + "LISTRESULT_END";
   
   /**
    * Indicates a download info request.
    */
   private final String PROT_REQUEST_DOWNLOADINFO = PROTOCOL_PLAIN_MARKER + "INFO";
   
   /**
    * Indicates a download info request end.
    */
   private final String PROT_REQUEST_DOWNLOADINFO_END = PROTOCOL_PLAIN_MARKER + "INFO_END";
   
   /**
    * Indicates a file size message.
    */
   private final String PROT_REQUEST_FILESIZE = PROTOCOL_PLAIN_MARKER + "SIZE_START";
   
   /**
    * Indicates a file size message.
    */
   private final String PROT_REQUEST_FILESIZE_END = PROTOCOL_PLAIN_MARKER + "SIZE_END";
   
   /**
    * Indicates a filename list request.
    */
   private final String PROT_REQUEST_FILENAMES = PROTOCOL_PLAIN_MARKER + "FILENAME_REQUEST";
   
   /**
    * Indicates a download request.
    */
   private final String PROT_REQUEST_DOWNLOAD = PROTOCOL_PLAIN_MARKER + "PERFORM_DOWNLOAD";
   
   /**
    * Client endpoint of the communication socket.
    */
   private BufferedReader clientConnectionPoint;
   
   /**
    * Server endpoint of the communication socket.
    */
   private PrintWriter serverConnectionPoint;

   /**
    * Constructor for client usage.
    * 
    * @param clientConnectionPoint Client endpoint of the communication socket.
    */
   public MessageFileProtocol(InputStream inputstream)
   {
      this.clientConnectionPoint = new BufferedReader(new InputStreamReader(inputstream));
   }
   
   /**
    * Constructor for server usage.
    * 
    * @param outputStream The output stream.
    */
   public MessageFileProtocol(OutputStream outputStream)
   {
      this.serverConnectionPoint = new PrintWriter(outputStream, true);
   }
   
   /**
    * Writes text into the socket.
    * 
    * @param text The text.
    */
   public void writeText(String text)
   {
      serverConnectionPoint.println(encode(text));
   }
   
   /**
    * Sends a force logoff message.
    */
   public void sendForceLogoff()
   {
      serverConnectionPoint.println(PROT_FORCE_LOGOFF);
   }
   
   /**
    * Sends a list filenames request.
    */
   public void sendFileNamesRequest()
   {  
      serverConnectionPoint.println(PROT_REQUEST_FILENAMES);
   }
   
   /**
    * Sends a list filenames request.
    */
   public void sendDownloadRequest()
   {  
      serverConnectionPoint.println(PROT_REQUEST_DOWNLOAD);
   }
   
   /**
    * Sends a file size request.
    * 
    * @param size
    */
   public void sendFileSizeInfo(long size)
   {
      serverConnectionPoint.println(PROT_REQUEST_FILESIZE);
      serverConnectionPoint.println(encode(String.valueOf(size)));
      serverConnectionPoint.println(PROT_REQUEST_FILESIZE_END);
   }
   
   public void sendFile(String fileName, String fileContent)
   {
      serverConnectionPoint.println(PROT_FILE_NAME);
      serverConnectionPoint.println(encode(fileName));
      serverConnectionPoint.println(PROT_FILE_CONTENT);
      serverConnectionPoint.println(encode(fileContent));
      serverConnectionPoint.println(PROT_TEXT_END);
   }
   
   /**
    * Sends a download info request.
    * 
    * @param fileName
    */
   public void sendDownloadInfoRequest(String fileName)
   {
      serverConnectionPoint.println(PROT_REQUEST_DOWNLOADINFO);
      serverConnectionPoint.println(encode(fileName));
      serverConnectionPoint.println(PROT_REQUEST_DOWNLOADINFO_END);
   }
   
   /**
    * Sends a filenames message.
    * 
    * @param fileNames The filenames.
    */
   public void sendFileNames(Vector<String> fileNames)
   {
      serverConnectionPoint.println(PROT_FILENAMES_START);
      
      for (String filename : fileNames)
      {
         serverConnectionPoint.println(encode(filename));
      }
      
      serverConnectionPoint.println(PROT_FILENAMES_END);
   }
   
   /**
    * Reads messages from the proxy and "unpacks" them into text or file
    * according to the protocol header strings.
    * 
    * @throws IOException 
    */
   public ProtocolMessage readMessage() throws IOException
   {
      ProtocolMessage result;
      String incomingLine = clientConnectionPoint.readLine();
      
      // also treat End of Stream as an IO exception
      if (incomingLine == null)
      {
         throw new IOException();
      }
      
      if (incomingLine.equals(PROT_FILE_NAME))
      {
         // received file header element, try to read the filename
         String fileName = readFileName();
         String fileContent = readFileContent();
         
         // fill return value with file information
         result = new ProtocolMessage(fileName, fileContent);
      }
      else if (incomingLine.equals(PROT_FILENAMES_START))
      {
         Vector<String> fileList = readFileList();
         
         // return filelist message
         result = new ProtocolMessage(fileList);
      }
      else if (incomingLine.equals(PROT_FORCE_LOGOFF))
      {
         // return force logoff message
         result = new ProtocolMessage(MessageType.ForceLogoff);
      }
      else if (incomingLine.equals(PROT_REQUEST_DOWNLOAD))
      {
         // return force logoff message
         result = new ProtocolMessage(MessageType.DownloadRequest);
      }
      else if (incomingLine.equals(PROT_REQUEST_DOWNLOADINFO))
      {
         String fileName = readInfoFileName();
         result = new ProtocolMessage(MessageType.DownloadInfoRequest, fileName);
      }
      else if (incomingLine.equals(PROT_REQUEST_FILESIZE))
      {
         long size = readFileSize();
         result = new ProtocolMessage(MessageType.FileSizeInfo, size);
      }
      else if (incomingLine.equals(PROT_REQUEST_FILENAMES))
      {
         // return force logoff message
         result = new ProtocolMessage(MessageType.FileNamesRequest);
      }
      else
      {
         // normal text message received, fill return value with text information
         result = new ProtocolMessage(decode(incomingLine));
      }
      
      return result;
   }
   
   /**
    * Tries to read the filename from the connection point
    * 
    * @returns The parsed file name.
    * @throws IOException 
    */
   private String readFileName() throws IOException
   {
      String fileName = "";
      String currentLine;
      
      // read until PROT_FILE_CONTENT is found
      // normally should only evaluate to one read because file names
      // are not allowed to contain line breaks
      // if line breaks are present the last line before PROT_FILE_CONTENT
      // will be used as filename
      while (!(currentLine = clientConnectionPoint.readLine()).equals(PROT_FILE_CONTENT))
      {
         fileName = currentLine;
      }
      
      return decode(fileName);
   }
   
   /**
    * Tries to read the filename from the connection point
    * 
    * @returns The parsed file name.
    * @throws IOException 
    */
   private String readInfoFileName() throws IOException
   {
      String fileName = "";
      String currentLine;
      
      // read until PROT_FILE_CONTENT is found
      // normally should only evaluate to one read because file names
      // are not allowed to contain line breaks
      // if line breaks are present the last line before PROT_FILE_CONTENT
      // will be used as filename
      while (!(currentLine = clientConnectionPoint.readLine()).equals(PROT_REQUEST_DOWNLOADINFO_END))
      {
         fileName = currentLine;
      }
      
      return decode(fileName);
   }
   
   /**
    * Tries to read the filesize from the connection point
    * 
    * @returns The parsed file size.
    * @throws IOException 
    */
   private long readFileSize() throws IOException
   {
      long size = -1;
      String currentLine;
      
      // read until PROT_REQUEST_FILESIZE_END is found
      // normally should only evaluate to one read because file names
      // are not allowed to contain line breaks
      // if line breaks are present the last line before PROT_REQUEST_FILESIZE_END
      // will be used as filename
      while (!(currentLine = clientConnectionPoint.readLine()).equals(PROT_REQUEST_FILESIZE_END))
      {
         try
         {
            size = Long.valueOf(decode(currentLine)).longValue();
         }
         catch (NumberFormatException e)
         {
            System.out.println("Received filesize has unexpected format!");
         }
      }
      
      return size;
   }
   
   /**
    * Tries to read the file content from the connection point
    * 
    * @return The parsed file content.
    * @throws IOException
    */
   private String readFileContent() throws IOException
   {
      StringBuilder fileContent = new StringBuilder();
      String currentLine;
      
      // read until PROT_FILE_END is found
      while(!(currentLine = clientConnectionPoint.readLine()).equals(PROT_TEXT_END))
      {
         fileContent.append(currentLine).append(System.getProperty("line.separator"));
      }
      
      // strip the last newline
      fileContent.setLength(fileContent.length() - System.getProperty("line.separator").length());
      
      return decode(fileContent.toString());
   }
   
   /**
    * Reads an incoming file list message.
    * 
    * @return the parsed message.
    * @throws IOException
    */
   private Vector<String> readFileList() throws IOException
   {
      Vector<String> fileList = new Vector<String>();
      String currentLine;
      
      // read until PROT_FILENAMES_END is found
      while(!(currentLine = clientConnectionPoint.readLine()).equals(PROT_FILENAMES_END))
      {
         fileList.add(currentLine);
      }
      
      return fileList;
   }
   
   /**
    * Encodes text to avoid
    * conflicting with Protocol constants.
    * 
    * @param encodedText The plain text.
    * @return The encoded text.
    */
   private String encode(String plainText)
   {
      return plainText.replaceAll(PROTOCOL_PLAIN_MARKER, PROTOCOL_ENCODE_MARKER);
   }
   
   /**
    * Decodes text that was encoded to avoid
    * conflicting with Protocol constants.
    * 
    * @param encodedText The encoded text.
    * @return The decoded text.
    */
   private String decode(String encodedText)
   {
      return encodedText.replaceAll(PROTOCOL_ENCODE_MARKER, PROTOCOL_PLAIN_MARKER);
   }
}
