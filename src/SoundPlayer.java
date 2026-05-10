import javax.sound.sampled.*;
import java.io.File;

public class SoundPlayer
{
    Clip bgClip;

    Clip effectClip;

    // =========================
    // BACKGROUND MUSIC
    // =========================

    public void playLoop(String path)
    {
        try
        {
            stopBackground();

            AudioInputStream audio =
                    AudioSystem.getAudioInputStream(
                            new File(path)
                    );

            bgClip = AudioSystem.getClip();

            bgClip.open(audio);

            bgClip.loop(Clip.LOOP_CONTINUOUSLY);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    // =========================
    // STOP BACKGROUND
    // =========================

    public void stopBackground()
    {
        if(bgClip != null)
        {
            bgClip.stop();
            bgClip.close();
        }
    }

    // =========================
    // PLAY EFFECT
    // =========================

    public void playEffect(String path)
    {
        try
        {
            stopEffect();

            AudioInputStream audio =
                    AudioSystem.getAudioInputStream(
                            new File(path)
                    );

            effectClip = AudioSystem.getClip();

            effectClip.open(audio);

            effectClip.start();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    // =========================
    // STOP EFFECT
    // =========================

    public void stopEffect()
    {
        if(effectClip != null)
        {
            effectClip.stop();

            effectClip.close();
        }
    }
}