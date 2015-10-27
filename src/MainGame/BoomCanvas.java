package MainGame;

import java.io.IOException;
import java.util.Random;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.GameCanvas;
import javax.microedition.lcdui.game.Sprite;

import BoomGame.BoomManager;
import Bluetooth.DataTransmiting;
import Bluetooth.BluetoothClient;
import Bluetooth.BluetoothServer;
import BoomGame.BoomManager;

import BoomGame.ImageStock;
import FontEnd.GameFont;
import FontEnd.Softkey;
import Original.ImageUtility;

public class BoomCanvas extends GameCanvas
						implements Runnable
{
	private final static int MILLISECONDS_PER_FRAME = 50 ; // 20 fps
	
	public static long sumOfMillisecondsPauseGame ;
	private static long firstMilliseconds ;
	
	private TestMIDlet midlet ;

	private Graphics  g ; 
	/*
	private Image     imgOffScreenBuffer ;
	*/
	private int currKey ;
	
	// manage pause/stop the game.
	private boolean shouldStop ;
	private boolean shouldPause ;
	private Thread  gameThread ; 
	
	// manage the game state
	public final static int STATE_NONE = 0 ;
	public final static int STATE_INIT  = 1 ;
	public final static int STATE_READY = 2 ;
	public final static int STATE_COMPLETE_MISSON = 3;
	public final static int STATE_RUNNING = 4 ;
	public final static int STATE_LOSE = 5; // game over 
	public final static int STATE_WANT_TO_CONTINUE_DIALOG_ = 6 ;
	public final static int STATE_WAITING_PLAYER_INPUT = 7 ;
	public final static int STATE_LOCAL_NEXT_MISSION_DIALOG = 8;
	
	public final static int STATE_BLUETOOTH_SERVER_SHOW_DIALOG = 9 ;
	public final static int STATE_BLUETOOTH_CLIENT_SHOW_DIALOG = 10 ;
	private int currState ;
	
	// the position and the game screen size. 
	private int xGameScreen ;
	private int yGameScreen ;
	private int widthGameScreen ;
	private int heightGameScreen ;
	
	// The reference to game logic.
	private BoomManager gameManager ;
	
	// use to draw the game bar.
	private Image imgNumber ;
	private Image imgBar ;
	private Image imgNormalFace ;
	private Image imgSadFace ;
	private Image imgFaceDialog ;
	
	// use to draw game over state.
	private int xBeginFillingRect ;
	private int xEndFillingRect ;
	private int xFlagStop;
	
	// use to draw game ready state.
	private StringBuffer mission = new StringBuffer("") ;
	private int    x_pxRightMoving ;
	
	// use to draw the strings.
	private GameFont font ; 
	
	// draw the soft key.
	private Softkey softkey ; 

	// use to slow down the drawing.
	private int drawingCount ;
	
	// use to optimize the time drawing.
	private int oldMinute; 
	private int oldC ;
	
	//use to optimize the score drawing.
	private int score; 
	private int bufferScore ; // to animate 
	
	//use to limit number of player life drawing.
	private int oldLife ;
	
	//use to draw the dialog that the face talks
	private int currentFaceState = FACE_DIED;
	private final static int FACE_NONE = 0 ;
	private final static int FACE_DIED = 1 ;
	private final static int FACE_NEWLIFE = 2 ;
	private long milliTimeFaceTalk ;
	private boolean enableFaceTalk = true ;
	private int count ;
	
	// use to show the dialog
	private int xAccelerator = 2 ;
	private int yAccelerator = 2 ;
	private boolean isOpeningWidth ;
	
	//
	private boolean isTimeOut ;
	
	//manage the game when over.
	private boolean isGameOver ;
	//bluetooth mode.
	private BluetoothClient client ;
	private BluetoothServer server ;
	private boolean isBluetoothMode=false ;
	private byte messageReceive ;
	
	private int anotherOldLife ;
	private Image imgNormalRedFace ;
        private int Level;
	public BoomCanvas(TestMIDlet mid,int level) throws IOException {
		super(false);
		setFullScreenMode(true) ;
		
		g = getGraphics() ;
		
		midlet = mid ;
				

		loadResource(level) ;
		mission.append("STATE ") ;
	}	
	public BoomCanvas(TestMIDlet mid,int level,BluetoothServer server,BluetoothClient client) throws IOException{
		super(false) ;
		setFullScreenMode(true) ;

		midlet = mid ;

		this.server = server ;
		this.client = client ;
		isBluetoothMode = true ;

		loadResource(level) ;
                mission.append("STATE ") ;
	}

	private void loadResource(int level) throws IOException{
                if(midlet.getmussic())
                FontEnd.Media.getInstance().Stopmedia();
		g = getGraphics() ;

		if( !isBluetoothMode ){
			gameManager = new BoomManager(this,level) ;
		}
		else{
			gameManager = new BoomManager(this,level,server,client) ;
		}

		font = GameFont.getInstance() ;
		softkey = Softkey.getInstance() ;

		imgBar = ImageStock.getImgBar() ;
		imgNumber = ImageStock.getImgNumber() ;
		imgNormalFace = ImageStock.getImgNormalFace() ;
		imgNormalRedFace = ImageStock.getImgNormalRedFace() ;
		imgSadFace    = ImageStock.getImgSadFace() ;
		imgFaceDialog = Image.createImage("/dialog.PNG") ;
                Level=level;
		init(level) ;
	}
	public int getKeyInput(){
		return currKey ;
	}
	
	protected void showNotify() {
		//System.out.println("GameCanvas showNotify") ;
		if( gameThread == null ){
			gameThread = new Thread(this) ;
			
			gameThread.start() ;		
			
			// set the atribute for game ready.
			currState = STATE_INIT ;		
			
			x_pxRightMoving = xGameScreen ;
			
			mission.append("") ;
			mission.append(gameManager.getMissionLevel());
		}		
		else{ 
			//System.out.println("resume") ;
			resume(); 
			 
		}
	}
	
	protected void hideNotify() {
		pause() ;
	 
	}

	public void handle(){
		
		if( isBluetoothMode ){
			processReceiver() ;
		}
		
		switch (currState) {
		
		case STATE_RUNNING:			
			
			tick() ;	
			input() ;
			render(g,true) ;
	
			if( !isBluetoothMode ){
				drawScore(g) ;
			}
						
			drawTime(g) ;
			drawPlayerLife(g) ;
			if( isBluetoothMode ){
				drawAnotherPlayerLife(g) ;
			}
			break;
			
		case STATE_READY :
			render(g,true) ;
			drawGameReady(g) ;				
			break ;
		
		case STATE_LOSE :				
			drawGameOver(g) ;			
			break ;
			
		case STATE_INIT:
			drawGameBar(g) ;			
			drawClock(g) ;		
			drawTime(g) ;			
			drawPlayerLife(g) ;		
			if( !isBluetoothMode ){
				drawScore(g) ;
			}			
			else{
				drawAnotherPlayerLife(g) ;
			}
			// next state
			currState = STATE_READY ;
			break ;
					
		case STATE_WANT_TO_CONTINUE_DIALOG_: 
			showDialog(g," Do You want /nto continue ",true) ;
			break ;
		
		case STATE_BLUETOOTH_SERVER_SHOW_DIALOG:
			showDialog(g," Press yes to/n play again",true) ;
			break;
			
		case STATE_BLUETOOTH_CLIENT_SHOW_DIALOG:
			showDialog(g," please wait /nfor the server",false) ;
			break;
			
		case STATE_LOCAL_NEXT_MISSION_DIALOG : 
			showDialog(g,"  Press yes to/n next mission",true) ;
			break;
			
		case STATE_COMPLETE_MISSON:
			render(g,false);
			drawCompleteMission(g) ; 
			break ;
			
		case STATE_WAITING_PLAYER_INPUT:
			break ;
			
		case STATE_NONE :
			break ;
			
			
		default:
			break;
		}
	
		
		flushGraphics(xGameScreen,0,widthGameScreen,yGameScreen+heightGameScreen) ;
	}
	
	public void run() {	 
		
		while( true ){
			
			firstMilliseconds = System.currentTimeMillis() ;

			if( shouldStop ){
				break ;
			}
			
			while( shouldPause ){
				
				synchronized (this) {
					try {				
						wait() ;				
					} catch (InterruptedException ex) {
						ex.printStackTrace() ;
					}
				}	
			}
				
			handle() ;
			
			//reset the drawing count.
			if( drawingCount == 20 ){
				drawingCount = 0 ;
			}
			
			// get the elapsed millisecond.
			long et = System.currentTimeMillis() - firstMilliseconds ;
			if( et < MILLISECONDS_PER_FRAME){
				try {
					Thread.sleep(MILLISECONDS_PER_FRAME - et);
				} catch (InterruptedException e) {
					
					e.printStackTrace();
				}
			}
			else{
				Thread.yield() ;
			}
						
		}
		
		//System.out.println(" out of run()");
	}
	
	public synchronized void pause(){
		 shouldPause = true ;
	}
	
	public synchronized void resume(){
		shouldPause = false ;
		sumOfMillisecondsPauseGame += (System.currentTimeMillis() - firstMilliseconds) ;
		//System.out.println("deltaMillisecondsPauseGame = "+sumOfMillisecondsPauseGame);
		notify() ;
	}
	
	public synchronized void stop(){
		shouldStop = true ;
	}
	
	public void input(){
		gameManager.processInput() ;	
	}
	
	public void tick(){
		gameManager.tick() ;
	}
	
	// process the soft key.
	protected void keyPressed(int keyCode) {		
       if( !isBluetoothMode ){
			processKeyCode(keyCode) ;
		}
		else{
			processSender(keyCode) ;
		}
	}	
	
	public void processSender(int keycode){
		if( softkey.isRightSoftkey(keycode)) {	
			if( currState == STATE_WAITING_PLAYER_INPUT){
				
				if( server != null ){ // The server send the information that continue to play game to the client.
					sendMessage(DataTransmiting.MESSAGE_PLAY_GAME_AGAIN) ;
					midlet.showGameMapScreen(true) ; 
				}
				
			}
			else{ // another state
				
				sendMessage(DataTransmiting.MESSAGE_PAUSE_GAME) ;
				midlet.showMenuScreen(true) ;
				
			}
		}	
		else if( softkey.isLeftSoftkey(keycode)){
			if( currState == STATE_WAITING_PLAYER_INPUT){
				
				if( server != null ){
					sendMessage(DataTransmiting.MESSAGE_EXIT_GAME) ;
					midlet.Exitmidlet() ;
				}
			}
		}
	}
	
	public void processReceiver(){
		messageReceive = receiveMessage() ; 
		if( messageReceive == DataTransmiting.MESSAGE_PLAY_GAME_AGAIN ){
			if( client != null )// the client is being wait the server.
			{				
				midlet.showGameMapScreen(true) ;
			}		
		}
		else if( messageReceive == DataTransmiting.MESSAGE_PAUSE_GAME ){
			if( server != null ){
				midlet.waitForMessage("Client is busy ") ;
			}
			else if( client != null ){
				midlet.waitForMessage("Server is busy ") ;
			}	
		}
		else if( messageReceive == DataTransmiting.MESSAGE_EXIT_GAME ){
			if( client != null ){
				midlet.setAlert("The Server device/nexits") ;
			}
		}
	}
	
	public void processKeyCode(int keyCode){
		if( softkey.isRightSoftkey(keyCode)){
			if( currState == STATE_WAITING_PLAYER_INPUT ){
				if( gameManager.isCompleteMission()){
                                        midlet.setScore(gameManager.getScore());
                                        if(Level>=midlet.getCurrLevel()){
                                            midlet.savegame(midlet.getCurrLevel()+1) ;
                                            midlet.nextMission() ;
                                        }else{
                                            midlet.showGameMapScreen(false);
                                        }
					
                                      
					//System.out.println("save "+(midlet.getCurrLevel()+1)+"test"+midlet.Loadsavegame()) ;
					
				}else{
					//midlet.showGameMapScreen() ;
                                    midlet.showGameMapScreen(false);
				}

			}else{
				pause() ;
				midlet.showMenuScreen(false) ;
			}
		}
		else if( softkey.isLeftSoftkey(keyCode)){
			if( currState == STATE_WAITING_PLAYER_INPUT ){

                            midlet.setScore(gameManager.getScore());
                            midlet.showsavegame();
             
			}
		}

	}
	protected void keyReleased(int keyCode) {
		currKey = 0 ;
	}
	
	private void render(Graphics g,boolean isDrawSofkey){
		
		// clear the screen.
		g.setColor(0) ;
		g.fillRect(xGameScreen,yGameScreen,widthGameScreen,heightGameScreen);
		
		gameManager.drawn(g,xGameScreen,yGameScreen) ;	
		// reset the clipping region. 
		g.setClip(0,0,getWidth(),getHeight()) ;		 
		
		if( isDrawSofkey ){
			softkey.drawRightSoftkey(g,"menu",xGameScreen+widthGameScreen,yGameScreen+heightGameScreen) ;
		}		
	}
	
	public void init(int level) throws IOException{
		
		int widthCanvas = getWidth() ;
		int heightCanvas = getHeight() ;
		int widthMap = gameManager.getMapWidth() ;
		int heightMap = gameManager.getMapHeight() ;
				
		xGameScreen = 0 ;
		yGameScreen = 20 ;
		
		if( widthCanvas < widthMap ){
			widthGameScreen  = widthCanvas   ;
		}
		else{
			widthGameScreen = widthMap ;
			xGameScreen =  (widthCanvas >> 1) - (widthMap >> 1)  ;
		}
		
		if( heightCanvas < heightMap  ){
			heightGameScreen = heightCanvas - yGameScreen ;
		}
		else {
			heightGameScreen = heightMap  ; 
		}		 		
				
		gameManager.setDrawingSize(widthGameScreen,heightGameScreen);	
		
		gameManager.init(level) ;		
	}
	
	private void drawGameBar(Graphics g) {		
		int barWidth = 10 ,barHeight = 20 ;
		int numImage = widthGameScreen / barWidth;				
		int halfNumImage = numImage >> 1 ;
		Image img = imgBar ; 
		for( int index = 0 ; index < halfNumImage ; index++ ){
			g.drawRegion(img,
					     0, 0,
					     barWidth,barHeight, 
					     Sprite.TRANS_NONE, 
					     xGameScreen+barWidth*index,0,
					     ImageUtility.GRAPHICS_TOP_LEFT) ;
		}
		
		for( int index = halfNumImage ; index <= numImage ; index++ ){
			g.drawRegion(img,
						 barWidth, 0,
						 barWidth,barHeight,
						 Sprite.TRANS_NONE, 
						 xGameScreen+barWidth*index,0,
						 ImageUtility.GRAPHICS_TOP_LEFT) ;
	
		}  
	}
	
	private void drawTime(final Graphics g){

		int remainMilisecond = (int)(gameManager.getRemainingMilisecond() + sumOfMillisecondsPauseGame);   
		if( remainMilisecond < 0 ){
			if( !isBluetoothMode ){
				currState = STATE_LOSE ;
			}
			else{
				currState = STATE_COMPLETE_MISSON ;
			}
			
			isTimeOut = true ;
			xFlagStop = xGameScreen ;
			xBeginFillingRect = xGameScreen ;
			xEndFillingRect   = xGameScreen + widthGameScreen ;
			return ;
		}
		
		int elapsedSecond  = remainMilisecond / 1000  ; 
		int elapsedMinute = 0;
		if( elapsedSecond >= 60 ){
			elapsedMinute = elapsedSecond / 60 ; ;
			elapsedSecond = elapsedSecond % 60 ;			
		}
		
		// drawing ... 

		int numberWidth = 8, numberHeight = 14 ; 
		int clockWidth = 32 ;
		int xCenter = xGameScreen + (widthGameScreen >> 1) - (clockWidth >> 1) ; 
		
			if( elapsedMinute != oldMinute ){
				g.drawRegion(imgNumber,
						 clockWidth+elapsedMinute*numberWidth, 0,
						 numberWidth,numberHeight,
						 Sprite.TRANS_NONE, 
					     xCenter,1,
						 ImageUtility.GRAPHICS_TOP_LEFT) ;
				
				oldMinute = elapsedMinute ;  
			}
			
			
			int c  = (int)( elapsedSecond / 10) ;
			if( c != oldC ){
				g.drawRegion(imgNumber,
					     clockWidth+c*numberWidth, 0,
					     numberWidth,numberHeight,
					     Sprite.TRANS_NONE, 
				         xCenter+numberWidth*2,1,
					     ImageUtility.GRAPHICS_TOP_LEFT) ;
				oldC = c ;
			}
			
			int dv = (elapsedSecond % 10) ;
			
			// always draw.
			g.drawRegion(imgNumber,
				     clockWidth+dv*numberWidth, 0,
				     numberWidth,numberHeight,
				     Sprite.TRANS_NONE, 
			         xCenter+numberWidth*3,1,
				     ImageUtility.GRAPHICS_TOP_LEFT) ;	
			
	}
	
	private void drawClock(Graphics g){	
		int clockWidth = 32 , clockHeight = 14 ; 
		int xCenter = xGameScreen+ (widthGameScreen >> 1) - (clockWidth >> 1) ; ; // w = 10 ; h = 30;
		g.drawRegion(imgNumber,
				     0, 0,
				     clockWidth,clockHeight,
				     Sprite.TRANS_NONE, 
				     xCenter,1,
				     ImageUtility.GRAPHICS_TOP_LEFT) ;
		
	//	System.out.println("Minute : " + elapsedMinute +" Second : " + remainSecond);
	}
	
	private void drawScore(Graphics g){
		score = gameManager.getScore() ;		
		
		int alignWidth = xGameScreen + widthGameScreen - 10 ;		
		int numberWidth = 8, numberHeight = 14 ; 
		int clockWidth = 32 ;
		Image img = imgNumber ;
		if( score == 0 ){
			// draw the score in the first time.			
			for( int index = 0 ; index < 5 ;index++){				
				g.drawRegion(img,
							 clockWidth, 0,
							 numberWidth,numberHeight,
							 Sprite.TRANS_NONE, 
							 alignWidth-(numberWidth*index),1,
							 ImageUtility.GRAPHICS_TOP_LEFT) ;
								
			}
			
			return ;
			//
		}
		if( score != bufferScore ){ 
			
			int oldScore = bufferScore ;
				bufferScore += 10 ;
			int drawingScore = bufferScore ;
			int dv ;
			int oldDV ;
			for( int index = 0 ; index < 5 ;index++){
				
				dv = drawingScore % 10 ;
				drawingScore = drawingScore / 10 ;
				
				oldDV = oldScore % 10 ;
				oldScore     = oldScore / 10 ;
				
				if( dv != oldDV ){
					g.drawRegion(img,
							 clockWidth+dv*numberWidth, 0,
							 numberWidth,numberHeight,
							 Sprite.TRANS_NONE, 
							 alignWidth-(numberWidth*index),1,
							 ImageUtility.GRAPHICS_TOP_LEFT) ;
			
				} // if
				
			}// for
			
		}			
		
	}
	
	private void drawGameOver(Graphics g){
	
		int widthFillingMoving = 10 ; 
		int heightFillingRect = 5 ;	
		
		int xRightFillingRect = xBeginFillingRect ;
		int xLeftFillingRect =  xEndFillingRect - widthFillingMoving;
		int yFillingRect     =  0 ;/*yGameScreen ;*/
		
		int max_yFillingRect = heightGameScreen + yGameScreen ;
		int max_xFillingRect = widthGameScreen + xGameScreen ; 
		
		g.setColor(0) ;
		while( yFillingRect <  max_yFillingRect){			 						
			g.fillRect(xRightFillingRect,yFillingRect,
					  widthFillingMoving,heightFillingRect);
		
			yFillingRect += heightFillingRect ;
			
			g.fillRect(xLeftFillingRect,yFillingRect,
					   widthFillingMoving,heightFillingRect) ;
			
			yFillingRect += heightFillingRect ;
		}
		
		xBeginFillingRect += widthFillingMoving ;
		xEndFillingRect   -= widthFillingMoving ;
		
		if( xBeginFillingRect >= max_xFillingRect ){
			xBeginFillingRect = xGameScreen ;			
		}
		if( xEndFillingRect <= xGameScreen ){			
			xEndFillingRect = max_xFillingRect ;		
		}
		
		xFlagStop += widthFillingMoving ;
		if( xFlagStop > widthGameScreen) {
			currState = STATE_WANT_TO_CONTINUE_DIALOG_ ;
		}
	}
	
	private void drawGameReady(Graphics g){
		
		// get the string.		
		String str = mission.toString() ;
		
		int xMaxDrawing = xGameScreen + ( widthGameScreen >> 1) - (font.getFontStringWidth(str) >> 1) ;
		int xDrawing = x_pxRightMoving ;
		int yDrawing = yGameScreen +  (heightGameScreen >> 2) ;
		
		font.drawString(g, str, xDrawing, yDrawing) ;
		
		x_pxRightMoving += 2 ; // 2 px for a tick
		if( xDrawing >= xMaxDrawing ){
			currState = STATE_RUNNING ;
			
			x_pxRightMoving = xGameScreen ;
			
			// let count down the clock time.
			gameManager.setClockTime(System.currentTimeMillis()) ;
			gameManager.lockTheBoomMan() ;
		}			

	}		
	
	private void drawPlayerLife(Graphics g){
		int life = gameManager.getBoomManLife() ;
		if( life < 0 ){
			if( !isBluetoothMode ){
				currState = STATE_LOSE ;
			}			 
			return ;
		}
		if( oldLife != life){
			int xDrawing = xGameScreen ;
			int yDrawing = -3 ; // because the height image is not good.
			
			// draw the face
			g.drawImage(imgNormalFace,
					    xDrawing,yDrawing,
					    ImageUtility.GRAPHICS_TOP_LEFT) ;
				
			int numberWidth = 8, numberHeight = 14 ;
			int clockWidth = 32 ;
			// draw the number of life. 
			xDrawing += 2 + imgNormalFace.getWidth();
			yDrawing = 1 ;
			
			g.drawRegion(imgNumber,
						 clockWidth+(life*numberWidth),0,
						 numberWidth,numberHeight,
						 Sprite.TRANS_NONE,
						 xDrawing,yDrawing,
						 ImageUtility.GRAPHICS_TOP_LEFT) ;
			
			oldLife = life ;
		}				
	}
	
	private void drawAnotherPlayerLife(Graphics g){
		int anotherlife = gameManager.getAnotherBoomManLife() ;
		if( anotherlife < 0 ){
			if( !isBluetoothMode ){
				currState = STATE_LOSE ;
			}			 
			return ;
		}
		if( anotherOldLife != anotherlife){ 
			int xDrawing = widthGameScreen - 4 - imgNormalRedFace.getWidth() -
						   8 /* 8 is number width */;
			int yDrawing = -3 ; // because the height image is not good.
			
			// draw the face
			g.drawImage(imgNormalRedFace, 
					    xDrawing,yDrawing,
					    ImageUtility.GRAPHICS_TOP_LEFT) ;
				
			int numberWidth = 8, numberHeight = 14 ;
			int clockWidth = 32 ;
			// draw the number of life. 
			xDrawing += 2 + imgNormalRedFace.getWidth();
			yDrawing = 1 ;
			
			g.drawRegion(imgNumber,
						 clockWidth+(anotherlife*numberWidth),0,
						 numberWidth,numberHeight,
						 Sprite.TRANS_NONE,
						 xDrawing,yDrawing,
						 ImageUtility.GRAPHICS_TOP_LEFT) ;
			
			anotherOldLife = anotherlife ;
		}				
	}
	
	private void drawCompleteMission(Graphics g){
		String str = null ;
		if( !isBluetoothMode ){
			// get the string.		
			str = "Congrulation" ;			
		}
		else{ // in the bluetooth mode			
			if( gameManager.isDraw() || isTimeOut ){ // game draw
				str = "Game draw" ;
			}
			else{
				if( gameManager.isWinner() ){
					str= "You are winner" ;
				}
				else{
					str= "You are loser" ;
				}
			}			
		}

		int xMaxDrawing = xGameScreen + ( widthGameScreen >> 1) - (font.getFontStringWidth(str) >> 1) ;
		int xDrawing = x_pxRightMoving ;
		int yDrawing = yGameScreen +  (heightGameScreen >> 2) ;
		
		font.drawString(g, str, xDrawing, yDrawing) ;
		
		x_pxRightMoving += 2 ; // 2 px for a tick
		if( xDrawing >= xMaxDrawing ){
			if( !isBluetoothMode ){
				currState = STATE_LOCAL_NEXT_MISSION_DIALOG;
			}
			else{
				if( server != null ){
					currState = STATE_BLUETOOTH_SERVER_SHOW_DIALOG ;
				}
				else if( client != null ){
					currState = STATE_BLUETOOTH_CLIENT_SHOW_DIALOG ;
				}
			}						
		}
	}
	
	private void drawTheFaceTalks(Graphics g,String msg){
		if( enableFaceTalk ){
			count++ ;
			int xDialog = xGameScreen + (imgNormalRedFace.getWidth() >> 1) + 2 ;
			int yDialog = (imgNormalRedFace.getHeight() >> 1) ; 
			g.drawImage(imgFaceDialog,
					    xDialog,yDialog,
					    ImageUtility.GRAPHICS_TOP_LEFT) ;

			long remaintime =  3000 - 
							   (System.currentTimeMillis() - milliTimeFaceTalk) + /*elapseTime*/
							   BoomCanvas.sumOfMillisecondsPauseGame;
			
			if( count >= 3 ){							
				int xMsg = xDialog + 5 ;
				int yMsg = yDialog + (imgFaceDialog.getHeight() >> 3 ) ;

				font.drawString(g,msg,xMsg,yMsg) ;
			}
			
		} 		
	}
	
	private void showDialog(Graphics g,String msg,boolean isDrawSoftkey){ 
		
		int width = 5;	
		if( isOpeningWidth ){
			width  = xAccelerator*5 ;
		}
		
		int xRect = xGameScreen + (widthGameScreen >> 1) - width ;
		int height = yAccelerator   ;
		int yRect = yGameScreen + (heightGameScreen >> 1) - height;
		
		int widthDrawing = width<<1 ;
		int heightDrawing = height<<1 ;
		g.setColor(0x00ff00) ;
		g.drawRoundRect(xRect,yRect,widthDrawing ,heightDrawing,40,60) ;
		
		g.setColor(0x0000ff) ;
		g.fillRoundRect(xRect+1,yRect+1,widthDrawing-1,heightDrawing-1,40,60) ;
		
		yAccelerator += 8 ;
		if( yAccelerator >= 15 ){
			yAccelerator = 15 ;
		}
		
		xAccelerator += 3 ;
		if( xAccelerator >= 15 ){
			xAccelerator = 2 ;
			if( !isOpeningWidth ){
				isOpeningWidth = true ;
			}
			else{
				font.drawString(g, msg,xRect+20,yRect+(heightDrawing>>3)) ;
				if( isDrawSoftkey){					
					softkey.drawLeftSoftkey(g,"no",
											xGameScreen+widthGameScreen, heightGameScreen+yGameScreen) ;
					softkey.drawRightSoftkey(g,"yes",
											 xGameScreen+widthGameScreen,heightGameScreen+yGameScreen) ;
				}
				
				currState =	STATE_WAITING_PLAYER_INPUT ;
			}//
		}
	}
	
	public void setStateCompleteMission(){
		currState = STATE_COMPLETE_MISSON ;
	}
	
	public int getWidthGameScreen() {
		return widthGameScreen;
	}
	
	public int getHeightGameScreen() {
		return heightGameScreen;
	}
       public boolean isMultiKeyPressed(int keyState){

		boolean isMultiKeyPressed = false ;

		if( (keyState & LEFT_PRESSED) != 0 ){

			if( (keyState & RIGHT_PRESSED) != 0 ){
				isMultiKeyPressed = true ;
			}
			else if( (keyState & UP_PRESSED) != 0 ){
				isMultiKeyPressed = true ;
			}
			else if( (keyState & DOWN_PRESSED) != 0 ){
				isMultiKeyPressed = true ;
			}
			else if((keyState & FIRE_PRESSED) != 0) {
				isMultiKeyPressed = true ;
			}
		}
		else if( (keyState & RIGHT_PRESSED) != 0 ){

			if( (keyState & LEFT_PRESSED) != 0 ){
				isMultiKeyPressed = true ;
			}
			else if( (keyState & UP_PRESSED) != 0 ){
				isMultiKeyPressed = true ;
			}
			else if( (keyState & DOWN_PRESSED) != 0 ){
				isMultiKeyPressed = true ;
			}
			else if((keyState & FIRE_PRESSED) != 0) {
				isMultiKeyPressed = true ;
			}
		}
		else if( (keyState & UP_PRESSED) != 0 ){

			if( (keyState & RIGHT_PRESSED) != 0 ){
				isMultiKeyPressed = true ;
			}
			else if( (keyState & LEFT_PRESSED) != 0 ){
				isMultiKeyPressed = true ;
			}
			else if( (keyState & DOWN_PRESSED) != 0 ){
				isMultiKeyPressed = true ;
			}
			else if((keyState & FIRE_PRESSED) != 0) {
				isMultiKeyPressed = true ;
			}
		}
		else if( (keyState & DOWN_PRESSED) != 0 ){

			if( (keyState & RIGHT_PRESSED) != 0 ){
				isMultiKeyPressed = true ;
			}
			else if( (keyState & UP_PRESSED) != 0 ){
				isMultiKeyPressed = true ;
			}
			else if( (keyState & LEFT_PRESSED) != 0 ){
				isMultiKeyPressed = true ;
			}
			else if((keyState & FIRE_PRESSED) != 0) {
				isMultiKeyPressed = true ;
			}
		}

		return isMultiKeyPressed ;
	 }
       
     public void sendMessage(byte msg){

   		byte dataSend = msg ;

   		if( server != null ){
   			server.sendByte(dataSend);
   		}
   		else if( client != null ){
   			client.sendByte(dataSend);
   		}

   	}

    public byte receiveMessage(){ 

   		byte dataReceive = DataTransmiting.KEY_NONE;
   		if( server != null ){
   			dataReceive = server.receiveByte() ;
   		}
   		else if( client != null ){
   			dataReceive = client.receiveByte() ;
   		}

   		return dataReceive ;
   	}   
}
