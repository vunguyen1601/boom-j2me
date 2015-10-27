package MainGame;
import FontEnd.About;
import java.io.IOException;

import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import FontEnd.MenuScreen;
import FontEnd.SplashScreen;
import Bluetooth.BluetoothClient;
import Bluetooth.BluetoothServer;
import FontEnd.Highscore;
import Original.RMSManager;
import FontEnd.MapCavas;
import FontEnd.Endgame;
import FontEnd.Warning;
import FontEnd.instructions;
import Original.RMS_Score;
import javax.microedition.io.Connector;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Command;

import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.TextField;
import javax.wireless.messaging.MessageConnection;
import javax.wireless.messaging.TextMessage;


public class TestMIDlet extends MIDlet implements CommandListener {
	private BoomCanvas gameCanvas ;
	private SplashScreen splashCanvas ;
	private MenuScreen menuCanvas ;
	private Display display ;
        private Highscore highscore;
        private About about;
        private int currLevel =8 ;
        private MapCavas mapcanvas;
        private Endgame endgame;
             // Bluetooth mode.
        private BluetoothServer server ;
        private BluetoothClient client ;
        private List listBluetooth ;
        private instructions instr;
        private Warning warning ;
        private int score;
        private RMS_Score rmsscore;
        private TextField txt;
        private Form form;
        private Alert alert;
        private boolean ismusic=false;
        private List listDeviceBTClient ;

	public TestMIDlet() throws IOException {

		display = Display.getDisplay(this) ;
		splashCanvas = new SplashScreen(this) ;
		//menuCanvas =  new MenuScreen(this) ;
               // highscore=new Highscore(this);
               // about=new About(this);
               // mapcanvas=new MapCavas(this);
                //instr=new instructions(this);
      	}

	protected void destroyApp(boolean arg0) throws MIDletStateChangeException {

	}

	protected void pauseApp() {
		System.out.println("midlet pauseApp()") ;
	}

	protected void startApp() throws MIDletStateChangeException {
		Displayable currentCanvas = display.getCurrent() ;
		if( currentCanvas == null ){
			//showSavescoreScreen();
                    display.setCurrent(splashCanvas) ;
			return ;
		}

		// manage the pausing state.
		if( currentCanvas == gameCanvas ){
		//showSavescoreScreen();
                    showMenuScreen(false) ;
		}

		if( currentCanvas == menuCanvas ){
		//showSavescoreScreen();
                   showMenuScreen(false) ;
		}
	}

	public Display getDisplay() {
		return display;
	}

	public void show(Displayable dp){
		display.setCurrent(dp) ;
	}

	public void showGameScreen(){

		show(gameCanvas) ;
	}

        public void showEndGameScreen(){
            try {
                releaseGameResource();
                endgame = new Endgame(this);
                show(endgame);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
	}
        public void showsavegame(){
          
            rmsscore=new RMS_Score();
            String name[]=rmsscore.getNames();
            int values[]=rmsscore.getValues();
            if(score>values[values.length-1]){
            txt=new TextField("Name: ", "No Name",50,TextField.ANY);
            StringItem lb1=new StringItem("High score","");
            form = new Form("Save high score");
            //System.out.println(5+"");
         
                form.append(txt);
          
                form.append(lb1);
                form.append("\n  "+"Name"+"   "+"Score");
            for (int i=0;i<name.length;i++)
                {
                    form.append("\n  "+name[i]+"   "+values[i]);
                }
            Command cmdsaveserver = new Command("Save score on server", Command.STOP,2);
           Command cmdmenu = new Command("Back", Command.BACK,2);
            Command cmdsave = new Command("Save score", Command.CANCEL,2);

     
         
                 form.addCommand(cmdsave);
                 form.addCommand(cmdsaveserver);
       
            form.addCommand(cmdmenu);
            form.setCommandListener(this);

            show(form);
            }else{
                showMenuScreen(false);
            }
	}
        public void showGameAboutScreen(){
        try {
            releaseGameResource();
            about = new About(this);
            show(about);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
	}

    public void showGameInstrucScreen(){
        try {
            releaseGameResource();
            instr = new instructions(this);
            show(instr);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
	}

        public void showhighscore(){
                try {
                    releaseGameResource();
                    highscore = new Highscore(this);
                    show(highscore);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
	}

	public void showMenuScreen(boolean  isBluetooh){
            try {
                // The canvas that are displaying when get into the function.
               // releaseGameResource();

                if(isBluetooh)
                menuCanvas=new MenuScreen(this,server,client);
                else
                menuCanvas = new MenuScreen(this);

                if (gameCanvas == getCurrentDisplay()) {
                    menuCanvas.setContinueString();
                }
                show(menuCanvas);
                System.out.println("midlet show(menuCanvas)");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
	}

	public Displayable getCurrentDisplay(){
		return display.getCurrent() ;
	}

	public void playGameAgain(boolean  isblue){
		try {
			releaseGameResource() ;
			if(isblue){
                            gameCanvas=new BoomCanvas(this, currLevel, server, client);
                        }else{
                           gameCanvas = new BoomCanvas(this,currLevel) ;
                        }
			showGameScreen() ;

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void playGameAgain(int level,boolean  isblue){
            try {
			releaseGameResource() ;
                    if(level>=5){
                        showEndGameScreen()  ;
                    }else{
                        if(isblue){
                            gameCanvas=new BoomCanvas(this, level, server, client);
                        }else{
                            gameCanvas = new BoomCanvas(this,level) ;
                        }
                            showGameScreen() ;
                    }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void nextMission(){
		try {
			releaseGameResource() ;
                        currLevel++;
			//gameCanvas = new BoomCanvas(this,++currLevel) ;
			mapcanvas=new MapCavas(this);
                        showGameMapScreen(false);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void releaseGameResource(){
                endgame=null;
                about=null;
                highscore=null;
                menuCanvas=null;
                splashCanvas=null;
		gameCanvas = null ;
                mapcanvas=null;
                instr=null;
		System.gc() ;
	}

    public void Exitmidlet()
    {
    	notifyDestroyed() ;
        FontEnd.Media.getInstance().Stopmedia();
    }

    public void savegame(int level)
    {
        RMSManager rmsma = new RMSManager("save");
        byte[]data=null;

        try {
            data=rmsma.toByteArray(level+"");
            rmsma.open();
            rmsma.updateData(data);
            rmsma.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public int Loadsavegame()
    {

        int level=1;
        byte[] RmsReturn=null;
        RMSManager	rmsMan=new RMSManager("save");
            try {
                    rmsMan.open();
                    RmsReturn=rmsMan.readRMS(1);
                    if(RmsReturn!=null)
                    level=Integer.parseInt(rmsMan.fromByteArray(RmsReturn));
                    else System.out.println("test");
                    rmsMan.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        return level;
    }

    public void setCurrLevel(int currLevel) {
		this.currLevel = currLevel;
	}

    public int getCurrLevel() {
		return currLevel;
	}

    // bluetooth API

    public void debugConsole(String info){
    	System.out.println(info) ;
    }

    public void setAlert(String info) {
        if( warning == null ){
        	try {
				warning = new Warning(this) ;
			} catch (IOException e) {
				e.printStackTrace();
			}
        }

        warning.setString(info);
      
        display.setCurrent(warning);
    }

    public void initBluetoothServer(){
    	 server=new BluetoothServer(this);
    }

    public void initBluetoothClient(){
    	client = new BluetoothClient(this) ;
    }

    public void showBlutoothList(){
    	listDeviceBTClient = new List("Select", List.IMPLICIT);
		listBluetooth = new List("Select", List.IMPLICIT);
		listBluetooth.append("Server", null);
		listBluetooth.append("Client", null);

	        Command cmd_ok = new Command("OK", Command.OK, 1);
                Command cmd_exit= new Command("Menu", Command.BACK, 1);
	        listBluetooth.addCommand(cmd_ok);
                 listBluetooth.addCommand(cmd_exit);
	        listBluetooth.setCommandListener(this);

	        show(listBluetooth) ;
	}

    public void showGameMapScreen(boolean isBluetoothMode) {
        releaseGameResource();
        if( !isBluetoothMode ){
    		 try {

                 mapcanvas = new MapCavas(this);
             } catch (IOException ex) {
                 ex.printStackTrace();
             }
    	 }
    	 else{
    		 try {
    				mapcanvas = new MapCavas(this,server,client) ;
    			} catch (IOException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
    	 }


         show(mapcanvas) ;
    }

    public void waitForMessage(String title){
    	setAlert(title) ;
    	warning.exchangeData(server, client) ;
    }

    private void sendSMS() throws IOException
    {
     
         //    Lets send an SMS now.
        //get a reference to the appropriate Connection object using an appropriate url
        MessageConnection conn = (MessageConnection) Connector.open("sms://"+System.getProperty("wireless.messaging.sms.smsc"));
        //generate a new text message

        TextMessage tmsg = (TextMessage) conn.newMessage(MessageConnection.TEXT_MESSAGE);
        //set the message text and the address
        tmsg.setPayloadText("HA HS 21 "+cut(txt.getString().trim())+" "+score);
        tmsg.setAddress("sms://" + 8017);
        //finally send our message
        conn.send(tmsg);
    }
   private String cut(String in)
   {
            String result ="";
            in=in.trim();
            int      pos = in.indexOf( ' ' );
            int i=0;
            while(pos!=-1)
            {

                result = in.substring( 0, pos ).trim();
                in=in.substring(pos+1).trim();
                in=result+in;
                result=in;
                pos = in.indexOf( ' ' );
            }
            return result;
   }
    public void commandAction(Command c, Displayable d) {
           if (c.getCommandType()==Command.STOP){
            try {
                rmsscore.updateScores(score,cut(txt.getString().trim()));
                 sendSMS();
//                alert.setString("So diem cua ban \nda duoc dang len website.");
//                display.setCurrent(alert,gameCanvas);
               waitForMessage("This score was posted/non website");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }else if (c.getCommandType()==Command.CANCEL){
            try {
                rmsscore.updateScores(score, cut(txt.getString().trim()));
                 showMenuScreen(false);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }else if (c.getCommandType()==Command.BACK){
                showMenuScreen(false);
	}else if (c.getCommandType()==Command.OK) {
	            switch (listBluetooth.getSelectedIndex()){
	                case 0: {
	                    initBluetoothServer() ;
	                    break;
	                }

	                case 1: {
	                	initBluetoothClient() ;
	                    break;
	                }
	                default:
	                	break ;
	            }
	        }
    }
    public void setmussic(boolean bien)
    {
        ismusic=bien;
    }
     public boolean  getmussic()
    {
       return ismusic;
    }
    public void setScore(int Score)
    {
        score=Score;
    }
    
    // bluetooth 
    public void addFriendlyDeviceName(String n){
    	listDeviceBTClient.append(n, null) ;
    }
    
    public void showListBTDevice(){
    	display.setCurrent(listDeviceBTClient) ;
    }
}
