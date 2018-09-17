import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.JOptionPane;

public class HomeGraphics extends GameGraphics {
	private static final long serialVersionUID = 5442146611591669343L;
	private BufferedImage img_background, img_wheel;
	private JLabel client_label, player_label, msg_label, ip_label;
	private JTextField address_text, player_name;
	private JButton button_connect, button_exit, button_back, button_start_host;
	private boolean wheel = false, popup = false, up = false;
	int offset;
	private float deg = 0;
	private Timer t;
	private int font_size_default;

	public HomeGraphics(Dimension d, GameState gs) {
		super(d, gs);
		img_background = img_wheel = null;
		font_size_default = (int)(15*Math.abs(this.getSize().getWidth()/this.getSize().getHeight()));
		offset = 30; //px
		this.setDoubleBuffered(true);
		loadImages();
		addMouseListener(this);
		t = new Timer(50, this);
	}
	
	private void loadImages(){
		try {
			img_background = ImageIO.read(new File("img/scrabble-wp.jpg"));
			img_background = ImageIO.read(new File("img/dswp.jpg"));
			img_wheel = ImageIO.read(new File("img/wheel.png"));
		} catch (IOException e) {
			System.out.println("Couldn't load the images");
			e.printStackTrace();
		}
	}
	
	public void menu(){		
		msg_label = new JLabel();
		msg_label.setForeground(new Color(78,190,255));
		msg_label.setFont(new Font("Verdana", Font.PLAIN, font_size_default));
		msg_label.setVisible(false);
		msg_label.setEnabled(false);
		
		ip_label = new JLabel(gs.getMyAddress());
		ip_label.setForeground(new Color(78,190,255));
		ip_label.setFont(new Font("Verdana", Font.PLAIN, font_size_default/2));
		ip_label.setBounds(new Rectangle(new Point(0,0), ip_label.getPreferredSize()));
		ip_label.setLocation(10, 10);
		ip_label.setVisible(true);
		ip_label.setEnabled(true);
		
		client_label = new JLabel("Connect to existing host");
		client_label.setForeground(new Color(78,190,255));
		client_label.setFont(new Font("Verdana", Font.PLAIN, font_size_default));
		client_label.setBounds(new Rectangle(new Point(0,0), client_label.getPreferredSize()));
		client_label.setLocation((int)(this.getSize().width/2)-(int)(client_label.getWidth()/2), (int)(this.getSize().height/2)-(int)(this.getSize().height*0.1));
		client_label.setVisible(true);
		client_label.setEnabled(true);
		
		address_text = new JTextField("xxx.xxx.xxx.xxx");//{@Override public void setBorder(Border border) {}};
		Border newBorder = BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(78,190,255));
		address_text.setBorder(newBorder);
		address_text.setBorder(BorderFactory.createCompoundBorder(address_text.getBorder(), BorderFactory.createEmptyBorder(0, 4, 0, 0)));
		address_text.setBounds(new Rectangle(new Point(0,0), address_text.getPreferredSize()));
		address_text.setSize(address_text.getWidth()*2, address_text.getHeight()*2);
		address_text.setLocation((int)(this.getSize().width/2 - address_text.getWidth())-5, (int)(this.getSize().height/2)-(int)(this.getSize().height*0.1)+offset);
		address_text.addActionListener(this);
		address_text.addMouseListener(this);
		address_text.setVisible(true);
		address_text.setEnabled(true);
		
		button_connect = setButton("Connect", 0, 0, "img/connect.png", true);
		button_connect.setSize(button_connect.getWidth(), address_text.getHeight());
		button_connect.setLocation((int)(this.getSize().width/2)+5, (int)((this.getSize().height/2)-(this.getSize().height*0.1)+offset));
		
		button_exit = setButton("Exit", 0, 0, "img/exit.png", true);
		button_exit.setLocation((int)((this.getSize().width/2)-(button_exit.getSize().width/2)), (int)((this.getSize().height/2)-(this.getSize().height*0.0)+offset*8));
		button_exit.setVisible(true);
		button_exit.setEnabled(true);
		
		button_back = setButton("Back", 0, 0, "img/back.png", false);
		button_back.setLocation((int)((this.getSize().width/2)-(button_back.getSize().width/2)), (int)((this.getSize().height/2)-(this.getSize().height*0.0)+offset*5));
		button_back.setVisible(false);
		button_back.setEnabled(false);
		
		player_label = new JLabel("Insert your nickname");
		player_label.setForeground(new Color(78,190,255));
		player_label.setFont(new Font("Verdana", Font.PLAIN, font_size_default));
		player_label.setBounds(new Rectangle(new Point(0,0), player_label.getPreferredSize()));
		player_label.setLocation((int)((this.getSize().width/2)-(player_label.getWidth()/2)), (int)((this.getSize().height/2)-(this.getSize().height*0.2)));
		player_label.setVisible(true);
		player_label.setEnabled(true);
		
		player_name = new JTextField("Player"+new Random().nextInt());//{@Override public void setBorder(Border border) {}};
		player_name.setBorder(newBorder);
		player_name.setBorder(BorderFactory.createCompoundBorder(player_name.getBorder(), BorderFactory.createEmptyBorder(0, 4, 0, 0)));
		player_name.setBounds(new Rectangle(new Point(0,0), player_name.getPreferredSize()));
		player_name.setSize(player_name.getWidth()*2, player_name.getHeight()*2);
		player_name.setLocation((int)(this.getSize().width/2) - (int)((player_name.getWidth()/2)), (int)((this.getSize().height/2)-(this.getSize().height*0.2)+offset));
		player_name.addActionListener(this);
		player_name.setVisible(true);
		player_name.setEnabled(true);
		
		button_start_host = setButton("Host", 0, 0, "img/host.png", true);
		button_start_host.setLocation((int)((this.getSize().width/2)-(button_start_host.getSize().width/2)), (int)(this.getSize().height/2)-(int)(this.getSize().height*0.0)+offset*2);
		button_start_host.setVisible(true);
		button_start_host.setEnabled(true);

		this.add(client_label);
		this.add(address_text);
		this.add(button_connect);
		this.add(button_exit);
		this.add(button_back);
		this.add(player_label);
		this.add(player_name);
		this.add(button_start_host);
		this.add(msg_label);
		this.add(ip_label);
                up = true;
		repaint();
	}
	
	public void host(){
		button_start_host.setVisible(false);
		button_start_host.setEnabled(false);
		player_label.setVisible(false);
		player_label.setEnabled(false);
		player_name.setVisible(false);
		player_name.setEnabled(false);
		button_connect.setVisible(false);
		button_connect.setEnabled(false);
		client_label.setVisible(false);
		client_label.setEnabled(false);
		address_text.setVisible(false);
		address_text.setEnabled(false);
		button_back.setVisible(true);
		button_back.setEnabled(true);
		wheel = true;
		msg_label.setText("Waiting for peers.. " + gs.getNumPlayers() + "/" + GameState.MAX_N_PLAYERS);
		msg_label.setBounds(new Rectangle(new Point(0, 0), msg_label.getPreferredSize()));
		msg_label.setLocation((int) (this.getWidth() / 2) - (int) (msg_label.getWidth() / 2),
				(int) (this.getHeight() / 2) - (int) (this.getHeight() * 0.2));
		msg_label.setForeground(new Color(78,190,255));
		msg_label.setFont(new Font("Verdana", Font.PLAIN, font_size_default));
		msg_label.setVisible(true);
		msg_label.setEnabled(true);
		t.start();
	}
	
	public void connect(){
		button_start_host.setVisible(false);
		player_label.setVisible(false);
		player_name.setVisible(false);
		button_connect.setVisible(false);
		client_label.setVisible(false);
		address_text.setVisible(false);
		button_back.setVisible(true);
		button_back.setEnabled(true);
		wheel = true;
		msg_label.setText("Connecting to " + address_text.getText());
		msg_label.setBounds(new Rectangle(new Point(0, 0), msg_label.getPreferredSize()));
		msg_label.setLocation((int) (this.getWidth() / 2) - (int) (msg_label.getWidth() / 2),
				(int) (this.getHeight() / 2) - (int) (this.getHeight() * 0.2));
		msg_label.setForeground(Color.WHITE);
		msg_label.setVisible(true);
		msg_label.setEnabled(true);
		t.start();
	}
	
	public String getNickname(){
		return player_name.getText();
	}
	
	private void drawTheWheel(Graphics2D g2){
		double img_x = img_wheel.getWidth() / 2;
		double img_y = img_wheel.getHeight() / 2;
		AffineTransform tx = AffineTransform.getRotateInstance(deg, img_x, img_y);
		AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
		float c = 0.3f;
		int w = img_wheel.getWidth();
		int h = img_wheel.getHeight();
		g2.drawImage(op.filter(img_wheel, null), (int)(this.getWidth()/2-(w*c/2)), (int)(this.getHeight()/2-(h*c)/2), (int)(w*c), (int)(h*c),this);
	}
	
	public synchronized void setMsg(String txt, Color text_color){
		JOptionPane.showMessageDialog(null, txt);
	}
	
	private JButton setButton(String name, int x, int y, String icon_file_path, boolean visible){
		JButton b = new JButton(name);
		b.setBounds(new Rectangle(new Point(0,0), b.getPreferredSize()));
		b.setBounds(0, 0, (int)(b.getWidth()*2), b.getHeight()*2);
		b.setLocation(x, y);
		b.setForeground(Color.BLACK);
		b.setBackground(Color.WHITE);
		b.addActionListener(this);
		Border line = new LineBorder(new Color(78,190,255), 2);
		Border margin = new EmptyBorder(5, 15, 5, 15);
		Border compound = new CompoundBorder(line, margin);
		b.setFocusPainted(false);
		b.setBorder(compound);
		b.setFont(new Font("Verdana", Font.PLAIN, (int)(font_size_default*0.6)));
		b.addMouseListener(this);
		b.setVisible(visible);
		return b;
	}
	
	public void refreshJoiningPlayers(){
		msg_label.setText("Waiting for peers.. " + gs.getNumPlayers() + "/" + GameState.MAX_N_PLAYERS);
		msg_label.setForeground(new Color(78,190,255));
		msg_label.setFont(new Font("Verdana", Font.PLAIN, font_size_default));
		msg_label.setBounds(new Rectangle(new Point(0,0), msg_label.getPreferredSize()));
		msg_label.setLocation((int)(this.getWidth()/2)-(int)(msg_label.getWidth()/2), (int)(this.getHeight()/2)-(int)(this.getHeight()*0.2));
		msg_label.setVisible(true);
	}

	public String getHostIP() {
		return address_text.getText();
	}

	public void backToHome() {
		client_label.setVisible(true);
		client_label.setEnabled(true);
		button_exit.setVisible(true);
		button_exit.setEnabled(true);
		address_text.setVisible(true);
		address_text.setEnabled(true);
		button_connect.setVisible(true);
		button_connect.setEnabled(true);
		button_start_host.setVisible(true);	
		button_start_host.setEnabled(true);	
		player_label.setVisible(true);
		player_label.setEnabled(true);
		player_name.setVisible(true);
		player_name.setEnabled(true);
		msg_label.setVisible(false);
		msg_label.setEnabled(false);
		button_back.setVisible(false);
		button_back.setEnabled(false);
		repaint();
	}
	
	public void back(){
		synchronized (gs) {
			gs.setState(GameState.STATE_MENU);
			button_back.setVisible(false);
			button_back.setEnabled(false);
			wheel = false;
			t.stop();
			gs.abort();
			backToHome();
		}
	}

	public void stopAnimation(){
		wheel = false;
		t.stop();
	}
        
    public boolean isUp(){
            return up;
    }

    @Override
	protected void paintComponent(Graphics g){
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.drawImage(img_background, 0, 0, this.getWidth(), this.getHeight(), this);
		if(wheel){
			drawTheWheel(g2);
		}
	}
    
	@Override
	public void actionPerformed(ActionEvent arg0) {
		if(wheel){	// rotate the wheel
			deg+=0.1;
			if(deg>=360) deg = 0;
		}
		if(arg0.getSource()==button_connect){
			synchronized(gs.getEnvironment()){
				this.connect();
				gs.setState(GameState.STATE_CONNECT);
				gs.getEnvironment().notify();
			}
		}else if(arg0.getSource()==button_start_host){
			synchronized(gs.getEnvironment()){
				gs.setState(GameState.STATE_HOST);
				gs.getEnvironment().notify();
			}
		}else if(arg0.getSource() == button_back){
			back();
		}else if(arg0.getSource()==button_exit){
			if(TheDistributedScrabble.DEBUG)System.out.println("Exiting from the game");
			gs.exitGame();
		}
		repaint();
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {

	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		if(arg0.getSource() instanceof JButton){
			JButton b = (JButton) arg0.getSource();
			if(b.isVisible())
				b.setBackground(new Color(208,238,255));
		}

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		if(arg0.getSource() instanceof JButton){
			JButton b = (JButton) arg0.getSource();
			if(b.isVisible())
				b.setBackground(Color.WHITE);
		}
	}

	@Override
	public void mousePressed(MouseEvent arg0) {

	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		if(arg0.getSource() == address_text && address_text.getText().equalsIgnoreCase("Insert your Nickname")){
			address_text.setText("");			
		}

	}

}
