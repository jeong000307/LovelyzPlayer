import java.io.File;
import java.nio.file.Paths;
import javax.sound.sampled.*;
import javafx.stage.Stage;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.application.Application;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Project extends Application {
    //Command command = new Command();
    public static void main(String[] args) {
        Frame f = new Frame();
        Application.launch();
    }

    public void start(Stage primaryStage) {
    }
}

class Frame extends JFrame implements ActionListener{
    ControlMPEG controlMP3 = new ControlMPEG();
    ControlWAV controlWAV = new ControlWAV();
    Frame() {
        Container panel = this.getContentPane();
        JButton play = new JButton("재생");
        panel.add(play);
        play.addActionListener(this);
        this.setTitle("test");
        this.setSize(300, 300);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    public void actionPerformed(ActionEvent e)  {
        if(e.getActionCommand().equals("재생"))   {
            controlMP3.playMusic("1.mp3");
        }
    }
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
        player.play();
        this.playFlag = true;
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