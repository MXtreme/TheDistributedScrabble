import java.io.Serializable;

public class Player implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5728696841946939139L;
	private String name, rmi_name, address, hand[];
	private int id, score;
	private long last_seen;
	
	public Player(String name, String rmi_name, String address, int id){
		this.name = name;
		this.rmi_name = rmi_name;
		this.address = address;
		this.id = id;
		this.hand = new String[8];
		setLastSeen();
		score = 0;
	}
	
	public String getName(){
		return this.name;
	}
	
	public String getRmiName(){
		return this.rmi_name;
	}
	
	public String getAddress(){
		return this.address;
	}
	
	public int getId(){
		return this.id;
	}
	
	public int getScore(){
		return this.score;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public void setRmiName(String rmi_name){
		this.rmi_name = rmi_name;
	}
	
	public void setAddress(String address){
		this.address = address;
	}
	
	public void setId(int n){
		this.id = n;
	}
	
	public void setScore(int n){
		this.score = n;
	}
	
	public void addToScore(int n){
		this.score += n;
	}
	
	public void fillHand(String letters[]){
		int k = 0;
		for(int i=0;i<hand.length;i++){
			if(hand[i]==null || hand[i].equals("")){
				hand[i] = letters[k];
				k++;
			}
		}
		if(TheDistributedScrabble.DEBUG)
		{	this.printHand();
			System.out.println("Filled the hand");
		}
	}
	
	
	public void removeLetterFromHand(String s){
		if(TheDistributedScrabble.DEBUG)
			{	this.printHand();
				System.out.println("Removing the letters " + s + " from the set " + getStringedHand());
			}
		String s2 = getStringedHand();
		if(TheDistributedScrabble.DEBUG)System.out.print("Removing: ");
		for(int i=0;i<s.length();i++){
			for(int j=0;j<s2.length();j++){
				if(s2.charAt(j)==s.charAt(i)){
					if(TheDistributedScrabble.DEBUG)System.out.print(hand[j]);
					hand[j]=null;
					StringBuilder s3 = new StringBuilder(s2);
					s3.setCharAt(j, ' ');
					s2 = s3.toString();
					break;
				}
			}
		}
		if(TheDistributedScrabble.DEBUG)System.out.println();
	}
	
	public void printHand(){
		System.out.println("Printing the hand:");
		for(int i=0;i<hand.length;i++){
			if(hand[i]!=null){
				if(!hand[i].equals("")){
					System.out.print(hand[i]);
				}
			}
		}
		System.out.println();
	}
	
	private String getStringedHand(){
		String s = "";
		for(int i=0;i<this.hand.length;i++){
			s+=this.hand[i];
		}
		return s;
	}
	
	public long getLastSeen(){
		return this.last_seen;
	}
	
	public int getNumberOfLetters(){
		int n = 0;
		for(int i=0;i<hand.length;i++){
			if(hand[i] == null)n++;
			if(hand[i]!=null && hand[i].equals(""))n++;
		}
		return n;
	}
	
	public String[] getHand(){
		return this.hand;
	}
	
	public void setLastSeen(){
		last_seen = System.currentTimeMillis();
	}
}
