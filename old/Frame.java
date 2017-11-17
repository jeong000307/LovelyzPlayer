import java.io.*;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.tree.TreeModel;

import javafx.embed.swing.SwingFXUtils;

import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.imageio.*;

class Frame extends JFrame implements ActionListener{
    ControlMusic controlMusic = new ControlMusic();
    File folder;
    ArrayList<File> musicFiles = new ArrayList<File>();
    ArrayList<String> musicList = new ArrayList<String>();
    JList list = new JList();
    JLabel albumArt = new JLabel();
    JLabel album = new JLabel();
    JLabel artist = new JLabel();
    JLabel title = new JLabel();
    JLabel year = new JLabel();
    JScrollPane listScrollPane = new JScrollPane(list);
    JRoundButton play = new JRoundButton("play");

    ImageIcon jisoo;
    ImageIcon kei;
    ImageIcon cover;

    int currentPlay = -1;

    Frame() {   	
        Container panel = this.getContentPane();
        JMenuBar menuBar = new JMenuBar();
        JPanel albumInfoPanel = new JPanel();
        JPanel controlPanel = new JPanel();
        JPanel listPanel = new JPanel();
        JPanel centerPanel = new JPanel();

        JRoundButton nextMusic = new JRoundButton("nextMusic");
        JRoundButton prevMusic = new JRoundButton("prevMusic");
        
        JMenu fileMenu = new JMenu("File(F)");
        JMenu editMenu = new JMenu("Edit(E)");
        JMenuItem open = new JMenuItem("Open(O)");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        editMenu.setMnemonic(KeyEvent.VK_E);
        
        play.setSize(70, 70);
        nextMusic.setSize(50, 50);
        prevMusic.setSize(50, 50);

        albumArt.setSize(250, 250);
        albumArt.setHorizontalAlignment(JLabel.CENTER);

        jisoo = this.autoResizePicture("asset/jisoo.png", play.getWidth(), play.getHeight());
        kei = this.autoResizePicture("asset/kei.png", play.getWidth(), play.getHeight());
        ImageIcon next = this.autoResizePicture("asset/jisoo.png", nextMusic.getWidth(), nextMusic.getHeight());
        ImageIcon prev = this.autoResizePicture("asset/kei.png", prevMusic.getWidth(), prevMusic.getHeight());
        cover = this.autoResizePicture("asset/A New Trilogy.jpg", albumArt.getWidth(), albumArt.getHeight());

        play.setIcon(jisoo);
        play.addActionListener(this);
        
        nextMusic.setIcon(next);
        nextMusic.addActionListener(this);
        
        prevMusic.setIcon(prev);
        prevMusic.addActionListener(this);
        
        albumArt.setIcon(cover);
        
        setJMenuBar(menuBar);
        
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        
        fileMenu.add(open);
        
        open.addActionListener(this);
        
        panel.add(albumInfoPanel, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);
        panel.add(listPanel, BorderLayout.SOUTH);

        
        centerPanel.add(albumArt, BorderLayout.NORTH);
        centerPanel.add(controlPanel, BorderLayout.SOUTH);

        albumInfoPanel.setLayout(new FlowLayout());
        albumInfoPanel.add(artist);
        albumInfoPanel.add(title);
        albumInfoPanel.add(year);
        albumInfoPanel.add(album);

        controlPanel.setLayout(new FlowLayout());
        controlPanel.add(prevMusic);
        controlPanel.add(play);
        controlPanel.add(nextMusic);
        
        listPanel.setLayout(new GridLayout(1, 1));
        listPanel.setBorder(new TitledBorder("목록"));
        listPanel.add(listScrollPane);

        list.addListSelectionListener(new ListSelectionListener(){
            @Override
            public void valueChanged(ListSelectionEvent event)    {
                currentPlay = list.getSelectedIndex();
                play.setIcon(kei);
                controlMusic.playMusic(musicFiles.get(currentPlay));
                changeAttribute();
            }
        });

        this.setTitle("Lovelyz Player");
        this.setSize(300, 800);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    public void actionPerformed(ActionEvent e)  {
        if(e.getActionCommand().equals("play"))   {
            if(controlMusic.isPausing() && controlMusic.isPlaying()) {
                play.setIcon(kei);
                System.out.println("2");
                controlMusic.resumeMusic();
            }
            else if(controlMusic.isPlaying()) {
                play.setIcon(jisoo);
                System.out.println("1");
                controlMusic.pauseMusic();
            }
        }
        else if(e.getActionCommand().equals("prevMusic"))	{
            System.out.println("3");
            controlMusic.stopMusic();
            if(currentPlay - 1 < 0) {
                currentPlay = musicFiles.size() - 1;
            }
            else    {
                --currentPlay;
            }
            controlMusic.playMusic(musicFiles.get(currentPlay));
            changeAttribute();
        }
        else if(e.getActionCommand().equals("nextMusic"))	{
            System.out.println("4");
            controlMusic.stopMusic();
            if(currentPlay + 1 >= musicFiles.size()) {
                currentPlay = 0;
            }
            else    {
                ++currentPlay;
            }
            controlMusic.playMusic(musicFiles.get(currentPlay));
            changeAttribute();
        }
        else if(e.getActionCommand().equals("Open(O)"))	{
        	JFileChooser dirChooser = new JFileChooser();
        	dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        	
        	int result = dirChooser.showOpenDialog(this);
            
            if(result == JFileChooser.APPROVE_OPTION)	{
            	folder = dirChooser.getSelectedFile();
            }
            else    {
                return;
            }

            musicList.clear();
            musicFiles.clear();
            selectFolder(folder);

            list.setListData(musicList.toArray());
            listScrollPane.revalidate();
        }
    }

    public void selectFolder(File folder)   {
        File[] fileList = folder.listFiles();

		try{
			for(int i = 0; i < fileList.length; ++i) {
				if(fileList[i].isFile() && filterFileExtension(fileList[i].getName())){
                    musicList.add(fileList[i].getName());
                    musicFiles.add(fileList[i]);
				}
                else if(fileList[i].isDirectory()){
					selectFolder(fileList[i]);
				}
			}
		} catch(Exception e){
            e.printStackTrace();
		}
    }

    public ImageIcon autoResizePicture(String str, int width, int height)  {
        str = "./" + str;
        BufferedImage img = null;
        Image dimg = null;
        ImageIcon imgIcon = new ImageIcon();

        try {
            img = ImageIO.read(new File(str));
        } catch(IOException e) {
            e.printStackTrace();
        }

        dimg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);

        imgIcon.setImage(dimg);

        return imgIcon;
    }

    public boolean filterFileExtension(String fileName)    {
        String filter[] = {"mp3", "wav", "m4a"};
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1);

        for(String filtering : filter)  {
            if(extension.equals(filtering)) {
                return true;
            }
        }

        return false;
    }

    public void changeAttribute() {
        if(controlMusic.cover != null)  {
            System.out.println("1");
            BufferedImage img = SwingFXUtils.fromFXImage(controlMusic.cover, null);
            cover = new ImageIcon();
            cover.setImage(img.getScaledInstance(albumArt.getWidth(), albumArt.getHeight(), Image.SCALE_SMOOTH));
        }
        
        album.setText(controlMusic.album);
        year.setText(controlMusic.year);
        artist.setText(controlMusic.artist);
        title.setText(controlMusic.title);
    }
}

class JRoundButton extends JToggleButton{
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
