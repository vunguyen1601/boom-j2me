package FontEnd;

import Bluetooth.BluetoothClient;
import Bluetooth.BluetoothServer;
import java.io.IOException;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import BoomGame.ImageStock;
import Original.ImageUtility;
import  MainGame.TestMIDlet;
import Bluetooth.BluetoothClient;
import Bluetooth.BluetoothServer;
import Bluetooth.DataTransmiting;

public class MenuScreen extends Canvas
					    implements Runnable
{
	private final static int SPACE_HEIGHT = 10 ;
	private int currOption ;

	private String menuStrs[] ={"New game","load game","High Scores","multiplayer","Instructions","About","exit"} ;
    private Image sao[];

	private final static int bombSequences[] = {12,13,14,15} ;
	private Image imgBomb[] ;
	private int currFrame ;

	private Image imgBrk ;

	private Softkey softkey ; 
	private GameFont font ;

	private TestMIDlet midlet ;

	private int shouldStop ;
    private int icurrsao;
    private boolean  isBluetoohMode=false; 
    private BluetoothServer server ;
    private BluetoothClient client ;
    
	public MenuScreen(TestMIDlet mid) throws IOException{
		super() ;
		setFullScreenMode(true) ;
		midlet = mid ;
		init();
	}

    public MenuScreen(TestMIDlet mid,BluetoothServer sserver,BluetoothClient sclient) throws IOException{
		super() ;
		setFullScreenMode(true) ;
		midlet = mid ;
		init();
                 isBluetoohMode=true;
                 server=sserver;
                 client=sclient;

	}
    private void init() throws IOException{
                softkey = Softkey.getInstance() ;
		imgBrk = ImageStock.getImgMenuBrk() ;
		font = GameFont.getInstance() ;
		imgBomb = ImageStock.getImgsExplosionBomb(20,20) ;
                sao=new Image[5];
                sao[0]=Image.createImage("/sao3.png");
                sao[1]=Image.createImage("/sao2.png");
                sao[2]=Image.createImage("/sao3.png");
                sao[3]=Image.createImage("/sao2.png");
                sao[4]=Image.createImage("/sao3.png");
                if(midlet.getmussic())
                    Media.getInstance().Startmedia();
    }
	protected void showNotify() {
		Thread t =new Thread(this) ;
		shouldStop = -1 ;
		t.start() ;
              
	}

	protected void hideNotify() {
		shouldStop = 0 ;
	}

	protected void keyPressed(int keyCode) {		
        System.out.println("menuCanvas right soft key"+keyCode);
               // int gameKey = getGameAction(keyCode) ;
        System.out.println("menuCanvas right soft key"+keyCode);
        if( !isBluetoohMode ){
			processKeyCode(keyCode) ;
		}
		else{
			// bluetoothServerProcessing(keyCode) ;
			processSender(keyCode) ; 
		}

	}
	
	public void processKeyCode(int keyCode){
                    int length=menuStrs.length;
                    if(isBluetoohMode){
                    length =1;
                    }
		  if( keyCode == getKeyCode(UP) ){
			currOption-- ;
			if( currOption < 0 ){
				currOption = length - 1  ;
			}
		}
		else if(  keyCode == getKeyCode(DOWN) ){
			currOption++ ;
			if( currOption >= length){
				currOption = 0 ;
			}
		}else if(  keyCode == getKeyCode(FIRE) || softkey.isRightSoftkey(keyCode) ){
			if( menuStrs[currOption].compareTo("New game") == 0 ){
				shouldStop = 0 ;
			}else if( menuStrs[currOption].compareTo("About") == 0 ){
				shouldStop = 5 ;
			}else if( menuStrs[currOption].compareTo("exit") == 0 ){
				shouldStop = 6 ;
			}else if( menuStrs[currOption].compareTo("High Scores") == 0 ){
				shouldStop = 2 ;
			}else if(menuStrs[currOption].compareTo("continue game") == 0 ){
				shouldStop = 7 ;
			}else if( menuStrs[currOption].compareTo("load game") == 0 ){
				shouldStop =1 ;
			}else if( menuStrs[currOption].compareTo("Instructions") == 0 ){
				shouldStop =4 ;
			}else if( menuStrs[currOption].compareTo("multiplayer") == 0 ){
				shouldStop =8 ;
			}
		}else if( softkey.isLeftSoftkey(keyCode) ){
			System.out.println("menuCanvas left soft key");
			midlet.Exitmidlet();
		}
	}
	
	protected void paint(Graphics g) {
		int widthCanvas = getWidth() ;
		int heightCanvas = getHeight() ;

		int xDrawing = ( widthCanvas >> 1 ) - ( imgBrk.getWidth() >> 1 ) ;
		int yDrawing = 0 ;

		g.setColor(0) ;
		g.fillRect(0,0,widthCanvas,heightCanvas) ;
		g.drawImage(imgBrk,
					xDrawing, yDrawing,
					ImageUtility.GRAPHICS_TOP_LEFT) ;
                // g.fillRect(0, imgBrk.getHeight()>>1,widthCanvas,heightCanvas) ;
                g.drawImage(sao[icurrsao],
					50, 50,
					ImageUtility.GRAPHICS_TOP_LEFT) ;
		// draw the menu font
		xDrawing += imgBrk.getWidth() >> 2 ;
		yDrawing =  90 ;
               // g.fillRoundRect(xDrawing,yDrawing,20,20, 20, 20);
		int length = menuStrs.length ;
                if(isBluetoohMode){
                    length =1;
                }
		for( int index = 0 ; index < length ; index++){
			font.drawString(g,menuStrs[index],xDrawing,yDrawing) ;
			yDrawing += 10 ;
		}

		xDrawing -= 20 ;
		yDrawing  = 85 + (currOption*10) ;
		g.drawImage(imgBomb[bombSequences[currFrame]],
					xDrawing,yDrawing,
					ImageUtility.GRAPHICS_TOP_LEFT) ;

		softkey.drawLeftSoftkey(g, "Exit", widthCanvas, heightCanvas) ;
		softkey.drawRightSoftkey(g, "OK", widthCanvas, heightCanvas) ;
	}

	public void run() {
		while( true ){
	
			if( shouldStop !=- 1) {
				break ;
			}

			repaint() ;
			
            icurrsao++ ;
			if( icurrsao >= 4){
				icurrsao = 0 ;
			}
			currFrame++ ;
			if( currFrame >= bombSequences.length){
				currFrame = 0 ;
			}
			//
			// System.out.println("Repaint") ;
			try {
				synchronized (this) {
					wait(100L) ;
				}
			}
			catch(InterruptedException iEX){
				iEX.printStackTrace() ;
			}

		}					

	if(shouldStop==0){
                    midlet.setCurrLevel(1);
                    midlet.showGameMapScreen(isBluetoohMode) ;
                }else if(shouldStop==5)
                  midlet.showGameAboutScreen()  ;
                 else if(shouldStop==4)
                  midlet.showGameInstrucScreen()  ;
                else if(shouldStop==6)
                    midlet.Exitmidlet();
                 else if(shouldStop==2)
                    midlet.showhighscore();
                 else if(shouldStop==1){
                	 int level = midlet.Loadsavegame() ;
                         //midlet.debugConsole("Test level:"+level);
                	 midlet.setCurrLevel(level) ;
                	 midlet.showGameMapScreen(isBluetoohMode) ;
            	 } else if(shouldStop==7){
                        Media.getInstance().Stopmedia();
                        midlet.showGameScreen() ;
                       

            	 }
                 else if( shouldStop == 8 ){
            		 midlet.showBlutoothList() ;
            	 }
		
	}
	
	public void processSender(int keyCode){
		processKeyCode(keyCode) ;
		sendMessage(shouldStop) ;			
	}
	
	public void sendMessage(int msg){

   		byte dataSend = DataTransmiting.KEY_NONE ;

   		if( msg == 7 ){ // continue game
   			dataSend = DataTransmiting.MESSAGE_CONTINUE_GAME ;
   		}
   		else if( msg == 6){ //exit game
   			dataSend = DataTransmiting.MESSAGE_EXIT_GAME ;
   		}

   		if( server != null ){
   			server.sendByte(dataSend);
   		}
   		else if( client != null ){
   			client.sendByte(dataSend);
   		}

   	}

	public void setContinueString(){
		menuStrs[0] = "continue game" ;
	}
       
}
