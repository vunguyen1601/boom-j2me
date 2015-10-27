package BoomGame;

import java.io.IOException;
import java.util.Random;

public class Enemy extends CellObject {	
	private final static int FRAME_WIDTH = 18 ;
	private final static int FRAME_HEIGHT = 22 ; 
	
	public final static int RED_ENEMY = 1 ;
	public final static int GREEN_ENEMY = 2 ;
	public final static int BLUE_ENEMY = 3 ;
	public final static int BLACK_ENEMY = 4 ;

	private final static int movingSequences[] = {0,1,2,3} ;
	private final static int deadSequences[] = {5,5,5,6,7,8,9} ;  	
	
	private int type ;
	
	private BoomWorld world ;
	
	protected Enemy(int type, BoomWorld world) throws IOException {			
		super(null,FRAME_WIDTH,FRAME_HEIGHT,world) ;
		this.world = world ;
		this.type = type ;
		speed = 5 ;
		
		switch (type) {
		case RED_ENEMY:
			setImgs(ImageStock.getImgsRedEnemy(FRAME_WIDTH,FRAME_HEIGHT));
			speed = 5 ;
			break;
			
		case GREEN_ENEMY :
			setImgs(ImageStock.getImgsGreenEnemy(FRAME_WIDTH,FRAME_HEIGHT));
			speed = 5 ;
			break ; 
			
		case BLUE_ENEMY :
			setImgs(ImageStock.getImgsBlueEnemy(FRAME_WIDTH,FRAME_HEIGHT));
			speed = 5 ;
			break ;
			
		case BLACK_ENEMY :
			setImgs(ImageStock.getImgsBlackEnemy(FRAME_WIDTH,FRAME_HEIGHT));
			speed = 5 ;
			break ;
			
		default:
			break;
		}	
		
	}
	
	private long prevTimeChangeDirection ;
	private int deltaTimeChangeDirection ;
	public void tick(int tickCount,Random random) {		
		if( (tickCount % 3) == 0 ){				
			if( isVisible() ){			
				
				if( currState == STATE_DEAD ){
					nextFrame() ;
					if( getCurrFrame() == deadSequences.length-1){
						setVisible( false ) ;
					}
					
					return ;
				}
				
				long currTimeChangeDirection = System.currentTimeMillis() ;
				if( (currTimeChangeDirection - prevTimeChangeDirection) >= 
					 deltaTimeChangeDirection){
					//	System.out.println("deltaTimeChangeDirection = " + (currTimeChangeDirection - prevTimeChangeDirection)) ;
						changeMovingDirection(random) ;	
						
						// set new 
						prevTimeChangeDirection = System.currentTimeMillis() ;
						deltaTimeChangeDirection = getRandomDeltaTime(4000, 10000, random) ;
				}
				
				nextFrame() ;
				
				switch (currState) {
				case STATE_MOVING_LEFT: // left
					moveLeft() ;
					break;
						
				case STATE_MOVING_RIGHT: // right
					moveRight() ;
					break ;
						
				case STATE_MOVING_DOWN ://  down
					moveDown() ;
					break ;
					
				case STATE_MOVING_UP : // up
					moveUp() ; 
					break ;
		
				default:
					break;
				}
				
				if( collideWithWall() ){
					undo() ;
					
					changeMovingDirection(random) ;
				}else if( collideWithBomb()){
					undo() ;
					
					// reversed the direction
					switch (currState) {
					case STATE_MOVING_DOWN:
						currState = STATE_MOVING_UP ;
						break;
						
					case STATE_MOVING_UP:
						currState = STATE_MOVING_DOWN ;
						break ;
						
					case STATE_MOVING_LEFT:
						currState = STATE_MOVING_RIGHT ;
						break;
						
					case STATE_MOVING_RIGHT :
						currState = STATE_MOVING_LEFT ;
						break ;
						
					default:
						break;
					}
				}
				
						
			} // visible
					
		} // tickCount

	}
	
	public void tick(int tickCount,Random random,BoomMan bombman){
		if( (tickCount % 3) == 0 ){
			
			if( isVisible() ){
				boolean performAI = false ;
				int xBomman = bombman.getXCell() ;
				int yBomman = bombman.getYCell() ;
				int xEnemy = getXCell() ;
				int yEnemy = getYCell() ;
								
				if( !bombman.isDead() ){
					
					boolean seeTarget = true ;
					int begin = 1 ,end = 0 ; // begin > end to quit if .. 
					if( xBomman > xEnemy ){
						begin = xEnemy ;
						end   = xBomman ;
					}
					else if( xBomman < xEnemy ){ 
						begin = xBomman ;
						end = xEnemy ;
					}
					
					if( yBomman > yEnemy ){
						begin = yEnemy ;
						end = yBomman ;
					}
					else if ( yBomman < yEnemy ){
						begin = yBomman ;
						end = yEnemy ;
					}
					
					if( begin > end ){
						return ;
					}
					
					if( xBomman == xEnemy ) {
						for( int yIndex = begin ; yIndex <= end ;yIndex++ ){ 
							if( world.isWall(xBomman,yIndex)){
								seeTarget = false ;
								break ;
							} 
						}
						
						
						if( seeTarget ){
							if( xBomman == xEnemy ){
								if( yBomman > yEnemy ){ 
									moveDown() ;
								}
								else if( yBomman < yEnemy ){ 
									moveUp() ;
								}
							
							performAI = true ;
						}
							nextFrame() ;
					} 
										
					}
					else if( yBomman == yEnemy ){					

						for( int xIndex = begin ; xIndex <= end ;xIndex ++ ){ 
							if( world.isWall(xIndex,yBomman) ){
								seeTarget = false ;
								break ;
							}
						}
						
						
						if( seeTarget ){
							if( xBomman > xEnemy ){
								moveRight() ;
							}
							else if( xBomman < xEnemy ){
								moveLeft() ;
							}
										
							performAI = true ;
						}
						
						nextFrame() ;
					}
					
				} // bombman not dead  
				
				if( collideWithWall() || collideWithBomb() ){
					undo() ;
				}
								  
				if( !performAI ){ // if not perform AI, just move normaly.
					tick(tickCount, random) ; 
				} 									

			}
		}
		
	}
	
	public void moveDown() {		
		super.moveDown();
		if( currState == STATE_MOVING_DOWN){
			setFrameSequences(movingSequences);
		}
	
	}
	
	public void moveLeft() {
		super.moveLeft() ;
		if( currState == STATE_MOVING_LEFT ){
			setFrameSequences(movingSequences);
		}
	}
	
	public void moveRight() {
		super.moveRight();
		if( currState == STATE_MOVING_RIGHT){
			setFrameSequences(movingSequences);
		}
			
	}
	
	public void moveUp() {
		super.moveUp();
		if( currState == STATE_MOVING_UP){
			setFrameSequences(movingSequences);
		}
		
	}
	
	public void die() {	
		super.die();
		if( currState == STATE_DEAD ){
			setFrameSequences(deadSequences) ;
		}
	}
	
	public int getType() {
		return type;
	}
	
	public void changeMovingDirection(Random random){
		int xCell = getXCell() ;
		int yCell = getYCell() ;
		BoomWorld world = getWorld() ;
		int bool = getRandom(0,1, random) ;
		switch (currState) {
		case STATE_MOVING_LEFT:
			if( bool != 0){ // check moving up firstly.
				if( !world.isWall( xCell , yCell - 1)){
					currState = STATE_MOVING_UP ;
				}
				else if( !world.isWall( xCell , yCell + 1 )){
					currState = STATE_MOVING_DOWN ;
				}			
				else if( !world.isWall( xCell - 1, yCell)){
					currState = STATE_MOVING_LEFT ; // continue to move
				}
				else {
					currState = STATE_MOVING_RIGHT ;
				}
			}
			else { // check moving down firstly.
				if( !world.isWall( xCell , yCell + 1 )){
					currState = STATE_MOVING_DOWN ;
				}
				else if( !world.isWall( xCell , yCell - 1)){
					currState = STATE_MOVING_UP ;
				}
				else if( !world.isWall(xCell - 1, yCell)){
					currState = STATE_MOVING_LEFT ; // continue to move
				}
				else{
					currState = STATE_MOVING_RIGHT ;
				}
			}
			
			break;
			
		case STATE_MOVING_RIGHT :
			if( bool != 0){
				if( !world.isWall( xCell , yCell - 1)){
					currState = STATE_MOVING_UP ;
				}
				else if( !world.isWall( xCell , yCell + 1 )){
					currState = STATE_MOVING_DOWN ;
				}		
				else if( !world.isWall( xCell + 1, yCell)) {
					currState = STATE_MOVING_RIGHT ;
				}
				else {
					currState = STATE_MOVING_LEFT ;
				}
			}
			else{
				if( !world.isWall( xCell , yCell + 1 )){
					currState = STATE_MOVING_DOWN ;
				}		
				else if( !world.isWall( xCell , yCell - 1)){
					currState = STATE_MOVING_UP ;
				}				
				else if( !world.isWall( xCell + 1, yCell)) {
					currState = STATE_MOVING_RIGHT ;
				}
				else {
					currState = STATE_MOVING_LEFT ;
				}
			}
			
			break ;
			
		case STATE_MOVING_UP :
			if( bool != 0){
				if( !world.isWall(xCell + 1, yCell)){
					currState = STATE_MOVING_RIGHT ;
				}
				else if( !world.isWall( xCell -1 , yCell)){
					currState = STATE_MOVING_LEFT ;
				}	
				else if( !world.isWall( xCell , yCell - 1)){
					currState = STATE_MOVING_UP ;
				}
				else {
					currState = STATE_MOVING_DOWN ;
				}
			}
			else {
				if( !world.isWall( xCell -1 , yCell)){
					currState = STATE_MOVING_LEFT ;
				}	
				else if( !world.isWall(xCell + 1, yCell)){
					currState = STATE_MOVING_RIGHT ;
				}				
				else if( !world.isWall( xCell , yCell - 1)){
					currState = STATE_MOVING_UP ;
				}
				else {
					currState = STATE_MOVING_DOWN ;
				}
			}
			
			break ;
			
		case STATE_MOVING_DOWN :
			if( bool != 0){
				if( !world.isWall( xCell -1 , yCell)){
					currState = STATE_MOVING_LEFT ;
				}
				else if( !world.isWall(xCell + 1, yCell)){
					currState = STATE_MOVING_RIGHT ;
				}
				else if( !world.isWall(xCell, yCell + 1)){
					currState = STATE_MOVING_DOWN ;
				}
				else {
					currState = STATE_MOVING_UP ;
				}
			}
			else {
				if( !world.isWall(xCell + 1, yCell)){
					currState = STATE_MOVING_RIGHT ;
				}
				else if( !world.isWall( xCell -1 , yCell)){
					currState = STATE_MOVING_LEFT ;
				}				
				else if( !world.isWall(xCell, yCell + 1)){
					currState = STATE_MOVING_DOWN ;
				}
				else {
					currState = STATE_MOVING_UP ;
				}
			}
			
			break ;
			
		default:
			break;
		}		
		
	}
	
	public int getRandomDeltaTime(int minTime,int maxTime,Random random){
		return getRandom(minTime,maxTime,random) ;
	}
	
	public void reNewLife(){
		currState = STATE_MOVING_LEFT ;
		setFrameSequences(movingSequences) ;
		setVisible(true);
	}
	
	public int getRandom(int low,int hight,Random rand ){
		return (rand.nextInt() %(hight - low + 1)) +low ;
	}
}
