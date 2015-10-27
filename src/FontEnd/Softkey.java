package FontEnd;

import java.io.IOException;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public class Softkey {
	
	public final static int SPACE_WIDTH = 5;
	public final static int SPACE_HEIGHT = 5;
	
	private int RIGHT_SOFTKEY1 ;
	private int RIGHT_SOFTKEY2 ;
	private int LEFT_SOFTKEY1 ;
	private int LEFT_SOFTKEY2 ; 
	
	public static Softkey instance ;
	
	private GameFont font;
	
	private Softkey() throws IOException{
		// assign the softkey for every platform.
		font = GameFont.getInstance() ;
		// WTK,Nokia,SamSung,SonyEricson 
		RIGHT_SOFTKEY1 = -7 ;
		RIGHT_SOFTKEY2 = -7 ;
		
		LEFT_SOFTKEY1 = -6 ;
		LEFT_SOFTKEY2 = -6 ;
	           //set Motorola
	           try {
	        	   
	        	   // NOTE: This APIs is supported in the true device,but the emulator is not.  
	               Class.forName("com.motorola.phonebook.PhoneBookRecord");
	               // Set Motorola specific keycodes
	               	RIGHT_SOFTKEY1 = 22;            //Different generations of Motorolas have different codes
	                RIGHT_SOFTKEY2 = -22;
	                
	                LEFT_SOFTKEY1 = 21;
	                LEFT_SOFTKEY2 = -21;
	           } catch (ClassNotFoundException ignore2) {
	        	   // set Siemens
	               try {
	                  Class.forName("com.siemens.mp.lcdui.Image");
	                 // Set Siemens specific keycodes
	                  RIGHT_SOFTKEY1 = -4;          
	                  RIGHT_SOFTKEY2 = -4;
	                  
	                  LEFT_SOFTKEY1 = -1 ;
	              } catch (ClassNotFoundException ignore3) {
	                 	         
	            	  // do nothing. 
	            	  
	              } // catch
	              
	           	           
	           }// catch
	  
	}
	
	public final static Softkey getInstance() throws IOException{
		if( instance == null ){
			instance = new Softkey() ;			
		}
		
		return instance ;
	}
	
	public final boolean isRightSoftkey(int keyCode){
		return keyCode == RIGHT_SOFTKEY1 || keyCode == RIGHT_SOFTKEY2 ;
	}
	
	public final boolean isLeftSoftkey(int keyCode){
		return keyCode == LEFT_SOFTKEY1 || keyCode == LEFT_SOFTKEY2 ;
	}
	
	public final void drawLeftSoftkey(Graphics g,String str,int widthScreen,int heightScreen){
		int xLeftDrawing = 0 + SPACE_WIDTH; // align left
		int yLeftDrawing = heightScreen - font.getFontHeight() - SPACE_HEIGHT ; 
		font.drawString(g, str, xLeftDrawing, yLeftDrawing) ;
	}
	
	public final void drawRightSoftkey(Graphics g,String str,int widthScreen,int heightScreen){
		int xRightDrawing = widthScreen  - font.getFontStringWidth(str)  - SPACE_WIDTH ; // align right.
		int yRightDrawing = heightScreen - font.getFontHeight() - SPACE_HEIGHT ; 
		font.drawString(g, str, xRightDrawing, yRightDrawing) ;
	}	
	
	public final void drawCenterSoftkey(Graphics g,String str,int widthScreen,int heightScreen){
		int xCenterDrawing = (widthScreen >> 1) - (font.getFontStringWidth(str) >> 1) ; // not align
		int yCenterDrawing = heightScreen - font.getFontHeight() - SPACE_HEIGHT ; 
		font.drawString(g, str, xCenterDrawing, yCenterDrawing) ;
	}
}
