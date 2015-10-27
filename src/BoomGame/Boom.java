package BoomGame;

import java.io.IOException;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;

import MainGame.BoomCanvas;
import Original.ImageUtility;
import Original.SoundEffects;
import Original.WorldObject;

public class Boom extends WorldObject{
	private final static int FRAME_WIDTH = 20;
	private final static int FRAME_HEIGHT = 20;
	private final static int MAX_FRAME = 4;
	
	private final static long BOMB_LIFE_TIME = 2000 ;// 2s
	
	private final static int endExplosionSequences[] = {0,1,2,3} ;
	private final static int centerExplosionSequences[] = {4,5,6,7} ;
	private final static int continueExplosionSequences[] = {8,9,10,11} ;
	private final static int bombSequences[] = {12,13,14,15} ;
	
	// imgsLeft is original image. These images is transformed.
	private static Image imgsRight[] ;
	private static Image imgsDown[] ;
	private static Image imgsLeft[] ;
	
	private int xExplode ;
	private int yExplode ;
	private int upExplode ;
	private int downExplode ; 
	private int leftExplode ;
	private int rightExplode ;
	
	private long firstLifeTime ;
	
	public final static int MAX_LEVEL = 6 ;
	// the bomb is exploding. 
	private boolean isExploding ;
	//Visible when plant the bomb, and when the bomb explode.
	private boolean isVisible ;
	
	private BoomMan owner ;
	
	private static BoomWorld board ;
	
	private static BoomManager manager ;
	
	private static boolean bombCoord[] ;
	
	private static SoundEffects sound ;
	
	private boolean enableReset ; // reset into the default
	
	public Boom(BoomWorld b,BoomManager manager) throws IOException {
		super(null,FRAME_WIDTH,FRAME_HEIGHT);
		Image imgs[] = ImageStock.getImgsExplosionBomb(FRAME_WIDTH,FRAME_HEIGHT) ;
		setImgs(imgs) ;
		imgsRight = ImageUtility.extractTransformFrames(imgs,
													   FRAME_WIDTH,FRAME_HEIGHT,
													   Sprite.TRANS_ROT90) ;

		imgsDown = ImageUtility.extractTransformFrames(imgs,
													   FRAME_WIDTH,FRAME_HEIGHT,
													   Sprite.TRANS_ROT180) ;

		imgsLeft = ImageUtility.extractTransformFrames(imgs,
													   FRAME_WIDTH,FRAME_HEIGHT,
													   Sprite.TRANS_ROT270) ;
		board = b ; 
		
		Boom.manager = manager ;
		
		bombCoord = new boolean[b.getNumCols()*b.getNumRows()];
		
		sound = SoundEffects.getInstance() ;
	}
	
	public void tick(int tickCount){
		if( isVisible ){
			int currFrame = getCurrFrame() ;
			if( tickCount % 3 == 0)
			{ // slow down the animation.
				currFrame++ ;
							
			    if( currFrame >= MAX_FRAME ){
					 currFrame = 0 ;
					 if( isExploding ){// if explosion animation reach at the last frame.
						 isExploding = false ;
						 isVisible = false ;	
					 }						 	
				 }	
			}
				
			//check when the bomb is planted.
				if( isVisible && !isExploding ){
					long remainTime =  BOMB_LIFE_TIME - 
									  (System.currentTimeMillis() - firstLifeTime) + /*elapseTime*/
									   BoomCanvas.sumOfMillisecondsPauseGame;
			
					if( remainTime <= 0 ){					
						firstLifeTime = 0 ;
						
						sound.startExplosionSound() ;
						
						isExploding = true ;
						// reset current frame for explosion animation.
						currFrame = 0 ;						
						
						Boom.setABomb(xExplode,yExplode,false) ;	
						
						owner.addBombPlant() ; // return a bomb for bomb man.
					}
				}		

				// check the explosion	
				if( isVisible && isExploding ){
					setExplosionArea() ;
				}
					
				setCurrFrame(currFrame) ;
		}		
	}
	
	public void plant(BoomMan man,int x,int y){
		if( !isVisible ){
			owner = man ;
			
			man.plant() ;
			int level = man.getLevelBombExplode() ;
			xExplode = x ;
			yExplode = y ;
			
			upExplode =   yExplode - level ;
			downExplode = yExplode + level ;
			leftExplode = xExplode - level ;
			rightExplode = xExplode + level ;
			
			isVisible = true ;			
			isExploding = false ;
			
			Boom.setABomb(x, y, true) ;
			
			firstLifeTime = System.currentTimeMillis() - BoomCanvas.sumOfMillisecondsPauseGame;
		}	
	}
	
	public boolean isInDrawingArea(int xView, int yView, int widthView,int heightView){
		
		int cellSize = BoomWorld.CELL_WIDTH ;
		// refpixel = 0 
		int boundLeft  = leftExplode*cellSize  - xView   ;
		int boundRight = rightExplode*cellSize - xView   ; 
		int boundUp    = upExplode*cellSize    - yView   ;
		int boundDown  = downExplode*cellSize  - yView   ;
		return (boundLeft + FRAME_WIDTH) > 0 && (boundUp + FRAME_HEIGHT) > 0 &&
			   (boundRight <  widthView) && (boundDown < heightView) ;	
	}
	
	public void paint(Graphics g, int xView, int yView, int widthView,int heightView) {		
		if( isVisible ){
			int currFrame = getCurrFrame() ;	
			int level = owner.getLevelBombExplode() ;
			Image imgs[] = getImgs() ;
			int TopLeft = ImageUtility.GRAPHICS_TOP_LEFT ;
			if( isExploding ){
			int sizeCell = BoomWorld.CELL_WIDTH ;
			// draw the center
			int xPxCenter = xExplode*sizeCell - xView;
			int yPxCenter = yExplode*sizeCell - yView;
			g.drawImage(imgs[centerExplosionSequences[currFrame]],
						xPxCenter,yPxCenter,					
						TopLeft );
				
			int x,y ;
			
			// draw the up
			int nextUp = yExplode - 1 ; // the next up.
			for(y = nextUp ; y >= upExplode ; y-- ){	
				if( y == yExplode - level ) break ;
						g.drawImage(imgs[continueExplosionSequences[currFrame]],
									xPxCenter,y*sizeCell - yView ,
									TopLeft) ;	
			}							
			if( yExplode - level == upExplode){
				if( !board.isWall(xExplode,upExplode)){
					g.drawImage(imgs[endExplosionSequences[currFrame]],
						        xPxCenter,upExplode*sizeCell - yView , 
						        TopLeft) ;

				}
			}

			// draw the down
			 int nextDown = yExplode + 1 ;	
			 for( y = nextDown; y <= downExplode ; y++ ){
				 if( y == yExplode + level ) break ;
						g.drawImage(imgsDown[continueExplosionSequences[currFrame]],			
								 	 xPxCenter,y*sizeCell - yView , 
								 	 TopLeft) ;			 
			}
			if( yExplode + level == downExplode ){
				if( !board.isWall(xExplode,downExplode)){
					g.drawImage(imgsDown[endExplosionSequences[currFrame]],
					    	 xPxCenter,downExplode*sizeCell - yView , 
					    	 TopLeft) ;
				}	
			}

			 //draw the left
			 int nextLeft = xExplode - 1 ;		 
			 for( x = nextLeft ; x >= leftExplode ; x-- ){
				 if( x == xExplode - level ) break ;
					 g.drawImage(imgsLeft[continueExplosionSequences[currFrame]],
							 	  x*sizeCell - xView ,yPxCenter, 
							 	  TopLeft) ;			
			 }
			 if( xExplode - level == leftExplode){
				 if( !board.isWall(leftExplode,yExplode)){
					 g.drawImage(imgsLeft[endExplosionSequences[currFrame]],
						 	  leftExplode*sizeCell - xView ,yPxCenter, 
					 	 	  TopLeft) ;

				 }
 			 }

			 //draw the right
			 int nextRight = xExplode + 1 ;
			 for( x = nextRight ; x <= rightExplode ; x++ ){
				 if( x == xExplode + level ) break ;
					 g.drawImage(imgsRight[continueExplosionSequences[currFrame]],
							 	  x*sizeCell - xView , yPxCenter,
							 	  TopLeft) ;			 
			 }
			 if( xExplode + level == rightExplode){
				 if( !board.isWall(rightExplode,yExplode)){
					 g.drawImage(imgsRight[endExplosionSequences[currFrame]],
							     rightExplode*sizeCell - xView , yPxCenter,
						 	     TopLeft) ;
				 }				 
			 }
			 				
			 } // is exploding	 
			
			else{ // is not Exploding 			
				
					// next frame for bomb.
					g.drawImage(imgs[bombSequences[currFrame]],
							    xExplode*BoomWorld.CELL_WIDTH - xView ,
							    yExplode*BoomWorld.CELL_WIDTH - yView ,
							    TopLeft);
				}
			}// is visible	  
		
	}
	
	public void setExplosionArea(){
		int x,y ;
		byte cell ;
		boolean isRandomItem ;
		
		// draw the up
		int nextUp = yExplode - 1 ; // the next up.
		for(y = nextUp ; y >= upExplode ; y-- ){	
			if( board.isInBounds(xExplode,y)){
				if( board.isExplosionCell(xExplode, y)){
					break ;
				}			
				if( board.isWall(xExplode, y)){
					cell = board.getCell(xExplode,y);
					if( cell == BoomWorld.CELL_SOFTWALL ){
						isRandomItem = manager.randomDestroyWall(xExplode, y) ; 
						if( !isRandomItem ){
							board.setExplosionCell(xExplode,y) ;
						}else{
							board.setCell(xExplode, y,BoomWorld.CELL_GRASS) ;
						}
					
						manager.score += 50 ;		
						manager.numDestroySoftWall++ ;
					}					
					break ;
				}
				if( hasABomb(xExplode,y)){
					y--;
					break ;
				}

			}							
		}
		upExplode = y + 1;
		
		// draw the down
		 int nextDown = yExplode + 1 ;	
		 for( y = nextDown; y <= downExplode ; y++ ){
			 if( board.isInBounds(xExplode,y)){
				 if( board.isExplosionCell(xExplode, y)){
						break ;
					}				 
				 if( board.isWall(xExplode,y)) {
					 cell = board.getCell(xExplode,y);
						if( cell == BoomWorld.CELL_SOFTWALL ){
							isRandomItem = manager.randomDestroyWall(xExplode, y) ;
							if( !isRandomItem ){
								board.setExplosionCell(xExplode,y) ;
							}
							else{
								board.setCell(xExplode, y,BoomWorld.CELL_GRASS) ;
							}
							manager.score += 50 ;		
							manager.numDestroySoftWall++ ;
						}		
					 break ;
				 }
				 if( hasABomb(xExplode, y)){
					 y++;
					 break ;
				 }

			 }
			 
		}
		 downExplode = y - 1;
		 
		 //draw the left
		 int nextLeft = xExplode - 1 ;		 
		 for( x = nextLeft ; x >= leftExplode ; x-- ){
			 if( board.isInBounds(x, yExplode)){
				 if( board.isExplosionCell(x,yExplode)){
						break ;
					}
				 
				 if( board.isWall(x, yExplode)){
					 cell = board.getCell(x,yExplode);
						if( cell == BoomWorld.CELL_SOFTWALL ){
							isRandomItem = manager.randomDestroyWall(x, yExplode) ;
							if( !isRandomItem ){
								board.setExplosionCell(x, yExplode) ;
							}
							else{
								board.setCell(x,yExplode,BoomWorld.CELL_GRASS) ;
							}
							manager.score += 50 ;
							manager.numDestroySoftWall++ ;
						}		
					 break ;
				 }
				 
				 if( hasABomb(x, yExplode)){
					 x--;
					 break ;
				 }
			 }			
		 }
		 leftExplode = x + 1;
		 
		 //draw the right
		 int nextRight = xExplode + 1 ;
		 for( x = nextRight ; x <= rightExplode ; x++ ){
			 if( board.isInBounds(x,yExplode)){
				 if( board.isExplosionCell(x,yExplode)){
						break ;
					}
				 
				 if( board.isWall(x, yExplode)) {
					 cell = board.getCell(x,yExplode);
						if( cell == BoomWorld.CELL_SOFTWALL ){
							isRandomItem = manager.randomDestroyWall(x, yExplode) ;
							if( !isRandomItem ){
								board.setExplosionCell(x, yExplode) ;
							}
							else{
								board.setCell(x,yExplode,BoomWorld.CELL_GRASS) ;
							}
							manager.score += 50 ;
							manager.numDestroySoftWall++ ;
						}		
					 break ;
				 }		
				 
				 if( hasABomb(x, yExplode)){
					 x++;
					 break ;
				 }

			 }
			 
		 }
		 rightExplode = x - 1 ;
	}
	
	public int getXExplode() {
		return xExplode;
	}
	
	public int getYExplode() {
		return yExplode;
	}
	
	public int getLevel() {
		return owner.getLevelBombExplode();
	}
	
	public boolean isVisible() {
		return isVisible;
	}
	
	public boolean isExploding() {
		return isExploding;
	}
	
	public void enableExploding(){
		firstLifeTime = BOMB_LIFE_TIME ;
	//	System.out.println("enableExploding() v2") ;
		setExplosionArea() ;
	}
	
	public boolean isInExplosionCell(int xCell,int yCell){
		if( xCell == xExplode ){
			return yCell >= upExplode && yCell <= downExplode ;			
		}
		if( yCell == yExplode ){
			return xCell >= leftExplode && xCell <= rightExplode ; 
		}

		return false ;
	}
	
	public boolean isInBoomCell(int xCell,int yCell){
		return (xExplode == xCell && yExplode == yCell) ;
	}
	
	public static boolean hasABomb(int xCell,int yCell){
		return  bombCoord[yCell*board.getNumCols() + xCell] ;
	}
	
	public static void setABomb(int xCell,int yCell,boolean yesOrNo){
		bombCoord[yCell*board.getNumCols() + xCell] = yesOrNo ;
	}	
}
