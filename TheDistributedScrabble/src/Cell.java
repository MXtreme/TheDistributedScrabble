

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;

public class Cell extends JButton implements ActionListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4074260117714327091L;
	static final int DOUBLE_LETTER = 0;
	static final int DOUBLE_WORD = 1;
	static final int TRIPLE_LETTER = 2;
	static final int TRIPLE_WORD= 3;
	static final int NORMAL= 4;
	
	private int type;
	ImageIcon i;
	Letter letter;
	private boolean selected;
	private boolean used;
	private char c;
	
	public Cell(String label, int type, int f, ImageIcon i) {
		super();
		this.type = type;
		this.setFont(new Font("Verdana", Font.BOLD, f));
		this.i = i;
		this.selected = false;
		this.used = false;
		deselect();
	}
	
	public void deselect(){
		switch (type) {
		case 0:
			this.setBackground(Color.YELLOW);
			this.setForeground(Color.GREEN);
			break;
		case 1:
			this.setBackground(Color.BLUE);
			this.setForeground(Color.WHITE);
			break;
		case 2:
			this.setBackground(Color.MAGENTA);
			this.setForeground(Color.BLUE);
			break;
		case 3:
			this.setBackground(Color.WHITE);
			this.setForeground(Color.RED);
			break;
		case 4:
			this.setBackground(new Color(255, 215, 140));
			break;
		}
		selected = false;
		c = '\0';
		this.setIcon(i);
	}
	
	public void deselectFromTable(){
		switch (type) {
		case 0:
			this.setBackground(Color.YELLOW);
			this.setForeground(Color.GREEN);
			break;
		case 1:
			this.setBackground(Color.BLUE);
			this.setForeground(Color.WHITE);
			break;
		case 2:
			this.setBackground(Color.MAGENTA);
			this.setForeground(Color.BLUE);
			break;
		case 3:
			this.setBackground(Color.WHITE);
			this.setForeground(Color.RED);
			break;
		case 4:
			this.setBackground(new Color(255, 215, 140));
			break;
		}
		selected = false;
		this.setIcon(i);
	}
	
	public void select(char c){
		this.c = c;
		this.selected = true;
	}
	
	public boolean isSelected(){
		return this.selected;
	}
	
	public void setC(char c){
		this.c  = c;
	}
	
	public char getC(){
		return this.c;
	}
	
	public void setTheIcon(ImageIcon i){
		this.i = i;
		this.setIcon(i);
	}
	
	public void setType(int t, ImageIcon i){
		type = t;
		this.i = i;
		deselect();
	}
	
	public int getType(){
		return this.type;
	}
	
	public void setLetter(Letter l){
		this.letter = l;
	}
	
	public Letter getLetter(){
		return this.letter;
	}
	
	public void setUsed(){
		this.used = true;
	}
	
	public void setUnused(){
		this.used = false;
	}
	
	public boolean isUsed(){
		return this.used;
	}
	
	public int getPoints(int score){
		switch(type){
		case DOUBLE_LETTER:
			return score *= 2;
		case TRIPLE_LETTER: 
			return score *= 3;
		default: return score;
		}
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		System.out.println("Cell type: " + type);
		System.out.println("Cell char: " + c);
		System.out.println("Cell is used: "+ used);
		System.out.println("Cell is selected: " + selected);
		System.out.println("");
		
	}
}

