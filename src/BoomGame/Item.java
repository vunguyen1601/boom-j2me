package BoomGame;

import java.io.IOException;

import javax.microedition.lcdui.Graphics;

import Original.WorldObject;

public class Item extends WorldObject {
	
	private final static int FRAME_WIDTH = 16 ;
	private final static int FRAME_HEIGHT = 16 ;
	
	public final static int TYPE_INCREASE_NUM_BOMB = 1 ;
	public final static int TYPE_INCREASE_SPEED = 2 ;
	public final static int TYPE_THROWN_BOMB = 3 ;
	public final static int TYPE_KICK_BOMB = 4 ;
	public final static int TYPE_INCREASE_LEVEL_EXPLOSION = 5 ;
	public final static int TYPE_MAX_LEVEL_EXPLOSION = 6 ;
	public final static int TYPE_REVERSED_DIRECTION = 7 ;
	
	private final static int frmSequences[] = {0,1} ;
	private boolean isVisible ;
	private int type ;
	
	private int xCell ;
	private int yCell ;
	
	protected Item(int type) throws IOException {
		super(null,FRAME_WIDTH,FRAME_HEIGHT);
		setType(type) ;
		defineRefpx( FRAME_WIDTH >> 1, FRAME_HEIGHT >> 1) ;		
		setFrameSequences(frmSequences);
		isVisible = true ;
	}
	
	public void setCellPosition(int col,int row){

		int cellSize = BoomWorld.CELL_WIDTH ;
		int alignCenter = cellSize >> 1 ;
		setPosition((col*cellSize)+alignCenter,
					(row*cellSize)+alignCenter) ;
		
		xCell = col ;
		yCell = row ;
	}
	
	public int getXCell() {
		return xCell;
	}
	
	public int getYCell() {
		return yCell;
	}
	
	public int getType() {
		return type;
	}
	
	public void tick(int tickCount){
		if( isVisible ){
			if( (tickCount % 5) == 0){
				nextFrame() ;
			}	
		}		
	}
	
	public void paint(Graphics g, int view, int view2, int widthView,
			int heightView) {
		if( isVisible){
			super.paint(g, view, view2, widthView, heightView);
		}		
	}
	public void setType(int type)  {
		this.type = type ;	
		
	try {		
		switch (type) {
		case TYPE_INCREASE_NUM_BOMB:
			setImgs(ImageStock.getImgBombItem(FRAME_WIDTH ,FRAME_HEIGHT)) ;
			break;
			
		case TYPE_INCREASE_SPEED :
			setImgs(ImageStock.getImgSpeedItem(FRAME_WIDTH ,FRAME_HEIGHT)) ;
			break ;
			
		case TYPE_KICK_BOMB:
			setImgs(ImageStock.getImgKickItem(FRAME_WIDTH ,FRAME_HEIGHT)) ;
			break ;
			
		case TYPE_THROWN_BOMB:
			setImgs(ImageStock.getImgThrownItem(FRAME_WIDTH ,FRAME_HEIGHT)) ;
			break;
			
		case TYPE_INCREASE_LEVEL_EXPLOSION :
			setImgs(ImageStock.getImgLevelExploItem(FRAME_WIDTH ,FRAME_HEIGHT)) ;
			break ;
			
		case TYPE_REVERSED_DIRECTION :
			setImgs(ImageStock.getImgReversedItem(FRAME_WIDTH ,FRAME_HEIGHT)) ;
			break ;
			
		case TYPE_MAX_LEVEL_EXPLOSION :
			setImgs(ImageStock.getImgMaxExploItem(FRAME_WIDTH ,FRAME_HEIGHT)) ;			
			break ;

		default:
			break;
		}
		
	} catch (Exception e) {
		// TODO: handle exception
	}
	
	}
	
	public boolean isVisible() {
		return isVisible;
	}
	
	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}
}
