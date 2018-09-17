import java.util.Vector;

public class Dispatcher_t extends Thread {
	private Vector<Message> read, write;
	private Message current;
	private boolean end = false;
	private GameState gs;
	
	public Dispatcher_t(Vector<Message> v, Vector<Message> w, GameState gs){
		this.read = v;
		this.write = w;
		this.gs = gs;
	}
	
	@Override
	public void run(){
		if(TheDistributedScrabble.DEBUG)System.out.println("Dispatcher started.");
		while(!end){
			if(read.isEmpty()){
				try{
					synchronized(read){
						read.wait();
					}
				}catch(InterruptedException e){
					e.printStackTrace();
				}
			}else{
				current = read.firstElement();
				doThings();
				synchronized (read) {
					read.remove(0);
				}
				current = null;
			}
		}
	}
	
	private void doThings(){
		if(current!=null){
			String him = current.getSender();
			String text;
			if(TheDistributedScrabble.DEBUG)System.out.println("Recieved message from " + him + " of type " + current.getType());
			Vector<Object> data = (Vector<Object>)current.getData();
			switch(current.getType()){
				case Message.MSG_OK:
					this.requestAccepted();
					break;
				case Message.MSG_ACK:
					String txt = data.elementAt(0).toString();
					System.out.println(him + " says:<<" + txt + ">>");	
					break;
				case Message.MSG_ERROR:
					text = data.elementAt(0).toString();
					System.out.println(him + " says:<<" + text + ">>");		
					break;
				case Message.MSG_MOVE:
					this.applyMove();
					break;
				case Message.MSG_JOIN_GAME_REQUEST:
					this.joinRequest();
					break;
				case Message.MSG_IS_ALIVE:
					this.playerAlive();
					break;
				case Message.MSG_START_GAME:
					this.startGame();
					break;
				case Message.MSG_DRAW:
					this.someoneDrawnFromTheBag();
					break;
				case Message.MSG_CLIENT_EXITUS:
					byePeer();
					break;
			}
		}
	}
	
	private void byePeer(){
		Vector<Object> data = (Vector<Object>)current.getData();
		Player p = (Player)data.elementAt(0);
		System.err.println("Our dear " + p.getName() + " is dead under unknown circumstances :(");
		this.gs.removePeer(p);
		this.gs.nextPlayingPlayer();
		synchronized(this.gs.getEnvironment()){
			this.gs.getEnvironment().notify();
		}
	}
	
	private void applyMove(){
		if(TheDistributedScrabble.DEBUG)System.out.println("Recieved a move from " + current.getSender() + ".");
		this.gs.nextTurn();
		Vector<Object> data = (Vector<Object>)current.getData();
		Move m = (Move)data.elementAt(0);
		if(!m.getWord().equals("")){
			this.gs.removeLetters(m.getWord());
			this.gs.updateScore(m.getScore(), m.getPlayer());
			this.gs.setMove(m);
		}
		synchronized(this.gs.getEnvironment()){
			this.gs.getEnvironment().notify();
		}
	}
	
	private void someoneDrawnFromTheBag(){
		if(TheDistributedScrabble.DEBUG)System.out.println("Someone drawn from the bag.");
		Vector<Object> data = (Vector<Object>)current.getData();
		String hand[] = (String[]) data.elementAt(0);
		this.gs.removeLetters(this.concatStrings(hand));
	}
	
	private void startGame(){
		System.out.println("Full lobby. We can start the game.");
		Vector<Object> data = (Vector<Object>)current.getData();
		Vector<Player> pl = (Vector<Player>)data.elementAt(0);
		int n = (int)data.elementAt(1);
		this.gs.refreshPlayersList(pl);
		this.gs.setPlayingPlayer(n);
		if(TheDistributedScrabble.DEBUG)System.out.println("Telling to the next to start.");
		this.gs.draw(8);
		this.gs.tellToNextToStart();
		synchronized(this.gs){
			this.gs.setState(GameState.STATE_GAME);
		}
		synchronized(this.gs.getEnvironment()){
			this.gs.getEnvironment().notify();
		}
		
	}
	
	private String concatStrings(String s[]){
		String w = "";
		for(String c : s){
			w+=c;
		}
		return w;
	}
	
	private void playerAlive(){
		Player p = gs.getPlayers().getPlayerWithAddress(current.getAddressSender());
		/*if(TheDistributedScrabble.DEBUG){
			System.out.println(current.getSender() + " is alive at the address " + current.getAddressSender());
			gs.getPlayers().listPlayers();
		}*/
		if(p!=null)	p.setLastSeen();
		else System.out.println("Something went wrong on searching a player");
	}
	
	private void requestAccepted(){
		Vector<Object> data = (Vector<Object>)current.getData();
		String him = current.getSender();
		String address_s = current.getAddressSender();
		String text;
		int n = (int)data.elementAt(0);
		text = data.elementAt(2).toString();
		System.out.println(him + " says:<<" + text + ">>");
		System.out.println("You're the player number " + n);
		gs.getMe().setId(n);
		text = data.elementAt(1).toString();
		Player p = new Player(text, him, address_s, 1);
		p.setLastSeen();
		this.gs.addNewPlayer(p);
		this.gs.addPlayersCounter();
	}
	
	private void joinRequest(){
		System.out.println(current.getSender() + " requested to join the game.");
		Vector<Object> data = new Vector<Object>();
		Message msg = new Message(gs.getMyName(), current.getSender(), gs.getMyAddress(), current.getAddressSender(), Message.MSG_OK, data);
		if(gs.getNumPlayers()<GameState.MAX_N_PLAYERS){	// there is room
			gs.addPlayersCounter();
			Player p = new Player(current.getSender(), current.getSender(), current.getAddressSender(), gs.getNumPlayers());
			p.setLastSeen();
			this.gs.addNewPlayer(p);
			data.add(gs.getNumPlayers());
			data.add(gs.getMe().getName());
			data.add("Welcome to the lobby. Let's wait for others to join.");
			msg.setData(data);
		}else{
			data.add("There's no more room for you. I'm sorry.");
			msg.setType(Message.MSG_ERROR);
		}
		synchronized(write){
			write.add(msg);
			write.notify();
		}
		gs.refreshHostingScreen();
		if(this.gs.getNumPlayers()==GameState.MAX_N_PLAYERS){
			if(TheDistributedScrabble.DEBUG)System.out.println("Number of minimum players reached. The game should start soon.");
			synchronized(gs.getEnvironment()){
				this.gs.getEnvironment().notify();
			}
		}
	}
	
	public boolean getEnd(){
		return this.end;
	}
	
	public void end(){
		end = !end;
		synchronized(read){
			read.notify();
		}
	}
}
