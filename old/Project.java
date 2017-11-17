import java.io.*;
import java.nio.file.Paths;
import javax.sound.sampled.*;

import javafx.collections.*;
import javafx.stage.Stage;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.application.Application;
import javafx.scene.image.Image;

public class Project extends Application {
    public static void main(String[] args) {
        Frame f = new Frame();
        Application.launch();
    }

    public void start(Stage primaryStage) {}
}

class ControlMusic implements Runnable {
    Image cover;
    String artist;
    String title;
    String year;
    String album;
    boolean playFlag = false;
    boolean pauseFlag = false;
    private static MediaPlayer player;

    public void run()   {}

    public boolean isPlaying() {
        return playFlag;
    }

    public boolean isPausing()  {
        return pauseFlag;
    }

    public void playMusic(File music)   {
        final Media media = new Media(music.toURI().toString());
        ObservableMap<String,Object> metadata;
        player = new MediaPlayer(media);
        this.playFlag = true;
        this.pauseFlag = false;

        metadata = media.getMetadata();
        metadata.addListener(new MapChangeListener<String,Object>(){
            @Override
            public void onChanged(Change<? extends String, ? extends Object> ch) {  
                if(ch.wasAdded()){
                    String key=ch.getKey();
                    Object value=ch.getValueAdded(); 

                    switch(key){
                        case "album":
                            album = value.toString(); break;
                        case "artist":  
                            artist = value.toString(); break;
                        case "title":
                            title = value.toString(); break;
                        case "year":
                            year = value.toString(); break;
                        case "image":
                            cover = (Image)value; break;
                    }
                }
            }
        });

        player.play();
    }

    public void stopMusic()    {
        if(this.isPlaying() == true)   {
            player.stop();
            this.playFlag = false;
            this.pauseFlag = false;
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