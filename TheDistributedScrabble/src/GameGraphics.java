import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;

public abstract class GameGraphics extends JPanel implements MouseListener, ActionListener {
	private static final long serialVersionUID = 1633661334079158190L;
	GameState gs;

	public GameGraphics(Dimension d, GameState gs) {
		super();
		this.gs = gs;
		this.setSize(d);
		this.setDoubleBuffered(true);
		this.setBorder(null);
		this.setLayout(null);
		this.addMouseListener(this);
	}
	
	@Override
	public abstract void actionPerformed(ActionEvent arg0);
	
	@Override
	public abstract void mouseClicked(MouseEvent arg0);

	@Override
	public abstract void mouseEntered(MouseEvent arg0);

	@Override
	public abstract void mouseExited(MouseEvent arg0);

	@Override
	public abstract void mousePressed(MouseEvent arg0);

	@Override
	public abstract void mouseReleased(MouseEvent arg0);

}
