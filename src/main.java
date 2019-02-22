import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.geom.Point2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import javax.swing.border.TitledBorder;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
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
import javafx.application.Application;
import javafx.collections.ObservableMap;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;

public class main extends Application {
    
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
    private JPanel timePanel = new JPanel();

    private JMenu fileMenu = new JMenu("File(F)");
    private JMenu editMenu = new JMenu("Edit(E)");

    private JMenuItem open = new JMenuItem("Open(O)");

    private JList list = new JList();

    private JScrollPane listScrollPane = new JScrollPane(list);

    private JSlider timeSlider = new JSlider(0, 0, 0);

    private JRoundButton playButton = new JRoundButton("play");
    private JRoundButton nextButton = new JRoundButton("nextMusic");
    private JRoundButton previousButton = new JRoundButton("previousMusic");
    private JRoundButton shuffleButton = new JRoundButton("shuffle");
    private JRoundButton repeatButton = new JRoundButton("repeat");

    private ImageIcon playIcon = new ImageIcon();
    private ImageIcon pauseIcon = new ImageIcon();
    private ImageIcon previousIcon = new ImageIcon();
    private ImageIcon nextIcon = new ImageIcon();
    private ImageIcon noRepeatIcon = new ImageIcon();
    private ImageIcon repeatIcon = new ImageIcon();
    private ImageIcon repeatOneIcon = new ImageIcon();
    private ImageIcon noShuffleIcon = new ImageIcon();
    private ImageIcon shuffleIcon = new ImageIcon();
    private ImageIcon albumIcon = new ImageIcon();

    private JLabel albumArt = new JLabel();
    private JLabel album = new JLabel();
    private JLabel artist = new JLabel();
    private JLabel title = new JLabel();
    private JLabel playDuration = new JLabel("0:00");
    private JLabel totalDuration = new JLabel("0:00");

    private ObservableMap<String,Object> metadata;

    private Media media = null;

    private static MediaPlayer player = null;

    private Timer timer = new Timer();

    private Duration duration;
    private Duration currentDuration;
    private Duration timeSliderValue;

    private File folder = null;

    private JFileChooser dirChooser;

    private ArrayList<File> musicFileList = new ArrayList<File>();
    private ArrayList<String> musicList = new ArrayList<String>();
    
    private Vector<Integer> previousList = new Vector<Integer>(1);
    
    private long seconds;
    private long minutes;

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

        timeSlider.setPreferredSize(new Dimension(200, 50));

        timeSlider.setValue(0);

        listScrollPane.setBorder(null);

        albumArt.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        artist.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        album.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        title.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        fileMenu.setMnemonic(KeyEvent.VK_SPACE);
        fileMenu.setMnemonic(KeyEvent.VK_F);
        editMenu.setMnemonic(KeyEvent.VK_E);

        playIcon = autoResizePicture(new File("./asset/jisooPlay.png"), playButton.getWidth(), playButton.getHeight());
        pauseIcon = autoResizePicture(new File("./asset/keiPause.png"), playButton.getWidth(), playButton.getHeight());
        previousIcon = autoResizePicture(new File("./asset/yeinPrev.png"), previousButton.getWidth(), previousButton.getHeight());
        nextIcon = autoResizePicture(new File("./asset/babysoulNext.png"), nextButton.getWidth(), nextButton.getHeight());
        noRepeatIcon = autoResizePicture(new File("./asset/jinNoRepeat.png"), nextButton.getWidth(), nextButton.getHeight());
        repeatIcon = autoResizePicture(new File("./asset/jinRepeat.png"), nextButton.getWidth(), nextButton.getHeight());
        repeatOneIcon = autoResizePicture(new File("./asset/jinRepeatOne.png"), nextButton.getWidth(), nextButton.getHeight());
        noShuffleIcon = autoResizePicture(new File("./asset/mijooNoShuffle.png"), nextButton.getWidth(), nextButton.getHeight());
        shuffleIcon = autoResizePicture(new File("./asset/mijooShuffle.png"), nextButton.getWidth(), nextButton.getHeight());
        albumIcon = autoResizePicture(new File("./asset/default.jpg"), albumArt.getWidth(), albumArt.getHeight());

        playButton.setIcon(playIcon);
        nextButton.setIcon(nextIcon);
        previousButton.setIcon(previousIcon);
        shuffleButton.setIcon(noShuffleIcon);
        repeatButton.setIcon(noRepeatIcon);
        albumArt.setIcon(albumIcon);

        playButton.addActionListener(this);
        nextButton.addActionListener(this);
        previousButton.addActionListener(this);
        shuffleButton.addActionListener(this);
        repeatButton.addActionListener(this);
        open.addActionListener(this);

        albumInfoPanel.setLayout(new BoxLayout(albumInfoPanel, BoxLayout.Y_AXIS));
        controlPanel.setLayout(new FlowLayout());
        listPanel.setLayout(new GridLayout(1, 1));
        timePanel.setLayout(new FlowLayout());

        albumInfoPanel.setBackground(new Color(100, 100, 100));
        centerPanel.setBackground(new Color(100, 100, 100));
        controlPanel.setBackground(new Color(100, 100, 100));
        timePanel.setBackground(new Color(100, 100, 100));
        listPanel.setBackground(new Color(100, 100, 100));
        list.setBackground(new Color(100, 100, 100));
        timeSlider.setBackground(new Color(100, 100, 100));

        artist.setForeground(Color.WHITE);
        title.setForeground(Color.WHITE);
        album.setForeground(Color.WHITE);
        playDuration.setForeground(Color.WHITE);
        totalDuration.setForeground(Color.WHITE);

        setJMenuBar(menuBar);

        listPanel.setBorder(new TitledBorder("List"));

        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        fileMenu.add(open);

        mainPanel.add(albumInfoPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(listPanel, BorderLayout.SOUTH);

        albumInfoPanel.add(albumArt);
        albumInfoPanel.add(artist);
        albumInfoPanel.add(title);
        albumInfoPanel.add(album);

        timePanel.add(playDuration);
        timePanel.add(timeSlider);
        timePanel.add(totalDuration);
        centerPanel.add(controlPanel, BorderLayout.CENTER);
        centerPanel.add(timePanel, BorderLayout.SOUTH);
        
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
                if(timeSlider.getMaximum() == 0)    {
                    return;
                }
                timeSliderValue = new Duration(timeSlider.getValue());
                player.seek(timeSliderValue);
            }
        });

        timeSlider.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent mouseEvent) {
                if(timeSlider.getMaximum() == 0)    {
                    return;
                }
                timeSliderValue = new Duration(timeSlider.getValue());
                player.seek(timeSliderValue);
            }
        });

        timer.schedule(new TimerTask()  {
            @Override
            public void run()   {
                if(duration != null)   {
                    updateSlider();
                    setTime();
                }
            }
        }, 0, 200);

        this.setTitle("Lovelyz Player");
        this.setSize(320, 700);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void updateSlider() {
        if(duration == null)    {
            return;
        }
        try {
            currentDuration = player.getCurrentTime();
            if (duration.greaterThan(Duration.ZERO)) {
                timeSlider.setValue((int)(currentDuration.toMillis()));
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

            case "previousMusic" :  setCurrentPlay(true);
                                    if(currentPlay >= 0 & currentPlay < musicList.size())   {
                                        playMusic(musicFileList.get(currentPlay));
                                    }
                                    break;

            case "nextMusic"    :   setCurrentPlay(false);
                                    if(currentPlay >= 0 && currentPlay < musicList.size())   {
                                        playMusic(musicFileList.get(currentPlay));
                                    }
                                    break;

            case "shuffle"      :   if(isShuffling) {
                                        isShuffling = false;
                                        shuffleButton.setIcon(noShuffleIcon);
                                    }
                                    else    {
                                        isShuffling = true;
                                        shuffleButton.setIcon(shuffleIcon);
                                    }
                                    isRepeating = 0;
                                    repeatButton.setIcon(noRepeatIcon);
                                    break;

            case "repeat"       :   if(isRepeating == 0) {
                                        isRepeating = 1;
                                        repeatButton.setIcon(repeatIcon);
                                    }
                                    else if(isRepeating == 1)    {
                                        isRepeating = 2;
                                        repeatButton.setIcon(repeatOneIcon);
                                    }
                                    else    {
                                        isRepeating = 0;
                                        repeatButton.setIcon(noRepeatIcon);
                                    }
                                    shuffleButton.setIcon(noShuffleIcon);
                                    isShuffling = false;
                                    break;

            case "Open(O)"      :   dirChooser = new JFileChooser();

        	                        dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

                                    if(dirChooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION)	{
                                        return;
                                    }

                                    folder = dirChooser.getSelectedFile();
                                
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

        if(previousList.size() == 10)   {
            previousList.remove(0);
        }

        player.setOnReady(new Runnable() {   
            @Override
            public void run() {
            	metadata = media.getMetadata();
            	
                if(metadata.get("title") == null)    {
                    title.setText("");
                }
                else    {
                    title.setText(metadata.get("title").toString());
                }
                if(metadata.get("artist") == null)    {
                    artist.setText("");
                }
                else    {
                    artist.setText(metadata.get("artist").toString());
                }
                if(metadata.get("album") == null)    {
                    album.setText("");
                }
                else    {
                    album.setText(metadata.get("album").toString());
                }
                if(metadata.get("image") == null)   {
                    albumIcon = autoResizePicture(new File("./asset/default.jpg"), albumArt.getWidth(), albumArt.getHeight());
                }
                else    {
                    albumIcon = autoResizePicture(SwingFXUtils.fromFXImage((javafx.scene.image.Image)metadata.get("image"), null), albumArt.getWidth(), albumArt.getHeight());
                }
                albumArt.setIcon(albumIcon);
                duration = media.getDuration();
                timeSlider.setMaximum((int)(duration.toMillis()));
                seconds = (long)Math.floor(duration.toSeconds());
                minutes = (long)Math.floor(seconds / 60.0);
                seconds %= 60;
                totalDuration.setText(String.format("%02d:%02d", minutes, seconds));
            }
        });

        playButton.setIcon(pauseIcon);
        player.play();

        player.setOnEndOfMedia(new Runnable()   {
            @Override
            public void run()   {
                setCurrentPlay(false);
                stopMusic();
                timeSlider.setValue(0);
                timeSlider.setMaximum(0);
                duration = Duration.ZERO;
                currentDuration = Duration.ZERO;
                totalDuration.setText("00:00");
                if(currentPlay >= 0 & currentPlay < musicList.size())   {
                    playMusic(musicFileList.get(currentPlay));
                }
            }
        });

        isPlaying = true;
        isPausing = false;
    }

    private void stopMusic()    {
        playButton.setIcon(playIcon);

        player.stop();

        isPlaying = false;
        isPausing = false;
    }

    private void pauseMusic()   {
        playButton.setIcon(playIcon);

        player.pause();

        isPausing = true;
    }

    private void resumeMusic()  {
        playButton.setIcon(pauseIcon);

        player.play();

        isPausing = false;
    }

    private void setCurrentPlay(boolean previousFlag)    {
        boolean flag = false;
        
        if(previousFlag)    {
            if(currentPlay < 0) {
                return;
            }

            if(isShuffling)    {
                if(previousList.isEmpty())    {
                    do{
                        randomPlay = randomMusicIndex();
                    } while(randomPlay == currentPlay);
                    currentPlay = randomPlay;
                }
                else    {
                    currentPlay = previousList.remove(previousList.size() - 1);
                }
                flag = true;
            }
            else if(isRepeating == 1)    {
                currentPlay = (currentPlay - 1 < 0)? musicList.size() - 1: --currentPlay;
            }
            else if(isRepeating == 2)   {
                flag = true;
            }
            else    {
                --currentPlay;
            }
        }
        else    {
            if(currentPlay >= musicList.size()) {
                return;
            }

            if(isShuffling)    {
                do{
                    randomPlay = randomMusicIndex();
                } while(randomPlay == currentPlay);
                currentPlay = randomPlay;
            }
            else if(isRepeating == 1)    {
                currentPlay = (currentPlay + 1 >= musicList.size())? 0: ++currentPlay;
            }
            else if(isRepeating == 2)   {
                flag = true;
            }
            else    {
                ++currentPlay;
            }
        }

        oldCurrentPlay = currentPlay;

        if(!flag)   {
            previousList.addElement(oldCurrentPlay);
        }
        list.setSelectedIndex(currentPlay);
    }

    private void setTime()  {
        seconds = (long)Math.floor(currentDuration.toSeconds());
        minutes = (long)Math.floor(seconds / 60.0);
        seconds %= 60;

        playDuration.setText(String.format("%02d:%02d", minutes, seconds));
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