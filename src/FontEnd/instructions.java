/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package FontEnd;

import java.io.IOException;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import Original.ImageUtility;
import MainGame.TestMIDlet;
import BoomGame.ImageStock;

public class instructions extends Canvas    implements Runnable
{
	private final static int SPACE_HEIGHT = 10 ;
	private Softkey softKey ;
	private GameFont font ;

	private TestMIDlet midlet ;

	private int shouldStop ;
        private String sabout[];
          private Image listboom;
          private Image imginstruction;

          private int ico=0;
         private Image []imageitemChan;
         private Image []imageitemLe;
         private int iCurr=0;
	public instructions(TestMIDlet mid) throws IOException{
		super() ;
		setFullScreenMode(true) ;
		midlet = mid ;
              	softKey = Softkey.getInstance() ;
                font = GameFont.getInstance() ;
                sabout=new String[9];
                sabout[0]="Boomberman";
                sabout[1]="ten trum";
                sabout[2]="Them mot trai bom";
                sabout[3]="Tang toc do di chuyen";
                sabout[4]="Kha nang da bom";
                sabout[5]="Tang them mot mang";
                sabout[6]="cong pha bom len mot";
                sabout[7]="roi vao hon loan";
                sabout[8]="cong pha bom lon nhat";
               
              imginstruction=Image.createImage("/ins.png");
              listboom=Image.createImage("/listboomber.png");
              imageitemChan=new Image[9];
              imageitemLe=new Image[9];
                Image []tam;
                tam=ImageStock.getImgBoomMan(18, 28);
                imageitemChan[0]=tam [4];
                imageitemLe[0]=tam[5];
                tam=ImageStock.getImgRedBoomMan(18, 28);
                imageitemChan[1]=tam [3];
                imageitemLe[1]=tam[4];
                tam=ImageStock.getImgBombItem(16, 16);
                imageitemChan[2]=tam [0];
                imageitemLe[2]=tam[1];
                tam=ImageStock.getImgSpeedItem(16, 16);
                imageitemChan[3]=tam[0];
                imageitemLe[3]=tam[1];
                tam=ImageStock.getImgKickItem(16, 16);
                imageitemChan[4]=tam[0];
                imageitemLe[4]=tam[1];
                tam=ImageStock.getImgThrownItem(16, 16);
                imageitemChan[5]=tam[0];
                imageitemLe[5]=tam[1];
                 tam=ImageStock.getImgLevelExploItem(16, 16);
                imageitemChan[6]=tam[0];
                imageitemLe[6]=tam[1];
                 tam=ImageStock.getImgReversedItem(16, 16);
                imageitemChan[7]=tam[0];
                imageitemLe[7]=tam[1];
                 tam=ImageStock.getImgMaxExploItem(16, 16);
                imageitemChan[8]=tam[0];
                imageitemLe[8]=tam[1];
                //imageitemChan[7]=ImageStock.getImgNormalFace();
                //imageitemLe[7]=ImageStock.getImgSadFace();

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
			if(ico==1){
                            ico=0;
                        }else{
                             shouldStop=0;
                        }
                      
		}else if(softKey.isRightSoftkey(keyCode)){
                    ico=1;
                }
	}

	protected void paint(Graphics g) {
		int widthCanvas = getWidth() ;
		int heightCanvas = getHeight() ;

		int xDrawing = ( widthCanvas >> 1 ) - ( imginstruction.getWidth() >> 1 ) ;
		int yDrawing = 0 ;

		g.setColor(0) ;
		g.fillRect(0,0,widthCanvas,heightCanvas) ;
                g.drawImage(imginstruction,
					xDrawing, yDrawing,
					ImageUtility.GRAPHICS_TOP_LEFT) ;
 
                int length = sabout.length ;
                yDrawing=yDrawing+5+imginstruction.getHeight();
                xDrawing=( widthCanvas  ) -font.getFontStringWidth(sabout[0])>>1;
//                   g.drawImage(imageitemChan[0],
//					xDrawing-20, yDrawing,
//					ImageUtility.GRAPHICS_TOP_LEFT) ;
                int index=0;
                if(ico==1){
                    index=4;
                }else{
                    length=4;
                }
                for( ; index < length ; index++){

                            if(index<imageitemChan.length){
                                if(iCurr%2==0 ){
                                g.drawImage(imageitemChan[index],
					xDrawing-40, yDrawing,
					ImageUtility.GRAPHICS_TOP_LEFT) ;
                                }else{
                                    g.drawImage(imageitemLe[index],
					xDrawing-40, yDrawing,
					ImageUtility.GRAPHICS_TOP_LEFT) ;
                                }
                            }
                            font.drawString(g,sabout[index],xDrawing-21,yDrawing) ;
                            if(index==0 || index==1){
                             yDrawing += 28 ;
                            }else{
                                 yDrawing += 20 ;
                            }
                           
                 }
  
                g.drawImage(listboom,
					widthCanvas-200, heightCanvas-90,
					ImageUtility.GRAPHICS_TOP_LEFT) ;

		g.setColor(0) ;
                if(ico==0){
		softKey.drawLeftSoftkey(g, "Exit", widthCanvas, heightCanvas) ;
                softKey.drawRightSoftkey(g, "Next", widthCanvas, heightCanvas) ;
                }else{
                    softKey.drawLeftSoftkey(g, "Back", widthCanvas, heightCanvas) ;
                }
        }

	
	public void run() {
		while( true ){

			if( shouldStop!=-1){
				break ;
			}
                        iCurr++;
			repaint() ;
                     	//
			// System.out.println("Repaint") ;
			try {
				synchronized (this) {
					wait(500L) ;
				}
			}
			catch(InterruptedException iEX){
				iEX.printStackTrace() ;
			}

		}

                    midlet.showMenuScreen(false) ;

	}


}
