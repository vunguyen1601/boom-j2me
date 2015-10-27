package FontEnd;

import Bluetooth.BluetoothClient;
import Bluetooth.BluetoothServer;
import java.io.IOException;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import BoomGame.ImageStock;
import Original.ImageUtility;
import  MainGame.TestMIDlet;
import Bluetooth.BluetoothClient;
import Bluetooth.BluetoothServer;
import Bluetooth.DataTransmiting;
public class MapCavas extends Canvas
					    implements Runnable
{
	private int currOption ;
	private Image imgbando ;
    private Image imgfocus[];//mau vi tri dang dung
    private Image imgGo;//mau xanh da di wa
	private Softkey softKey ;
	private GameFont font ;

	private TestMIDlet midlet ;

	private boolean  shouldStop=false ;
        private int currlevel;
        private int  ilevel=1;
        private int currframefocus=0;
        private String MissionName[]={"village","ghost house","cold plane","old forest","oCean","the fires of hell","forest crime","Boss"};

    // Bluetooth Mode.
    private boolean isBluetoothMode ;
    private BluetoothServer server ;
    private BluetoothClient client ;
    private int so=1;
    private boolean ikiem=false;
	public MapCavas(TestMIDlet mid) throws IOException{
		super() ;
		init(mid) ;
	}

	public MapCavas(TestMIDlet mid,BluetoothServer s,BluetoothClient c) throws IOException{
		super() ;
		init(mid) ;

		server = s ;
		client = c ;
		isBluetoothMode = true ;
	}

	public void init(TestMIDlet mid) throws IOException{
		setFullScreenMode(true) ;
		midlet = mid ;
		softKey = Softkey.getInstance() ;
		imgbando = Image.createImage("/bando.png") ;
                imgGo=Image.createImage("/dadi.png");
		font = GameFont.getInstance() ;
                ilevel=mid.getCurrLevel();
                currlevel= ilevel;
               // System.out.println("map canvas"+ilevel) ;
                imgfocus=new Image[2];
                try {
                    imgfocus[0] = Image.createImage("/focus1.png");
                    imgfocus[1] = Image.createImage("/focus2.png");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
	}


  
	protected void showNotify() {
		Thread t =new Thread(this) ;
		shouldStop =false ;
		t.start() ;
               
	}

	protected void hideNotify() {
		shouldStop = true ;
                
	}

	protected void keyPressed(int keyCode) {
		 // int gameKey = getGameAction(keyCode) ;
                if( !isBluetoothMode ){
			processKeyCode(keyCode) ;
		}
		else{
			bluetoothServerProcessing(keyCode) ;
		}
                if( softKey.isLeftSoftkey(keyCode)){
	            shouldStop=true;
	            if( server != null ){
	            	server.sendByte((byte)DataTransmiting.MESSAGE_EXIT_GAME) ;
	            }
	            midlet.showMenuScreen(false);
                }	
	}
	// The server's duty is send the key state for the client.
	private void bluetoothServerProcessing(int keyCode){
		// process in bluetooth mode
		if( server != null ){ // the server will send the key state to client.
							  // and the client's work is only receive the send information.

			if( softKey.isRightSoftkey(keyCode)){
	            shouldStop=true;
	            //  midlet.setCurrLevel(currlevel);+

	            server.sendByte(DataTransmiting.SOFTKEY_RIGHT) ;
	            midlet.playGameAgain( currlevel , isBluetoothMode );

			}else if(keyCode == getKeyCode(UP))
			{
				if( currlevel < ilevel ){
					currlevel++;

					server.sendByte((byte)currlevel) ;
				}
			}
			else if(keyCode == getKeyCode(DOWN))
			{
				if( currlevel > 1 ){
					currlevel--;

					server.sendByte((byte)currlevel) ;
				}
			} else if(keyCode == getKeyCode(FIRE))
			{
	           shouldStop=true;
	          //  midlet.setCurrLevel(currlevel);         
	           server.sendByte(DataTransmiting.KEY_FIRE) ;
               midlet.playGameAgain( currlevel , isBluetoothMode );
			}
		}
	}

	// The client's duty is receive the key state from the server.
	private void bluetoothClientProcessing(){

		if( client != null ){
			byte keyReceive = client.receiveByte() ;

			if( keyReceive == DataTransmiting.SOFTKEY_RIGHT ||
				keyReceive == DataTransmiting.KEY_FIRE	){
 
				shouldStop=true;
	            //  midlet.setCurrLevel(currlevel);+
	            midlet.playGameAgain( currlevel ,isBluetoothMode );

			}
			else if( keyReceive == DataTransmiting.MESSAGE_EXIT_GAME ){
				midlet.setAlert("The device disconnected") ;
			}
			else {
				currlevel = keyReceive ;
			}
			

		}
	}

	public void processKeyCode(int keyCode){
		if( softKey.isRightSoftkey(keyCode)){
                   // System.out.println("save r "+currlevel) ;
                       // shouldStop=true;
                        ikiem=true;
                
		}else if(keyCode == getKeyCode(UP))
                {

                    if(currlevel<ilevel){
                        currlevel++;
                    }
                }
                else if(keyCode == getKeyCode(DOWN))
                {

                    if(currlevel>1){
                        currlevel--;
                    }
                } else if(keyCode == getKeyCode(FIRE))
                {
                        ikiem=true;
//                       shouldStop=true;
//                      //  midlet.setCurrLevel(currlevel);
//                        midlet.playGameAgain(currlevel,isBluetoothMode);
                }

	}
        private void paintloading(Graphics g){

          
            Image[] fontt = null;
                try {
                    fontt = ImageUtility.extractFrames(ImageStock.getImgFont(), 0, 0, 50, 1, 7, 13, false);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                int widthCanvas = getWidth() ;
		int heightCanvas = getHeight() ;
                g.setColor(0x000000) ;
		g.fillRect(0,heightCanvas-20,widthCanvas,heightCanvas) ;
		int xDrawing = ( widthCanvas >> 1 ) - ( font.getFontStringWidth("State 1") >> 1 ) ;
                int yDrawing =0 ;
		font.drawString(g, "STATe "+currlevel,xDrawing,yDrawing) ;
                xDrawing = ( widthCanvas >> 1 ) - ( font.getFontStringWidth(MissionName[currlevel-1]) >> 1 ) ;
                font.drawString(g, MissionName[currlevel-1],xDrawing,yDrawing+15) ;
                font.drawString(g, "Loading",20,heightCanvas-40) ;
                
            
                 xDrawing=font.getFontStringWidth("Loading")+20;
                for(int i=0;i<5;i++){
                  g.drawImage(fontt[10],
					 xDrawing,heightCanvas-40,
					ImageUtility.GRAPHICS_TOP_LEFT) ;
                    xDrawing+=10;
                }

                font.drawString(g, " Please waiting", font.getFontStringWidth("Loading ")-10,heightCanvas-20) ;
                     shouldStop=true;
                      //midlet.setCurrLevel(currlevel);
                     midlet.playGameAgain(currlevel,isBluetoothMode);
              

        }
	protected void paint(Graphics g) {
                if(ikiem){
                    paintloading( g);
                }else{
                int xXDrawing,yXDrawing;//toa do ve nut xanh
		int widthCanvas = getWidth() ;
		int heightCanvas = getHeight() ;

		int xDrawing = ( widthCanvas >> 1 ) - ( imgbando.getWidth() >> 1 ) ;
                int yDrawing =( heightCanvas >> 1 ) - ( imgbando.getHeight() >> 1 ) ;

		g.setColor(0) ;
		g.fillRect(0,0,widthCanvas,heightCanvas) ;
		g.drawImage(imgbando,
					xDrawing, yDrawing,
					ImageUtility.GRAPHICS_TOP_LEFT) ;
                //--------------------
                switch (ilevel) {

                    case 1:
			xXDrawing=xDrawing+95;
                        yXDrawing=yDrawing+183;
                        g.drawImage(imgGo,
					xXDrawing,yXDrawing,
					ImageUtility.GRAPHICS_TOP_LEFT) ;
                      	break;
                    case 2:
                        xXDrawing=xDrawing+95;
                        yXDrawing=yDrawing+183;
                        g.drawImage(imgGo,
					xXDrawing,yXDrawing,
					ImageUtility.GRAPHICS_TOP_LEFT) ;
                        xXDrawing=xDrawing+89;
                        yXDrawing=yDrawing+171;
                        g.drawImage(imgGo,
					xXDrawing,yXDrawing,
					ImageUtility.GRAPHICS_TOP_LEFT) ;
			break;
                    case 3:
                        xXDrawing=xDrawing+95;
                        yXDrawing=yDrawing+183;
                        g.drawImage(imgGo,
					xXDrawing,yXDrawing,
					ImageUtility.GRAPHICS_TOP_LEFT) ;
                        xXDrawing=xDrawing+89;
                        yXDrawing=yDrawing+171;
                        g.drawImage(imgGo,
					xXDrawing,yXDrawing,
					ImageUtility.GRAPHICS_TOP_LEFT) ;
                        xXDrawing=xDrawing+110;
                        yXDrawing=yDrawing+160;
                        g.drawImage(imgGo,
					xXDrawing,yXDrawing,
					ImageUtility.GRAPHICS_TOP_LEFT) ;
			break;
                    case 4:
                        xXDrawing=xDrawing+95;
                        yXDrawing=yDrawing+183;
                        g.drawImage(imgGo,
					xXDrawing,yXDrawing,
					ImageUtility.GRAPHICS_TOP_LEFT) ;
                        xXDrawing=xDrawing+89;
                        yXDrawing=yDrawing+171;
                        g.drawImage(imgGo,
					xXDrawing,yXDrawing,
					ImageUtility.GRAPHICS_TOP_LEFT) ;
                        xXDrawing=xDrawing+110;
                        yXDrawing=yDrawing+160;
                        g.drawImage(imgGo,
					xXDrawing,yXDrawing,
					ImageUtility.GRAPHICS_TOP_LEFT) ;
                        xXDrawing=xDrawing+82;
                        yXDrawing=yDrawing+137;
                        g.drawImage(imgGo,
					xXDrawing,yXDrawing,
					ImageUtility.GRAPHICS_TOP_LEFT) ;
			break;
                    case 5:
                        xXDrawing=xDrawing+95;
                        yXDrawing=yDrawing+183;
                        g.drawImage(imgGo,
					xXDrawing,yXDrawing,
					ImageUtility.GRAPHICS_TOP_LEFT) ;
                        xXDrawing=xDrawing+89;
                        yXDrawing=yDrawing+171;
                        g.drawImage(imgGo,
					xXDrawing,yXDrawing,
					ImageUtility.GRAPHICS_TOP_LEFT) ;
                        xXDrawing=xDrawing+110;
                        yXDrawing=yDrawing+160;
                        g.drawImage(imgGo,
					xXDrawing,yXDrawing,
					ImageUtility.GRAPHICS_TOP_LEFT) ;
                        xXDrawing=xDrawing+82;
                        yXDrawing=yDrawing+137;
                        g.drawImage(imgGo,
					xXDrawing,yXDrawing,
					ImageUtility.GRAPHICS_TOP_LEFT) ;
                        xXDrawing=xDrawing+124;
                        yXDrawing=yDrawing+130;
                        g.drawImage(imgGo,
					xXDrawing,yXDrawing,
					ImageUtility.GRAPHICS_TOP_LEFT) ;
			break;
                    case 6:
                        xXDrawing=xDrawing+95;
                        yXDrawing=yDrawing+183;
                        g.drawImage(imgGo,
					xXDrawing,yXDrawing,
					ImageUtility.GRAPHICS_TOP_LEFT) ;
                        xXDrawing=xDrawing+89;
                        yXDrawing=yDrawing+171;
                        g.drawImage(imgGo,
					xXDrawing,yXDrawing,
					ImageUtility.GRAPHICS_TOP_LEFT) ;
                        xXDrawing=xDrawing+110;
                        yXDrawing=yDrawing+160;
                        g.drawImage(imgGo,
					xXDrawing,yXDrawing,
					ImageUtility.GRAPHICS_TOP_LEFT) ;
                        xXDrawing=xDrawing+82;
                        yXDrawing=yDrawing+137;
                        g.drawImage(imgGo,
					xXDrawing,yXDrawing,
					ImageUtility.GRAPHICS_TOP_LEFT) ;
                        xXDrawing=xDrawing+124;
                        yXDrawing=yDrawing+130;
                        g.drawImage(imgGo,
					xXDrawing,yXDrawing,
					ImageUtility.GRAPHICS_TOP_LEFT) ;
                        xXDrawing=xDrawing+102;
                        yXDrawing=yDrawing+118;
                        g.drawImage(imgGo,
					xXDrawing,yXDrawing,
					ImageUtility.GRAPHICS_TOP_LEFT) ;
			break;
                    case 7:
                        xXDrawing=xDrawing+95;
                        yXDrawing=yDrawing+183;
                        g.drawImage(imgGo,
					xXDrawing,yXDrawing,
					ImageUtility.GRAPHICS_TOP_LEFT) ;
                        xXDrawing=xDrawing+89;
                        yXDrawing=yDrawing+171;
                        g.drawImage(imgGo,
					xXDrawing,yXDrawing,
					ImageUtility.GRAPHICS_TOP_LEFT) ;
                        xXDrawing=xDrawing+110;
                        yXDrawing=yDrawing+160;
                        g.drawImage(imgGo,
					xXDrawing,yXDrawing,
					ImageUtility.GRAPHICS_TOP_LEFT) ;
                        xXDrawing=xDrawing+82;
                        yXDrawing=yDrawing+137;
                        g.drawImage(imgGo,
					xXDrawing,yXDrawing,
					ImageUtility.GRAPHICS_TOP_LEFT) ;
                        xXDrawing=xDrawing+124;
                        yXDrawing=yDrawing+130;
                        g.drawImage(imgGo,
					xXDrawing,yXDrawing,
					ImageUtility.GRAPHICS_TOP_LEFT) ;
                        xXDrawing=xDrawing+102;
                        yXDrawing=yDrawing+118;
                        g.drawImage(imgGo,
					xXDrawing,yXDrawing,
					ImageUtility.GRAPHICS_TOP_LEFT) ;
                        xXDrawing=xDrawing+135;
                        yXDrawing=yDrawing+99;
                        g.drawImage(imgGo,
					xXDrawing,yXDrawing,
					ImageUtility.GRAPHICS_TOP_LEFT) ;
			break;
                    case 8:
                       xXDrawing=xDrawing+95;
                        yXDrawing=yDrawing+183;
                        g.drawImage(imgGo,
					xXDrawing,yXDrawing,
					ImageUtility.GRAPHICS_TOP_LEFT) ;
                        xXDrawing=xDrawing+89;
                        yXDrawing=yDrawing+171;
                        g.drawImage(imgGo,
					xXDrawing,yXDrawing,
					ImageUtility.GRAPHICS_TOP_LEFT) ;
                        xXDrawing=xDrawing+110;
                        yXDrawing=yDrawing+160;
                        g.drawImage(imgGo,
					xXDrawing,yXDrawing,
					ImageUtility.GRAPHICS_TOP_LEFT) ;
                        xXDrawing=xDrawing+82;
                        yXDrawing=yDrawing+137;
                        g.drawImage(imgGo,
					xXDrawing,yXDrawing,
					ImageUtility.GRAPHICS_TOP_LEFT) ;
                        xXDrawing=xDrawing+124;
                        yXDrawing=yDrawing+130;
                        g.drawImage(imgGo,
					xXDrawing,yXDrawing,
					ImageUtility.GRAPHICS_TOP_LEFT) ;
                        xXDrawing=xDrawing+102;
                        yXDrawing=yDrawing+118;
                        g.drawImage(imgGo,
					xXDrawing,yXDrawing,
					ImageUtility.GRAPHICS_TOP_LEFT) ;
                        xXDrawing=xDrawing+135;
                        yXDrawing=yDrawing+99;
                        g.drawImage(imgGo,
					xXDrawing,yXDrawing,
					ImageUtility.GRAPHICS_TOP_LEFT) ;
                        xXDrawing=xDrawing+74;
                        yXDrawing=yDrawing+102;
                        g.drawImage(imgGo,
					xXDrawing,yXDrawing,
					ImageUtility.GRAPHICS_TOP_LEFT) ;
			break;
                    default:
                        break;
                }
                //--------------------
                 switch (currlevel) {

                    case 1:
			xDrawing=xDrawing+95;
                        yDrawing=yDrawing+183;
			break;
                    case 2:
                        xDrawing=xDrawing+89;
                        yDrawing=yDrawing+171;
			break;
                    case 3:
                        xDrawing=xDrawing+110;
                        yDrawing=yDrawing+160;
			break;
                    case 4:
                        xDrawing=xDrawing+82;
                        yDrawing=yDrawing+137;
			break;
                    case 5:
                        xDrawing=xDrawing+124;
                        yDrawing=yDrawing+130;
			break;
                    case 6:
                        xDrawing=xDrawing+102;
                        yDrawing=yDrawing+118;
			break;
                    case 7:
                        xDrawing=xDrawing+135;
                        yDrawing=yDrawing+99;
			break;
                    case 8:
                        xDrawing=xDrawing+74;
                        yDrawing=yDrawing+102;
			break;
                    default:
                        break;
                }
                g.setColor(255, 0, 0);
                try{
                	font.drawString(g,MissionName[currlevel-1], xDrawing,yDrawing+3);
                	
                	g.drawImage(imgfocus[currframefocus],
          					xDrawing,yDrawing,
          					ImageUtility.GRAPHICS_TOP_LEFT) ;
                }
                	
                catch(Exception ex){ 
                	
                }
               softKey.drawRightSoftkey(g, "Next", widthCanvas, heightCanvas);
                softKey.drawLeftSoftkey(g, "Menu", widthCanvas, heightCanvas);
                }
	}

	public void run() {
		while( true ){
                        if( isBluetoothMode ){
			 bluetoothClientProcessing() ;
                        }
			if( shouldStop){
				break ;
			}
                       
                      
                        currframefocus++ ;
			if( currframefocus >= 2){
				currframefocus = 0 ;
			}
			repaint() ;

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
		
	}

	
}
