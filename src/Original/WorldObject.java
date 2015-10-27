package Original;


import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;


public abstract class WorldObject {
	private int xWorld ; //  xWorld = 0 
	private int yWorld ; //  yWorld = 0
	
	private int xRefpx ;
	private int yRefpx ;
	private Image imgs[] ;
	private int frameSequences[] ;	
	private int currFrame ; // currFrame = 0 
	private int frame_width   ;
	private int frame_height ;
	
	private boolean isVisible ;
	private int transform ;
	private boolean isTransform ;
	
	protected WorldObject(Image [] imgSet,int frmWidth,int frmHeight) {
		imgs = imgSet ;		 
		int frmSequences[] = {0} ;
		frameSequences = frmSequences ;
		frame_width  = frmWidth ; 
		frame_height = frmHeight ;
		isVisible = true ;
	}
	
	public Image[] getImgs() {
		return imgs;
	} 
	
	public void setImgs(Image imgs[]){
		this.imgs = imgs ;
	}
	
	protected void defineRefpx(int x,int y){
		xRefpx = x ;
		yRefpx = y ;
	}
	
	public int getXRefpx() {
		return xRefpx;
	}
	
	public int getYRefpx() {
		return yRefpx;
	}
	
	protected void setFrameSequences(int[] frameSequences) {
		this.frameSequences = frameSequences;
	}
	
	public int[] getFrameSequences() {
		return frameSequences;
	}
	
	public int getXWorld() {
		return xWorld;
	}
	
	public int getYWorld() {
		return yWorld;
	}
	
	public void setPosition(int x,int y){
		xWorld = x ;
		yWorld = y ;
	}
	
	public void move(int dx,int dy){
		xWorld += dx ;
		yWorld += dy ;
	}
	
	public void nextFrame(){
		if( frameSequences == null ){
			throw new NullPointerException() ;
		}
		
		currFrame++ ;
		if( currFrame >= frameSequences.length ){			
			// first index images.
			currFrame = 0 ;
		}
	}
	
	public void prevFrame(){
		if( frameSequences == null ){
			throw new NullPointerException() ;
		}
		
		currFrame-- ;
		if( currFrame < 0){
			// last index images.
			currFrame = frameSequences.length - 1 ; 
		}
	}
	
	public int getCurrFrame() {
		return currFrame;
	}
		
	public void setCurrFrame(int currFrame) {
		this.currFrame = currFrame;
	}
	
	public boolean isFirstFrame(){
		return currFrame == 0 ;
	}
	
	public boolean isLastFrame(){
		return currFrame == frameSequences.length -1 ;
	}
		
	protected void setTransform(int transform){
		if( transform == Sprite.TRANS_NONE ){
			isTransform = false ;
		}
		else {			
			isTransform = true ;
		}		
		
		this.transform = transform ;
	}
	
	protected int getTransform() {
		return transform;
	}
	
	public boolean isVisible() {
		return isVisible;
	}
	
	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}
	
	/**
	 *  Check the view window that is being in the canvas coordinate system.
	 * */
	public boolean isInDrawingArea(int xView,int yView,int widthView,int heightView){
		// the position when is transformed 
		// and convert to screen coordinate system.
		int xDrawing = xWorld - xView - xRefpx  ;
		int yDrawing = yWorld - yView - yRefpx  ;
	
		// we are drawing at (0,0)coordinate system.
		if( (xDrawing+frame_width ) > 0 && (yDrawing + frame_height) > 0 &&
			 xDrawing < widthView       &&  yDrawing < heightView ){ 
			return true ;
		}				
			return false ;
	}
	
	/**
	 *  Override this function to paint the things that you want.Please convert as the following 
	 *  to paint the current frame :
	 *   xDraw = xWorld - xViewWindow ;
	 *   yDraw = yWorld - yViewWindow ; 
	 *   To optimize, call the following statements for the drawing area reduce.
	 *   if( isInDrawingArea(xViewWindow, yViewWindow, widthViewWindow, heightViewWindow)){} 
	 * */	 
	public void paint(Graphics g,int xView,int yView,int widthView,int heightView){
		if( isVisible ){
			if( isInDrawingArea(xView, yView, widthView, heightView)){		
				//convert to screen coordinate system.
				//	Println.show(" drawing ... ");
					int xDrawing = xWorld - xView - xRefpx ;
					int yDrawing = yWorld - yView - yRefpx ;
					if( !isTransform ){
						g.drawImage(imgs[frameSequences[currFrame]],xDrawing,yDrawing,ImageUtility.GRAPHICS_TOP_LEFT) ;
					}
					else{
						g.drawRegion(imgs[frameSequences[currFrame]],
									 0,0,
									 frame_width,frame_height,
									 transform,
									 xDrawing,yDrawing,
									 ImageUtility.GRAPHICS_TOP_LEFT) ;
					}
					
				} // is in drawing area.
			
		}	// is visible
	}
	
	public boolean collideWith(WorldObject wo){
		int left = xWorld - xRefpx ;
		int top  = yWorld - yRefpx ;
		int right =  left + frame_width ;
		int bottom = top  + frame_height ;
		int posX = wo.getXWorld() - wo.getXRefpx() ;
		int posY = wo.getYWorld() - wo.getYRefpx() ;
		// checks the posX,posY whether is in the rectangle. 
		if( left <= posX && right >= posX && 
			top  <= posY && bottom >= posY	) {  
			
			return true ;
		}
		return false ;
	}
}
