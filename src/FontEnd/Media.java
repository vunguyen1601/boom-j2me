/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package FontEnd;

/**
 *
 * @author Thach Vu
 */
import java.io.IOException;
import javax.microedition.media.*;
public class Media {
   private Player amthanh;
   private static Media instance;
   private Media()
    {
        try {
            amthanh = Manager.createPlayer(getClass().getResourceAsStream("/midimenu.mid"), "audio/midi");
            amthanh.setLoopCount(-1);
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (MediaException ex) {
            ex.printStackTrace();
        }
    }
   public static Media getInstance()
    {
        if (instance == null)
        {
            instance = new Media();

        }
        return instance;
    }
   
   public void Startmedia(){
        try {
            amthanh.start();
        } catch (MediaException ex) {
            ex.printStackTrace();
        }
   }
   public void Stopmedia(){
        try {
            amthanh.stop();
        } catch (MediaException ex) {
            ex.printStackTrace();
        }
   }
}
