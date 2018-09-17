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
	
	public void add(Player p){
		this.players.add(p);
	}
	
	public void removeElement(Player p){
		this.players.remove(p);
	}
	
	public void removeHost(){
		for(Player pl : players){
			if(pl.getName().equals("INetworkManager"))players.remove(pl);
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
		for(Player p : players){
			if(p.getId()==(id+1))return p;
			else if(id==4 && p.getId()==1) return p;
		}
		return null;
	}
	
	public Vector<Player> getPlayers(){
		return this.players;
	}
	
	public void listPlayers(){
		System.out.println("Listing current players:");
		for(Player p : players){
			System.out.println("- " + p.getId() + " Nickname:" + p.getName() + " RMI:" + p.getRmiName() + " Address:"+ p.getAddress());
		}
	}
	
	public void reset(){
		players = new Vector<Player>();
	}
	
	public void refresh(PlayerList pl){
		Player tmp = null;
		for(Player p : pl.getPlayers()){
			boolean found = false;
			for(Player p2 : players){
				if(p2.getRmiName().equals(p.getRmiName())){
					found = true;
					tmp = p2;
					break;
				}
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
}
