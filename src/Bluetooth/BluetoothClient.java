package Bluetooth;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Vector;
import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.L2CAPConnection;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.DatagramConnection;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

import MainGame.TestMIDlet;


public class BluetoothClient implements Runnable {
 
    private InquiryListener inq_listener;
    private ServiceListener serv_listener;
    private boolean listening = true;
    private TestMIDlet midlet;
    private String deviceName;
    private StreamConnection streamConnection ;
    private DataInputStream inputStream ;
    private DataOutputStream outputStream ;
    private Thread clientThread ;   
    private byte dataSends[] = new byte[2] ;
    private byte dataReceives[] = new byte [2] ;
    
    private int bombmanSend[] = new int[6] ;
    private int bombmanReceive[] = new int[6] ;
    
    private boolean isSend ;
    private boolean isReceive ;
 
    /** Creates a new instance of BluetoothClient */
    public BluetoothClient(TestMIDlet midlet){
        this.midlet = midlet;
        clientThread = new Thread(this);
        clientThread.start();
               
    }
    
    public void run() {
        midlet.debugConsole("Starting client - please wait...");
        try {
            LocalDevice local_device = LocalDevice.getLocalDevice();
            DiscoveryAgent disc_agent = local_device.getDiscoveryAgent();
            local_device.setDiscoverable(DiscoveryAgent.LIAC);
            inq_listener = new InquiryListener();
            
            midlet.debugConsole("client initialize ..") ;
            synchronized(inq_listener)	{
                disc_agent.startInquiry(DiscoveryAgent.LIAC, inq_listener);
                try {
                	inq_listener.wait();
                } catch(InterruptedException e){
                	
                }
            }
            
            midlet.debugConsole("after inq_listener.wait() ") ;
 
            Enumeration devices = inq_listener.cached_devices.elements();
 
            UUID[] u = new UUID[]{new UUID( "00000000000010008000006057028A06", false )};
            int attrbs[] = { 0x0100 };
            serv_listener = new ServiceListener();
            
            while( devices.hasMoreElements() ) {
                synchronized(serv_listener)	{
                    disc_agent.searchServices(attrbs, u, (RemoteDevice)devices.nextElement(), serv_listener);
                    try {
                    	serv_listener.wait();
                    } catch(InterruptedException e){
                    	
                    }
                }
            } // while
            
            midlet.debugConsole("after serv_listener.wait();") ;
            
        } catch (BluetoothStateException e) {
        	System.out.println(e);
        }
        
        if (serv_listener.service!=null){
            try {
                String url;
                url = serv_listener.service.getConnectionURL(0, false);
                deviceName = LocalDevice.getLocalDevice().getFriendlyName();
                streamConnection = (StreamConnection) Connector.open( url );    
                inputStream  = new DataInputStream(streamConnection.openInputStream()) ;
                outputStream = new DataOutputStream(streamConnection.openOutputStream()) ;
                midlet.debugConsole("after Connector.open( url );") ;

  /*              
                byte[] b = new byte[1000];
                while (listening) {
                    if (con.ready()){
                        con.receive(b);
                        String s = new String(b, 0, b.length);
                        System.out.println("Received from server: " + s.trim());
                  //      midlet.setAlert(s.trim());
                        
                        send("Hello server, my name is: " + getName());
                        
                        listening = false;
                    }
                }
    */            
                
                // test 
                
            	
            	    
            	
            	byte b = inputStream.readByte() ;
                midlet.debugConsole("Client receive : " + b ) ;
                
                
                midlet.debugConsole("Client send : 99") ;
                b = 99 ; 
                sendByte(b) ;
                outputStream.writeByte(b) ; 
                
                midlet.showGameMapScreen(true) ;
                
                comunicate() ;            
                            
            } catch (IOException g) {
            	System.out.println(g);
            }
        }       
        
    }// run
    
    private void comunicate() throws IOException{
    	while( listening ){

    		bombmanReceive[0] = inputStream.readInt() ;
    		bombmanReceive[1] = inputStream.readInt() ;   		                      
    		bombmanReceive[2] = inputStream.readInt() ;
    		bombmanReceive[3] = inputStream.readInt() ;
    		bombmanReceive[4] = inputStream.readInt() ;
    		bombmanReceive[5] = inputStream.readInt() ;
    	
    		
    		outputStream.writeInt(bombmanSend[0]) ; 
    		outputStream.writeInt(bombmanSend[1]) ;
    		outputStream.writeInt(bombmanSend[2]) ;  	
    		outputStream.writeInt(bombmanSend[3]) ;
    		outputStream.writeInt(bombmanSend[4]) ;
    		outputStream.writeInt(bombmanSend[5]) ;
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
    	String s = "r" ;
    	try {
			if( con.ready() ){
				byte [] b = new byte[20] ;
				con.receive(b) ;
				s = new String(b,0,b.length) ;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
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
  public static int parseInt(byte[] data) throws IOException {
	    DataInputStream stream 
	      = new DataInputStream(new ByteArrayInputStream(data));
	    int retVal = stream.readInt();
	    stream.close();
	    return(retVal);
	  }

	  /**
	   * Uses an output stream to convert an int to four bytes.
	   */
  public static byte[] intToFourBytes(int i) throws IOException {
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

class InquiryListener implements DiscoveryListener {
    public Vector cached_devices;
    public InquiryListener() {
        cached_devices = new Vector();
    }
 
    public void deviceDiscovered( RemoteDevice btDevice, DeviceClass cod )	{
        int major = cod.getMajorDeviceClass();
        if( !cached_devices.contains( btDevice ) )	{
            cached_devices.addElement( btDevice );
            System.out.println("foundDevice");
        }
    }
 
    public void inquiryCompleted( int discType )	{
        synchronized(this){
        	this.notify();
        }
    }
 
    public void servicesDiscovered( int transID, ServiceRecord[] servRecord )	{
    	
    }
    
    public void serviceSearchCompleted( int transID, int respCode )	{
    	
    }  
}
 
class ServiceListener implements DiscoveryListener	{
    public ServiceRecord service;
    public ServiceListener()	{
    	
    }
 
    public void servicesDiscovered( int transID, ServiceRecord[] servRecord )	{
        service = servRecord[0];
        System.out.println("foundService");
    }
 
    public void serviceSearchCompleted( int transID, int respCode )	{
        synchronized( this ){	
        	this.notify();
        }
    }
 
    public void deviceDiscovered( RemoteDevice btDevice, DeviceClass cod ){
    	
    }
    
    public void inquiryCompleted( int discType ){
    	
    }
}
