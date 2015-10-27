package BoomGame;

import javax.microedition.lcdui.Image;

import Original.WorldObject;

public abstract class CellObject extends WorldObject{

	protected final static int STATE_MOVING_LEFT = 0 ;
	protected final static int STATE_MOVING_RIGHT = 1 ;
	protected final static int STATE_MOVING_UP = 2 ;
	protected final static int STATE_MOVING_DOWN = 3 ;	
	protected final static int STATE_DEAD = 4 ;
	
	protected final static int CELL_LEFT_AREA = 6 ; // 0 - 6
	protected final static int CELL_CENTER_AREA = 7 ; // 7 - 12 
	protected final static int CELL_RIGHT_AREA = 13; // 13 - 19
	
	protected int currState ;
	
	protected int speed = 1; 
	
	private BoomWorld world ;
	
	//save the cell position in the world for fast accessing.
	private int xCell ,yCell ;
	
	
	private int lastDx,lastDy ;
		
	protected CellObject(Image[] imgSet,int frmWidth,int frmHeight,BoomWorld world) {
		super(imgSet,frmWidth,frmHeight);
		// TODO Auto-generated constructor stub
		defineRefpx(frmWidth >> 1 , frmHeight -10 ) ;
		this.world = world ;
	}
		
	public int getXCell() {
		return xCell;
	}
	
	public int getYCell() {
		return yCell;
	}
	
	/**
	 *  Place the sprite in the center cell.
	 * */
	public void setCellPosition(int col,int row){		
		
		int cellSize = BoomWorld.CELL_WIDTH ;
		int alignCenter = cellSize >> 1 ;
		setPosition((col*cellSize)+alignCenter,
					(row*cellSize)+alignCenter) ;
		
		xCell = col ;
		yCell = row ;
	}
	
	public int getLastDx() {
		return lastDx;
	}
	
	public int getLastDy() {
		return lastDy;
	}
		
	public void move(int dx,int dy){
		lastDx = dx ;
		lastDy = dy ;
		super.move(lastDx, lastDy) ;		
		updateCellPos() ;
	}
	
	public BoomWorld getWorld() {
		return world;
	}

	public int getSpeed() {
		return speed; 
	}
	
	public void setSpeed(int speed) {
		this.speed = speed;
	}
	
	public void tick(int tickCount){
		
	}
	
	public boolean collideWith(int xOtherCell,int yOtherCell){
		return xCell == xOtherCell && yCell == yOtherCell ;
	}
		
	public boolean collideWith(WorldObject wo) {
		CellObject cell = (CellObject)(wo) ;
		return xCell == cell.getXCell() && yCell == cell.getYCell() ;
	}
	
	public boolean collideWithBomb(){
		return Boom.hasABomb(xCell, yCell) ;
	}
	public boolean collideWithExplosion(Boom boom){
		return boom.isInExplosionCell(xCell, yCell) ;
	}
	
	public boolean collideWithWall(){
		return world.isWall(xCell,yCell) ;
	}
	
	public void moveLeft(){		
		move(-speed,0) ;
		if( currState  != STATE_MOVING_LEFT ){		
			currState = STATE_MOVING_LEFT ;
		}		
		
	}
	
	public void moveRight(){		
		move(speed, 0) ;
		if( currState != STATE_MOVING_RIGHT ){			
			currState = STATE_MOVING_RIGHT ;
		}		
	}
	
	public void moveUp(){
		move(0, -speed) ;	
		if( currState != STATE_MOVING_UP ){
			currState = STATE_MOVING_UP ;
		}			
	}
	
	public void moveDown(){		
		move(0, speed) ;
		if( currState != STATE_MOVING_DOWN ){
			currState = STATE_MOVING_DOWN ;
		}		
		
	}	
	
	public void die(){		
		if( currState != STATE_DEAD ){
			currState = STATE_DEAD ;
		}
	}
	
	public boolean isDead(){
		return currState == STATE_DEAD ;
	}
	
	protected void updateCellPos(){
		// simulate division.
		int cellSize = BoomWorld.CELL_WIDTH ;
		int xCellPX = xCell*cellSize ;
		int yCellPX = yCell*cellSize ;
		int xWorld = getXWorld() ; 
		int yWorld = getYWorld() ;
			
		if( xWorld >= (xCellPX+cellSize)){ // if the sprite is in the next xCell.
			xCell++ ;			
		}
		else if( xWorld < xCellPX ){ //if the sprite is in the previous xCell.
			xCell-- ;
		}
		
		if( yWorld >= (yCellPX+cellSize)){ // if the sprite is in the next yCell.
			yCell++ ;
		}
		else if( yWorld < yCellPX ){ //if the sprite is in the previous yCell.
			yCell-- ;
		}
	}
	
	public void undo(){
		super.move(-lastDx, -lastDy);
		
		updateCellPos() ;
	}
	
	public int getXAreaInCell(){
		int xPosInCell =  getXWorld() - getXCell()*BoomWorld.CELL_WIDTH ;
		if( xPosInCell <= CELL_LEFT_AREA ){
			return CELL_LEFT_AREA ;
		}
		else if( xPosInCell >= CELL_RIGHT_AREA ){
			return CELL_RIGHT_AREA ;
		}
		else {
			return CELL_CENTER_AREA ;
		}		
	}
	
	public int getYAreaInCell(){
		int yPosInCell =  getYWorld() - getYCell()*BoomWorld.CELL_WIDTH ;
		if( yPosInCell <= CELL_LEFT_AREA ){
			return CELL_LEFT_AREA ;
		}
		else if( yPosInCell >= CELL_RIGHT_AREA ){
			return CELL_RIGHT_AREA ;
		}
		else {
			return CELL_CENTER_AREA ;
		}
	}
}
