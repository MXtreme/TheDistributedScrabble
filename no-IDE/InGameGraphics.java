import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

public class InGameGraphics extends GameGraphics {
	private static final long serialVersionUID = -2805714521564914502L;
	private final int font_size;
	private Image img[], imgs[], mult[];
	private JButton button_exit, button_pass, button_reset;
	private JLabel label_playing_player, label_title, label_p[], label_score[];
	private JPanel panel_top, panel_left, panel_bottom, panel_right, panel_hand;
	private Cell table[][];
	private Letter label_letters[], holder;
	private Vector<Cell> anchors;
	private boolean hold = false, row = false, col = false;
	private int anchor1_x, anchor1_y, anchor2_x, anchor2_y, tmp_score, j, dw, tw;

	public InGameGraphics(Dimension d, GameState gs) {
		super(d, gs);
		imgs = new Image[22];
		img = new Image[22];
		mult = new Image[5];
		anchors = new Vector<Cell>();
		loadImages();	
		font_size = (int)Math.abs(10 * (d.getWidth()/d.getHeight()));
		table = new Cell[17][17];		
		this.addMouseListener(this);
	}
	
	private void loadImages(){
		try {
			int i=0;
			for(char c='a';c<='z';c++){
				if(c!='j' && c!='k' && c!='w' && c!='x' && c!='y'){
					img[i] = ImageIO.read(new File("img/"+c+".jpg"));
					imgs[i] = ImageIO.read(new File("img/"+c+"s.jpg"));
					i++;
				}
			}
			img[21] = ImageIO.read(new File("img/scrabble.jpg"));
			imgs[21] = ImageIO.read(new File("img/scrabbles.jpg"));
			mult[0] = ImageIO.read(new File("img/2l.jpg"));
			mult[1] = ImageIO.read(new File("img/2w.jpg"));
			mult[2] = ImageIO.read(new File("img/3l.jpg"));
			mult[3] = ImageIO.read(new File("img/3w.jpg"));
			mult[4] = ImageIO.read(new File("img/blank.jpg"));
		} catch (IOException e) {
			System.out.println("Error on loading the images. :(");
			e.printStackTrace();
		}
	}
	
	public void initGUI(){
		setTopPanel();
		setLeftPanel();
		setRightPanel();
		setBottomPanel();
		this.setVisible(true);
	}
	
	private void setRightPanel(){
		panel_right = new JPanel();
		panel_right.setLayout(new GridLayout(17, 17,0 ,0));
		panel_right.setSize((int)(this.getWidth()*0.7), (int)(this.getHeight()*0.7));
		panel_right.setLocation((int)(this.getWidth()*0.3), (int)(this.getHeight()*0.1));
		panel_right.setBorder(BorderFactory.createEmptyBorder());
		panel_right.setBackground(Color.WHITE);
		for(int i=0;i<table.length;i++){
			for(int j=0;j<table[i].length;j++){
				Cell b = new Cell("",Cell.NORMAL, font_size, new ImageIcon(mult[4]));
				b.setLayout(new BorderLayout());
				b.setBorder(BorderFactory.createEmptyBorder());
				b.setHorizontalAlignment(Cell.CENTER);
                b.setBackground(new Color(255, 215, 140));
                b.setSize((int)(panel_right.getSize().getWidth()/17), (int)(panel_right.getSize().getHeight()/17));
                b.addMouseListener(this);
                table[i][j] = b;
                panel_right.add(b);
			}
		}
		set3P();
		set2P();
		set3L();
		set2L();
		setJolly();
		this.add(panel_right);
	}
	
	private void setBottomPanel(){
		panel_bottom = new JPanel(null);
		panel_bottom.setSize(this.getWidth(), (int)(this.getHeight()*0.2));
		panel_bottom.setLocation(0, (int)(this.getHeight()*0.8));
		panel_bottom.setLayout(null);
		panel_bottom.setBackground(Color.WHITE);
		
		panel_hand = new JPanel();
		panel_hand.setLayout(null);
		panel_hand.setSize((int)(panel_bottom.getWidth()*0.7), (int)(panel_bottom.getHeight()));
		panel_hand.setLocation((int)(panel_bottom.getWidth()*0.05), (int)(panel_bottom.getHeight()/2 - panel_hand.getHeight()/2));
		panel_hand.setBackground(Color.WHITE);
		
		label_letters = new Letter[8];
		Border line = new LineBorder(new Color(78,190,255), 1);
		for(int i=0;i<8;i++){
			label_letters[i] = new Letter();
			label_letters[i].setBackground(Color.WHITE);
			label_letters[i].setSize((int)(this.getWidth()/15), (int)(this.getWidth()/15));
			label_letters[i].setLocation((int)(this.getWidth()*0.1*i + this.getWidth()*0.02), (int)((this.getHeight() - label_letters[i].getHeight()*2.4)));
			label_letters[i].setHorizontalAlignment(Cell.CENTER);
			label_letters[i].addMouseListener(this);
			label_letters[i].setVisible(true);
			this.add(label_letters[i]);
		}
		
		button_pass = new JButton("Pass");
		button_pass.setSize((int)(this.getWidth()*0.10), (int)(this.getWidth()*0.10));
		button_pass.setLocation((int)(this.getWidth()*0.9), (int)((this.getHeight() - button_pass.getHeight()*1.8)));
		button_pass.setBorder(line);
		button_pass.setBackground(Color.WHITE);
		button_pass.addActionListener(this);
		button_pass.addMouseListener(this);
		this.add(button_pass);
		
		button_reset = new JButton("Reset");
		button_reset.setSize((int)(this.getWidth()*0.10), (int)(this.getWidth()*0.10));
		button_reset.setLocation((int)(this.getWidth()*0.8), (int)((this.getHeight() - button_reset.getHeight()*1.8)));
		button_reset.setBorder(line);
		button_reset.setBackground(Color.WHITE);
		button_reset.addActionListener(this);
		button_reset.addMouseListener(this);
		this.add(button_reset);
	}
	
	private void setLeftPanel(){
		Font f = new Font("Verdana", Font.BOLD, font_size);
		panel_left = new JPanel(new GridLayout(GameState.MAX_N_PLAYERS, 1));
		panel_left.setSize((int)(this.getWidth()*0.3), (int)(this.getHeight()*0.7));
		panel_left.setLocation(0, (int)(this.getHeight()*0.1));
		panel_left.setLayout(null);
		panel_left.setBackground(Color.WHITE);
		
		label_p = new JLabel[GameState.MAX_N_PLAYERS];
		label_score = new JLabel[GameState.MAX_N_PLAYERS];
		for(int i=0;i<GameState.MAX_N_PLAYERS;i++){
			label_p[i] = new JLabel("Player name");
			label_score[i] = new JLabel("0");
			label_p[i].setFont(f);
			label_score[i].setFont(f);
			label_p[i].setHorizontalAlignment(JLabel.CENTER);
			label_score[i].setHorizontalAlignment(JLabel.CENTER);
			label_p[i].setBounds(new Rectangle(new Point(0,0), label_p[i].getPreferredSize()));
			label_score[i].setBounds(new Rectangle(new Point(0,0), label_score[i].getPreferredSize()));
			label_p[i].setSize((int)(this.getWidth()*0.3), (int)((this.getHeight()*0.7)/8));
			label_score[i].setSize((int)(this.getWidth()*0.3), (int)((this.getHeight()*0.7)/8));
			label_p[i].setLocation(0, (int)(this.getHeight()*0.1) + (label_p[i].getHeight()*2*i));
			label_score[i].setLocation(0, (int)(this.getHeight()*0.1) + (label_score[i].getHeight()*2*i) + label_score[i].getHeight());
			label_p[i].setForeground(new Color(78,190,255));
			label_score[i].setForeground(new Color(78,190,255));
			label_p[i].setBackground(Color.WHITE);
			label_score[i].setBackground(Color.WHITE);
			label_p[i].setOpaque(true);
			label_score[i].setOpaque(true);
			label_p[i].addMouseListener(this);
			label_score[i].addMouseListener(this);
			this.add(label_p[i]);
			this.add(label_score[i]);
		}
	}
	
	private void setTopPanel(){
		Font f = new Font("Verdana", Font.BOLD, font_size);
		panel_top = new JPanel();
		panel_top.setLayout(null);
		panel_top.setSize(this.getWidth(), (int)(this.getHeight()*0.1));
		panel_top.setLocation(0, 0);
		button_exit = new JButton("Exit");
		button_exit.setFont(f);
		button_exit.setSize((int)(this.getWidth()/4), (int)(this.getHeight()*0.1));
		button_exit.setLocation(0, 0);
		button_exit.setForeground(new Color(78,190,255));
		button_exit.setBackground(Color.WHITE);
		button_exit.setBorder(BorderFactory.createEmptyBorder());
		button_exit.addActionListener(this);
		button_exit.addMouseListener(this);
		label_playing_player = new JLabel("Player name");
		label_playing_player.setFont(f);
		label_playing_player.setHorizontalAlignment(JLabel.CENTER);
		label_playing_player.setSize((int)(this.getWidth()/4), (int)(this.getHeight()*0.1));
		label_playing_player.setLocation(this.getWidth()-label_playing_player.getWidth(), 0);
		label_playing_player.setBackground(Color.WHITE);
		label_playing_player.setForeground(new Color(78,190,255));
		label_playing_player.setOpaque(true);
		label_title = new JLabel("DISTRIBUITED SCRABBLE");
		f = new Font("Verdana", Font.BOLD, (int) (Math.abs(30* (this.getWidth()/this.getHeight()))));
		label_title.setFont(f);
		label_title.setHorizontalAlignment(JLabel.CENTER);
		label_title.setSize((int)(this.getWidth()/2), (int)(this.getHeight()*0.1));
		label_title.setLocation(this.getWidth()/4, 0);
		label_title.setBackground(Color.WHITE);
		label_title.setForeground(new Color(78,190,255));
		label_title.setOpaque(true);
		this.add(button_exit);
		this.add(label_title);
		this.add(label_playing_player);
		this.setVisible(true);
	}
	
	private void set3P(){
		mult[3] = mult[3].getScaledInstance((int)(table[0][0].getWidth()), (int)(table[0][0].getHeight()), java.awt.Image.SCALE_SMOOTH);
		table[0][0].setType(Cell.TRIPLE_WORD, new ImageIcon(mult[3]));
		table[0][8].setType(Cell.TRIPLE_WORD, new ImageIcon(mult[3]));
		table[0][16].setType(Cell.TRIPLE_WORD, new ImageIcon(mult[3]));
		table[8][0].setType(Cell.TRIPLE_WORD, new ImageIcon(mult[3]));
		table[8][16].setType(Cell.TRIPLE_WORD, new ImageIcon(mult[3]));
		table[16][0].setType(Cell.TRIPLE_WORD, new ImageIcon(mult[3]));
		table[16][8].setType(Cell.TRIPLE_WORD, new ImageIcon(mult[3]));
		table[16][16].setType(Cell.TRIPLE_WORD, new ImageIcon(mult[3]));
	}
	
	private void set2L(){
		mult[0] = mult[0].getScaledInstance((int)(table[0][0].getWidth()), (int)(table[0][0].getHeight()), java.awt.Image.SCALE_SMOOTH);
		table[0][4].setType(Cell.DOUBLE_LETTER, new ImageIcon(mult[0]));
		table[0][12].setType(Cell.DOUBLE_LETTER, new ImageIcon(mult[0]));
		table[2][7].setType(Cell.DOUBLE_LETTER, new ImageIcon(mult[0]));
		table[2][9].setType(Cell.DOUBLE_LETTER, new ImageIcon(mult[0]));
		table[3][8].setType(Cell.DOUBLE_LETTER, new ImageIcon(mult[0]));
		table[4][0].setType(Cell.DOUBLE_LETTER, new ImageIcon(mult[0]));
		table[4][16].setType(Cell.DOUBLE_LETTER, new ImageIcon(mult[0]));
		table[7][2].setType(Cell.DOUBLE_LETTER, new ImageIcon(mult[0]));
		table[7][7].setType(Cell.DOUBLE_LETTER, new ImageIcon(mult[0]));
		table[7][9].setType(Cell.DOUBLE_LETTER, new ImageIcon(mult[0]));
		table[7][14].setType(Cell.DOUBLE_LETTER, new ImageIcon(mult[0]));
		table[8][3].setType(Cell.DOUBLE_LETTER, new ImageIcon(mult[0]));
		table[8][13].setType(Cell.DOUBLE_LETTER, new ImageIcon(mult[0]));
		table[9][2].setType(Cell.DOUBLE_LETTER, new ImageIcon(mult[0]));
		table[9][7].setType(Cell.DOUBLE_LETTER, new ImageIcon(mult[0]));
		table[9][9].setType(Cell.DOUBLE_LETTER, new ImageIcon(mult[0]));
		table[9][14].setType(Cell.DOUBLE_LETTER, new ImageIcon(mult[0]));
		table[12][0].setType(Cell.DOUBLE_LETTER, new ImageIcon(mult[0]));
		table[12][16].setType(Cell.DOUBLE_LETTER, new ImageIcon(mult[0]));
		table[13][8].setType(Cell.DOUBLE_LETTER, new ImageIcon(mult[0]));
		table[14][7].setType(Cell.DOUBLE_LETTER, new ImageIcon(mult[0]));
		table[14][9].setType(Cell.DOUBLE_LETTER, new ImageIcon(mult[0]));
		table[16][4].setType(Cell.DOUBLE_LETTER, new ImageIcon(mult[0]));
		table[16][12].setType(Cell.DOUBLE_LETTER, new ImageIcon(mult[0]));
	}
	
	private void set3L(){
		mult[2] = mult[2].getScaledInstance((int)(table[0][0].getWidth()), (int)(table[0][0].getHeight()), java.awt.Image.SCALE_SMOOTH);
		table[1][6].setType(Cell.TRIPLE_LETTER, new ImageIcon(mult[2]));
		table[1][10].setType(Cell.TRIPLE_LETTER, new ImageIcon(mult[2]));
		table[6][1].setType(Cell.TRIPLE_LETTER, new ImageIcon(mult[2]));
		table[6][6].setType(Cell.TRIPLE_LETTER, new ImageIcon(mult[2]));
		table[6][10].setType(Cell.TRIPLE_LETTER, new ImageIcon(mult[2]));
		table[6][15].setType(Cell.TRIPLE_LETTER, new ImageIcon(mult[2]));
		table[10][1].setType(Cell.TRIPLE_LETTER, new ImageIcon(mult[2]));
		table[10][6].setType(Cell.TRIPLE_LETTER, new ImageIcon(mult[2]));
		table[10][10].setType(Cell.TRIPLE_LETTER, new ImageIcon(mult[2]));
		table[10][15].setType(Cell.TRIPLE_LETTER, new ImageIcon(mult[2]));
		table[15][6].setType(Cell.TRIPLE_LETTER, new ImageIcon(mult[2]));
		table[15][10].setType(Cell.TRIPLE_LETTER, new ImageIcon(mult[2]));
	}
	
	private void set2P(){
		mult[1] = mult[1].getScaledInstance((int)(table[0][0].getWidth()), (int)(table[0][0].getHeight()), java.awt.Image.SCALE_SMOOTH);
		for(int i=0;i<table.length;i++){
			for(int j=0;j<table[i].length;j++){
				if(i>=1 & i<=5 && i==j){
					table[i][j].setType(Cell.DOUBLE_WORD, new ImageIcon(mult[1]));
					table[i][16-j].setType(Cell.DOUBLE_WORD, new ImageIcon(mult[1]));
				}else if(i>=10 & i<=15 && i==j){
					table[i][j].setType(Cell.DOUBLE_WORD, new ImageIcon(mult[1]));
					table[i][16-j].setType(Cell.DOUBLE_WORD, new ImageIcon(mult[1]));
				}else continue;
			}
		}
	}
	
	private void setJolly(){
		img[21] = img[21].getScaledInstance((int)(table[0][0].getWidth()), (int)(table[0][0].getHeight()), java.awt.Image.SCALE_SMOOTH);
		table[8][8].setType(Cell.NORMAL, new ImageIcon(img[21]));
	}
	
	@Override
    public Dimension getMinimumSize() {
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		if(d.getWidth()>d.getHeight()){
			return new Dimension((int)(d.height/1.1), (int)(d.height/1.1));
		}
		else{
			return new Dimension((int)(d.height/1.1), (int)(d.height/1.1));
		}
    }

    @Override
    public Dimension getPreferredSize() {
    	Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		if(d.getWidth()>d.getHeight()){
			return new Dimension((int)(d.height/1.1), (int)(d.height/1.1));
		}
		else{
			return new Dimension((int)(d.height/1.1), (int)(d.height/1.1));
		}
    }

    @Override
    public Dimension getMaximumSize() {
    	Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		if(d.getWidth()>d.getHeight()){
			return new Dimension((int)(d.height/1.1), (int)(d.height/1.1));
		}
		else{
			return new Dimension((int)(d.height/1.1), (int)(d.height/1.1));
		}
    }
	
	@Override
	public void paint(Graphics g){
		super.paint(g);
		Graphics2D g2 = (Graphics2D)g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	}
	
	private void deselectUsedLetters(boolean row, int anchor1_x, int anchor2_x, int anchor1_y, int anchor2_y) {
		System.out.println("Deselecting things");
		if(row){
			for(int i=anchor1_x;i<=anchor2_x;i++){
				table[anchor2_y][i].deselectFromTable();
				if(TheDistributedScrabble.DEBUG)System.out.println("deselecting " + table[anchor2_y][i].getC());
			}
		}else{
			for(int i=anchor1_y;i<=anchor2_y;i++){
				table[i][anchor2_x].deselectFromTable();
				if(TheDistributedScrabble.DEBUG)System.out.println("deselecting " + table[i][anchor2_x].getC());
			}
		}
		
	}

	public void refreshLetters(String letters[]){
		Image tmp;
		for(int i=0; i<letters.length;i++){
			if(TheDistributedScrabble.DEBUG){
				if(label_letters[i]==null)System.out.println("labels letter di i " + label_letters[i]==null);
				if(letters[i]==null)System.out.println("letters[i] e' null");
				if(letters[i]!=null)System.out.println("letters[i].charAt " + letters[i].charAt(0));
			}
			label_letters[i].setC(letters[i].charAt(0));
			if(letters[i].charAt(0) == '*'){
				tmp = img[21].getScaledInstance(label_letters[i].getWidth(), label_letters[i].getHeight(), java.awt.Image.SCALE_SMOOTH);
				label_letters[i].setUnselectedImage(tmp);
				tmp = imgs[21].getScaledInstance(label_letters[i].getWidth(), label_letters[i].getHeight(), java.awt.Image.SCALE_SMOOTH);
				label_letters[i].setSelectedImage(tmp);
			}
			else {
				tmp = img[getLetterIndex(letters[i].charAt(0))].getScaledInstance(label_letters[i].getWidth(), label_letters[i].getHeight(), java.awt.Image.SCALE_SMOOTH);
				label_letters[i].setUnselectedImage(tmp);
				tmp = imgs[getLetterIndex(letters[i].charAt(0))].getScaledInstance(label_letters[i].getWidth(), label_letters[i].getHeight(), java.awt.Image.SCALE_SMOOTH);
				label_letters[i].setSelectedImage(tmp);
			}
			label_letters[i].unselect();
			label_letters[i].setPoints(Letter.points(label_letters[i].getC()));
		}
	}
	
	private int getLetterIndex(char c){
		int i=0;
		char tmp = 'A';
		if(c == '*') return 21;
		while(i<img.length && tmp<='Z'){
			if(tmp!='J' && tmp!='K' && tmp!='W' && tmp!='X' && tmp!='Y'){
				if(tmp == c) return i;
				i++;
			}
			tmp++;
		}
		return -1;
	}
	
	private void rowOrColumn(int x, int y, Cell c){
		for(int i=0;i<table.length;i++){
			if(i!=x)row = (table[y][i].isSelected()) ? true : false;
			if(row){
				System.out.println("row at " + i);
				break;
			}
		}
		if(!row)
			for(int i=0;i<table.length;i++){
				if(i!=y) col = (table[i][x].isSelected()) ? true : false;
				if(col){
					System.out.println("col at " + i);
					break;
				}
			}
		else col = false;
		if(row == col){
			System.err.println("Move is not a row or column or it's a single tile move.");
		}
	}
	
	/*
	 * 
	 * This function is needed in order to compute anchors.
	 * 
	 * - When booleans row and col are equal means that that the player is
	 * setting the first letter and we try to discover anchors in bot directions;
	 * 
	 * - If booleans row and col are not equal we try to discover anchors on the
	 * direction of the boolean set to true; 
	 * 
	 * 
	 * */
	public void recomputeAnchors2(int x, int y, Cell c){
		cleanAnchors();
		if(row == col){
			searchHorizontalAnchors(x, y);
			searchVerticalAnchors(x, y);
		}else{
			if(row)
				searchHorizontalAnchors(x, y);
			else if(col)
				searchVerticalAnchors(x, y);
			else {
				if(TheDistributedScrabble.DEBUG)System.out.println("Move is not a row or column. Is this this the first tile?");
				searchHorizontalAnchors(x, y);
				searchVerticalAnchors(x, y);
			}
		}
	}
	
	/*
	 * As it is the first letter we can have at least 4 anchors, a couple for each direction
	 * */
	public void recomputeAnchors(int x, int y, Cell c){
		if(row && col){	// we can establish if it's  a row or a column word
			for(int i=0;i<table.length;i++){
				if(i!=x)row = (table[y][i].isSelected()) ? true : false;
				if(row){
					if(TheDistributedScrabble.DEBUG)System.out.println("row at " + i);
					break;
				}
			}
			if(!row)
				for(int i=0;i<table.length;i++){
					if(i!=y) col = (table[i][x].isSelected()) ? true : false;
					if(col){
						if(TheDistributedScrabble.DEBUG)System.out.println("col at " + i);
						break;
					}
				}
			else col = false;
			if(row == col){
				if(TheDistributedScrabble.DEBUG)System.err.println("Move is (not) a row or column. This is weird and shouldn't happen.");
			}
			anchor1_x = x;
			anchor1_y = y;
			cleanAnchors();
			if(row) searchHorizontalAnchors(x, y);
			else if(col) searchVerticalAnchors(x, y);
		}else{
			cleanAnchors();
			if(x==8 && y == 8){
				searchHorizontalAnchors(x, y);
				searchVerticalAnchors(x, y);
				row = col = true;
			}else{
				if(row)
					searchHorizontalAnchors(x, y);
				else
					searchVerticalAnchors(x, y);
			}
		}
	}
	
	private void searchHorizontalAnchors(int x, int y){
		int i = x;
		while(i>0 && (table[y][i-1].isUsed() || table[y][i-1].isSelected())){	//check the row for an empty tile on the left side
			i--;
		}
		if(i>0 && !table[y][i-1].isUsed() && !table[y][i-1].isSelected() ){
			setAsAnchor(table[y][i-1]);
		}
		
		i = x;
		while(i<table[y].length-1 && (table[y][i+1].isUsed() || table[y][i+1].isSelected())){	//check the row for an empty tile on the right side
			i++;
		}
		if(i<table[y].length-1 && !table[y][i+1].isUsed() && !table[y][i+1].isSelected()){
			setAsAnchor(table[y][i+1]);
		}
	}
	
	private void searchVerticalAnchors(int x, int y){
		int i = y;
		while(i>0 && (table[i-1][x].isUsed() || table[i-1][x].isSelected())){	//check the row for an empty tile on the top side
			i--;
		}
		if(i>0 && !table[i-1][x].isUsed() && !table[i-1][x].isSelected()){
			setAsAnchor(table[i-1][x]);
		}
		
		i = y;
		while(i<table[x].length-1 && (table[i+1][x].isUsed() || table[i+1][x].isSelected())){	//check the row for an empty tile on the bottom side
			i++;
		}
		if(i<table[x].length-1 && !table[i+1][x].isUsed() && !table[i+1][x].isSelected()){
			setAsAnchor(table[i+1][x]);
		}
	}
	
	/*
	 * 
	 * This function discovers anchors at the start of every turn.
	 * It searches in every tile of the board looking at left and up:
	 * 
	 * -If there's a letter in my position set anchors on the left and up if they're empty;
	 * -If there are no letters in my position check if there is one on the left and up and add an anchor
	 * at my position
	 * 
	 * */
	public void computeAnchors(){
		row = col = false;
		cleanAnchors();
		if(!table[8][8].isUsed()){	// if it's the first move of the game there will be only one anchor at the center
			setAsAnchor(table[8][8]);
		}else{	// the first move has already done so we need to check for anchors in the whole board
			for (int i = 0; i < table.length; i++) {
				for (int j = 0; j < table[0].length; j++) {
					if (table[i][j].isUsed()) { // if there's a letter
						if (j > 0 && !table[i][j - 1].isUsed() && !anchors.contains(table[i][j - 1])) { // if  there's nothing on the left and there's no anchor yet
							setAsAnchor(table[i][j - 1]);
						}
						if (i > 0 && !table[i - 1][j].isUsed() && !anchors.contains(table[i - 1][j])) { // if there's nothing up and there's no anchor yet
							setAsAnchor(table[i - 1][j]);
						}
					} else {
						if ((j > 0 && table[i][j - 1].isUsed()) || (i > 0 && table[i - 1][j].isUsed())) { // if there's something on the left or there's something up
							setAsAnchor(table[i][j]);
						}
					}
				}
			}
		}
	}
	
	private void cleanAnchors(){
		if(!anchors.isEmpty()){
			for(Cell c : anchors){
				c.setBorder(BorderFactory.createEmptyBorder());
			}
			anchors = new Vector<Cell>();
			System.gc();
		}
	}
	
	private void deselectAll(){
		for(int i=0;i<table.length;i++){
			for(int j=0;j<table[i].length;j++){
				if(table[i][j].isSelected()){
					table[i][j].deselect();
					table[i][j].setUnused();
				}
			}
		}
		for(int i=0;i<label_letters.length;i++){
			if(label_letters[i].isSelected())label_letters[i].unselect();
		}
	}
	
	private String getWord(){
		if(row)return checkTheRow();
		else if(col)return checkTheCol();
		else{
			String s = checkTheRow();
			int i = tmp_score;
			String q = checkTheCol();
			if(i > tmp_score){
				tmp_score = i;
				System.out.println("anchor1_x: " + anchor1_x + "anchor2_x: " + anchor2_x +"anchor1_y: " + anchor1_y +"anchor2_y: " + anchor2_y);
				return s;
			}
			else {
				System.out.println("anchor1_x: " + anchor1_x + "anchor2_x: " + anchor2_x +"anchor1_y: " + anchor1_y +"anchor2_y: " + anchor2_y);
				return q;
			}
			
		}
	}
	
	private String checkTheCol(){
		String s = "";
		int i = anchor1_y;
		int tmp = anchor1_y;
		anchor2_y = anchor1_y;
		while(i>=0 && (table[i][anchor1_x].isSelected() || table[i][anchor1_x].isUsed())){
			s = table[i][anchor1_x].getC() + s;
			if(!table[i][anchor1_x].isUsed())table[i][anchor1_x].setUsed();
			anchor1_y = i;
			checkMultipliers(table[i][anchor1_x]);
			i--;
		}
		i = tmp + 1;
		while(i<table.length && (table[i][anchor1_x].isSelected() || table[i][anchor1_x].isUsed())){
			s += table[i][anchor1_x].getC();
			if(!table[i][anchor1_x].isUsed())table[i][anchor1_x].setUsed();
			anchor2_y = i;
			checkMultipliers(table[i][anchor1_x]);
			i++;
		}
		anchor2_x = anchor1_x;
		tmp_score += additionalPoints(s, dw, tw, j, tmp_score);
		System.out.println("anchor1_x: " + anchor1_x + " anchor2_x: " + anchor2_x +" anchor1_y: " + anchor1_y +" anchor2_y: " + anchor2_y);
		return s;
	}
	
	private String checkTheRow(){
		String s = "";
		int i = anchor1_x;
		int tmp = anchor1_x;
		anchor2_x = anchor1_x;
		while(i>=0 && (table[anchor1_y][i].isSelected() || table[anchor1_y][i].isUsed())){
			s = table[anchor1_y][i].getC() + s;
			if(!table[anchor1_y][i].isUsed())table[anchor1_y][i].setUsed();
			anchor1_x = i;
			checkMultipliers(table[anchor1_y][i]);
			i--;
		}
		i = tmp + 1;
		while(i<table.length && (table[anchor1_y][i].isSelected() || table[anchor1_y][i].isUsed())){
			s += table[anchor1_y][i].getC();
			if(!table[anchor1_y][i].isUsed())table[anchor1_y][i].setUsed();
			anchor2_x = i;
			checkMultipliers(table[anchor1_y][i]);
			i++;
		}
		anchor2_y = anchor1_y;
		tmp_score += additionalPoints(s, dw, tw, j, tmp_score);
		System.out.println("anchor1_x: " + anchor1_x + " anchor2_x: " + anchor2_x +" anchor1_y: " + anchor1_y +" anchor2_y: " + anchor2_y);
		return s;
	}
	
	private void checkMultipliers(Cell c){
		if(c.getC() == '*')j++;
		tmp_score += c.getPoints(Letter.points(c.getC()));
		int t = c.getType();
		if(t == Cell.DOUBLE_WORD)dw++;
		else if(t == Cell.TRIPLE_WORD)tw++;
	}
	
	private int additionalPoints(String word, int dw, int tw, int j, int score){
		int tmp = score;
		if(dw>0) tmp *= (2*dw);
		if(tw>0) tmp *= (2*tw);
		if(word.length()==6) tmp += 10;
		if(word.length()==7) tmp += 30;
		if(word.length()==8) tmp += 50;
		if(word.toLowerCase().equals("scarabeo") || word.toLowerCase().equals("scarabei")) tmp += 100;   
		if(j == 0) tmp += 10;
		return tmp;
	}
	
	public void redrawTable(char tab[][]){
		for(int i=0;i<table.length;i++){
			for(int j=0;j<table.length;j++){
				table[i][j].setC(tab[i][j]);
				table[i][j].setTheIcon(new ImageIcon(img[getLetterIndex(tab[i][j])].getScaledInstance(table[i][j].getWidth(), table[i][j].getHeight(), java.awt.Image.SCALE_SMOOTH)));
			}
		}
	}
	
	public void playerList(String names[]){
		for(int i=0;i<GameState.MAX_N_PLAYERS;i++){
			if(TheDistributedScrabble.DEBUG){
				System.out.println("Graphic player list.");
				if(names[i]==null)System.out.println("null");
				else System.out.println(names[i]);
			}
			label_p[i].setText(names[i]);
		}
		repaint();
	}
	
	public void playingPlayer(String name, boolean my_turn){
		System.out.println("it's playing " + name);
		label_playing_player.setText(name);
		if(my_turn) label_playing_player.setBackground(new Color(161,255,161));
		else label_playing_player.setBackground(new Color(255,161,161));
		repaint();
	}

	public void setTheMove(Move mv) {
		String s= "";
		if(mv.isReverse()){
			s = new StringBuilder(mv.getWord()).reverse().toString();
		}else s = mv.getWord();
		for(int i=0; i<s.length();i++){
			if(mv.getX1() == mv.getX2()){// vertical
				table[mv.getY1()+i][mv.getX1()].setC(s.charAt(i));
				table[mv.getY1()+i][mv.getX1()].setUsed();
				table[mv.getY1()+i][mv.getX1()].setTheIcon(new ImageIcon(img[getLetterIndex(s.charAt(i))].getScaledInstance(table[mv.getY1()+i][mv.getX1()].getWidth(), table[mv.getY1()+i][mv.getX1()].getHeight(), java.awt.Image.SCALE_SMOOTH)));
			}else if(mv.getY1() == mv.getY2()){ //horizontal
				table[mv.getY1()][mv.getX1()+i].setC(s.charAt(i));
				table[mv.getY1()][mv.getX1()+i].setUsed();
				table[mv.getY1()][mv.getX1()+i].setTheIcon(new ImageIcon(img[getLetterIndex(s.charAt(i))].getScaledInstance(table[mv.getY1()][mv.getX1()+i].getWidth(), table[mv.getY1()][mv.getX1()+i].getHeight(), java.awt.Image.SCALE_SMOOTH)));
			}else{
				System.err.println("This word isn't either vertical nor horizontal. :'-)");
			}
		}
		String f = gs.getPlayerNameByNumber(mv.getPlayer());
		refreshScore(f, mv.getScore());
	}
	
	private void removeUsedLetters(){
		for(int i=0;i<label_letters.length;i++){
			if(label_letters[i].isSelected())
				label_letters[i].reset();
		}
	}
	
	private void refreshScore(String player_name, int score){
		int i = 0;
		while(i<GameState.MAX_N_PLAYERS && !label_p[i].getText().equals(player_name))i++;
		label_score[i].setText(Integer.toString(score));
	}
	
	public void hailToTheWinner(int winning_player){
		if(winning_player == this.gs.getMe().getId()){
			JOptionPane.showMessageDialog(null, "You Win!", "End Game", JOptionPane.INFORMATION_MESSAGE);
			System.out.println("OMG YOU WIN!!!!!!1!!11!1111 :D");
		}else{
			JOptionPane.showMessageDialog(null, "You Lose!", "End Game", JOptionPane.INFORMATION_MESSAGE);
			System.out.println("YOU LOSE! :(");
		}
		synchronized (this.gs) {
			this.gs.setState(GameState.STATE_GAME_OVER);
		}
		synchronized(this.gs.getEnvironment()){
			this.gs.getEnvironment().notify();
		}
	}
	
	private void setAsAnchor(Cell c){
		anchors.add(c);
		c.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
	}
	
	public void clientExitus(String name){
		int idx = getLabelByPlayerName(name);
		if(idx > 0 && idx < label_p.length) label_p[idx].setBackground(Color.RED);
	}
	
	public int getLabelByPlayerName(String name){
		int i = 0;
		while(!label_p[i].getText().equals(name)){
			i++;
			if(i>=label_p.length) return -1;
		}
		return i;
	}
	
	@Override
	public void mouseClicked(MouseEvent arg0) {
		if (arg0.getSource() instanceof Cell) {
			if(TheDistributedScrabble.DEBUG){
				Cell l = (Cell) arg0.getSource();
				System.out.println("Cell type: " + l.getType());
				System.out.println("Cell char: " + l.getC());
				System.out.println("Cell is used: " + l.isUsed());
				System.out.println("Cell is selected: " + l.isSelected());
				System.out.println("");
			}
		}
		if (this.gs.isMyTurn()) {
			if (!hold) {	// if a letter is not selected yet check from the hand pool
				if (arg0.getSource() instanceof Letter) {
					Letter l = (Letter) arg0.getSource();
					if (!l.isSelected()) {
						holder = l;
						Border line = new LineBorder(Color.RED, 1);
						holder.setBorder(line);
						holder.select();
						hold = true;
					}
				} else if (arg0.getSource() instanceof JButton) {
					if (arg0.getClickCount() == 2 && !arg0.isConsumed()) {
					}
				}
			} else {	// if we have a selected letter check if the clicked tile in the board has an anchor and so on
				if (arg0.getSource() instanceof Cell) {
					Cell l = (Cell) arg0.getSource();
					if(TheDistributedScrabble.DEBUG){
						System.out.println("Cell type: " + l.getType());
						System.out.println("Cell char: " + l.getC());
						System.out.println("Cell is used: " + l.isUsed());
						System.out.println("Cell is selected: " + l.isSelected());
						System.out.println("");
					}
					boolean find = false;
					for (int i = 0; i < table.length; i++) {
						for (int j = 0; j < table.length; j++) {
							if (l.equals(table[i][j]) && anchors.contains(table[i][j])) {
								if(l.getC() == '*'){//it's a jolly
									
								}
								table[i][j].setBackground(Color.GREEN);
								table[i][j].setIcon(holder.getIcon());
								table[i][j].setLetter(holder);
								table[i][j].select(holder.getC());
								anchor1_x = j;
								anchor1_y = i;
								if(row == col) rowOrColumn(j, i, table[i][j]);
								recomputeAnchors2(j, i, table[i][j]);
								find = true;
								break;
							}
						}
						if (find)
							break;
					}
					if (!find) {
						Border line = new LineBorder(new Color(78, 190, 255), 1);
						holder.setBorder(line);
						holder.unselect();
					}
				} else {
					Border line = new LineBorder(new Color(78, 190, 255), 1);
					holder.setBorder(line);
					holder.unselect();
				}
				holder = null;
				hold = false;
			}
		}

	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		if(arg0.getSource() instanceof JLabel){
			JLabel l = (JLabel) arg0.getSource();
			if(l != null) {
				if(l == label_p[0]){
					label_p[0].setBackground(new Color(255, 255, 190));
					label_score[0].setBackground(new Color(255, 255, 190));				
				}else if(l == label_p[1]){
					label_p[1].setBackground(new Color(255, 255, 190));
					label_score[1].setBackground(new Color(255, 255, 190));
				}else if(this.gs.getNumPlayers()>2 && l == label_p[2]){
					label_p[2].setBackground(new Color(255, 255, 190));
					label_score[2].setBackground(new Color(255, 255, 190));
				}else if(this.gs.getNumPlayers()>3 &&l == label_p[3]){
					label_p[3].setBackground(new Color(255, 255, 190));
					label_score[3].setBackground(new Color(255, 255, 190));
				}
			}
		}else if(arg0.getSource() instanceof JButton){
			JButton b = (JButton) arg0.getSource();
			if(b == button_pass || b == button_exit){
				b.setBackground(new Color(255, 255, 190));
			}
		}
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		if(arg0.getSource() instanceof JLabel){
			JLabel l = (JLabel) arg0.getSource();
			if(l == label_p[0]){
				label_p[0].setBackground(Color.WHITE);
				label_score[0].setBackground(Color.WHITE);
			}else if(l == label_p[1]){
				label_p[1].setBackground(Color.WHITE);
				label_score[1].setBackground(Color.WHITE);				
			}else if(this.gs.getNumPlayers() > 2 && l == label_p[2]){
				label_p[2].setBackground(Color.WHITE);
				label_score[2].setBackground(Color.WHITE);				
			}else if(this.gs.getNumPlayers() > 3 && l == label_p[3]){
				label_p[3].setBackground(Color.WHITE);
				label_score[3].setBackground(Color.WHITE);				
			}
		}else if(arg0.getSource() instanceof JButton){
			JButton b = (JButton) arg0.getSource();
			if(b == button_pass || b == button_exit){
				b.setBackground(Color.WHITE);
			}
		}

	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		if(arg0.getSource() == button_exit){
			int choice = JOptionPane.showConfirmDialog(this, "Are you sure?", "Exit", JOptionPane.YES_NO_OPTION);
			if(choice == 0){
				if(TheDistributedScrabble.DEBUG)System.out.println("Exiting from the game");
				gs.resumeToMenu();
			}
		}else if(gs.isMyTurn() && arg0.getSource() == button_pass){
			boolean reverse = false;
			if(TheDistributedScrabble.DEBUG)System.out.println("Pass button "+ row + " " + col);
			if(row)
				if(TheDistributedScrabble.DEBUG)System.out.println("Pass button row");
			else if(col)
				if(TheDistributedScrabble.DEBUG)System.out.println("Pass button col");
			else if(TheDistributedScrabble.DEBUG)System.out.println("No row or col. maybe one tile or empty word");
			String word = getWord();
			if(word.equals("")){
				System.out.println("Empty word. No points. :(");
				this.gs.emptyWord();
				Move m = new Move("", 0, 0, 0, 0, gs.getMe().getScore(), gs.getMe().getId(), false);
				this.gs.sendMove(m);
			}else{
				if(TheDistributedScrabble.DEBUG)System.out.println("Checking the word: "+word);
				boolean b = this.gs.checkWord(word);
				if(!b) {	// check the reverse word
					String s = new StringBuilder(word).reverse().toString();
					if(TheDistributedScrabble.DEBUG)System.out.println("Checknig the reverse word: "+ s);
					b = this.gs.checkWord(s);
					if(b){
						reverse = true;
						word = s;
						if(TheDistributedScrabble.DEBUG)System.out.println("Reverse is better: " + word);
					}
				}
				if(b){
					System.out.println("Legal move. :)");
					this.gs.addScore(tmp_score);
					refreshScore(this.gs.getMe().getName(), this.gs.getMe().getScore());
					Move m = new Move(word, anchor1_x, anchor2_x, anchor1_y, anchor2_y, this.gs.getMe().getScore(), this.gs.getMe().getId(), reverse);
					tmp_score = 0;
					this.gs.sendMove(m);
					cleanAnchors();
					removeUsedLetters();
					//deselectAll();
					deselectUsedLetters(row, anchor1_x, anchor2_x, anchor1_y, anchor2_y);
					setTheMove(m);
				}else{
					System.out.println("Not legal move: " + word + " b: " + b + " :(");
					cleanAnchors();
					removeUsedLetters();
					//deselectAll();
					deselectUsedLetters(row, anchor1_x, anchor2_x, anchor1_y, anchor2_y);
				}
			}
			row = col = false;
			anchor1_x = 8;
			anchor1_y = 8;
		}
		else if(this.gs.isMyTurn() && arg0.getSource() == button_reset){
			cleanAnchors();
			deselectAll();
			row = col = false;
			anchor1_x = 8;
			anchor1_y = 8;
			computeAnchors();
		}
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

}
