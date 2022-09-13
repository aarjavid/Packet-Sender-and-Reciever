import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;;
import java.io.File;
import java.io.OutputStream;
/* include any additional imports below this line */
import java.io.*;
import java.net.*;
import java.io.FileNotFoundException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class Sender2 {
    private Socket socket = null;
    private ServerSocket server = null;
    public OutputStream opt;
    
    public static void main(String[] args) throws IOException {
        
        if (args.length != 2) {
            System.err.println("<port number>, <file name>");
            System.exit(1);
        }
        int portNumber = Integer.parseInt(args[0]);
        String fileName = args[1];
        Path filePath = Paths.get(fileName);
        long fileSize = Files.size(filePath);
        System.out.format("File Size: %d bytes", fileSize); 
         

        Sender2 send = new Sender2(portNumber);
        send.sendPackets(fileName, fileSize);
        send.close();
        //private Socket socket = null;
        //private ServerSocket server = null;
        //public DataOutputStream out;
    }
        public Sender2(int port) throws IOException {
          try {
            server = new ServerSocket(port);
            System.out.println("Sender listening on port" + port);
            socket = server.accept(); 
            opt = socket.getOutputStream();
            //DataOutputStream out = new DataOutputStream(socket.getOutputStream());    
          }catch (FileNotFoundException f){
            System.out.println(f.getMessage());
          }catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port "
              + port + " or listening for a connection");
            System.out.println(e.getMessage());
          }
     }
    
    public void close() throws IOException 
    {
        try{
          opt.close();
          socket.close();
        }catch(FileNotFoundException e){
          System.out.println(e);
        }catch(IOException i){
          System.out.println(i);
        }
    } 

    public void sendPackets(String fName, long fsize) throws IOException{
        //DataOutputStream out;
	      byte[] buff = new byte[1506];
      	int totalBytes = 0;
        int currentPosition = 0;
 //     byte[] seqNo = new byte[4];
        int seqNum = 0;
 //    	byte[] len = new byte[2];   
        byte[] payload = new byte[1500];
      	PacketC pkt;
        byte[] array = Files.readAllBytes(Paths.get(fName));
        
        int arrlen = array.length;
        System.out.println(arrlen);
        // Situation where the file is empty
        if(arrlen == 0) {
          // sending byte[] payload, int seqNum, short 
          //Packet pkt = new Packet(payload,seqNum,(short)0);
          pkt = new PacketC(payload,seqNum,(short)0);
          buff = pkt.getPacket();
          //send an emtpy packet here SocketProgramming
          opt.write(buff);
          opt.flush();
          //socket.close();
        }
        short j = 0;
        while(arrlen != 0)
        {
            for (j = 0; j < 1500 && arrlen>0 ; j++,arrlen--)
            {
              payload[j] = array[currentPosition];
              currentPosition++;
            }
            seqNum++;
           
             //Packet pkt = new Packet(payload,seqNum,j);
            pkt = new PacketC(payload,seqNum,j);
            buff = pkt.getPacket();
             
             
             //send packet here SocketProgramming
             System.out.println(buff.length);
             opt.write(buff);
             opt.flush();
             try{
        Thread.sleep(2);
        }catch(InterruptedException ex)
        {
          Thread.currentThread().interrupt();
        }
            }
          // socket closes once all the byff are flushed
          //socket.close();            
    }    
}  

   class PacketC {
        private byte[] buff = new byte[1506];
        private byte[] seqNo;
        private byte[] len;
        
    	// Ctor2 - ask Vishnu if you can't figure out what this is for!
        public PacketC(byte[] buff1, int seqNo1, short len1) 
        {
          seqNo = IntToByte(seqNo1);
          len = ShortToByte(len1);
          for (int i = 0; i<4; i++) {
	  	      buff[i] = seqNo[i];
	        }
	        for (int i = 4; i<6; i++) {
		        buff[i] = len[i-4];
	        }
         System.out.println(len1);
	        for (int i=0; i<(int)len1; i++) {
	  	      buff[i+6] = buff1[i];		
	        }
        }
	
	      private byte[] ShortToByte(short len2) {
                // your code here: must use this to convert short to byte[]
      		 byte[] output = new byte[] {(byte)(len2 >>> 8), (byte)len2};
      		 return output;
    	  }
    	  private byte[] IntToByte(int seqNo2) {
          // your code here: must use this to convert int to byte[]
       		byte[] output = new byte[] {(byte)(seqNo2 >>> 24), (byte)(seqNo2 >>> 16), (byte)(seqNo2 >>> 8), (byte)(seqNo2)};
       		return output;
  	}
	public byte[] getSeqNo() { return seqNo; } 
 
	    
	public byte[] getLen() { return len; }

	public byte[] getPacket() { return buff; }
   
}
