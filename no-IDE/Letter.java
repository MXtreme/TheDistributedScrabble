

import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JButton;

public class Letter extends JButton {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7005948919788615922L;
	private char c;
	private Image unselected, selected;
	private boolean flag = false;
	private int points;
	
	
	public Letter(){
		super();
	}
	
	/*
	 * Function to get the point associated to a letter
	 * 1 punto per ogni A, C, E, I, O, R, S, T; 2 punti per L, M, N; 3 per P; 4 per B, D, F, G, U, V; 8 punti per H e Z; 10 per Q
	 * */
	public static int points(char c){
		int val = 0;
		if(c == 'A' || c == 'a' || c == 'C' || c == 'c' || c == 'E' || c == 'e' || c == 'I' || c == 'i' || c == 'O' || c == 'o' || c == 'R' || c == 'r' || c == 'S' || c == 's' || c == 'T' || c == 't')
			val = 1;
		else if(c == 'L' || c == 'l' || c == 'M' || c == 'm' || c == 'N' || c == 'n') 
			val = 2;
		else if(c == 'P' || c == 'p')
			val = 3;
		else if(c == 'B' || c == 'b' || c == 'D' || c == 'd' || c == 'F' || c == 'f' || c == 'G' || c == 'g' || c == 'U' || c == 'u' || c == 'V' || c == 'v')
			val = 4;
		else if(c == 'H' || c == 'h' || c == 'Z' || c == 'z')
				val = 8;
		else if(c == 'Q' || c == 'q')
			val = 10;
		else if(c == '*') val = 0;
		return val;
	}
	
	public Letter(char c, Image unselected, Image selected){
		super();
		this.c = c;
		this.unselected = unselected;
		this.selected = selected;
	}
	
	public void select(){
		this.setIcon(new ImageIcon(selected));
		flag = true;
	}
	
	public void unselect(){
		this.setIcon(new ImageIcon(unselected));
		flag = false;
	}
	
	public void setUnselectedImage(Image i){
		this.unselected = i;
	}
	
	public void setSelectedImage(Image i){
		this.selected = i;
	}
	
	public void setC(char c){
		this.c = c;
	}
	
	public char getC(){
		return this.c;
	}
	
	public boolean isSelected(){
		return this.flag;
	}
	
	public void reset(){
		this.c = '\0';
		this.unselected = null;
		this.selected = null;
		this.setIcon(null);
		flag = false;
	}
	
	public void setPoints(int p){
		this.points = p;
	}
	
	public int getPoints(){
		return this.points;
	}
	
}

