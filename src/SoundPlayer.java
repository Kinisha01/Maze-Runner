import javax.sound.sampled.*;
import java.io.File;

public class SoundPlayer {

    //Clip clip;
    Clip currentClip;

    public void playLoop(String path)
    {
        try
        {
            if(currentClip != null) currentClip.stop();

            AudioInputStream audio =
                    AudioSystem.getAudioInputStream(new File(path));

            currentClip = AudioSystem.getClip();
            currentClip.open(audio);
            currentClip.loop(Clip.LOOP_CONTINUOUSLY);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void stop()
    {
        if(currentClip != null) currentClip.stop();
    }

    public void playOnce(String path)
    {
        try
        {
            if(currentClip != null) currentClip.stop();

            AudioInputStream audio =
                    AudioSystem.getAudioInputStream(new File(path));

            currentClip = AudioSystem.getClip();
            currentClip.open(audio);
            currentClip.start();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}