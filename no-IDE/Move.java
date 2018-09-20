

import java.io.Serializable;

public class Move implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1339097317250470872L;
	private String word;
	private int x1, x2, y1, y2, score, player;
	private boolean reverse;
	
	public Move(String word, int x1, int x2, int y1, int y2, int score, int player, boolean reverse) {
		this.word = word;
		this.x1 = x1;
		this.x2 = x2;
		this.y1 = y1;
		this.y2 = y2;
		this.score = score;
		this.player = player;
		this.reverse = reverse;
	}
	public String getWord() {
		return word;
	}
	public void setWord(String word) {
		this.word = word;
	}
	public int getX1() {
		return x1;
	}
	public void setX1(int x1) {
		this.x1 = x1;
	}
	public int getX2() {
		return x2;
	}
	public void setX2(int x2) {
		this.x2 = x2;
	}
	public int getY1() {
		return y1;
	}
	public void setY1(int y1) {
		this.y1 = y1;
	}
	public int getY2() {
		return y2;
	}
	public void setY2(int y2) {
		this.y2 = y2;
	}
	
	public int getScore(){
		return score;
	}
	
	public void setScore(int score){
		this.score = score;
	}
	
	public int getPlayer() {
		return player;
	}
	
	public boolean isReverse(){
		return this.reverse;
	}
	
	public void setReverse(boolean b){
		this.reverse = b;
	}
	
	public void setPlayer(int player) {
		this.player = player;
	}
	
	
}
