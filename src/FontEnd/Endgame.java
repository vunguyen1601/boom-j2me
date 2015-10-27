package FontEnd;

import java.io.IOException;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import BoomGame.ImageStock;
import Original.ImageUtility;
import MainGame.TestMIDlet;

public class Endgame extends Canvas
					    implements Runnable
{
	private final static int SPACE_HEIGHT = 10 ;
	private int currOption ;





	private Softkey softKey ;
	private GameFont font ;

	private TestMIDlet midlet ;

	private int shouldStop ;
        private String sabout[];
        private int icurrabout;
        private Image backgroup;

	public Endgame(TestMIDlet mid) throws IOException{
		super() ;

		setFullScreenMode(true) ;
		midlet = mid ;
		softKey = Softkey.getInstance() ;
                font = GameFont.getInstance() ;
                sabout=new String[10];
                sabout[0]="Vi day la ban demo";
                sabout[1]="Va dang duoc kiem tra";
                sabout[2]="cho nen chi co 4 man choi";
                sabout[3]="chung toi se co gang";
                sabout[4]="hoan thanh ban day du";
                sabout[5]="Va cap nhat len website";
                sabout[6]="de ban co the soan tin";
                sabout[7]="ha tg g21 gui 8017";
                sabout[8]="de download ban moi nhat";
                sabout[9]="chan thanh Cam on";
                backgroup=Image.createImage("/menubrk.png");


	}

	protected void showNotify() {
		Thread t =new Thread(this) ;
		shouldStop = -1 ;
		t.start() ;
                //  Media.getInstance().Startmedia();
	}

	protected void hideNotify() {
		shouldStop = 0 ;
                  //Media.getInstance().Stopmedia();
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

		int xDrawing = ( widthCanvas >> 1 ) - ( backgroup.getWidth() >> 1 ) ;
                //System.out.print("abc:"+widthCanvas );
                 //System.out.print("\n");
                 //System.out.print("abc:"+imgBrk.getWidth() );
                //xDrawing=0;
		int yDrawing = 0 ;

		g.setColor(0) ;
		g.fillRect(0,0,widthCanvas,heightCanvas) ;


		g.drawImage(backgroup,
					xDrawing, yDrawing,
					ImageUtility.GRAPHICS_TOP_LEFT) ;
               g.fillRect(0,backgroup.getHeight()>>1,widthCanvas,heightCanvas) ;
		
                 int length = sabout.length ;
                yDrawing=heightCanvas-20-icurrabout;
                 xDrawing=( widthCanvas  ) -font.getFontStringWidth(sabout[0])>>1;
                if(yDrawing<90)
                    yDrawing=90;
                for( int index = 0 ; index < length ; index++){

                            if(yDrawing<heightCanvas-20)
                            font.drawString(g,sabout[index],xDrawing,yDrawing) ;
                            yDrawing += 10 ;
                 }
                //xDrawing= ( widthCanvas >> 1 )-font.getFontStringWidth(sabout[0])>>1;

                //font.drawString(g, sabout,20 , font.getFontHeight()+50);
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
					wait(100L) ;
				}
			}
			catch(InterruptedException iEX){
				iEX.printStackTrace() ;
			}

		}

                    midlet.showsavegame() ;

	}


}
