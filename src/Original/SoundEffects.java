package Original;






import javax.microedition.media.*;
import java.io.*;

/**
 *
 * @author long
 */

public class SoundEffects
{
    private static SoundEffects instance;
    private Player explosionSoundPlayer;       
	private Player musicbackgroup;
	
    private SoundEffects()   
    {
       explosionSoundPlayer = createPlayer("/xplode.wav", "audio/x-wav");   
    }

    public static SoundEffects getInstance()
    {
        if (instance == null)
        {
            instance = new SoundEffects();

        }
        return instance;
    }

    public void startExplosionSound()
    {
     	 startPlayer(explosionSoundPlayer) ;
    }

    public void startGameOverSound()
    {

    }       
  	
  	public void startGamebr()
    {
//        try {
//            Player p = createPlayer("/Arcade.mid", "audio/midi");
//            p.start();
//            p.setLoopCount(-1);
//        } catch (MediaException ex) {
//            ex.printStackTrace();
//        }
    }
    
    private void startPlayer(Player p)
    {
        if (p != null)
        {
            try
            {
                p.stop();
                p.setMediaTime(0L);
                p.start();                              
            }
            catch (MediaException me)
            {
                // ignore
            }
        }
    }

    private Player createPlayer(String filename, String format)
    {
        Player p = null;
        try
        {
            InputStream is = getClass().getResourceAsStream(filename);
            p = Manager.createPlayer(is, format);
            p.prefetch();
        }
        catch (IOException ioe)
        {
            // ignore
        }
        catch (MediaException me)
        {
            // ignore
        }
        return p;
    }
   

}
