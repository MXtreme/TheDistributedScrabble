

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;
import java.util.Vector;


/*
 * 	17x17 tavola quadrata
 *  130 tessere + 2 jolly
 * 	12 A, E, I, O; 4 U
 * 	7 C, R, S, T; 6 L, M, N; 4 B, D, F, G, P, V; 2 H, Q, Z
 * 
 * 	1 punto per ogni A, C, E, I, O, R, S, T; 2 punti per L, M, N; 3 per P; 4 per B, D, F, G, U, V; 8 punti per H e Z; 10 per Q
 * 	10 punti bonus se si compone una parola di 6 lettere;
 *	30 punti bonus se la parola è di 7 lettere;
 *	50 se è formata da tutte e 8 le lettere;
 *	100 punti bonus se si riesce a scrivere la parola "scarabeo" o "scarabei"
 *	A questi punteggi vanno sommati ulteriori 10 punti se non si sono utilizzati jolly nel comporre le parole.
 *
 * 	inizio gioco con 8 tessere a testa.
 * 	alla fine del turno si pescano un numero di tessere pari a quelle utilizzate.
 * 	i giocatori successivi sono costretti ad intersecare le proprie tessere con quelle già sulla tavola.
 * 	il gioco prosegue fino a quando uno dei giocatori non hanno più lettere
 * 	il jolly sostituisce qualsiasi lettera e in un turno successivo un giocatore che possiede tale lettare può sostituirla per procurarsi il jolly
 *	L'ultimo giocatore ad aver composto una parola ottiene come punteggio anche la somma di tutte le lettere rimanenti agli altri giocatori e non ancora utilizzate
 * */


public class Engine {
	private TreeNode<Character> vocabular;
	private Vector<String> bag;
	final static Charset ENCODING = StandardCharsets.UTF_8;
	
	public Engine(){
		initTheBag();
		shuffleTheBag();
		initVocabular();
		System.out.println("Engine initialized");
	}
	private void shuffleTheBag(){
		   Collections.shuffle(bag);
	}
	
	private void initTheBag(){
		bag = new Vector<String>(130);
		for(int i=0;i<12;i++){
			bag.add("A");
			bag.add("E");
			bag.add("I");
			bag.add("O");
		}
		for(int i=0;i<4;i++){
			bag.add("U");
			bag.add("B");
			bag.add("D");
			bag.add("F");
			bag.add("G");
			bag.add("P");
			bag.add("V");
		}
		for(int i=0;i<7;i++){
			bag.add("C");
			bag.add("R");
			bag.add("S");
			bag.add("T");
		}
		for(int i=0;i<6;i++){
			bag.add("L");
			bag.add("M");
			bag.add("N");
		}
		for(int i=0;i<2;i++){
			bag.add("H");
			bag.add("Q");
			bag.add("Z");
			bag.add("*");
		}
	}
	
	private void initVocabular(){
		System.out.println("Loading the vocabular. Wait a few moments please.");
		vocabular = new TreeNode<Character>(null, '*');
		TreeNode<Character> n = vocabular;
		Path path = Paths.get("res/ok.txt");
	    try (Scanner scanner =  new Scanner(path, ENCODING.name())){
	    	String st;
	      while (scanner.hasNextLine()){
	    	  st = scanner.nextLine();
	    	  //System.out.println(st);
	    	  for(char c : st.toCharArray()){
	    		  c = Character.toLowerCase(c);
	    		  String s = Character.toString(c);  
	    		  s = Normalizer.normalize(s, Normalizer.Form.NFD);
	    		  s = s.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
	    		  c = s.charAt(0);
	    		  //System.out.print(c);
	    		  if(!n.hasChildrenWithElement(c))n.addChild(c);
	    		  n = n.getChildAtElement(c);
	    	  }
	    	  //System.out.println("#");
	    	  n.addChild('#');
	    	  n = vocabular;
	      }
	      scanner.close();

		System.out.println("Vocabular loaded.");
	      	/*TreeNode<Character> h;
			System.out.println("LEGAL: "+isLegalWord("vite"));
			System.out.println("LEGAL2: "+isLegalWord("moro"));
			System.out.println("LEGAL3: "+isLegalWord("mo*o"));
			System.out.println("LEGAL4: "+isLegalWord("*avolo"));
			System.out.println("LEGAL5: "+isLegalWord("cavall*"));
			System.out.println("LEGAL6: "+isLegalWord("cava*tro"));
*/
	      if(TheDistributedScrabble.DEBUG){
	      	TreeNode<Character> a = vocabular.getChildAtElement('c');
			System.out.println("LEGAL8: "+isLegalWord("cava*tro", 0, a));
	      	a = vocabular.getChildAtElement('d');
			System.out.println("LEGAL9: "+isLegalWord("daga", 0, a));
	      	a = vocabular.getChildAtElement('m');
			System.out.println("LEGAL10: "+isLegalWord("mo*o", 0, a));
	      	a = vocabular.getChildAtElement('c');
			System.out.println("LEGAL11: "+isLegalWord("*avolo", 0, a));
	      	a = vocabular.getChildAtElement('c');
			System.out.println("LEGAL12: "+isLegalWord("cavall*", 0, a));
			a = vocabular.getChildAtElement('p');
			System.out.println("LEGAL13: "+isLegalWord("porc*ll", 0, a));
			a = vocabular.getChildAtElement('v');
			System.out.println("LEGAL14: "+isLegalWord("v*t*", 0, a));
	      }
	      //if(isLegalWord("vite"))System.out.println("true"); //DEBUG
	     //n.DFS(); you won't read the words in the tree with this. Just think about it to understand it.
	    } catch (IOException e) {
	    	System.err.println("could not initialize the vocabular");
			e.printStackTrace();
		}
	}
	
	public String[] getNewLetters(int n_letters) {
		String s[] = new String[n_letters];
		for(int i=0;i<n_letters;i++){
			if(TheDistributedScrabble.DEBUG)System.out.println("Bag size: " + bag.size());
			int r = new  Random().nextInt(bag.size());
			s[i] = bag.elementAt(r);
			bag.removeElementAt(r);
		}
		return s;
	}
	
	public TreeNode<Character> getVocabular() {
		return vocabular;
	}
	
	public Vector<String> getBag() {
		return bag;
	}
	
	public boolean isLegalWord(String s, int i, TreeNode<Character> t)throws NullPointerException{
		if(TheDistributedScrabble.DEBUG)System.out.println("i="+i+" s(i)="+s.charAt(i));
		if(i<s.length()-1){	//not at the end
			i++;
			if(s.charAt(i)=='*'){
				for(TreeNode<Character> n: t.getChildren()){
					if(n!=null && n.getElement()!='#' && isLegalWord(s, i, n))return true;
				}
				return false;
			}else{	// if not a jolly
				TreeNode<Character> c = t.getChildAtElement(s.charAt(i));
				if(c==null) return false;
				else{
					if(c.getElement()!='#' && isLegalWord(s, i, c)) return true;
					else return false;
				}
			}
		}else{
			if(t.hasChildrenWithElement('#')) return true;
			else return false;
		}
	}
	
	/*public boolean isLegalWord(String word) {
		word = word.toLowerCase();
		word = Normalizer.normalize(word, Normalizer.Form.NFD);
		word = word.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
		TreeNode<Character> c = vocabular, v = vocabular;
		boolean there_is_a_jolly = (word.contains("*"));
		int i=0;
		//String b = "";
		if(there_is_a_jolly){
			while(i<word.length()){
				if(word.charAt(i)=='*'){
					int k = i;
					java.util.List<TreeNode<Character>> l = c.getChildren();
					for(TreeNode<Character> n: l){
						int j = i+1;
						while(j<word.length() && n.hasChildrenWithElement(word.charAt(j))){
							n = n.getChildAtElement(word.charAt(j));
							j++;
						}
						if(n.hasChildrenWithElement('#'))return true;
					}
					return false;
				}
				if(c.hasChildrenWithElement(word.charAt(i))){
					c = c.getChildAtElement(word.charAt(i));
					i++;
				}
			}
		}else{
			while(i<word.length() && c.hasChildrenWithElement(word.charAt(i))){
				c = c.getChildAtElement(word.charAt(i));
				//b += c.getElement().toString();
				i++;
			}
		}
		//System.out.println(b);
		if(c.hasChildrenWithElement('#'))return true;
		else return false;
	}
	*/
	public void setBag(Vector<String> bag) {
		this.bag = bag;
	}
	
	public void removeLetters(String s[]){
		for(int i=0; i<s.length;i++){
			this.bag.removeElement(s[i]);
		}
		if(TheDistributedScrabble.DEBUG)System.out.println("After this draw the bag size is of " + this.bag.size());
	}
	
	public void removeLetters(String s){
		for(int i=0; i<s.length();i++){
			this.bag.removeElement(s.charAt(i));
		}
		if(TheDistributedScrabble.DEBUG)System.out.println("After this draw the bag size is of " + this.bag.size());
	}
	
	public void resetTheBag(){
		initTheBag();
		if(TheDistributedScrabble.DEBUG)System.out.println("Bag size is " + this.bag.size());
	}
	
	public String[] concatHands(String hand1[], String hand2[]){
		String s[] = new String[hand1.length+hand2.length];
		int i;
		for(i=0;i<hand1.length;i++)s[i] = hand1[i];
		for(;i<hand1.length+hand2.length;i++) s[i] = hand2[(hand1.length-(i-hand1.length))-1];
		return s;
	}
}
