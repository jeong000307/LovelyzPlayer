import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.Observable;
import java.io.IOException;
import java.io.File;

import java.awt.geom.Point2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.FlowLayout;
import java.awt.BorderLayout;
import java.awt.Dimension;

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
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.BorderFactory;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.xml.crypto.dsig.spec.ExcC14NParameterSpec;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import javafx.collections.ObservableMap;
import javafx.collections.MapChangeListener;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.application.Platform;

public class Lovelyz extends Application {
    
    public static void main(String[] args) {
        new GUI();
        Application.launch();
    }

    @Override
    public void start(Stage primaryStage) {}
}

class GUI extends JFrame implements ActionListener, Runnable  {
    Container mainPanel = this.getContentPane();
    JMenuBar menuBar = new JMenuBar();
    JPanel albumInfoPanel = new JPanel();
    JPanel controlPanel = new JPanel();
    JPanel listPanel = new JPanel();
    JPanel centerPanel = new JPanel();

    JMenu fileMenu = new JMenu("File(F)");
    JMenu editMenu = new JMenu("Edit(E)");
    JMenu helpMenu = new JMenu("Help(H)");
    JMenuItem open = new JMenuItem("Open(O)");

    ArrayList<File> musicFileList = new ArrayList<File>();
    ArrayList<String> musicList = new ArrayList<String>();
    
    Vector<Integer> previousList = new Vector<Integer>(1);

    JList list = new JList();
    JScrollPane listScrollPane = new JScrollPane(list);

    JSlider timeSlider = new JSlider();

    JRoundButton playButton = new JRoundButton("play");
    JRoundButton nextButton = new JRoundButton("nextMusic");
    JRoundButton previousButton = new JRoundButton("previousMusic");
    JRoundButton shuffleButton = new JRoundButton("shuffle");
    JRoundButton repeatButton = new JRoundButton("repeat");

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
    JLabel playTime = new JLabel();

    Media media = null;
    ObservableMap<String,Object> metadata;
    private static MediaPlayer player = null;

    Timer timer = new Timer();

    Duration duration;
    Duration currentTime;
    Duration timeSliderValue;

    File folder = null;

    int currentPlay = 0;
    int oldCurrentPlay = 0;
    int randomPlay = 0;

    boolean isPlaying = false;
    boolean isPausing = false;
    boolean isShuffling = false;
    int isRepeating = 0;

    public GUI()   {
        playButton.setSize(70, 70);
        nextButton.setSize(50, 50);
        previousButton.setSize(50, 50);
        albumArt.setSize(250, 250);
        albumArt.setHorizontalAlignment(JLabel.CENTER);

        fileMenu.setMnemonic(KeyEvent.VK_SPACE);
        fileMenu.setMnemonic(KeyEvent.VK_F);
        editMenu.setMnemonic(KeyEvent.VK_E);

        jisooIcon = autoResizePicture(new File("./asset/play.png"), playButton.getWidth(), playButton.getHeight());
        keiIcon = autoResizePicture(new File("./asset/kei.png"), playButton.getWidth(), playButton.getHeight());
        smallJisooIcon = autoResizePicture(new File("./asset/jisoo.png"), nextButton.getWidth(), nextButton.getHeight());
        smallKeiIcon = autoResizePicture(new File("./asset/kei.png"), nextButton.getWidth(), nextButton.getHeight());
        albumIcon = autoResizePicture(new File("./asset/default.jpg"), albumArt.getWidth(), albumArt.getHeight());

        playButton.setIcon(jisooIcon);
        nextButton.setIcon(smallJisooIcon);
        previousButton.setIcon(smallKeiIcon);
        shuffleButton.setIcon(smallJisooIcon);
        repeatButton.setIcon(smallKeiIcon);
        playButton.addActionListener(this);
        nextButton.addActionListener(this);
        previousButton.addActionListener(this);
        shuffleButton.addActionListener(this);
        repeatButton.addActionListener(this);
        
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
        albumInfoPanel.add(album);

        centerPanel.add(albumArt, BorderLayout.NORTH);
        centerPanel.add(controlPanel, BorderLayout.CENTER);
        centerPanel.add(timeSlider, BorderLayout.SOUTH);
        timeSlider.setValue(0);
        timeSlider.setPreferredSize(new Dimension(250, 50));

        controlPanel.setLayout(new FlowLayout());
        controlPanel.add(shuffleButton);
        controlPanel.add(previousButton);
        controlPanel.add(playButton);
        controlPanel.add(nextButton);
        controlPanel.add(repeatButton);
        
        listPanel.setLayout(new GridLayout(1, 1));
        listPanel.setBorder(new TitledBorder("목록"));
        listPanel.add(listScrollPane);
        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                JList list = (JList)mouseEvent.getSource();
                if (mouseEvent.getClickCount() == 2) {
                    currentPlay = list.locationToIndex(mouseEvent.getPoint());
                    playMusic(musicFileList.get(currentPlay));
                }
            }
        });

        timeSlider.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                timeSliderValue = new Duration(timeSlider.getValue());
                player.seek(timeSliderValue);
            }
        });
        timeSlider.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent mouseEvent) {
                // dv = timeSlider.getValue();
                // timeSlider.setValue(dv);
                // timeSliderValue = new Duration(dv);
                // player.seek(draggedVal);
                timeSliderValue = new Duration(timeSlider.getValue());
                System.out.println(timeSliderValue);
                player.seek(timeSliderValue);
            }
        });

        timer.schedule(new TimerTask()  {
            @Override
            public void run()   {
                if(duration != null && !duration.equals(Duration.ZERO))   {
                    updateSlider();
                }
            }
        }, 0, 200);


        this.setTitle("Lovelyz Player");
        this.setSize(300, 650);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    @Override
    public void run()   {}

    @Override
    public void actionPerformed(ActionEvent e)  {
        if(e.getActionCommand().equals("play"))   {
            if(isPausing & isPlaying) {
                resumeMusic();
            }
            else if(isPlaying) {
                pauseMusic();
            }
        }
        else if(e.getActionCommand().equals("previousMusic"))	{
            if(isShuffling) {
                currentPlay = previousList.remove(previousList.size() - 1);
            }
            else if(isRepeating > 0)   {
                currentPlay = setCurrentPlay();
            }
            else if(currentPlay - 1 < 0) {
                currentPlay = musicFileList.size() - 1;
            }
            else    {
                --currentPlay;
            }
            if(currentPlay >= 0 & currentPlay < musicFileList.size())   {
                playMusic(musicFileList.get(currentPlay));
            }
        }
        else if(e.getActionCommand().equals("nextMusic"))	{
            if(isRepeating > 0 || isShuffling)   {
                currentPlay = setCurrentPlay();
            }
            else if(currentPlay + 1 >= musicFileList.size()) {
                currentPlay = 0;
            }
            else    {
                ++currentPlay;
            }
            if(currentPlay >= 0 & currentPlay < musicFileList.size())   {
                playMusic(musicFileList.get(currentPlay));
            }
        }
        else if(e.getActionCommand().equals("shuffle")) {
            if(isShuffling) {
                isShuffling = false;
            }
            else    {
                isShuffling = true;
            }
        }
        else if(e.getActionCommand().equals("repeat"))  {
            if(isRepeating < 2) {
                ++isRepeating;
            }
            else    {
                isRepeating = 0;
            }
            isShuffling = false;
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
            currentPlay = 0;

            selectFolder(folder);

            list.setListData(musicList.toArray());
            listScrollPane.revalidate();
        }
    }

    private void playMusic(File music)    {
        if(music == null)   {
            return;
        }
        if(player != null)  {
            stopMusic();
        }

        media = new Media(music.toURI().toString());
        player = new MediaPlayer(media);
        metadata = media.getMetadata();

        if(previousList.size() == 10)   {
            previousList.remove(0);
        }

        if(oldCurrentPlay != currentPlay || isRepeating == 2)   {
            previousList.addElement(oldCurrentPlay);
        }

        playButton.setIcon(keiIcon);
        player.play();

        player.setOnReady(new Runnable() {   
            @Override
            public void run() {
                title.setText(media.getMetadata().get("title").toString());
                artist.setText(media.getMetadata().get("artist").toString());
                album.setText(media.getMetadata().get("album").toString());
                albumIcon = autoResizePicture(SwingFXUtils.fromFXImage((javafx.scene.image.Image)media.getMetadata().get("image"), null), albumArt.getWidth(), albumArt.getHeight());
                albumArt.setIcon(albumIcon);
                duration = media.getDuration();
                timeSlider.setMaximum((int)(duration.toMillis()));
            }
        });

        player.setOnEndOfMedia(new Runnable()   {
            @Override
            public void run()   {
                setCurrentPlay();
                stopMusic();
                timeSlider.setValue(0);
                duration = Duration.ZERO;
                if(currentPlay >= 0 & currentPlay < musicFileList.size())   {
                    playMusic(musicFileList.get(currentPlay));
                }
            }
        });

        isPlaying = true;
        isPausing = false;
    }

    private void stopMusic()    {
        playButton.setIcon(jisooIcon);

        player.stop();

        isPlaying = false;
        isPausing = false;
    }

    private void pauseMusic()   {
        playButton.setIcon(jisooIcon);

        player.pause();

        isPausing = true;
    }

    private void resumeMusic()  {
        playButton.setIcon(keiIcon);

        player.play();

        isPausing = false;
    }

    private void updateSlider() {
        if(duration == null)    {
            return;
        }
        try {
            currentTime = player.getCurrentTime();
            if (duration.greaterThan(Duration.ZERO)) {
                timeSlider.setValue((int)(currentTime.toMillis()));
            }
        } catch(Exception e)    {
            e.printStackTrace();
        }
    }

    private ImageIcon autoResizePicture(BufferedImage image, int width, int height)  {
        ImageIcon imageIcon = new ImageIcon();

        imageIcon.setImage(image.getScaledInstance(width, height, Image.SCALE_SMOOTH));

        return imageIcon;
    }

    private ImageIcon autoResizePicture(File file, int width, int height)  {
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

    private int setCurrentPlay()    {
        oldCurrentPlay = currentPlay;
        if(isShuffling)    {
            do{
                randomPlay = randomMusicIndex();
            } while(randomPlay == currentPlay);
            currentPlay = randomPlay;
        }
        else if(isRepeating == 1)    {
            if(currentPlay + 1 >= musicFileList.size()) {
                currentPlay = 0;
            }
            else    {
                ++currentPlay;
            }
        }
        else if(isRepeating == 2)   {}
        else    {
            if(currentPlay + 1 >= musicFileList.size()) {
                currentPlay = -1;
            }
            else    {
                ++currentPlay;
            }
        }
        return currentPlay;
    }

    private int randomMusicIndex()   {
        return (int)(Math.random() * musicList.size());
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
