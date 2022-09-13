//This code receives and processes basic TCP packets and calculates delay between 2 packet arrivals.

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class Recv {
    private Socket socket = null;
    private InputStream inputStream = null;
    private List<Packet> packetList = null;	
    public FileOutputStream fos;   
    public Recv (String host, int port) throws IOException {
        try{
          socket = new Socket(host, port);
          inputStream = socket.getInputStream();
          System.out.println("Connected to " + socket.getInetAddress().getHostAddress() + " on port "+port);
        }catch(UnknownHostException u){
          System.out.println(u);
        }catch(IOException i){
          System.out.println(i);
        }
    }
    public void close() throws IOException {
        try{
          inputStream.close();
          socket.close();
	        fos.close();
        }catch(IOException i){
          System.out.println(i);
        }
    }
    public int receivePacket(String fname) throws IOException {
        byte[] buff = new byte[1506];
		    int nPackets = 0;                 
        int totalBytes=0;
		    byte[] seqNo = new byte[4];
		    byte[] len = new byte[2];
		    byte[] payload = new byte[1500];
        long totalTime=0;
        long timeDelay;
        long timeStart=System.currentTimeMillis();
		    while(inputStream.read(buff,0,1506) != -1) {
          nPackets ++;
          timeDelay = System.currentTimeMillis();
          timeDelay = timeDelay - timeStart;
	  timeStart = System.currentTimeMillis();
          totalTime += timeDelay;
          System.out.println("Pkt "+nPackets+" delay = "+timeDelay+" ms");
		      int i = 0;
		      for(; i<4; i++) {
			      seqNo[i] = buff[i];		
		      }
		      for(; i<6; i++) {
			      len[i-4] = buff[i];	      
          }
		      for(; i<1506; i++) {
			      payload[i-6] = buff[i];
          }
		      Packet Pkt = new Packet(payload,seqNo,len);
          totalBytes+=(int)Pkt.getLen();
		      Pkt.saveToFile(fos);
        }
        if(nPackets>0)
		      System.out.println("Total "+nPackets+" packets / "+totalBytes+" bytes recd. Total delay = "+totalTime+" ms, average = "+(totalTime/nPackets)+" ms");
        return nPackets;
    }   
    void WriteToFile(String fileName) throws IOException {  // Write this packet to File
        File fname = new File(fileName);
        if(fname.createNewFile()) {
		      fos = new FileOutputStream(fname, true);
		    }
	      else{
	 	      System.out.println("File already exists. Exiting");
		      System.exit(0);		
		    }
	  }
    public static void main(String[] args) throws IOException {
        if(args.length != 3) {
            System.out.println("Usage: host  port filename");
            return;
        }
        String hostname = args[0], fileName = args[2];
        int port = Integer.parseInt(args[1]);
        Recv recv = new Recv(hostname, port);
        // your code here, 4 lines max
        recv.WriteToFile(fileName);
	      recv.receivePacket(fileName);
		    recv.close();
    }
}
class Packet {
    private byte[] buff = new byte[1500];
    private int seqNo;
    private short len;
    public Packet(byte[] buff1, byte[] seqNo1, byte[] len1) {
  	seqNo = ToInt(seqNo1);
  	len = ToShort(len1);
  	for (int i =0; i<(int)len; i++) {
  	buff[i] = buff1[i];
  	}} 
  	
    private short ToShort(byte[] b) {
       return (short)(((b[0] & 0xFF)<<8)+(b[1]&0xFF));
    }
    private int ToInt(byte[] b) {
       return ((b[0] << 24)+ ((b[1] & 0xFF) << 16)+ ((b[2] & 0xFF) << 8)+ (b[3] & 0xFF));
    }

    public int getSeqNo() { return seqNo; }
    public short getLen() { return len; }
    public void saveToFile(FileOutputStream fos) throws IOException {
    	fos.write(buff, 0, (int)len);
    }
}
