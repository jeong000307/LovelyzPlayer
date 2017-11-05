import java.io.*;
import java.nio.file.Paths;
import javax.sound.sampled.*;
import javafx.stage.Stage;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.application.Application;

public class Project extends Application {
    public static void main(String[] args) {
        Frame f = new Frame();
        Application.launch();
    }

    public void start(Stage primaryStage) {}
}

abstract class ControlMusic {
    boolean playFlag = false;
    boolean pauseFlag = false;

    public abstract void playMusic(String musicName);
    public abstract void stopMusic();

    public boolean isPlaying() {
        return playFlag;
    }

    public boolean isPausing()  {
        return pauseFlag;
    }
}

class ControlMPEG extends ControlMusic implements Runnable  {
    private static MediaPlayer player;

    public void run()   {}

    public void playMusic(String musicName)   {
        final Media media = new Media(Paths.get(musicName).toUri().toString());
        player = new MediaPlayer(media);
        this.playFlag = true;
        player.play();
    }

    public void stopMusic()    {
        if(this.isPlaying() == true)   {
            player.stop();
            this.playFlag = false;
        }
    }

    public void pauseMusic()    {
        if(this.isPlaying() == true & this.isPausing() == false)   {
            player.pause();
            this.pauseFlag = true;
        }
    }

    public void resumeMusic()   {
        if(this.isPlaying() == true & this.isPausing() == true)   {
            player.play();
            this.pauseFlag = false;
        }
    }
}

class ControlWAV extends ControlMusic implements Runnable    { 
    AudioInputStream stream;
    Clip player;
    long pauseTime;

    public void run()   {}

    public void playMusic(String musicName)   { 
        try{
            stream = AudioSystem.getAudioInputStream(new File(musicName));
            player = AudioSystem.getClip(); 
            player.open(stream);
            player.start();
            this.playFlag = true;
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void stopMusic()    {
        if(this.isPlaying() == true)    {
            player.stop();
            this.playFlag = false;
        }
    }

    public long pauseMusic()    {
        if(this.isPlaying() == true & this.isPausing() == false)    {
            pauseTime = player.getMicrosecondPosition();
            player.stop();
            this.pauseFlag = true;
        }

        return pauseTime;
    }

    public void resumeMusic(long pauseTime)  {
        if(this.isPlaying() == true & this.isPausing() == true)    {
            player.setMicrosecondPosition(pauseTime);
            player.start();
            this.pauseFlag = false;
        }
    }
}
