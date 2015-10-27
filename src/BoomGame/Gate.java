package BoomGame;

import java.io.IOException;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import Original.WorldObject;

public class Gate extends WorldObject{
	private final static int FRAME_WIDTH = 20 ;
	private final static int FRAME_HEIGHT = 20 ;
	
	private final static int frmSequences[] = {0} ;
	private boolean isVisible ;
	private int type ; 
	
	private int xCell ;
	private int yCell ;
	
	protected Gate() throws IOException {
		super(null,FRAME_WIDTH,FRAME_HEIGHT);
		defineRefpx( FRAME_WIDTH >> 1, FRAME_HEIGHT >> 1) ;		
		setFrameSequences(frmSequences);
		Image imgs[] = new Image[1] ;
		imgs[0] = Image.createImage("/gate.png") ;
		setImgs(imgs) ;
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
}
