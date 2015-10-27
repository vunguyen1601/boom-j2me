package MainGame;


import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import Original.ImageUtility;
public class GameBoard {
	public final static byte CELL_BLANK = 0 ;
	private byte cells[] ;
	private int CELL_WIDTH ;
	private int CELL_HEGHT  ;
	private int cols ;
	private int rows ;
	private Image imgs[] ;

	private int xViewWindow ;
	private int yViewWindow ;
	private int widthViewWindow ;
	private int heightViewWindow ;
	
	private int xCellDrawing ;
	private int yCellDrawing ;
	
	public GameBoard(Image img,int cols,int rows,int frameWidth,int frameHeihgt){
		if( img == null ){
			throw new NullPointerException();
		}
		
		CELL_WIDTH = frameWidth ;
		CELL_HEGHT = frameHeihgt ;
		this.cols = cols ;
		this.rows = rows ;		
		cells = new byte[this.cols*this.rows] ;
		
		int numFramesWidth = img.getWidth()/frameWidth ;
		int numFramesHeight = img.getHeight() / frameHeihgt ;
	//	Println.show(" "+numFramesWidth+" " + numFramesHeight);
	//	Println.show(" " + Byte.MAX_VALUE +" "+ Byte.MIN_VALUE  ) ;
		imgs = ImageUtility.extractFrames(img,0,0,numFramesWidth,numFramesHeight,frameWidth,frameHeihgt,true) ;	
	}
	
	public void setCell(int x,int y ,byte tiled){
		cells[y*cols + x] = tiled ;
	}
	
	public byte getCell(int x,int y){
		return cells[y*cols + x] ;
	}
	
	public int getRows(){
		return rows ;
	}
	
	public int getCols(){
		return cols ;
	}
	
	public void drawn(Graphics g){
/*		g.clipRect(xViewWindow, yViewWindow,
				   widthViewWindow ,heightViewWindow) ;
*/		
		byte currCell ;
		int numCells = cells.length ;
		int x = 0,y = 0  ;
		for(int index = 0 ;index < numCells ;index++){
			currCell = cells[index] ;
			g.drawImage(imgs[currCell],
					    xViewWindow+(x*CELL_WIDTH),yViewWindow+(y*CELL_HEGHT),
					    ImageUtility.GRAPHICS_TOP_LEFT ) ;
			//Println.show("x =" + (-xViewWindow+(x*CELL_WIDTH))+" y = " + (-yViewWindow+(y*CELL_HEGHT))) ;
			x++ ;
			if( x >= cols){
				x = 0 ;
				y++ ;
			}
		}
	//	Println.show("-----------------");
	}
	
	public void setDrawingArea(Graphics g,int x,int y,int width,int height){
		xViewWindow = x ;
		yViewWindow = y ;
		widthViewWindow = width ;
		heightViewWindow = height ;
		g.clipRect(xViewWindow, yViewWindow,
				   widthViewWindow ,heightViewWindow) ;
	}
	
	public void adjust(int dx,int dy){
	/*	xCellDrawing += dx ;
		yCellDrawing += dy ;
		*/
		xViewWindow -= dx ;
		yViewWindow -= dy ;
	}
}
