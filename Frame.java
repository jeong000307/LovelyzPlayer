import java.io.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.imageio.*;

class Frame extends JFrame implements ActionListener{
    ControlMPEG controlMP3 = new ControlMPEG();
    ControlWAV controlWAV = new ControlWAV();

    Frame() {   	
        Container panel = this.getContentPane();
        JMenuBar menuBar = new JMenuBar();
        JPanel controlPanel = new JPanel();
        JPanel listPanel = new JPanel();

        JToggleButton play = new JRoundButton("play");
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

        JLabel albumArt = new JLabel();
        albumArt.setSize(250, 250);
        albumArt.setHorizontalAlignment(JLabel.CENTER);

        ImageIcon jisoo = this.autoResizePicture("asset/jisoo.png", play.getWidth(), play.getHeight());
        ImageIcon kei = this.autoResizePicture("asset/kei.png", play.getWidth(), play.getHeight());
        ImageIcon next = this.autoResizePicture("asset/jisoo.png", nextMusic.getWidth(), nextMusic.getHeight());
        ImageIcon prev = this.autoResizePicture("asset/kei.png", prevMusic.getWidth(), prevMusic.getHeight());
        ImageIcon cover = this.autoResizePicture("asset/A New Trilogy.jpg", albumArt.getWidth(), albumArt.getHeight());

        play.setIcon(jisoo);
        play.setSelectedIcon(kei);
        play.addActionListener(this);
        
        nextMusic.setIcon(next);
        
        prevMusic.setIcon(prev);
        
        albumArt.setIcon(cover);
        
        setJMenuBar(menuBar);
        
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        
        fileMenu.add(open);
        
        open.addActionListener(this);
        
        panel.add(albumArt, BorderLayout.NORTH);
        panel.add(controlPanel, BorderLayout.CENTER);
        panel.add(listPanel, BorderLayout.SOUTH);
        
        controlPanel.setLayout(new FlowLayout());
        controlPanel.add(prevMusic);
        controlPanel.add(play);
        controlPanel.add(nextMusic);
        
        listPanel.setBorder(new TitledBorder("목록"));

        this.setTitle("Lovelyz Player");
        this.setSize(300, 800);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    public void actionPerformed(ActionEvent e)  {
        if(e.getActionCommand().equals("play"))   {
           // controlMP3.playMusic("1.mp3");
        }
        else if(e.getActionCommand().equals("prevMusic"))	{
        	
        }
        else if(e.getActionCommand().equals("nextMusic"))	{
        	
        }
        else if(e.getActionCommand().equals("Open(O)"))	{
        	JFileChooser dirChooser = new JFileChooser();
        	dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        	
        	int result = dirChooser.showOpenDialog(this);
            System.out.println(result);
            
            if(result == JFileChooser.APPROVE_OPTION)	{
            	File folder = dirChooser.getSelectedFile();
            }
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
