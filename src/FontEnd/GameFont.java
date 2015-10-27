package FontEnd;


import java.io.IOException;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import javax.microedition.lcdui.game.Sprite;

import BoomGame.ImageStock;
import Original.ImageUtility;

/**
 *
 * @author thanhlongNguyen
 */
public class GameFont
{ 
    private final static int FONT_WIDTH = 7 ;
    private final static int FONT_HEIGHT = 13 ;
    
    // index's imgFont array.
    public final static int NUMBER  = 0 ;
    public final static int ENGLISH_CHARACTER = 24 ;    
    
    private int spaceWidth ;
    private int spaceHeight ;
    
    private static GameFont instance ;
    private Image imgFont[] ;
    
    
    private GameFont(int spaceWidth,int spaceHeight) throws IOException{
    	imgFont = ImageUtility.extractFrames(ImageStock.getImgFont(),
    										 0, 0,
    										 50,1,
    										 FONT_WIDTH,FONT_HEIGHT, 
    										 false) ;
    	this.spaceWidth = spaceWidth ;
    	this.spaceHeight = spaceHeight ;
    }
    
    public final static GameFont getInstance() throws IOException{
    	if( instance == null ){
    		instance = new GameFont(1,3) ;
    	}
    	
    	return instance ;
    }
    
    public final void drawNumber(Graphics g,int x,int y,int charPosition) 
    {  	
        int character = charPosition ;
        Image font[] = imgFont ;		
        int numCharacter = font[0].getWidth() / FONT_WIDTH ; 
        
        while( character < numCharacter ){
        	g.drawImage(font[character],
        			    x, y,
        			    ImageUtility.GRAPHICS_TOP_LEFT) ;
      			  
      			  // next character			  
      			  character++ ;
      
      			  // the next position for the next character.
      			  x += FONT_WIDTH + spaceWidth; 
        }
        
    }
    
    public final void drawString(Graphics g,String str,int x,int y){
    	// save for the next line.
    	int beginX = x ;
    	int length = str.length() ;
    	char c ; 
    	for(int index = 0 ; index < length ; index++ ){
    		 c = Character.toLowerCase(str.charAt(index)) ;
    		 
    		 if( c == '/' ){ // processing the next line.
    			 
    			 if( (index + 1) == length){
    				 throw new ArrayIndexOutOfBoundsException("Error processing char '/n'") ;
    			 }
    			 
    			 char nextChar = str.charAt(index+1);
    			 if( nextChar == 'n'){
    				 
    				 // the next line 
    				 y += (FONT_HEIGHT >> 1) + spaceHeight ;
    				 x = beginX ;
    			 }   			 
    			 
    			 // not processing the next char.
    			 index++ ;
    		 }
    		 else if( c == ' '){  // processing white space.
    			 
    			 x += (FONT_WIDTH >> 1) + spaceWidth;
    			 
    		 }
    		 else { // processing the character.
    			 
    			 drawChar(g, c, x, y) ;
    			 
    			 // the next position for the next character. 
    			 x += FONT_WIDTH + spaceWidth;
    		 }
    	}
    }
    
    private final void drawChar(Graphics g,char c,int x,int y){
    	
    	int posInABC ;
    	char a = 'a' ;	
    	
		 if( Character.isDigit(c)){ // processing the digit.
			 
			 posInABC = Character.digit(c, Character.MAX_RADIX) ;
			   			 
			 drawFont(g,
					 NUMBER + posInABC,
					 x, y );
 	       		
		 }
		 else{ // processing the English character.
			 
		 posInABC = c - a ;
		 
  		 drawFont(g,
  				  ENGLISH_CHARACTER + posInABC,
  				  x, y );
  	       		
		 }		 		
    }
    
    private final void drawFont(Graphics g,int font,int x,int y){ 
    	g.drawImage(imgFont[font],
  			  		x, y,
  			  		ImageUtility.GRAPHICS_TOP_LEFT) ; 
    }
    
    public int getFontStringWidth(String str){
    	return str.length()*(spaceWidth+FONT_WIDTH) ;
    }
    
    public int getFontHeight(){
    	return FONT_HEIGHT ;
    }
    
}
