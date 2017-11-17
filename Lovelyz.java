//import org.jaudiotagger.audio.*;

import java.util.ArrayList;

import java.io.IOException;
import java.io.File;

import java.awt.geom.Point2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.FlowLayout;
import java.awt.BorderLayout;

import javax.imageio.ImageIO;

import javax.swing.JMenu;
import javax.swing.JList;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JScrollPane;
import javax.swing.BorderFactory;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import javafx.collections.ObservableMap;
import javafx.collections.MapChangeListener;
import javafx.stage.Stage;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;

public class Lovelyz extends Application {
    
    public static void main(String[] args) {
        new GUI();
        Application.launch();
    }

    @Override
    public void start(Stage primaryStage) {}
}

class GUI extends JFrame implements ActionListener, ListSelectionListener, Runnable  {
    Container mainPanel = this.getContentPane();
    JMenuBar menuBar = new JMenuBar();
    JPanel albumInfoPanel = new JPanel();
    JPanel controlPanel = new JPanel();
    JPanel listPanel = new JPanel();
    JPanel centerPanel = new JPanel();

    JMenu fileMenu = new JMenu("File(F)");
    JMenu editMenu = new JMenu("Edit(E)");
    JMenuItem open = new JMenuItem("Open(O)");

    ArrayList<File> musicFileList = new ArrayList<File>();
    ArrayList<String> musicList = new ArrayList<String>();

    JList list = new JList();
    JScrollPane listScrollPane = new JScrollPane(list);

    JRoundButton playButton = new JRoundButton("play");
    JRoundButton nextButton = new JRoundButton("nextMusic");
    JRoundButton previousButton = new JRoundButton("previousMusic");

    ImageIcon jisooIcon = new ImageIcon();
    ImageIcon keiIcon = new ImageIcon();
    ImageIcon playIcon = new ImageIcon();
    ImageIcon smallJisooIcon = new ImageIcon();
    ImageIcon smallKeiIcon = new ImageIcon();
    ImageIcon albumIcon = new ImageIcon();

    JLabel albumArt = new JLabel();
    JLabel album = new JLabel();
    JLabel artist = new JLabel();
    JLabel title = new JLabel();
    JLabel year = new JLabel(); 

    Media media = null;
    ObservableMap<String,Object> metadata;
    private static MediaPlayer player = null;

    File folder = null;

    int currentPlay = -1;

    boolean isPlaying = false;
    boolean isPausing = false;

    public GUI()   {
        playButton.setSize(70, 70);
        nextButton.setSize(50, 50);
        previousButton.setSize(50, 50);
        albumArt.setSize(250, 250);

        albumArt.setHorizontalAlignment(JLabel.CENTER);

        fileMenu.setMnemonic(KeyEvent.VK_F);
        editMenu.setMnemonic(KeyEvent.VK_E);

        jisooIcon = autoResizePicture(new File("./asset/jisoo.png"), playButton.getWidth(), playButton.getHeight());
        keiIcon = autoResizePicture(new File("./asset/kei.png"), playButton.getWidth(), playButton.getHeight());
        smallJisooIcon = autoResizePicture(new File("./asset/jisoo.png"), nextButton.getWidth(), nextButton.getHeight());
        smallKeiIcon = autoResizePicture(new File("./asset/kei.png"), nextButton.getWidth(), nextButton.getHeight());
        albumIcon = autoResizePicture(new File("./asset/A New Trilogy.jpg"), albumArt.getWidth(), albumArt.getHeight());

        playButton.setIcon(jisooIcon);
        nextButton.setIcon(smallJisooIcon);
        previousButton.setIcon(smallKeiIcon);
        playButton.addActionListener(this);
        nextButton.addActionListener(this);
        previousButton.addActionListener(this);
        
        albumArt.setIcon(albumIcon);
        
        setJMenuBar(menuBar);
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        fileMenu.add(open);
        open.addActionListener(this);
        
        mainPanel.add(albumInfoPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(listPanel, BorderLayout.SOUTH);

        albumInfoPanel.setLayout(new FlowLayout());
        albumInfoPanel.add(artist);
        albumInfoPanel.add(title);
        albumInfoPanel.add(year);
        albumInfoPanel.add(album);

        centerPanel.add(albumArt, BorderLayout.NORTH);
        centerPanel.add(controlPanel, BorderLayout.SOUTH);

        controlPanel.setLayout(new FlowLayout());
        controlPanel.add(previousButton);
        controlPanel.add(playButton);
        controlPanel.add(nextButton);
        
        listPanel.setLayout(new GridLayout(1, 1));
        listPanel.setBorder(new TitledBorder("목록"));
        listPanel.add(listScrollPane);
        list.addListSelectionListener(this);

        this.setTitle("Lovelyz Player");
        this.setSize(300, 800);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    @Override
    public void run()   {}

    @Override
    public void actionPerformed(ActionEvent e)  {
        if(e.getActionCommand().equals("play"))   {
            if(isPausing & isPlaying) {
                playButton.setIcon(keiIcon);
                resumeMusic();
            }
            else if(isPlaying) {
                playButton.setIcon(jisooIcon);
                pauseMusic();
            }
        }
        else if(e.getActionCommand().equals("prevMusic"))	{
            if(currentPlay - 1 < 0) {
                currentPlay = musicFileList.size() - 1;
            }
            else    {
                --currentPlay;
            }
            playMusic(musicFileList.get(currentPlay));
        }
        else if(e.getActionCommand().equals("nextMusic"))	{
            if(currentPlay + 1 >= musicFileList.size()) {
                currentPlay = 0;
            }
            else    {
                ++currentPlay;
            }
            playMusic(musicFileList.get(currentPlay));
        }
        else if(e.getActionCommand().equals("Open(O)"))	{
            int result;
            JFileChooser dirChooser = new JFileChooser();
            
        	dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        	
        	result = dirChooser.showOpenDialog(this);
            
            if(result == JFileChooser.APPROVE_OPTION)	{
            	folder = dirChooser.getSelectedFile();
            }
            else    {
                return;
            }

            musicList.clear();
            musicFileList.clear();

            selectFolder(folder);

            list.setListData(musicList.toArray());
            listScrollPane.revalidate();
        }
    }
    
    @Override
    public void valueChanged(ListSelectionEvent e)  {
        currentPlay = list.getSelectedIndex();
        playButton.setIcon(keiIcon);
        this.playMusic(musicFileList.get(currentPlay));
    }

    private void playMusic(File music)    {
        media = new Media(music.toURI().toString());
        player = new MediaPlayer(media);

        metadata = media.getMetadata();
        metadata.addListener(new MapChangeListener<String, Object>() {
            @Override
            public void onChanged(Change<? extends String, ? extends Object> change) {  
                if(change.wasAdded()){
                    String key=change.getKey();
                    Object value=change.getValueAdded(); 
                
                    switch(key){
                        case "album":
                            album.setText(value.toString()); break;
                        case "artist":  
                            artist.setText(value.toString()); break;
                        case "title":
                            title.setText(value.toString()); break;
                        case "year":
                            year.setText(value.toString()); break;
                        case "image":
                            albumIcon = autoResizePicture(SwingFXUtils.fromFXImage((javafx.scene.image.Image)value, null), albumArt.getWidth(), albumArt.getHeight()); 
                            albumArt.setIcon(albumIcon); break;
                    }
                }
            }
        });

        stopMusic();

        player.play();

        isPlaying = true;
        isPausing = false;
    }

    private void stopMusic()    {
        player.stop();

        isPlaying = false;
        isPausing = false;
    }

    private void pauseMusic()   {
        player.pause();

        isPausing = true;
    }

    private void resumeMusic()  {
        player.play();

        isPausing = false;
    }

    public ImageIcon autoResizePicture(BufferedImage image, int width, int height)  {
        ImageIcon imageIcon = new ImageIcon();

        imageIcon.setImage(image.getScaledInstance(width, height, Image.SCALE_SMOOTH));

        return imageIcon;
    }

    public ImageIcon autoResizePicture(File file, int width, int height)  {
        BufferedImage image = null;
        ImageIcon imageIcon = new ImageIcon();

        try {
            image = ImageIO.read(file);
        } catch(IOException e) {
            e.printStackTrace();
        }

        imageIcon.setImage(image.getScaledInstance(width, height, Image.SCALE_SMOOTH));

        return imageIcon;
    }

    private void selectFolder(File folder)   {
        File[] fileList = folder.listFiles();

		try{
			for(int i = 0; i < fileList.length; ++i) {
				if(fileList[i].isFile() && filterFileExtension(fileList[i].getName())){
                    musicList.add(fileList[i].getName());
                    musicFileList.add(fileList[i]);
				}
                else if(fileList[i].isDirectory()){
					selectFolder(fileList[i]);
				}
			}
		} catch(Exception e){
            e.printStackTrace();
		}
    }

    private boolean filterFileExtension(String fileName)    {
        String filter[] = {"mp3", "wav", "m4a"};
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1);

        for(String filtering : filter)  {
            if(extension.equals(filtering)) {
                return true;
            }
        }

        return false;
    }
}

class JRoundButton extends JButton{
	public JRoundButton(String text){
        super();
        super.setBorder(BorderFactory.createEmptyBorder());
        super.setActionCommand(text);
        super.setContentAreaFilled(false);
        super.setFocusPainted(false);
        super.setContentAreaFilled(false);
	}
	
	private int getDiameter(){
        int diameter = Math.min(getWidth(), getHeight());
        
		return diameter;
	}
	
	@Override
	public boolean contains(int x, int y){
        float radius = getDiameter() / 2;
        
		return Point2D.distance(x, y, getWidth() / 2, getHeight() / 2) < radius;
	}
}
