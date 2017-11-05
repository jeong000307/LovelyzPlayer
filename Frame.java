import java.io.*;
import javax.swing.*;
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
        JPanel centerPanel = new JPanel();

        JToggleButton play = new JRoundButton("play");
        play.setSize(70, 70);

        JLabel albumArt = new JLabel();
        albumArt.setSize(250, 250);
        albumArt.setHorizontalAlignment(JLabel.CENTER);

        ImageIcon jisoo = this.autoResizePicture("jisoo.png", play.getWidth(), play.getHeight());
        ImageIcon kei = this.autoResizePicture("kei.png", play.getWidth(), play.getHeight());
        ImageIcon cover = this.autoResizePicture("A New Trilogy.jpg", albumArt.getWidth(), albumArt.getHeight());

        play.setIcon(jisoo);
        play.setSelectedIcon(kei);
        albumArt.setIcon(cover);

        panel.add(albumArt, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);
        centerPanel.setLayout(new FlowLayout());
        play.addActionListener(this);
        centerPanel.add(play);

        this.setTitle("Lovelyz Player");
        this.setSize(300, 800);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    public void actionPerformed(ActionEvent e)  {
        if(e.getActionCommand().equals("play"))   {
            controlMP3.playMusic("1.mp3");
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