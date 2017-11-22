import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Observable;
import java.util.Vector;

import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.geom.Point2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.Rectangle;

import javax.imageio.ImageIO;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.border.TitledBorder;
import javax.swing.BorderFactory;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableMap;
import javafx.collections.MapChangeListener;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;

public class LovelyzPlayer extends Application {
    
    public static void main(String[] args) {
        new GUI();
        Application.launch();
    }

    @Override
    public void start(Stage primaryStage) {}
}

class GUI extends JFrame implements ActionListener, Runnable  {
    private Container mainPanel = this.getContentPane();

    private JMenuBar menuBar = new JMenuBar();

    private JPanel albumInfoPanel = new JPanel();
    private JPanel controlPanel = new JPanel();
    private JPanel listPanel = new JPanel();
    private JPanel centerPanel = new JPanel();

    private JMenu fileMenu = new JMenu("File(F)");
    private JMenu editMenu = new JMenu("Edit(E)");
    private JMenu helpMenu = new JMenu("Help(H)");

    private JMenuItem open = new JMenuItem("Open(O)");

    private JList list = new JList();

    private JScrollPane listScrollPane = new JScrollPane(list);

    private JSlider timeSlider = new JSlider();

    private JRoundButton playButton = new JRoundButton("play");
    private JRoundButton nextButton = new JRoundButton("nextMusic");
    private JRoundButton previousButton = new JRoundButton("previousMusic");
    private JRoundButton shuffleButton = new JRoundButton("shuffle");
    private JRoundButton repeatButton = new JRoundButton("repeat");

    private ImageIcon jisooIcon = new ImageIcon();
    private ImageIcon keiIcon = new ImageIcon();
    private ImageIcon playIcon = new ImageIcon();
    private ImageIcon smallJisooIcon = new ImageIcon();
    private ImageIcon smallKeiIcon = new ImageIcon();
    private ImageIcon albumIcon = new ImageIcon();

    private JLabel albumArt = new JLabel();
    private JLabel album = new JLabel();
    private JLabel artist = new JLabel();
    private JLabel title = new JLabel();
    private JLabel playTime = new JLabel();

    private Media media = null;

    private static MediaPlayer player = null;

    private ObservableMap<String,Object> metadata;

    private Timer timer = new Timer();

    private Duration duration;
    private Duration currentTime;
    private Duration timeSliderValue;

    private File folder = null;

    private JFileChooser dirChooser;

    private ArrayList<File> musicFileList = new ArrayList<File>();
    private ArrayList<String> musicList = new ArrayList<String>();
    
    private Vector<Integer> previousList = new Vector<Integer>(1);

    private int currentPlay = 0;
    private int oldCurrentPlay = 0;
    private int randomPlay = 0;
    private int isRepeating = 0;

    private boolean isPlaying = false;
    private boolean isPausing = false;
    private boolean isShuffling = false;
    
    public GUI()   {
        playButton.setSize(70, 70);
        nextButton.setSize(50, 50);
        previousButton.setSize(50, 50);
        albumArt.setSize(250, 250);

        timeSlider.setPreferredSize(new Dimension(250, 50));

        timeSlider.setValue(0);

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
        albumArt.setIcon(albumIcon);

        playButton.addActionListener(this);
        nextButton.addActionListener(this);
        previousButton.addActionListener(this);
        shuffleButton.addActionListener(this);
        repeatButton.addActionListener(this);
        open.addActionListener(this);

        albumInfoPanel.setLayout(new FlowLayout());
        controlPanel.setLayout(new FlowLayout());
        listPanel.setLayout(new GridLayout(1, 1));

        setJMenuBar(menuBar);

        listPanel.setBorder(new TitledBorder("목록"));

        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        fileMenu.add(open);

        mainPanel.add(albumInfoPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(listPanel, BorderLayout.SOUTH);

        albumInfoPanel.add(artist);
        albumInfoPanel.add(title);
        albumInfoPanel.add(album);

        centerPanel.add(albumArt, BorderLayout.NORTH);
        centerPanel.add(controlPanel, BorderLayout.CENTER);
        centerPanel.add(timeSlider, BorderLayout.SOUTH);
        
        controlPanel.add(shuffleButton);
        controlPanel.add(previousButton);
        controlPanel.add(playButton);
        controlPanel.add(nextButton);
        controlPanel.add(repeatButton);
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
                timeSliderValue = new Duration(timeSlider.getValue());
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

    @Override
    public void run()   {}

    @Override
    public void actionPerformed(ActionEvent e)  {
        switch(e.getActionCommand())    {
            case "play"         :   if(isPausing & isPlaying) {
                                        resumeMusic();
                                    }
                                    else if(isPlaying) {
                                        pauseMusic();
                                    }
                                    break;
                                    
            case "previousMusic" :  currentPlay = (isShuffling)? previousList.remove(previousList.size() - 1): setCurrentPlay();
                                    if(currentPlay >= 0 && currentPlay < musicFileList.size())   {
                                        playMusic(musicFileList.get(currentPlay));
                                    } 
                                    break;

            case "nextMusic"    :   currentPlay = setCurrentPlay();
                                    if(currentPlay >= 0 && currentPlay < musicFileList.size())   {
                                        playMusic(musicFileList.get(currentPlay));
                                    }
                                    break;

            case "shuffle"      :   isShuffling = (isShuffling)? false: true;
                                    break;

            case "repeat"       :   isRepeating = (isRepeating < 2)? ++isRepeating: 0;
                                    isShuffling = false;
                                    break;

            case "Open(O)"      :   dirChooser = new JFileChooser();

        	                        dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

                                    if(dirChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)	{
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
                                    break;
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

    private int setCurrentPlay()    {
        oldCurrentPlay = currentPlay;
        if(isShuffling)    {
            do{
                randomPlay = randomMusicIndex();
            } while(randomPlay == currentPlay);
            currentPlay = randomPlay;
        }
        else if(isRepeating == 1)    {
            /*if(currentPlay + 1 >= musicFileList.size()) {
                currentPlay = 0;
            }
            else    {
                ++currentPlay;
            }*/

            currentPlay = (currentPlay + 1 >= musicFileList.size())? 0: ++currentPlay;
        }
        else if(isRepeating == 2)   {}
        else    {
            /*if(currentPlay + 1 >= musicFileList.size()) {
                currentPlay = -1;
            }
            else    {
                ++currentPlay;
            }*/
            currentPlay = (currentPlay + 1 >= musicFileList.size())? -1: ++currentPlay;
        }
        return currentPlay;
    }

    private ImageIcon autoResizePicture(BufferedImage image, int width, int height)  {
        ImageIcon imageIcon = new ImageIcon();

        imageIcon.setImage(image.getScaledInstance(width, height, Image.SCALE_SMOOTH));

        return imageIcon;
    }

    private ImageIcon autoResizePicture(File file, int width, int height)  {
        BufferedImage image = null;
        ImageIcon imageIcon = new ImageIcon();

        try{
            image = ImageIO.read(file);
		} catch(Exception e){
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