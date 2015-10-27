package FontEnd;

import java.io.IOException;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;

import Bluetooth.BluetoothClient;
import Bluetooth.BluetoothServer;
import Bluetooth.DataTransmiting;
import MainGame.TestMIDlet;

public class Warning extends Canvas implements Runnable{
	
    private BluetoothServer server ;
    private BluetoothClient client ;
    private TestMIDlet midlet ;
    private boolean shouldStop ;
    private GameFont font ;
    private String info ;
    private byte dataReceive ;
       private Softkey softkey ;
	public Warning(TestMIDlet mid) throws IOException {
		super() ;		
		setFullScreenMode(true) ;
		midlet = mid ;
		font = GameFont.getInstance() ;
                softkey = Softkey.getInstance() ;
	}	
	
	public void run() {

		while( true){
			
			if( shouldStop ){
				break ;
			}
			
			processReceiver() ;
			
			repaint() ;
			
			try {
				Thread.sleep(50l);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
				
		}

	}
	 protected void keyPressed(int keyCode) {
        System.out.println("menuCanvas right soft key"+keyCode);
               // int gameKey = getGameAction(keyCode) ;
        System.out.println("menuCanvas right soft key"+keyCode);

		if( softkey.isRightSoftkey(keyCode) ){
			midlet.showMenuScreen(false);
		}


	}
	public void exchangeData(BluetoothServer ser,BluetoothClient clie){
		client = clie ;
		server = ser  ;
		shouldStop = false ;
		Thread t = new Thread(this) ;
		t.start() ;
	}
	
	private byte receiveMessage(){
		dataReceive = DataTransmiting.KEY_NONE;
		if( server != null ){
			dataReceive = server.receiveByte() ;
		}
		else if( client != null ){
			dataReceive = client.receiveByte() ;
		}
		return dataReceive ;
	}
	
	private void processReceiver(){
		
		dataReceive = receiveMessage() ;
		if( dataReceive == DataTransmiting.MESSAGE_CONTINUE_GAME ){
			shouldStop = true ;
			midlet.showGameScreen() ;
		}
		else if( dataReceive == DataTransmiting.MESSAGE_EXIT_GAME){
			shouldStop = true ;
			midlet.Exitmidlet() ;				
		}
		else if( dataReceive == DataTransmiting.MESSAGE_PAUSE_GAME){
			// pause game message from bomb canvas. 
		}		
	
	}	

	protected void paint(Graphics g) {
		int xPaint = getWidth() >> 4 ; 
		int yPaint = getHeight() >> 2 ;
		
		g.setColor(0) ;
		g.fillRect(0,0,getWidth(),getHeight()) ;
		
		font.drawString(g,info,xPaint,yPaint) ;

              softkey.drawRightSoftkey(g, "Menu", getWidth(), getHeight()) ;
	}
	
	public void setString(String info){
		this.info = info ;
	}
}
