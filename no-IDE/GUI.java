

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;

public class GUI extends JFrame {
	private static final long serialVersionUID = 5593950695513464355L;
	private GameState gs;
	private HomeGraphics hg;
	private InGameGraphics igg;
	
	public GUI(String title, GameState gs) {
		super(title);
		this.gs = gs;
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension s;
		if(d.getWidth()>d.getHeight()){
			s = new Dimension((int)(d.height/1.1), (int)(d.height/1.1));
		}
		else{
			s = new Dimension((int)(d.height/1.1), (int)(d.height/1.1));
		}
		setMinimumSize(s);
		this.pack();
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}
	
	public void switchToHome(){
		if(TheDistributedScrabble.DEBUG)System.out.println("Switching to the homescreen Graphics");
		hg = new HomeGraphics(this.getSize(), gs);
		hg.menu();
		if(igg!=null){
			this.remove(igg);
			igg = null;
		}
		this.add(hg);
		this.revalidate();
		this.repaint();
		System.gc();
	}
	
	public void switchToGame(){
		if(TheDistributedScrabble.DEBUG)System.out.println("Switching to the ingame Graphics");
		igg = new InGameGraphics(this.getSize(), gs);
		igg.initGUI();
		if(hg!=null){
			this.remove(hg);
			hg = null;
		}
		this.add(igg);
		this.revalidate();
		this.repaint();
		System.gc();
	}
	
	public HomeGraphics getHome(){
		return this.hg;
	}
	
	public InGameGraphics getInGameGraphics(){
		return this.igg;
	}
}
