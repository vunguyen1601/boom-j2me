package FontEnd;

import java.io.IOException;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import MainGame.TestMIDlet;
import Original.ImageUtility;

public class SplashScreen extends Canvas
						  implements Runnable
{
	private Image imgtentruong;
	private Image imgtp;
	private Image imglogo;
	private int xpxRightMoving = 0 ;
	private int xtxLeftMoving=0;
	private boolean shouldStop ;
	
	private TestMIDlet midlet ;
	private boolean isCo=false;
        private Softkey softkey ;
        private GameFont font ;
	public SplashScreen(TestMIDlet mid) throws IOException {
		super() ;
		midlet = mid ;
		setFullScreenMode(true) ;
                softkey = Softkey.getInstance() ;
		font = GameFont.getInstance() ;
		imgtentruong=Image.createImage("/dhktcn.png") ;
		imgtp=Image.createImage("/tphcm.png") ;
		imglogo=Image.createImage("/logohutech.png") ;
		xpxRightMoving = -imgtentruong.getWidth() ;
		xtxLeftMoving=2*imgtentruong.getWidth();
	}
	
	protected void showNotify() {
		Thread t = new Thread(this) ;
		t.start() ;
	}
        protected void keyPressed(int keyCode) {
        System.out.println("menuCanvas right soft key"+keyCode);
               // int gameKey = getGameAction(keyCode) ;
        System.out.println("menuCanvas right soft key"+keyCode);

		if( softkey.isRightSoftkey(keyCode) ){
			midlet.setmussic(true);
                         Media.getInstance().Startmedia();
			isCo=true;
		}else if( softkey.isLeftSoftkey(keyCode) ){

			midlet.setmussic(false);
                        Media.getInstance().Stopmedia();
                        isCo=true;
		}


	}
	protected void paint(Graphics g) {
		int width = getWidth() ;
		int height = getHeight() ;
		g.setColor(0x000000) ;
		g.fillRect(0,0,width,height) ;

                if(isCo){
                        int xdieukien = (width >> 1) - (imgtentruong.getWidth() >> 1) ;
                        if(xpxRightMoving<xdieukien-10)
                            xpxRightMoving += 8 ; // move right 3px.
                        int xten = xpxRightMoving ;
                        int yten =(height>>1)-2*imgtentruong.getHeight() ;
                        g.drawImage(imgtentruong,xten,yten,ImageUtility.GRAPHICS_TOP_LEFT) ;
                          if(xtxLeftMoving>xdieukien-5)
                            xtxLeftMoving -= 10 ; // move right 3px.
                        xten=xtxLeftMoving;
                        yten=yten+imgtentruong.getHeight()-10;
                        g.drawImage(imgtp,xten,yten,ImageUtility.GRAPHICS_TOP_LEFT) ;
                        if(xtxLeftMoving<=xdieukien-5 && xpxRightMoving>=xdieukien-10){
                            g.drawImage(imglogo,(width >> 1) - (imglogo.getWidth() >> 1)-10,yten+imgtentruong.getHeight()-10,ImageUtility.GRAPHICS_TOP_LEFT) ;
                            shouldStop = true ;
                        }
                }else{
                   font.drawString(g,"Do you want to ",(width>>1)-(font.getFontStringWidth("Do you want to ")>>1),height >> 2) ;
                   font.drawString(g,"open the music menu",(width>>1)-(font.getFontStringWidth("open the music menu")>>1),(height >> 2)+20) ;
                   //  font.drawString(g,"jkjhkj",width >> 1 - font.getFontStringWidth("Ban co muon mo nhac nen menu") >> 1,height >> 1 - font.getFontHeight()) ;
                    softkey.drawLeftSoftkey(g, "No", width, height) ;
                    softkey.drawRightSoftkey(g, "Yes", width, height) ;
                }

		

	}

	public void run() {
		while( true ){
			
			if( shouldStop ){
				break ;
			}
			
			repaint() ;
			
			try {
				synchronized (this) {
					wait(100L) ;
				}
			}
			catch(InterruptedException iEX){
				iEX.printStackTrace() ;
			}
			
		}
		
		// wait 1s to change the screen.
		try {
			Thread.sleep(1000l) ;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		midlet.showMenuScreen(false) ;
	}	
}
