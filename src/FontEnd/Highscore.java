/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package FontEnd;

import java.io.IOException;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import BoomGame.ImageStock;
import Original.ImageUtility;
import MainGame.TestMIDlet;

import Original.RMS_Score;

public class Highscore extends Canvas
					    implements Runnable
{
	private final static int SPACE_HEIGHT = 10 ;
	private int currOption ;

       
	private Softkey softKey ;
	private GameFont font ;

	private TestMIDlet midlet ;
  private int icurrabout;
	private int shouldStop ;
      
        private Image imgtest;
         private Image imgtest1;
          private Image imgtest2;
          private Image listboom;
          private Image sao1;
          private Image sao2;
	public Highscore(TestMIDlet mid) throws IOException{
		super() ;
		setFullScreenMode(true) ;
		midlet = mid ;
             
		softKey = Softkey.getInstance() ;
                font = GameFont.getInstance() ;
                imgtest=Image.createImage("/hs4.png");
               imgtest1=Image.createImage("/hs1.png");
                imgtest2=Image.createImage("/hs3.png");
               listboom=Image.createImage("/listboomber.png");
                sao1=Image.createImage("/sao2.png");
                sao2=Image.createImage("/sao3.png");
	}

	protected void showNotify() {
		Thread t =new Thread(this) ;
		shouldStop = -1 ;
		t.start() ;
                 // Media.getInstance().Startmedia();
	}

	protected void hideNotify() {
		shouldStop = 0 ;
                 // Media.getInstance().Stopmedia();
	}

	protected void keyPressed(int keyCode) {
		if( softKey.isLeftSoftkey(keyCode) ){
			System.out.println("menuCanvas left soft key");
                       shouldStop=0;
		}
	}

	protected void paint(Graphics g) {
		int widthCanvas = getWidth() ;
		int heightCanvas = getHeight() ;

		int xDrawing = (widthCanvas>>1)-(imgtest.getWidth()>>1) ;
		int yDrawing = 0 ;

		g.setColor(0) ;
		g.fillRect(0,0,widthCanvas,heightCanvas) ;
             
               

                g.drawImage(listboom,
					widthCanvas-200, heightCanvas-90,
					ImageUtility.GRAPHICS_TOP_LEFT) ;
                if(icurrabout%3==0)
                g.drawImage(imgtest1,
					xDrawing, 0,
					ImageUtility.GRAPHICS_TOP_LEFT) ;
                else if(icurrabout%3==1) g.drawImage(imgtest,
					xDrawing, 0,
					ImageUtility.GRAPHICS_TOP_LEFT) ;
                else  g.drawImage(imgtest2,
					xDrawing, 0,
					ImageUtility.GRAPHICS_TOP_LEFT) ;
		// draw the menu font
		
		yDrawing =  40 ;
             
                xDrawing=(widthCanvas>>1)-(imgtest.getWidth()>>1)+10;
		RMS_Score rmsscore=new RMS_Score();
                String name[]=rmsscore.getNames();
                int values[]=rmsscore.getValues();
                font.drawString(g,"Name",xDrawing,yDrawing) ;
                 font.drawString(g,"Score",xDrawing+50,yDrawing) ;
                 yDrawing += 20 ;
                if(icurrabout%2==0)
                g.drawImage(sao1,
					xDrawing-20, yDrawing,
					ImageUtility.GRAPHICS_TOP_LEFT) ;
                else  g.drawImage(sao2,
					xDrawing-20, yDrawing,
					ImageUtility.GRAPHICS_TOP_LEFT) ;
                for (int i=0;i<5;i++)
                {
                     font.drawString(g,name[i],xDrawing,yDrawing) ;
                     xDrawing+=50;
                     font.drawString(g,values[i]+"",xDrawing,yDrawing) ;
                     xDrawing-=50;
                      yDrawing += 10 ;
                }

		//xDrawing -= 20 ;
		//yDrawing  = 85 + (currOption*10) ;
		//g.drawImage(imgBomb[bombSequences[currFrame]],
		//			xDrawing,yDrawing,
		//			ImageUtility.GRAPHICS_TOP_LEFT) ;
		g.setColor(0) ;
		softKey.drawLeftSoftkey(g, "Exit", widthCanvas, heightCanvas) ;
		//softKey.drawRightSoftkey(g, "OK", widthCanvas, heightCanvas) ;
	}

	public void run() {
		while( true ){

			if( shouldStop!=-1){
				break ;
			}

			repaint() ;
  icurrabout++;

			//
			// System.out.println("Repaint") ;
			try {
				synchronized (this) {
					wait(200L) ;
				}
			}
			catch(InterruptedException iEX){
				iEX.printStackTrace() ;
			}

		}

                    midlet.showMenuScreen(false) ;

	}


}
