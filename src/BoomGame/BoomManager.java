package BoomGame;
import java.io.IOException;
import java.util.Random;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.game.GameCanvas;

import Bluetooth.BluetoothClient;
import Bluetooth.BluetoothServer;
import Bluetooth.DataTransmiting;
import MainGame.BoomCanvas;

public class BoomManager {

	private int tickCount ;
	private BoomWorld board ;
	private BoomMan bomberMans[] ;
	private Item    items[] ;
	private Boom    bombs[]  ;
	private Enemy   enemys[] ;
	private Gate    gate ;
	private static BoomCanvas canvas ;
	private Random random = new Random(Long.MAX_VALUE) ;

	public final static int MAX_CLOCK_TIME = 1000*180 ; // 3 minutes.
	private long prevTime ;

	public  int score ;

	private int missionLevel = 1 ;

	private boolean isCompleteMission ;

	//use to determine when random the gate.
	public int numDestroySoftWall ;

	//use to generate enemy
	private int numDeadEnemy ; 

	// bluetooth mode
	private boolean isBluetoothMode;
	private BluetoothClient client ;
	private BluetoothServer server ;
	private boolean isWinner ;
	private boolean isDraw ;
	private int infosSend[] = new int[6] ;
	private int infosReceive[] = new int[6] ;

	public BoomManager(BoomCanvas canvas,int level) throws IOException {
		BoomManager.canvas = canvas ;

		missionLevel = level ;

		loadResource(level) ;
	}

	public BoomManager(BoomCanvas canvas,int level,BluetoothServer server,BluetoothClient client) throws IOException {
		BoomManager.canvas = canvas ;

		missionLevel = level ;

		this.server = server ;
		this.client = client ;
		isBluetoothMode = true ;

		loadResource(level) ;
	}

	private void loadResource(int level) throws IOException{
		// new objects.
		board = new BoomWorld(level) ;

		if( isBluetoothMode ){
			bomberMans = new BoomMan[2] ;
			if( server != null ){
				bomberMans[0] = new BoomMan(board,BoomMan.BOMBMAN_WHITE) ;
				bomberMans[1] = new BoomMan(board,BoomMan.BOMBMAN_RED) ;
			}
			else if( client != null ){
				bomberMans[0] = new BoomMan(board,BoomMan.BOMBMAN_RED) ;
				bomberMans[1] = new BoomMan(board,BoomMan.BOMBMAN_WHITE) ;
			}
			
		}else {
			///System.out.println(" none bluetoothmode ") ;
			bomberMans = new BoomMan[1] ;
			bomberMans[0] = new BoomMan(board,BoomMan.BOMBMAN_WHITE) ;
		}

		items = new Item[1] ;
		items[0] = new Item(Item.TYPE_INCREASE_NUM_BOMB) ;
		items[0].setVisible(false) ;

		bombs = new Boom[2] ;

		gate = new Gate() ;
		gate.setVisible( false) ;

		for(int index = bombs.length-1 ; index >= 0 ; index -- ){
			bombs[index] = new Boom(board,this) ;
		}

		//System.out.println("Enemy initiallizing ... ") ;
		if( !isBluetoothMode ){
			enemys = new Enemy[5] ;
			
			if( level == 1 ){
				enemys[0] = new Enemy(Enemy.RED_ENEMY,board) ;
				enemys[1] = new Enemy(Enemy.RED_ENEMY,board) ;
				enemys[2] = new Enemy(Enemy.RED_ENEMY,board) ;
				enemys[3] = new Enemy(Enemy.RED_ENEMY,board) ;
				enemys[4] = new Enemy(Enemy.RED_ENEMY,board) ;
			}
			else if( level == 2){
				enemys[0] = new Enemy(Enemy.GREEN_ENEMY,board) ;
				enemys[1] = new Enemy(Enemy.GREEN_ENEMY,board) ;
				enemys[2] = new Enemy(Enemy.GREEN_ENEMY,board) ;
				enemys[3] = new Enemy(Enemy.GREEN_ENEMY,board) ;
				enemys[4] = new Enemy(Enemy.GREEN_ENEMY,board) ;
				
				enemys[0].setSpeed(9) ;
				enemys[1].setSpeed(9) ; 
				enemys[2].setSpeed(9) ;
				enemys[3].setSpeed(9) ;
				enemys[4].setSpeed(9) ;
			}
			else if( level == 3){
				enemys[0] = new Enemy(Enemy.BLUE_ENEMY,board) ;
				enemys[1] = new Enemy(Enemy.BLUE_ENEMY,board) ;
				enemys[2] = new Enemy(Enemy.BLUE_ENEMY,board) ;
				enemys[3] = new Enemy(Enemy.BLUE_ENEMY,board) ;
				enemys[4] = new Enemy(Enemy.BLUE_ENEMY,board) ;
				
				enemys[0].setSpeed(6) ;
				enemys[1].setSpeed(6) ; 
				enemys[2].setSpeed(6) ;
				enemys[3].setSpeed(6) ;
				enemys[4].setSpeed(6) ;
			}
			else if( level == 4){
				enemys[0] = new Enemy(Enemy.BLACK_ENEMY,board) ;
				enemys[1] = new Enemy(Enemy.BLACK_ENEMY,board) ;
				enemys[2] = new Enemy(Enemy.BLACK_ENEMY,board) ;
				enemys[3] = new Enemy(Enemy.BLACK_ENEMY,board) ;
				enemys[4] = new Enemy(Enemy.BLACK_ENEMY,board) ;
			}
			else{
				enemys[0] = new Enemy(Enemy.RED_ENEMY,board) ;
				enemys[1] = new Enemy(Enemy.RED_ENEMY,board) ;
				enemys[2] = new Enemy(Enemy.GREEN_ENEMY,board) ;
				enemys[3] = new Enemy(Enemy.BLUE_ENEMY,board) ;
				enemys[4] = new Enemy(Enemy.BLACK_ENEMY,board) ;
			}
			
		} // bluetooth mode
	}

	public final static BoomCanvas getCanvas(){
		return canvas ;
	}

	public void tick(){
		
		handleAllCollisions() ;
		
		int index ;
		for( index = bomberMans.length-1 ; index >= 0 ;index-- ){
			bomberMans[index].tick(tickCount) ;
		}

		for(index = items.length -1 ;index >= 0 ;index-- ){
			items[index].tick(tickCount) ;
		}

		for(index = bombs.length-1 ; index >= 0 ; index-- ){
			bombs[index].tick(tickCount) ;
		}

		if( !isBluetoothMode ){
			for(index = enemys.length-1 ; index >= 0 ; index-- ){
				if( missionLevel == 3 )
				{
					enemys[index].tick(tickCount, random,bomberMans[0]) ;
				}
				else
				{
					enemys[index].tick(tickCount,random) ;
				}				
			}
		}
		
		board.tick(tickCount) ;	

		tickCount++ ;

		if( tickCount >= 10){
			tickCount = 0 ;
		}
	}

	public void drawn(Graphics g,int xScreen,int yScreen){
		g.translate(xScreen,yScreen)  ;
		// draw the background.
		board.paint(g, xScreen, yScreen) ;

		int xView = board.getXViewWindow() ;
		int yView = board.getYViewWindow() ;
		int widthViewWindow  = board.getWidthViewWindow()  ;
		int heightViewWindow = board.getHeightViewWindow() ;
		int index ;
		for( index = bombs.length-1 ; index >= 0 ; index -- ){
			bombs[index].paint(g,xView,yView,widthViewWindow,heightViewWindow);
		}

		for( index = items.length -1 ; index >= 0 ;index--){
			if( items[index].isInDrawingArea(xView, yView, widthViewWindow, heightViewWindow)){
				items[index].paint(g, xView, yView, widthViewWindow, heightViewWindow) ;
			}
		}

		if( !isBluetoothMode ){
			for( index = enemys.length-1 ; index >= 0 ; index -- ){
				if( enemys[index].isInDrawingArea(xView, yView, widthViewWindow, heightViewWindow)){
					enemys[index].paint(g,xView,yView,widthViewWindow,heightViewWindow);
				}
			}
		}
		
		if( gate.isInDrawingArea(xView, yView, widthViewWindow, heightViewWindow)){
			gate.paint(g,xView,yView,widthViewWindow,heightViewWindow);
		}
		
		if( isBluetoothMode ){	
			if( server != null ){
				bomberMans[0].paint(g, xView, yView, widthViewWindow, heightViewWindow) ;
				bomberMans[1].paint(g, xView, yView, widthViewWindow, heightViewWindow) ;
			}
			else if( client != null ){
				bomberMans[1].paint(g, xView, yView, widthViewWindow, heightViewWindow) ;
				bomberMans[0].paint(g, xView, yView, widthViewWindow, heightViewWindow) ;
			}		
		}
		else{
			for(index = bomberMans.length-1 ; index >= 0 ;index-- ){
				if( bomberMans[index].isInDrawingArea(xView, yView, widthViewWindow, heightViewWindow)){
					bomberMans[index].paint(g,xView,yView, widthViewWindow, heightViewWindow) ;
				}
			}
		}
		

		g.translate(-xScreen,-yScreen) ;
	}

	public void keyLeftPressed(){
		bomberMans[0].moveLeft() ;
/*
		int xCell = bomberMan.getXCell() ;
		int yCell = bomberMan.getYCell() ;
		System.out.println("xCell = " + xCell +" yCell = " + yCell);
	*/
	}

	public void keyRightPressed(){
		bomberMans[0].moveRight() ;
/*
		int xCell = bomberMan.getXCell() ;
		int yCell = bomberMan.getYCell() ;
		System.out.println("xCell = " + xCell +" yCell = " + yCell);
*/
	}

	public void keyUpPressed(){
		bomberMans[0].moveUp() ;
/*
		int xCell = bomberMan.getXCell() ;
		int yCell = bomberMan.getYCell() ;
		System.out.println("xCell = " + xCell +" yCell = " + yCell);
	*/
	}

	public void keyDownPressed(){
		bomberMans[0].moveDown() ;
/*
		int xCell = bomberMan.getXCell() ;
		int yCell = bomberMan.getYCell() ;
		System.out.println("xCell = " + xCell +" yCell = " + yCell);
	*/
	}

	public void keyFirePressed(){
		int xCell = bomberMans[0].getXCell() ;
		int yCell = bomberMans[0].getYCell() ;
		
		for(int index = bombs.length-1 ; index >= 0 ; index -- ){
			if( !bombs[index].isVisible() &&
				!Boom.hasABomb(xCell, yCell) && 	
				bomberMans[0].letPlant() ) {
				
				bombs[index].plant(bomberMans[0],xCell,yCell) ;

				break ;
			}
		}
	}

	public void init(int level) throws IOException{
		switch (level) {
		case 1:
			 if( isBluetoothMode ){
				if( server != null ){
					bomberMans[0].setCellPosition(10,7) ;
					bomberMans[1].setCellPosition(1,1) ;
				}
				else if( client != null ){
					bomberMans[1].setCellPosition(10,7) ;
					bomberMans[0].setCellPosition(1,1) ;
				}
			}else{
				bomberMans[0].setCellPosition(10,7) ;

				enemys[0].setCellPosition( 2, 1) ;
				enemys[1].setCellPosition( 7, 1) ;
				enemys[2].setCellPosition( 8, 3) ;
				enemys[3].setCellPosition( 7, 8) ;
				enemys[4].setCellPosition( 7,10) ;
			}
			break;

		case 2:
			if( isBluetoothMode ){
				if( server != null ){
					bomberMans[0].setCellPosition(10,8) ;
					bomberMans[1].setCellPosition(1,1) ;
				}
				else if( client != null ){
					bomberMans[1].setCellPosition(10,8) ;
					bomberMans[0].setCellPosition(1,1) ;
				}
			}else{
				bomberMans[0].setCellPosition(10, 9) ;

				enemys[0].setCellPosition( 1, 1) ;
				enemys[1].setCellPosition( 10, 2) ;
				enemys[2].setCellPosition( 4, 9) ;
				enemys[3].setCellPosition( 7, 8) ;
				enemys[4].setCellPosition( 3, 4) ;
			}
			break ;
			
         case 3:
			if( isBluetoothMode ){
				if( server != null ){
					bomberMans[0].setCellPosition(10,8) ;
					bomberMans[1].setCellPosition(1,1) ;
				}
				else if( client != null ){
					bomberMans[1].setCellPosition(10,8) ;
					bomberMans[0].setCellPosition(1,1) ;
				}
			}else{
				bomberMans[0].setCellPosition(10, 9) ;

				enemys[0].setCellPosition( 1, 1) ;
				enemys[1].setCellPosition( 4, 8) ;
				enemys[2].setCellPosition( 4, 8) ;
				enemys[3].setCellPosition( 8, 8) ;
				enemys[4].setCellPosition( 4, 4) ;
			}
			break ;
			
          case 4:
			if( isBluetoothMode ){
				if( server != null ){
					bomberMans[0].setCellPosition(10,8) ;
					bomberMans[1].setCellPosition(2,3) ;
				}
				else if( client != null ){
					bomberMans[1].setCellPosition(10,8) ;
					bomberMans[0].setCellPosition(2,3) ;
				}
			}else{
				bomberMans[0].setCellPosition(10, 9) ;

				enemys[0].setCellPosition( 1, 4) ;
				enemys[1].setCellPosition( 1, 9) ;  
				enemys[2].setCellPosition( 5, 4) ;  
				enemys[3].setCellPosition( 8, 1) ;
				enemys[4].setCellPosition( 4, 6) ;
			}
			break ;
			
		default:
			break;
		}


		///System.out.println("x = "+bomberMans[0].getXWorld()) ;
		///System.out.println("y = "+bomberMans[0].getYWorld()) ;
		board.setViewWindowPos(bomberMans[0].getXWorld(),
							   bomberMans[0].getYWorld()) ;
		///System.out.println("Enemy setCellPosition() ") ;
	}

	public void setDrawingSize(int width,int height){
		board.setDrawingSize(width, height);
		bomberMans[0].setTheScrollBound(width >> 1,height >> 1) ;
	}

public void processInput(){
		
		int keystate = canvas.getKeyStates() ;
		if( isBluetoothMode ){

			if( canvas.isMultiKeyPressed(keystate)){
				keystate = 0 ;
			}						
			
			if( client != null ){
				infosReceive = client.getBomman() ;
			}else if( server != null ){
				infosReceive = server.getBomman() ;
			}				
			
			int xWorld = infosReceive[0] ;
			int yWorld = infosReceive[1] ;
			int keyStateReceive = infosReceive[2] ;

			bomberMans[1].setPosition(xWorld, yWorld) ;			 
			tickTheBombman(bomberMans[1], keyStateReceive ,false ) ;
			
			infosSend[0] = bomberMans[0].getXWorld() ;
			infosSend[1] = bomberMans[0].getYWorld() ;
			infosSend[2] = keystate  ;
			
			try {
				Thread.sleep(20l) ; // delay to receive the information
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			tickTheBombman(bomberMans[0], keystate,true) ;	
			
			if( client != null ){
				client.sendBomman(infosSend) ;
			}
			else if( server != null ){
				server.sendBomman(infosSend) ;
			}			
			
		} // end bluetooth mode				
		else{ // none bluetooth mode
			
			tickTheBombman(bomberMans[0], keystate,true) ;
			
		} // none bluetooth
	
	}
	
	private void tickTheBombman(BoomMan bombman,int keystate,boolean letScroll){
		if( !bombman.isDead()){

			boolean isScroll = bombman.move(keystate) ;
			if( letScroll && isScroll ) {
				board.scroll(bombman.getLastDx(),
							 bombman.getLastDy()) ;
			}

			if( (keystate & GameCanvas.FIRE_PRESSED) != 0 ){
				if( !bombman.collideWith(gate.getXCell(),gate.getYCell())){
					int xCell = bombman.getXCell() ;
					int yCell = bombman.getYCell() ;

					for(int index = bombs.length-1 ; index >= 0 ; index -- ){
						if( !bombs[index].isVisible() &&
							!Boom.hasABomb(xCell, yCell) &&
							 bombman.letPlant() ) {
							
							bombs[index].plant(bombman,xCell,yCell) ;
							break ;
						}
					}
				}

			} // fire press

		} // 
	}
	
	private void handleAllCollisions(){
		int xBoomMan  ;
		int yBoomMan  ;

		for(int bombIndex = bombs.length - 1 ; bombIndex >= 0 ;bombIndex-- ){
			Boom b = bombs[bombIndex];
			if( b.isExploding()){
				for(int bomberManIndex = bomberMans.length-1 ; bomberManIndex >= 0 ; bomberManIndex--){
					if( !bomberMans[bomberManIndex].isDead() &&
						!bomberMans[bomberManIndex].isLocking() ){

						xBoomMan = bomberMans[bomberManIndex].getXCell() ;
						yBoomMan = bomberMans[bomberManIndex].getYCell() ;

						if( b.isInExplosionCell(xBoomMan, yBoomMan)){
							bomberMans[bomberManIndex].die() ;
						}
					}
				}
				
				if( !isBluetoothMode ){
					for(int enemyIndex = enemys.length - 1 ; enemyIndex >= 0 ; enemyIndex-- ){
						if( enemys[enemyIndex].isVisible() ){
							if( !enemys[enemyIndex].isDead() ){
								if( enemys[enemyIndex].collideWithExplosion(b)){
									enemys[enemyIndex].die() ;
									score += 200 ;

									numDeadEnemy++ ;
								}
							}
						}

					} // enemys iterating

				}
					
					for(int otherBombIndex = bombIndex - 1 ; otherBombIndex >= 0 ; otherBombIndex-- ){
						if( !bombs[otherBombIndex].isExploding() ){
							if( b.isInExplosionCell(bombs[otherBombIndex].getXExplode(),
													bombs[otherBombIndex].getYExplode())){
								bombs[otherBombIndex].enableExploding() ;
							}
						}

					} // another bomb iterating...


					for(int itemIndex = items.length - 1 ; itemIndex >= 0 ; itemIndex-- ){
						if( items[itemIndex].isVisible() ){
							if( b.isInExplosionCell(items[itemIndex].getXCell(),
													items[itemIndex].getYCell()) ){
								items[itemIndex].setVisible(false) ;
							}
						}
					}

					if( gate.isVisible()){
						if( b.isInExplosionCell(gate.getXCell(),gate.getYCell())){

						}
					}

				}	// bomb is exploding..

		} // bombs iterating

		if( !isBluetoothMode ){
			for(int enemyIndex = enemys.length - 1 ; enemyIndex >= 0 ;enemyIndex-- ){
				if( enemys[enemyIndex].isVisible() ){
					for(int bomberManIndex = bomberMans.length - 1 ; bomberManIndex >=0 ;bomberManIndex--){
						if( !bomberMans[bomberManIndex].isDead() &&
							!bomberMans[bomberManIndex].isLocking() ){
							if( enemys[enemyIndex].collideWith(bomberMans[bomberManIndex])){
								bomberMans[bomberManIndex].die() ;
							}
						}
					}
				}
			} // enemys iterating
		}
		
		for(int itemIndex = items.length - 1 ; itemIndex >= 0 ; itemIndex-- ){
			if( items[itemIndex].isVisible() ){
				for(int bomberManIndex = bomberMans.length - 1 ; bomberManIndex >= 0 ;bomberManIndex-- ){
					if( !bomberMans[bomberManIndex].isDead() ) {
						if( bomberMans[bomberManIndex].collideWith(items[itemIndex].getXCell(),
												  				   items[itemIndex].getYCell())){

							switch (items[itemIndex].getType()) {
							case Item.TYPE_MAX_LEVEL_EXPLOSION:
								bomberMans[bomberManIndex].setLevelBombExplode(Boom.MAX_LEVEL)  ;
								break;

							case Item.TYPE_INCREASE_SPEED :
								int speed = bomberMans[bomberManIndex].getSpeed() ;
								speed += 2 ;
								bomberMans[bomberManIndex].setSpeed(speed) ;
								break ;

							case Item.TYPE_INCREASE_NUM_BOMB :
								addMoreBoomObject() ;
								bomberMans[bomberManIndex].addBombPlant() ;						
								break ;

							case Item.TYPE_INCREASE_LEVEL_EXPLOSION :
								int level = bomberMans[bomberManIndex].getLevelBombExplode();
								level++ ;
								if( level > Boom.MAX_LEVEL){
									level = Boom.MAX_LEVEL ;
								}
								bomberMans[bomberManIndex].setLevelBombExplode(level) ;
								break ;

							case Item.TYPE_REVERSED_DIRECTION :
								bomberMans[bomberManIndex].reversedDirection() ;
								break ;

							case Item.TYPE_KICK_BOMB :
								bomberMans[bomberManIndex].possibleKickBomb(true) ;
								break ;

							default:
								break;
							}// switch

							items[itemIndex].setVisible(false) ;
							break ;
						}
					} // if the man not dead

				} //

			} // if item visible
		}// items iterating.

			if( !isBluetoothMode ){
				if( gate.isVisible() ){

				//	System.out.println("numDeadEnemy = "+numDeadEnemy) ;
				
						if(  (numDeadEnemy >= enemys.length) ){
							for( int bomberManIndex = bomberMans.length - 1 ; bomberManIndex >= 0 ; bomberManIndex--){
								if( bomberMans[bomberManIndex].collideWith(gate.getXCell(), gate.getYCell())){
									isCompleteMission = true ;
									//System.out.println("nextMission") ;
							}
						}
					}
				}			
			}

		// reset if bombman dead
		for(int index = bomberMans.length - 1 ; index >= 0 ;index-- ){
			if( bomberMans[index].isDead() ){
				bomberMans[index].reset() ;
			}
		}
	
		if( isBluetoothMode ){
			boolean endLife = bomberMans[0].isEndLife() ;
			boolean anotherEndLife = bomberMans[1].isEndLife() ;
			if( endLife && anotherEndLife ){ //both die -> game draw
				isCompleteMission = true ;
				isDraw = true ;	// game draw
			}
			else if( endLife || anotherEndLife ){ // one die,one live -> not draw
				isCompleteMission = true ;
				isDraw = false ;
				if( !endLife ){ // player1 win 
					isWinner = true ; 
				}
				else{			// player1 lose
					isWinner = false ;
				}
				
			}
		}
		
		if( isCompleteMission ){
			canvas.setStateCompleteMission() ;
		}
	}

	public void addMoreBoomObject(){
		int oldLength = bombs.length ;
		int newLength = oldLength + 1;
		Boom newBombs[] = new Boom[newLength] ;

		// copy the old element
		for( int i = 0 ; i < oldLength ; i++ ){
			newBombs[i] = bombs[i] ;
		}

		// save the new element. Now, oldLength is last index of the new array.
		try {

			newBombs[oldLength] = new Boom(board,this) ;

		} catch (IOException e) {
			e.printStackTrace();
		}

		//now, the new object array.
		bombs = newBombs ;
	}
	
	public void setClockTime(long time){
		prevTime = time ;
	}

	public long getRemainingMilisecond(){
		if( prevTime != 0 ){
			return MAX_CLOCK_TIME - getElapsedMilliseconds() ;
		}
		return MAX_CLOCK_TIME ;
	}

	public long getElapsedMilliseconds(){
		return System.currentTimeMillis() - prevTime ;
	}

	public void lockTheBoomMan(){
		for( int index= bomberMans.length-1 ; index >= 0 ;index-- ){
			bomberMans[index].setLocking() ;
		}
	}

	/*
	public boolean isBombManDead(){
		return bomberMan.isDead() ;
	}
	 */

	public boolean isBombManNewLife(){
		return true ;
	}

	public int getScore() {
		return score;
	}

	public int getBoomManLife() {
		int life = 0;
		if( isBluetoothMode ){
			if( server != null ){
				life = bomberMans[0].getBommanlife() ;
			}
			else if( client != null ){
				life = bomberMans[1].getBommanlife() ;
			}
		}
		else {// no bluetooth mode
			life = bomberMans[0].getBommanlife() ;
		} 
		 
		return life ;
	}
	
	public int getAnotherBoomManLife(){
		int life = 0;
		if( isBluetoothMode ){
			if( server != null ){
				life = bomberMans[1].getBommanlife() ;
			}
			else if( client != null ){
				life = bomberMans[0].getBommanlife() ;
			}
		}
		else{ // no bluetooth mode
			life = bomberMans[0].getBommanlife() ;
		}
		
		return life ;
	}

	public int getMapWidth(){
		return board.getNumCols()*BoomWorld.CELL_WIDTH ;
	}

	public int getMapHeight(){
		return board.getNumRows()*BoomWorld.CELL_HEIGHT ;
	}

	public int getMissionLevel() {
		return missionLevel;
	}

	public boolean randomDestroyWall(int xCell,int yCell){
		int rand = random.nextInt() ;
		boolean isRandomItem = false;
		boolean isRandomGate = false ;
		int numSoftWall = board.getNumSoftWall() ;

		if( numDestroySoftWall >= (numSoftWall-5) ){
			isRandomGate = true ;
		}
 
		if( rand % 7 == 0 ){
			isRandomItem = true ;
			if( numDestroySoftWall >= (numSoftWall >> 1)){
				if( !isBluetoothMode ){ // only random for the non bluetooth mode.
		//			isRandomGate = true ;
				}				
			}
		}

		if( isRandomGate ){
			if( !gate.isVisible() ){
			//	System.out.println("Random gate") ;
				gate.setVisible(true) ;
				gate.setCellPosition(xCell, yCell) ;
			}
		}else if( isRandomItem ) {
			System.out.println("Random Item") ;
			for( int index = items.length - 1 ;index >= 0 ;index-- ){
				if( !items[index].isVisible() ){
					items[index].setVisible(true);
					items[index].setCellPosition(xCell,yCell) ;
					rand = random.nextInt() ;
					if( rand % 5 == 0 ){
						items[index].setType(Item.TYPE_INCREASE_LEVEL_EXPLOSION) ;
					}else if( rand % 7 == 0 ){
						items[index].setType(Item.TYPE_INCREASE_NUM_BOMB) ;
					}else if( rand % 3 == 0){
						items[index].setType(Item.TYPE_INCREASE_SPEED) ;
					}
					else if( rand % 11 == 0){
						items[index].setType(Item.TYPE_MAX_LEVEL_EXPLOSION) ;
					}
					else if( rand % 2 == 0 ){
						items[index].setType(Item.TYPE_REVERSED_DIRECTION) ;
					}

					break ;
				}
			}

		}

		return isRandomItem ;
	}

	public int getCellHeight(){
		return board.getImgHeight() ;
	}

	public boolean isCompleteMission() {
		return isCompleteMission;
	}
	
	// Bluetooth mode 
	public boolean isDraw() {
		return isDraw;
	}
	
	public boolean isWinner() {
		return isWinner;
	}

	public void sendKeyStates(int keyState){

		byte dataSend = DataTransmiting.KEY_NONE ;

		if( (keyState & GameCanvas.LEFT_PRESSED ) != 0){
			dataSend = DataTransmiting.KEY_LEFT ;
		}
		else if( (keyState & GameCanvas.UP_PRESSED ) != 0 ){
			dataSend = DataTransmiting.KEY_UP ;
		}
		else if( (keyState & GameCanvas.RIGHT_PRESSED ) != 0 ){
			dataSend = DataTransmiting.KEY_RIGHT ;
		}
		else if( (keyState & GameCanvas.DOWN_PRESSED ) != 0 ){
			dataSend = DataTransmiting.KEY_DOWN ;
		}
		else if( (keyState & GameCanvas.FIRE_PRESSED ) != 0 ){
			dataSend = DataTransmiting.KEY_FIRE ;
		}

		if( server != null ){
			server.sendByte(dataSend);
		}
		else if( client != null ){
			client.sendByte(dataSend);
		}

	}

	public int receiveKeyStates(){

		byte dataReceive = DataTransmiting.KEY_NONE;
		int  keyStates = 0 ;
		if( server != null ){
			dataReceive = server.receiveByte() ;
		}
		else if( client != null ){
			dataReceive = client.receiveByte() ;
		}

		if( dataReceive == DataTransmiting.KEY_LEFT ){
			keyStates = GameCanvas.LEFT_PRESSED ;
		}
		else if( dataReceive == DataTransmiting.KEY_UP ){
			keyStates = GameCanvas.UP_PRESSED ;
		}
		else if( dataReceive == DataTransmiting.KEY_RIGHT ){
			keyStates = GameCanvas.RIGHT_PRESSED ;
		}
		else if( dataReceive == DataTransmiting.KEY_DOWN ){
			keyStates = GameCanvas.DOWN_PRESSED ;
		}
		else if( dataReceive == DataTransmiting.KEY_FIRE ){
			keyStates = GameCanvas.FIRE_PRESSED ;
		}

		return keyStates ;
	}
	
	public boolean isReceiveKeyState(){
		boolean isReceive = false ;
		if( server != null ){
			isReceive = server.isReceive() ;
		}
		else if( client != null ){
			isReceive = client.isReceive() ;
		}
		
		return isReceive ;
	}
	
	public boolean isSendKeyState(){
		boolean isSend = false ;
		if( server != null ){
			isSend = server.isSend() ;
		}
		else if( client != null ){
			isSend = client.isSend() ;
		}
		
		return isSend ;
	}
}
