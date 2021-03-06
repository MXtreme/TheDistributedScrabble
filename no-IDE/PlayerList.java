import java.io.Serializable;
import java.util.Vector;

public class PlayerList implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6464616219263014420L;
	private Vector<Player> players;
	
	public PlayerList(){
		players = new Vector<Player>();
	}
	
	public synchronized void add(Player p){
		this.players.add(p);
	}
	
	public synchronized void removeElement(Player p){
		/*for(int i=0;i<this.players.size();i++){
			if(this.players.elementAt(i).getName().equals(p.getName()))this.players.removeElementAt(i);
			break;
		}*/
		for(Player d : this.players){
			if(d.getName().equals(p.getName())){
				this.players.remove(d);
				break;
			}
		}
	}
	
	public synchronized void removeHost(){
		for(Player pl : players){
			if(pl.getName().equals("INetworkManager"))players.remove(pl);
			break;
		}
	}
	
	public Player getFirst(){
		return this.players.firstElement();
	}
	
	public Player getPlayerWithName(String name){
		for(Player pl : players){
			if(pl.getName().equals(name))return pl;
		}
		return null;
	}
	
	public Player getPlayerWithAddress(String address){
		for(Player pl : players){
			if(pl.getAddress().equals(address))return pl;
		}
		return null;
	}
	
	public Player getPlayerWithId(int id){
		for(Player pl : players){
			if(pl.getId() == id)return pl;
		}
		return null;
	}
	
	public Player getTheHost(){
		for(Player pl : players){
			if(pl.getRmiName().equals("INetworkManager"))return pl;
		}
		return null;
	}
	
	public String getPlayerNameByID(int n){
		for(Player p : players){
			if(p.getId()==n) return p.getName();
		}
		return null;
	}
	
	public String[] getPlayersNameList(){
		String s[] = new String[players.size()];
		if(TheDistributedScrabble.DEBUG)System.out.println("Retrieving player list of " + players.size() + " size");
		for(int i=0;i<players.size();i++){
			s[i] = players.elementAt(i).getName();
			if(TheDistributedScrabble.DEBUG)System.out.println(s[i] + " " + players.elementAt(i).getName());
		}
		return s;
	}
	
	public Player getNextToMe(int id){
		/*if(players.size() < 2){
			return null;
		}*/
		int playing = id;
		do{
			playing++;
			if(playing>GameState.MAX_N_PLAYERS) playing = 1;
		}while(!this.isThereAPlayerWithThisId(playing));
		return this.getPlayerWithId(playing);
		
		//Player next = new Player("", "", "", GameState.MAX_N_PLAYERS + 1);
		/*
		for(Player p : players){
			if(p.getId() > id && p.getId() < next.getId()) next = p;			
		}		
		return next;*/
		
		/*
		if(this.players.indexOf(getPlayerByID(id))+1<this.players.size())return this.players.elementAt(this.players.indexOf((getPlayerByID(id)))+1);
		else return this.players.elementAt(0);
		*/
		
		/*boolean found = false;
		int k = id+1;
		Player d = new Player("", "", "", id);
		while(!found){
			for(Player p : this.players){
				if(p.getId()==k){
					return p;
				}
			}
			k++;
			if(k == id) return d;
			if(k>=players.size())k = 0;
		}
		return d;*/
	}
	
	public Vector<Player> getPlayers(){
		return this.players;
	}
	
	public void listPlayers(){
		System.out.println(TheDistributedScrabble.ANSI_YELLOW + "Listing current players:" + TheDistributedScrabble.ANSI_RESET);
		for(Player p : players){
			System.out.println(TheDistributedScrabble.ANSI_YELLOW + "- " + p.getId() + " Nickname:" + p.getName() + " RMI:" + p.getRmiName() + " Address:"+ p.getAddress() + TheDistributedScrabble.ANSI_RESET);
		}
	}
	
	public void reset(){
		players = new Vector<Player>();
	}
	
	public void refresh(Vector<Player> pl){
		Player tmp = null;
		for(Player p : pl){
			boolean found = false;
			for(Player p2 : players){
				if(p2.getRmiName().equals(p.getRmiName())){
					found = true;
					break;
				}
				
				tmp = p;
			}
			if(!found)players.add(tmp);
		}
		this.resort();
	}
	
	public void resort(){
		if(TheDistributedScrabble.DEBUG){
			System.out.println("List players befor resorting.");
			this.listPlayers();
			System.out.println("Number of players: " + players.size());
		}
		Vector<Player> tmp = new Vector<Player>(players.size());
		for(Player p : players){
			tmp.add(p);
		}
		for(Player p : players){
			tmp.setElementAt(p, p.getId()-1);
		}
		this.players = tmp;
	}
	
	public boolean isThereAPlayerWithThisId(int n){
		for(Player p: players){
			if(p.getId()==n)return true;
		}
		return false;
	}
	
	public boolean amITheGreaterPeer(Player me){
		for(Player p : players){
			if(p.getId()>me.getId()) return false;
		}
		return true;
	}
	
	public void weNeedANewLeader(){
		Player leader = new Player("", "", "", -1);
		for(Player p : players){
			if(p.getId()>leader.getId()) leader = p;
		}
		leader.setRmiName("INetworkManager");
	}
	
	public Vector<Player> getCopy(){
		Vector<Player> pl = new Vector<Player>();
		for(Player p : players){
			Player g = new Player(p.getName(), p.getRmiName(), p.getAddress(), p.getId());
			pl.add(g);
		}
		return pl;
	}
	
	public Player getPlayerByName(String name){
		for(Player p : this.players){
			if(p.getName().equals(name))return p;
		}
		return null;
	}
	
	public Player getPlayerByID(int id){
		for(Player p : this.players){
			if(p.getId()==id)return p;
		}
		return null;
	}
	
	public Player getPreviousToMe(int id) {
		if(players.size() < 2){
			return null;
		}
		/*Player back = new Player("", "", "", 0);
		
		for(Player p : players){
			if(p.getId()!=id && p.getId() < id && p.getId() > back.getId()) back = p;			
		}
		*/
		
		if(this.players.indexOf(getPlayerByID(id))>0)return this.players.elementAt(this.players.indexOf((getPlayerByID(id)))-1);
		else return this.players.elementAt(this.players.size()-1);
	}
}
