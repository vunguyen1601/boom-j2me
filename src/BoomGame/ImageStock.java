package BoomGame;

import java.io.IOException;

import javax.microedition.lcdui.Image;

import Original.ImageUtility;

public class ImageStock {
	private static Image imgEnemy ;
	private static Image imgsBlueEnemy[] ;
	private static Image imgsRedEnemy[] ;
	private static Image imgsGreenEnemy[] ;
	private static Image imgsBlackEnemy[] ;
	private static Image imgsExplosionBomb[] ;
	
	private static Image imgItem ;
	private static Image imgSpeedItem[] ;
	private static Image imgBombItem[];
	private static Image imgThrownItem[];
	private static Image imgKickItem[];
	private static Image imgLevelExploItem[];
	private static Image imgMaxExploItem[];
	private static Image imgReversedItem[];
	
	private static Image imgFont ;
	
	private static Image imgBoomMan[] ;
	private static Image imgRedBoomMan[] ;
	
	private static Image imgNumber ;
	
	private static Image imgBar;
	
	private static Image imgMenuBrk ;
	
	public final static Image getImgMenuBrk() throws IOException{
		if( imgMenuBrk == null ){
			imgMenuBrk = Image.createImage("/menubrk.png") ;
		}
		
		return imgMenuBrk ;
	}
	public final static Image getImgNumber() throws IOException{
		if( imgNumber == null ){
			imgNumber = Image.createImage("/number.png") ;
		}
		
		return imgNumber ;
	}
	
	public final static Image getImgBar() throws IOException{
		if( imgBar == null ){
			imgBar = Image.createImage("/bar.png") ;
		}
		
		return imgBar ;
	}
	
	public final static Image[] getImgBoomMan(int frameWidth,int frameHeight) throws IOException{
		if( imgBoomMan == null ){
			imgBoomMan = ImageUtility.extractFrames(Image.createImage("/bomberman.png"), 
										   		    0,0,
										   		    24,1,
										   		    frameWidth,frameHeight ,
										   		    false) ;
		}
		
		return imgBoomMan ;
	}
	
	public final static Image[] getImgRedBoomMan(int frameWidth,int frameHeight) throws IOException{
		if( imgRedBoomMan == null ){
			imgRedBoomMan = ImageUtility.extractFrames( Image.createImage("/bombermanRed.png"),
													    0,0,
										   		    	24,1,
										   		    	frameWidth,frameHeight ,
										   		    	false) ;
		}

		return imgRedBoomMan ;
	}
	
	public final static Image getImgNormalRedFace() throws IOException{
		if( imgRedBoomMan == null ){
			imgRedBoomMan = ImageUtility.extractFrames(Image.createImage("/bombermanRed.png"), 
													0,0,
													23, 1,
													18,28 ,
													false) ;
		}
		
		return imgRedBoomMan[0] ;
	}
	
	public final static Image getImgNormalFace() throws IOException{
		if( imgBoomMan == null ){
			imgBoomMan = ImageUtility.extractFrames(Image.createImage("/bomberman.png"), 
													0,0,
													23, 1,
													18,28 ,
													false) ;
		}
		
		return imgBoomMan[0] ;
	}
	
	public final static Image getImgSadFace() throws IOException{
		if( imgBoomMan == null ){
			imgBoomMan = ImageUtility.extractFrames(Image.createImage("/bomberman.png"), 
													0,0,
													23, 1,
													18,28 ,
													false) ;
		}
		
		return imgBoomMan[1] ;
	}
	
	public final static Image getImgItem() throws IOException{
		if( imgItem == null ){
			imgItem = Image.createImage("/item.png") ;
		}
		
		return imgItem ;
	}
	
	public final static Image[] getImgBombItem(int frameWidth,int frameHeight) throws IOException{
		if( imgBombItem == null ){
			imgBombItem = ImageUtility.extractFrames(getImgItem(),
													  0,0,
													  1,2,
													  frameWidth, frameHeight,
													  false) ;
		}
		
		return imgBombItem ;
	}
	
	public final static Image[] getImgSpeedItem(int frameWidth,int frameHeight) throws IOException{
		if( imgSpeedItem == null ){
			imgSpeedItem = ImageUtility.extractFrames(getImgItem(),
													  frameWidth,0,
													  1,2,
													  frameWidth, frameHeight,
													  false) ;
		}
		
		return imgSpeedItem ;
	}
	
	public final static Image[] getImgKickItem(int frameWidth,int frameHeight) throws IOException{
		if( imgKickItem == null ){
			imgKickItem = ImageUtility.extractFrames(getImgItem(),
													  2*frameWidth,0,
													  1,2,
													  frameWidth, frameHeight,
													  false) ;
		}
		
		return imgKickItem ;
	}
	
	public final static Image[] getImgThrownItem(int frameWidth,int frameHeight) throws IOException{
		if( imgThrownItem == null ){
			imgThrownItem = ImageUtility.extractFrames(getImgItem(),
													  frameWidth*3,0,
													  1,2,
													  frameWidth, frameHeight,
													  false) ;
		}
		
		return imgThrownItem ;
	}
	
	public final static Image[] getImgLevelExploItem(int frameWidth,int frameHeight) throws IOException{
		if( imgLevelExploItem == null ){
			imgLevelExploItem = ImageUtility.extractFrames(getImgItem(),
													  frameWidth*4,0,
													  1,2,
													  frameWidth, frameHeight,
													  false) ;
		}
		
		return imgLevelExploItem ;
	}
	
	public final static Image[] getImgReversedItem(int frameWidth,int frameHeight) throws IOException{
		if( imgReversedItem == null ){
			imgReversedItem = ImageUtility.extractFrames(getImgItem(),
													  frameWidth*5,0,
													  1,2,
													  frameWidth, frameHeight,
													  false) ;
		}
		
		return imgReversedItem ;
	}
	
	public final static Image[] getImgMaxExploItem(int frameWidth,int frameHeight) throws IOException{
		if( imgMaxExploItem == null ){
			imgMaxExploItem = ImageUtility.extractFrames(getImgItem(),
													  frameWidth*6,0,
													  1,2,
													  frameWidth, frameHeight,
													  false) ;
		}
		
		return imgMaxExploItem ;
	}
	
	public final static Image getImgFont() throws IOException{
		if( imgFont == null ){
			imgFont = Image.createImage("/font.PNG");
		}
		
		return imgFont ;
	}
	
	public final static Image getImgEnemy() throws IOException {
		if( imgEnemy == null ){
			imgEnemy = Image.createImage("/enemy.png") ;
		}
		return imgEnemy;
	}
	
	public final static Image[] getImgsExplosionBomb(int frmWidth,int frmHeight) throws IOException{
		if( imgsExplosionBomb == null ){
			imgsExplosionBomb = ImageUtility.extractFrames(Image.createImage("/explosionEX.png"),
					 										0,0,
					 										4, 4,
					 										frmWidth,frmHeight,
					 										false) ;
		}
		  return imgsExplosionBomb ;
	}
	
	public static Image[] getImgsBlackEnemy(int frmWidth,int frmHeight) throws IOException {
		if( imgsBlackEnemy == null){
			imgsBlackEnemy = ImageUtility.extractFrames(getImgEnemy() ,
														5*frmWidth,2*frmHeight,
														5, 2,
					    								frmWidth,frmHeight,
														false) ;
		}
		return imgsBlackEnemy;
	}
	
	public static Image[] getImgsBlueEnemy(int frmWidth,int frmHeight) throws IOException {
		if( imgsBlueEnemy == null ){
			imgsBlueEnemy = ImageUtility.extractFrames(getImgEnemy(),
													   0, 2*frmHeight,
													   5, 2,
					    							   frmWidth,frmHeight,
													   false) ;
		}
		return imgsBlueEnemy;
	}
	
	public static Image[] getImgsGreenEnemy(int frmWidth,int frmHeight) throws IOException {
		if( imgsGreenEnemy == null ){
			imgsGreenEnemy = ImageUtility.extractFrames(getImgEnemy(),
													    5*frmWidth, 0,
													    5, 2,
					    							    frmWidth,frmHeight,
													    false) ;
		}
		return imgsGreenEnemy;
	} 
	
	public static Image[] getImgsRedEnemy(int frmWidth,int frmHeight) throws IOException {
		if( imgsRedEnemy == null ){
			imgsRedEnemy = ImageUtility.extractFrames(getImgEnemy(),
				    								  0,0,
				    								  5, 2,
				    								  frmWidth,frmHeight,
				    								  false) ;
		}
		return imgsRedEnemy;
	}
}
