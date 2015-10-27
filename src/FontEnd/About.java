package FontEnd;

import java.io.IOException;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import BoomGame.ImageStock;
import Original.ImageUtility;
import MainGame.TestMIDlet;

public class About extends Canvas
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
       
	public About(TestMIDlet mid) throws IOException{
		super() ;
		setFullScreenMode(true) ;
		midlet = mid ;
		softKey = Softkey.getInstance() ;
                font = GameFont.getInstance() ;
                sabout=new String[8];
                sabout[0]="truong DH KTCN TPHCM";
               sabout[1]="Do an tot nghiep K2005";
               sabout[2]="chuyen nganh CNPM";
                sabout[3]="GVHD";
                sabout[4]="ThS Le Trung Hieu";
                sabout[5]="sinh vien";
                sabout[6]="Nguyen Thanh Long";
                sabout[7]="Nguyen Thach Vu";
                
                backgroup=Image.createImage("/menubrk.png");
            

	}

	protected void showNotify() {
		Thread t =new Thread(this) ;
		shouldStop = -1 ;
		t.start() ;
               // Media.getInstance().Startmedia();
	}

	protected void hideNotify() {
		shouldStop = 0 ;
               
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
		
                    midlet.showMenuScreen(false) ;
              
	}

	
}
