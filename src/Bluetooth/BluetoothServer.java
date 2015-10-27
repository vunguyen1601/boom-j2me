package Bluetooth;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

import MainGame.TestMIDlet;


public class BluetoothServer implements Runnable {
 
    private boolean listening = true;
    private LocalDevice local_device;
    private TestMIDlet midlet;
    private String deviceName;
    private StreamConnection streamConnection ;
    private DataInputStream inputStream ;
    private DataOutputStream outputStream ;
    private Thread serverThread ;
  
    private byte dataSends[] = new byte[2] ;
    private byte dataReceives[] = new byte [2] ;
    
    private int bombmanSend[] = new int[6] ;
    private int bombmanReceive[] = new int[6] ;
    
    private boolean isSend ;
    private boolean isReceive ;
    
    /** Creates a new instance of BluetoothServer */
    public BluetoothServer(TestMIDlet midlet) {
        this.midlet = midlet;
        
        serverThread = new Thread(this);
        serverThread.start();
        
    }
 
    public void run(){
    	midlet.debugConsole("server running ... ") ;
        try {
            local_device = LocalDevice.getLocalDevice();
            DiscoveryAgent disc_agent = local_device.getDiscoveryAgent();
            local_device.setDiscoverable(DiscoveryAgent.LIAC);
            String service_UUID = "00000000000010008000006057028A06";
            deviceName = local_device.getFriendlyName();
            String url = "btspp://localhost:" + service_UUID + ";name=" + deviceName;
            
            midlet.debugConsole("server initialize ...") ;
            
            StreamConnectionNotifier notifier = (StreamConnectionNotifier)Connector.open(url);
            midlet.debugConsole("server- connector.open(url)") ;
            midlet.setAlert("Starting server /nwait for the client ");
            streamConnection = notifier.acceptAndOpen();
            inputStream  = new DataInputStream(streamConnection.openInputStream()) ;
            outputStream = new DataOutputStream(streamConnection.openOutputStream()) ;
            midlet.debugConsole("notifier.acceptAndOpen()") ;
           
  /*          // test    
            while (listening) {
                if (con.ready()){
                	
                    send("Hello client, my name is: " + getName());
                	 
                    byte[] b = new byte[1000];
                    con.receive(b);
                    String s = new String(b, 0, b.length);
                    System.out.println("Recieved from client: " + s.trim());
            //      midlet.setAlert(s.trim());
                   
                    listening=false;
                }
            }
    */      
                   	
            // test
            midlet.debugConsole("Server send : 100"  ) ;
            byte b = 100 ;
            outputStream.writeByte(b) ; 
           
            byte rB = inputStream.readByte() ;
   	
            midlet.debugConsole("Server receive : " + rB) ;	           	           	                            	
            
	        midlet.showGameMapScreen(true) ;     
	      
            comunicate() ;
 
        } catch(BluetoothStateException e){
        	System.out.println(e);
        } catch(IOException f){
        	System.out.println(f);
        }

    }
    
    private void comunicate() throws IOException{
    	while( listening ){
    		outputStream.writeInt(bombmanSend[0]) ; 
    		outputStream.writeInt(bombmanSend[1]) ;
    		outputStream.writeInt(bombmanSend[2]) ;  	
    		outputStream.writeInt(bombmanSend[3]) ;
    		outputStream.writeInt(bombmanSend[4]) ;
    		outputStream.writeInt(bombmanSend[5]) ;

    		bombmanReceive[0] = inputStream.readInt() ;
    		bombmanReceive[1] = inputStream.readInt() ;   		                      
    		bombmanReceive[2] = inputStream.readInt() ;
    		bombmanReceive[3] = inputStream.readInt() ;
    		bombmanReceive[4] = inputStream.readInt() ;
    		bombmanReceive[5] = inputStream.readInt() ;
    	}
    }
    /*
    public void send(String s){
        byte[] b = s.getBytes();
        try {
            con.send(b);
        } catch(IOException e){
            System.out.println(e);
        }
    }
    
    public String receive(){  	
    	String s = "" ;
    	try {
			if( con.ready() ){
				byte [] b = new byte[1000] ;
				con.receive(b) ;
				s = new String(b,0,b.length) ;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	return s.trim();
    }
   */ 
    
    public synchronized void sendByte(byte b){
    	bombmanSend[5] = b ;
    }
    
    public synchronized byte receiveByte(){
    	return (byte) bombmanReceive[5] ;
    }
    
    public void sendTwoByte(byte bs[]){  	
    	dataSends[0] = bs[0] ;
    	dataSends[1] = bs[1] ;
    }
    
    public byte[] receiveTwoByte(){		
		return dataReceives ;
    }
    
    public synchronized void sendBomman(int infos[]){
    	bombmanSend[0] = infos[0] ;
    	bombmanSend[1] = infos[1] ;
    	bombmanSend[2] = infos[2] ;
    	bombmanSend[3] = infos[3] ;
    	bombmanSend[4] = infos[4] ;
    }
    
    public synchronized int [] getBomman(){
    	return bombmanReceive ;
    }
 /*   
    public void sendInt(int i){
	   	
    	try{
    		
    		byte datas[] = intToFourBytes(i) ;
    		con.send(datas) ;
			  		
    	}catch (IOException e) {
    		e.printStackTrace() ;
		}
    }
    
  public int receiveInt(){
    	byte datas[] = null;
    	int ret = 0 ;
    	try{
    		
    	  	if( con.ready()){    	  		
    	  		con.receive(datas);
    	  		ret = parseInt(datas) ;
    	  	}
			  		
    	}catch (IOException e) {
    		e.printStackTrace() ;
		}
    	
    	return ret;
    }
   */ 
  public int parseInt(byte[] data) throws IOException {
	  return 1 ;
  }

	  /**
	   * Uses an output stream to convert an int to four bytes.
	   */
  public byte[] intToFourBytes(int i) throws IOException {
	    ByteArrayOutputStream baos = new ByteArrayOutputStream(4);
	    DataOutputStream dos = new DataOutputStream(baos);
	    dos.writeInt(i);
	    baos.close();
	    dos.close();
	    byte[] retArray = baos.toByteArray();
	    return(retArray);
  }
    
    private String getName(){
        return deviceName;
    }
    
    public boolean isSend() {
		return isSend;
	}
    
    public boolean isReceive() {
		return isReceive;
	}
    
    public DataInputStream getInputStream() {
		return inputStream;
	}
    
    public DataOutputStream getOutputStream() {
		return outputStream;
	}
}
