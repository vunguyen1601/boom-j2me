package Original;


import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;

public class ImageUtility {
	/**
	 *  Precalculate Graphics.TOP | Graphics.LEFT 
	 * */
	public final static int GRAPHICS_TOP_LEFT = Graphics.TOP | Graphics.LEFT ;
	
	public static Image getRegion(Image imgSource,
								  int x,int y,
								  int width,int height)
	{
		if( imgSource == null ){
			throw new NullPointerException() ;
		}
		return Image.createImage(imgSource, x, y, width, height,Sprite.TRANS_NONE) ;		
	}
	
	public static Image[] extractFrames(Image imgSource,
										int x,int y,
										int numFramesWidth,int numFramesHeight, 
										int frameWidth,int frameHeight,
										boolean createBlankFrame)
	{
		if( imgSource == null ){
			throw new NullPointerException() ;
		}
		
		Image imgs[] ;
		int currFrame = 0 ;
		if( createBlankFrame ){
			 imgs = new Image[(numFramesWidth*numFramesHeight)+1] ;
			 imgs[0] = Image.createImage(frameWidth,frameHeight) ;
			 currFrame = 1 ;
		}
		else{
			 imgs = new Image[numFramesWidth*numFramesHeight] ;
		}		
		
		for( int frameY = 0 ; frameY < numFramesHeight ; frameY++ ){
			for( int frameX = 0 ; frameX < numFramesWidth ; frameX++ ){
				imgs[currFrame] = getRegion(imgSource, 
											x+(frameX*frameWidth) , y+(frameY*frameHeight),
											frameWidth,frameHeight) ;
				
				currFrame++ ;
			}
		}
		
		return imgs ;
	}
	
	public final static Image[] extractTransformFrames(Image[] imgs,int frameWidth,int frameHeight,int transform){
		if( imgs == null ){
			throw new NullPointerException() ;
		}
		int length = imgs.length ;
		Image newImgs[] = new Image[length] ;
		for( int index = 0 ; index < length ;index++ ){
			newImgs[index] = Image.createImage(imgs[index],0,0,frameWidth,frameHeight,transform) ;
		}
		
		return newImgs ;
	}

}
