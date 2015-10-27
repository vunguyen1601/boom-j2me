package Original;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public abstract class GameWorld {
	private byte cells[] ;
	private int numCols ;
	private int numRows ;
	private Image imgs[] ;
	
	private int widthViewWindow ;
	private int heightViewWindow ; 	
	private int xViewWindow ;
	private int yViewWindow ;
		
	private int cell_width ; 
	private int cell_height  ;
	
	public GameWorld(Image imgs[],int cols,int rows,					
					 int frameWidth,int frameHeight){
		
		numCols = cols ;
		numRows = rows ;
		cells = new byte [numCols*numRows] ;
		cell_width   = frameWidth ;
		cell_height  = frameHeight ; 
		this.imgs = imgs ;
	}
	public void setCells(){
		cells = new byte [numCols*numRows] ;
	}
	public void setCell(int col,int row, byte cellType){
		cells[row*numCols + col] = cellType ;
	}
	
	public byte getCell(int col,int row){	
		 return cells[row*numCols + col] ;
	}
	
	public void setCell(int index,byte cellTye){
		cells[index] = cellTye ;
	}
	
	public byte getCell(int index){
		return cells[index] ;
	}
		
	public int getNumCols() {
		return numCols;
	}
	
	public int getNumRows() {
		return numRows;
	}
	public void setNumCols(int col) {
		numCols = col ;
	}

	public void setNumRows(int row) {
		numRows= row ;
	}
	/**
	 *  By Default, the function draws the background at (0,0) position. 
	 *  Using graphics.translate(xScreen,yScreen) when override this function.	 
	 *  
	 *  NOTE: call g.setClip(0,0,getWidth(),getHeight()) to reset the clipping region
	 *        for drawing the area that is outside.
	 * */
	public void paint(Graphics g,int xGameScreen,int yGameScreen){
/*		
	// draw the background... 	
		byte currCell ;
		int numCells = cells.length ;
		int x = 0,y = 0  ;
		for(int index = 0 ;index < numCells ;index++){
			
			// just draw the cells that are in the view window. 
	//		if( cellInDrawingArea(x, y)){
				currCell = cells[index] ;
				g.drawImage(imgs[currCell],
						    xViewWindow+(x*CELL_WIDTH),yViewWindow+(y*CELL_HEIGTH),
						    ImageUtility.GRAPHICS_TOP_LEFT ) ;			
	//		}	
			x++ ;
			if( x >= numCols){
				x = 0 ;
				y++ ;
			}
		}

		/*
		if( objects != null ){
			int objsLength = objects.length ;
			for(int index = 0 ; index < objsLength ; index++ ){
				objects[index].paint(g, xViewWindow, yViewWindow,widthViewWindow,heightViewWindow) ;
			}
		}
*/
		// default draw the background at (0,0) positions.
		//
		
		//clip the drawing.  
		g.setClip(0,0,widthViewWindow,heightViewWindow);
		
		final int GRAPHICS_TOP_LEFT = ImageUtility.GRAPHICS_TOP_LEFT ;
		
		int startViewCol =  xViewWindow / cell_width ;
		int endViewCol   = (xViewWindow+widthViewWindow) / cell_width ;
		int startViewRow =  yViewWindow / cell_height ;
		int endViewRow   = (yViewWindow+heightViewWindow) / cell_height ;
		int numDrawingCol = endViewCol - startViewCol + 1 ;
		int numDrawingRow = endViewRow - startViewRow + 1 ;
		 
		int xOffset =  - (xViewWindow % cell_width)  ;
		int yOffset =  - (yViewWindow % cell_height) ;
/*		
		Println.show("startCol = " + startViewCol + " endCol = " + endViewCol ) ;
		Println.show("startRow = " + startViewRow + " endRow = " + endViewRow) ;
		Println.show("----------------------") ;
*/		
		byte currCell ;	
		int nextViewCol,nextViewRow ;
		for( int drawingRow = 0 ; drawingRow < numDrawingRow ; drawingRow++ ){
			for( int drawingCol = 0 ; drawingCol < numDrawingCol ;drawingCol++ ){
				nextViewCol = drawingCol + startViewCol ;  
				nextViewRow = drawingRow + startViewRow ;
				if( isInBounds(nextViewCol,nextViewRow) ){
					currCell = getCell(nextViewCol,nextViewRow) ;
					g.drawImage(imgs[currCell],
								xOffset+(drawingCol*cell_width),yOffset+(drawingRow*cell_height),
								GRAPHICS_TOP_LEFT ) ;
				}			
			} // for
		} // for			
	}
	
	public void setViewWindowPos(int x,int y){
		xViewWindow = x; 
		yViewWindow = y ;
	}
	
	public void scroll(int dx,int dy){
		xViewWindow += dx ;
		yViewWindow += dy ;
	}

	public void setDrawingSize(int widthDraw,int heightDraw){
		widthViewWindow = widthDraw ;
		heightViewWindow = heightDraw ;		
	}
		
	public boolean isInBounds(int col,int row){
		return col >= 0 && col < numCols && 
			   row >= 0 && row < numRows ;
	}
	
	public int getXViewWindow() {
		return xViewWindow;
	}
	
	public int getYViewWindow() {
		return yViewWindow;
	}
	
	public int getWidthViewWindow() {
		return widthViewWindow ;
	}
	
	public int getHeightViewWindow() {
		return heightViewWindow ;
	}
	
	public int getImgHeight(){
		return imgs[0].getHeight() ;
	}
        public void setimgs(Image imgsbien[]) {
		this.imgs = imgsbien ;
	}
}
