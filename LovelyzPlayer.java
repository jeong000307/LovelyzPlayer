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