import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Command extends Thread    {
    ControlMusic controlMPEG = new ControlMPEG();
    ControlMusic controlWAV = new ControlWAV();

    Scanner scan = new Scanner(System.in);
    String command;
    int determiner = 0;
    long pauseTime;

    public void run()   {

        while(true) {
            command = scan.nextLine();
            
            String commandArray[] = command.split(" ");
            
            switch(commandArray[0])   {
                case "quit" : 
                case "exit" : System.exit(1);
                case "play" : playMusic(); break;
                case "stop" : stopMusic(); break;
                case "pause" : pauseMusic(); break;
                case "resume" : resumeMusic(); break;
                case "list" : System.out.println("Root Directory = ");
                                catalog(".", 0); break;
                default : System.out.println("Unsupported command");
            }
        }
    }

    public void playMusic()  {
        String playList = command.replaceFirst("play ", "");
        String extensionArray[] = command.split("\\.");
        switch(extensionArray[extensionArray.length - 1])   {
            case "mp3": 
            case "MP3":
            case "m4a":
            case "M4A": stopMusic(); controlMPEG.playMusic(playList);
                        determiner = 1; break;
            case "wav":
            case "WAV": stopMusic(); controlWAV.playMusic(playList);
                        determiner = 2; break;            
            default: System.out.println("Unsupported type");
        }
    }

    public void stopMusic()  {
        switch(determiner)   {
            case 1: controlMPEG.stopMusic(); break;
            case 2: controlWAV.stopMusic(); break;
        }
    }

    public void pauseMusic()    {
        switch(determiner)  {
            case 1: ((ControlMPEG)controlMPEG).pauseMusic(); break;
            case 2: pauseTime = ((ControlWAV)controlWAV).pauseMusic(); break;
        }
    }

    public void resumeMusic()    {
        switch(determiner)  {
            case 1: ((ControlMPEG)controlMPEG).resumeMusic(); break;
            case 2: ((ControlWAV)controlWAV).resumeMusic(pauseTime); break;
        }
    }

    public void catalog(String source, int count)   {
        File path = new File(source);

		File[] fileList = path.listFiles(); 

		try{
			for(int i = 0; i < fileList.length; ++i){
                for(int j = 0; j < count; ++j)  {
                    System.out.print(" ");
                }
                
				if(fileList[i].isFile()){
					System.out.println(" file = " + fileList[i].getName());
				}
                else if(fileList[i].isDirectory()){
					System.out.println(" Sub Directory = " + fileList[i].getName());
					catalog(fileList[i].getCanonicalPath().toString(), count + 1); 

				}
			}
		} catch(IOException e){
            e.printStackTrace();
		}
    }
}