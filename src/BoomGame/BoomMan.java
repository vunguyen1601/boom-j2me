package BoomGame;

import java.io.IOException;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.game.GameCanvas;

import MainGame.BoomCanvas;


public class BoomMan extends CellObject {
	private final static int FRAME_WIDTH = 18 ;  // 5*21 = 105px
	private final static int FRAME_HEIGHT = 28; 
	
	private final static int movingDownSequences[] = {3,4,5} ;
	private final static int movingRightSequences[] = {9,10,11} ;
	private final static int movingUpSequences[]   = {12,13,14} ;
	private final static int movingLeftSequences[] = {6,7,8} ;
	private final static int deadSequences[] = {19,19,19,19,20,20,21,21,22,22} ;
	private final static int lockStateSequences[] = {23} ;
	
	public final static int BOMBMAN_RED = 1 ;
	public final static int BOMBMAN_WHITE = 2 ;
	private int type ;
	
	private final static long MAX_MILLI_TIME_LOCK = 3000 ; // 3s
	private long    milliTimeLock ;
	private boolean isLocking ;	
	private boolean isLockOn ;

	private boolean shouldCheckCollideWithBomb ;
	
	private int lineVScroll ;
	private int lineHScroll ;
	
	private boolean isReversingDirection ;
	private long    milliTimeReversed ;
	private final static long MAX_MILLI_TIME_REVERSED_DIRECTION = 10*1000 ;
		
	private boolean possibleKickBomb ;
	
	private int bommanlife = 2;
	private int numBombPlant = 1 ;
	private int levelBombExplode = 1 ;

	public BoomMan(BoomWorld world,int type) throws IOException {
		
		super(null ,
			  FRAME_WIDTH , FRAME_HEIGHT,
			  world );
		
		setFrameSequences(movingDownSequences);
		defineRefpx(FRAME_WIDTH >> 1 , FRAME_HEIGHT -10 ) ;
		speed  = 6 ;
		this.type = type ;
		
		switch (type) {
		case BOMBMAN_RED:
			setImgs(ImageStock.getImgRedBoomMan(FRAME_WIDTH,FRAME_HEIGHT ));
			break;
			
		case BOMBMAN_WHITE :
			setImgs(ImageStock.getImgBoomMan(FRAME_WIDTH,FRAME_HEIGHT)) ;
			break ;
			
		default:
			break;
		}
	}
	
	public void tick(int tickCount){
		if( tickCount % 3 == 0){	
			if( currState == STATE_DEAD ){
				nextFrame() ;
				
				// disable reversed mode.
				isReversingDirection = false ;
				
				if( getCurrFrame() == deadSequences.length - 1 ){
					// set the locking for the new life.
					setLocking() ;
					currState = STATE_MOVING_DOWN ;
				}
				
			} // the other states.			
			else{ // animate the lock
				if( isLocking ){
					// sets when isLockOn = false 
					int currSequences[] ;
					if( isLockOn ){
						setFrameSequences(lockStateSequences) ;
						// turn off for the next tick.
						isLockOn = false ;
					}
					else{ // turn on for the next tick
						// check whether the lock time elapsed.
					
					/* CALCULATE DESCRIPTION :
					 * 
					 Case 1:	
					  		|----------|*******|--------------|
					  		( .. MaxMilli time lock ...       )
					  		(  elapsed Time ...)
                                 	   (.pause.)
                                 	   (...remainTime.......  )
                                                                               
                     case 2:       
                            |----------|**********************|********|
                            (.. MaxMilli......................)
                            (...........elapsed time...................)
                                       (.........pause.................)                                                       
                                       (...... remain time....)                    	   		   	                        	   		   								
					*/						
						long remainTime = MAX_MILLI_TIME_LOCK - 
										 (System.currentTimeMillis() - milliTimeLock) + /*elapseTime*/
										  BoomCanvas.sumOfMillisecondsPauseGame;
						//System.out.println("remainTime = " +remainTime) ;
						if( remainTime <= 0 ){														
							isLocking = false ;
						}
						
						switch (currState) {
						case STATE_MOVING_LEFT:
							currSequences = movingLeftSequences ;
							break;
							
						case STATE_MOVING_RIGHT :
							currSequences = movingRightSequences ;
							break ;
							
						case STATE_MOVING_DOWN :
							currSequences = movingDownSequences ;
							break ;
							
						case STATE_MOVING_UP :
							currSequences = movingUpSequences ;
							break ;
							
						default:
							currSequences = getFrameSequences() ;
							break;
						}
						setFrameSequences(currSequences) ;
						isLockOn = true ;
					}
					
					nextFrame() ;
					
				} // isLocking
				
			} // animate the lock
			
			// check for the reversed time whether elapsed.
			long remainTime = MAX_MILLI_TIME_REVERSED_DIRECTION - 
			 				 (System.currentTimeMillis() - milliTimeReversed) + /*elapseTime*/
			 				  BoomCanvas.sumOfMillisecondsPauseGame;

				if( remainTime <= 0 ){														
					isReversingDirection = false ;
				}
			
		} // tickCount
	}
	
	private boolean handleWallCollision(){
		boolean isCollide = false;
		BoomWorld world = getWorld() ;
		if( collideWithWall()){
	//		System.out.println(" wallX = " + getXCell() + "; wallY = " + getYCell()) ;
			
			isCollide = true ;
			undo() ;
			/**		
			* 			Auto move
			*/ 
			int xCell = getXCell() ;
			int yCell = getYCell() ;
	//		System.out.println(" x = " + getXCell() + "; y = " + getYCell()) ;
			if( currState == STATE_MOVING_LEFT || currState == STATE_MOVING_RIGHT ){
				int yArea = getYAreaInCell() ;
				if( yArea == CELL_LEFT_AREA ){ // auto move up
					if( (currState == STATE_MOVING_LEFT  && !world.isWall(xCell - 1, yCell - 1)) || 
						(currState == STATE_MOVING_RIGHT && !world.isWall(xCell + 1, yCell - 1)) ){
						move(0, -speed) ;
						
					}
				}
				else if( yArea == CELL_RIGHT_AREA ){ // auto move down
					if( (currState == STATE_MOVING_LEFT  && !world.isWall(xCell - 1, yCell  + 1)) || 
						(currState == STATE_MOVING_RIGHT && !world.isWall(xCell + 1, yCell  + 1))	){
						move(0, speed) ;
					
					}					
				}
			}
			else if( currState == STATE_MOVING_UP || currState == STATE_MOVING_DOWN ){
				int xArea = getXAreaInCell() ;
				if( xArea == CELL_LEFT_AREA ){ // auto move left
					if( (currState == STATE_MOVING_UP   && !world.isWall(xCell - 1, yCell  - 1)) || 
						(currState == STATE_MOVING_DOWN && !world.isWall(xCell - 1, yCell  + 1))	){
						move(-speed, 0) ;
				
					}			
				}
				else if( xArea == CELL_RIGHT_AREA ){ // auto move right
					if( (currState == STATE_MOVING_UP   && !world.isWall(xCell + 1, yCell  - 1)) || 
						(currState == STATE_MOVING_DOWN && !world.isWall(xCell + 1, yCell  + 1))	){
						move(speed, 0) ;
					
					}				
				}
			}
			
			// check and undo after auto-moving.
			if( collideWithWall()){
				undo() ;		
			}	
		
		}
		
		return isCollide ;
	}
	
	public boolean move(int keystate) {
			
		int xOldCell = getXCell() ;
		int yOldCell = getYCell() ;
		BoomWorld world = getWorld() ;
		if( (keystate & GameCanvas.DOWN_PRESSED) != 0){
			if( isReversingDirection ){
				moveUp() ;
			}
			else{
				moveDown() ;
			}			
		}
		else if((keystate & GameCanvas.UP_PRESSED) != 0){
			if( isReversingDirection ){
				moveDown() ;
			}
			else{
				moveUp() ;
			}			
		}
		else if( (keystate & GameCanvas.LEFT_PRESSED) != 0){
			if( isReversingDirection ){
				moveRight() ;
			}
			else{
				moveLeft() ;
			}			
		}
		else if((keystate & GameCanvas.RIGHT_PRESSED) != 0) {
			if( isReversingDirection ){
				moveLeft( ) ;
			}
			else{
				moveRight() ;
			}			
		}	
		else {
			//
			return false;
		}
		
		boolean isCollide1 = handleWallCollision() ;
		
		// set to check bomb collison
		int newXCell = getXCell() ;
		int newYCell = getYCell() ;
		
		if( xOldCell != newXCell ||
			yOldCell != newYCell){
				shouldCheckCollideWithBomb = true ;
			}		
		
		boolean isCollide2 = handleBombCollision() ;
		
		if( isCollide1 ){
			return false ;
		}
		
		if( isCollide2 ){
			return false ;
		}
		
		boolean isScroll = false ;
		int xDrawing = getXWorld()-world.getXViewWindow() ;
		int yDrawing = getYWorld()-world.getYViewWindow() ;
	
		switch (currState) {
		case STATE_MOVING_DOWN:
			if( yDrawing > lineVScroll ){
				isScroll = true ;
			}
			break;
			
		case STATE_MOVING_LEFT:
			if( xDrawing < lineHScroll) {
				isScroll = true ;
			}
			break;
			
		case STATE_MOVING_RIGHT:
			if( xDrawing > lineHScroll ){
				isScroll = true ;
			}
			break;
			
		case STATE_MOVING_UP:
			if( yDrawing < lineVScroll){
				isScroll = true ;
			}
			break;

		default:
			break;
		}
	
		return isScroll ;
	}
	
	private boolean handleBombCollision(){
		boolean isCollide = false ;
		if( shouldCheckCollideWithBomb ){
			if( collideWithBomb()){
				isCollide = true ;
				undo() ;
			}			
			shouldCheckCollideWithBomb = false  ;
		}	
		return isCollide ;
	}
	
	public void moveLeft(){
		super.moveLeft() ;
		if(currState == STATE_MOVING_LEFT ){
			setFrameSequences(movingLeftSequences) ;
		}
		nextFrame() ;
	}
	
	public void moveRight(){		
		super.moveRight() ;
		if( currState == STATE_MOVING_RIGHT ){
			setFrameSequences(movingRightSequences) ;
		}
		nextFrame() ;
	}
	
	public void moveUp(){
		super.moveUp() ;
		
		if( currState == STATE_MOVING_UP ){
			setFrameSequences(movingUpSequences) ;
		}
		
		nextFrame() ;
	}
	
	public void moveDown(){		
		super.moveDown() ;
		
		if( currState == STATE_MOVING_DOWN ){
			setFrameSequences( movingDownSequences) ;
		}
		
		nextFrame() ;		 
	}
	
	public void die(){		
		super.die() ;
		
		bommanlife-- ;
		
		if( currState == STATE_DEAD ){
			setFrameSequences(deadSequences) ;
		}
	}
	
	public void setTheScrollBound(int hScroll,int vScroll){
		this.lineHScroll = hScroll ;
		this.lineVScroll = vScroll ;
	}
	
	public void setLocking(){
		isLocking = true ;
		isLockOn = true ;
		milliTimeLock = System.currentTimeMillis() - BoomCanvas.sumOfMillisecondsPauseGame ;
	}
	
	public boolean isLocking() {
		return isLocking;
	}
	
	public boolean isReversingDirection() {
		return isReversingDirection;
	}
	
	public void reversedDirection(){
		isReversingDirection = true ;
		milliTimeReversed = System.currentTimeMillis() - BoomCanvas.sumOfMillisecondsPauseGame ;
	}
	
	public void possibleKickBomb(boolean yesorno){
		possibleKickBomb = yesorno ;
	}
	
	 //set so mang cho bomberman
    public void setBommanlife(int numlife)
    {
        this.bommanlife=numlife;
    }
    
    public int getBommanlife()
    {
        return this.bommanlife;
    }
 /*  
    public void addLife(){
    	bommanlife++ ;
    }
    
    public void substractLife(){
    	bommanlife-- ;
    }
 */   
    public boolean isEndLife(){
    	return bommanlife < 0 ; 
    }
  
    public void plant() {
		numBombPlant-- ;
	}

    public void addBombPlant(){
    	numBombPlant++ ;
    }
    
    public boolean letPlant(){
    	return numBombPlant != 0 ;
    }
    
    public void setLevelBombExplode(int levelBombExplode) {
		this.levelBombExplode = levelBombExplode;
	}
    
    public int getLevelBombExplode() {
		return levelBombExplode;
	}
    
    public void reset(){
    	if( getCurrFrame() == 8){
    		speed = 6 ; //default speed
        	numBombPlant = 1 ; // default
        	levelBombExplode = 1 ;
    	}	
    }
}
