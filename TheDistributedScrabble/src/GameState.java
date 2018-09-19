import java.awt.Color;
import java.util.Random;
import java.util.Vector;

public class GameState {
	static final int STATE_MENU = 0;
	static final int STATE_HOST = 1;
	static final int STATE_CONNECT = 2;
	static final int STATE_GAME = 3;
	static final int STATE_GAME_OVER = 4;
	static int MAX_N_PLAYERS;
	private RMINetworkManager rmi;
	private PlayerList players;
	private Player me;
	private Engine engine;
	private Environment env;
	private GUI gui;
	private int state, num_players, playing_player, zero_score;
	private boolean hosting;
	
	public GameState(int state, RMINetworkManager rmi, int n, Environment env){
		if(TheDistributedScrabble.DEBUG)System.out.println("GameState initialized");
		MAX_N_PLAYERS = n;
		this.state = state;
		this.rmi = rmi;
		this.engine = new Engine();
		this.env = env;
		this.players = new PlayerList();
		this.num_players = 1;
		this.hosting = false;
		this.gui = new GUI("Distributed Scrabble", this);
		this.zero_score = 0;
	}
	
	public void doThings(){
		switch(state){
			case STATE_MENU:
				if(TheDistributedScrabble.DEBUG)System.out.println("Game state at MENU");
				this.gui.switchToHome();
				break;
			case STATE_HOST:
				if(TheDistributedScrabble.DEBUG)System.out.println("Game state at HOSTING");
				this.rmi.setName("INetworkManager");
				if(!hosting){
					if(rmi.selfExport()) {
						this.hosting = true;
						if(this.env.getConnectioner().isSuspended()){
							this.env.getConnectioner().setGS(this);
							this.env.getConnectioner().safeSuspend(); // this will just switch it from suspend to resume the thread in a safe and not deprecated way
						}
						this.gui.getHome().host();
						me = new Player(this.gui.getHome().getNickname(), "INetworkManager", rmi.getMyIP(), num_players);
						players.add(me);
					}else{
						this.hosting = false;
						if(!this.env.getConnectioner().isSuspended())this.env.getConnectioner().safeSuspend();
						me = null;
						this.gui.getHome().setMsg("Unable to host", Color.RED);
					}
				}else{
					if(TheDistributedScrabble.DEBUG)System.out.println("Hosting and waiting for players to join.");
					if(num_players == GameState.MAX_N_PLAYERS){
						System.out.println("Full lobby. Let's start to play. :)");
						this.chooseStartingPlayer();
						this.draw(8);
						this.tellToNextToStart();
						state = GameState.STATE_GAME;
						this.doThings();
					}
				}
				break;
			case STATE_CONNECT:
				if(TheDistributedScrabble.DEBUG)System.out.println("Game state at CONNECTING");
				if(!hosting){
					this.rmi.setName(this.gui.getHome().getNickname());
					if(rmi.selfExport()){
						if (!rmi.connect(this.gui.getHome().getHostIP(), "INetworkManager")) {
							String msg="";
							if (this.gui.getHome().getHostIP().equals(rmi.getMyIP()))
								msg = "Unable to connect to myself.";
							else
								msg = "Unable to connect to host " + this.gui.getHome().getHostIP() + ".";
							this.gui.getHome().setMsg(msg, Color.RED);
							if(!this.env.getConnectioner().isSuspended())this.env.getConnectioner().safeSuspend();
							this.gui.getHome().back();
						} else {
							System.out.println("Connection to host successful.");
							me = new Player(this.gui.getHome().getNickname(), rmi.getName(), rmi.getMyIP(), num_players);
							players.add(me);
							this.hosting = false;
							if(this.env.getConnectioner().isSuspended()){
								this.env.getConnectioner().setGS(this);
								this.env.getConnectioner().safeSuspend();
							}
						}
						this.hosting = false;
					}else{
						this.hosting = false;
						System.out.println("Couldn't self export.");
					}
				}else{
					System.out.println("WARNING: Your're trying to connect but it seems you're hosting.");
				}
				break;
			case STATE_GAME:
				if(TheDistributedScrabble.DEBUG)System.out.println("Game state at GAME");
				if(this.gui.getInGameGraphics()==null){
					this.gui.switchToGame();
					String s[] = players.getPlayersNameList();
					if(TheDistributedScrabble.DEBUG){
						for(int i=0;i<s.length;i++)System.out.println("Retrieved player list as String[" + i + "] = " + s[i]);
					}
					this.num_players = GameState.MAX_N_PLAYERS;
					this.gui.getInGameGraphics().playerList(s);
					this.gui.getInGameGraphics().repaint();
				}
				this.gui.getInGameGraphics().playingPlayer(players.getPlayerNameByID(playing_player), this.isMyTurn());
				int n_letters = this.me.getNumberOfLetters();
				if(TheDistributedScrabble.DEBUG)System.out.println("N letters: " + n_letters);
				if(n_letters<8 && n_letters > 0){
					draw(n_letters);
				}
				this.gui.getInGameGraphics().refreshLetters(this.me.getHand());
				this.gui.getInGameGraphics().computeAnchors();
				this.gui.getInGameGraphics().repaint();
				if(TheDistributedScrabble.DEBUG)this.me.printHand();
				break;
			case STATE_GAME_OVER:
				if(TheDistributedScrabble.DEBUG)System.out.println("Game state at GAME OVER");
				this.num_players = 1;
				this.zero_score = 0;
				this.players = new PlayerList();
				this.engine.resetTheBag();
				this.hosting = false;
				this.abort();
				this.gui.switchToHome();
				break;
		}
	}
	
	public synchronized void setState(int state){
		this.state = state;
	}
	
	public void removePeer(Player p){
		this.num_players--;
		this.players.removeElement(p);
		this.gui.getInGameGraphics().clientExitus(p.getName());
	}
	
	public void peerIsDead(Player p){
		System.err.println("Our dear " + p.getName() + " is dead under unknown circumstances :(");
		Vector<Object> data = new Vector<Object>();
		data.add(p);
		for(Player d: players.getPlayers()){
			if(d!= this.me && d!=p){
				Message m = new Message(this.rmi.getName(), d.getRmiName(), this.rmi.getMyIP(), d.getAddress(), Message.MSG_CLIENT_EXITUS, data);
				synchronized(this.getEnvironment().getWriteBuffer()){
					this.getEnvironment().getWriteBuffer().add(m);
					this.getEnvironment().getWriteBuffer().notify();
				}
			}
		}
		removePeer(p);
		if(num_players == 1){
			this.gui.getInGameGraphics().hailToTheWinner(this.getMe().getId());
		}else{
			if(this.playing_player==p.getId()){	// it was his turn, damn him	è.é
				if(TheDistributedScrabble.DEBUG)System.out.println("It was his turn, damn him.");
				this.nextPlayingPlayer();
				synchronized(this.getEnvironment()){
					this.getEnvironment().notify();
				}
			}
		}
	}
	
	/*public void hostIsDead(){
		System.out.println("The host is unreachable. It won't be forgotten :(");
		switch(state){
			case STATE_CONNECT:
				synchronized(this){
					state = GameState.STATE_MENU;
					this.players = new PlayerList();
					hosting = false;
					num_players = 1;
					this.rmi.close();
					this.gui.getHome().stopAnimation();
				}
			break;
			case STATE_GAME:
				this.removePeer(players.getTheHost());
				if(TheDistributedScrabble.DEBUG)System.out.println("Now there are left " + this.num_players + " players.");
				if(num_players == 1){
					this.gui.getInGameGraphics().hailToTheWinner(this.getMe().getId());
				}else if(this.players.amITheGreaterPeer(this.getMe())){	// i'll be the new "leader" ;(
					try{
						if(TheDistributedScrabble.DEBUG)System.out.println("I'll be their leader ;(");
						this.rmi.close();
						this.rmi.setName("INetworkManager");
						this.rmi.selfExport();
						hosting = true;
						if(TheDistributedScrabble.DEBUG)System.out.println("Rebind with INetworkManager name successful");
					}catch(Exception e){
						e.printStackTrace();
					}
				}else{ // there should be another peer set as leader
					players.weNeedANewLeader();
				}
				nextPlayingPlayer();
			break;
		}
		synchronized(this.getEnvironment()){
			this.getEnvironment().notify();
		}
	}*/
	
	public void tellToNextToStart(){
		if(this.me.getId() == GameState.MAX_N_PLAYERS) return;
		if(TheDistributedScrabble.DEBUG)System.out.println("Telling to the next.");
		Player next = this.players.getNextToMe(this.me.getId());
		if(TheDistributedScrabble.DEBUG && next == null)System.out.println("No next player found.");
		Vector<Object> data = new Vector<Object>();
		this.players.resort();
		if(TheDistributedScrabble.DEBUG){
			System.out.println("Resorted player list.");
			players.listPlayers();
		}
		if(next!=null) {
			data.add(this.players.getPlayers());
			data.add(playing_player);
			Message m = new Message(me.getRmiName(), next.getRmiName(), this.rmi.getMyIP(), next.getAddress(), Message.MSG_START_GAME, data);
			synchronized(this.env.getWriteBuffer()){
				this.env.getWriteBuffer().add(m);
				this.env.getWriteBuffer().notify();
			}
		}
	}
	
	public boolean checkWord(String word){
		word = word.toLowerCase();
		TreeNode<Character> w = engine.getVocabular().getChildAtElement(word.charAt(0));
		System.out.println(w.getElement());
		return engine.isLegalWord(word, 0, w);
	}
	
	public void emptyWord(){
		zero_score++;
		if(zero_score==6){
			this.gameOver();
			this.gui.getInGameGraphics().hailToTheWinner(me.getId());
		}
	}
	
	public void draw(int n){
		if(n>0){
			if(TheDistributedScrabble.DEBUG)System.out.println("Drawing...");
			String s[] = engine.getNewLetters(n);
			this.me.fillHand(s);
			if(TheDistributedScrabble.DEBUG)System.out.println("Filled the hand.");
			Vector<Object> data = new Vector<Object>();
			data.add(s);
			for(Player p : players.getPlayers()){
				if(p!=this.me){
					Message m = new Message(this.rmi.getName(), p.getRmiName(), this.rmi.getMyIP(), p.getAddress(), Message.MSG_DRAW, data);
					synchronized(this.getEnvironment().getWriteBuffer()){
						this.getEnvironment().getWriteBuffer().add(m);
						this.getEnvironment().getWriteBuffer().notify();
					}
				}
			}
			if(TheDistributedScrabble.DEBUG)System.out.println("Finished the drawing.");
		}
	}
	
	private void gameOver(){
		Vector<Object> data = new Vector<Object>();
		data.addElement(me.getId());
		for(Player p : players.getPlayers()){
			if(!p.equals(me)){
				Message m = new Message(me.getRmiName(), p.getRmiName(), rmi.getMyIP(), p.getAddress(), Message.MSG_END_GAME, data);
				synchronized(this.getEnvironment().getWriteBuffer()){
					this.getEnvironment().getWriteBuffer().add(m);
					this.getEnvironment().getWriteBuffer().notify();
				}
			}
		}
	}
	
	public void sendMove(Move move){
		Vector<Object> data = new Vector<Object>();
		data.addElement(move);
		for(Player p : players.getPlayers()){
			if(!p.equals(me)){
				Message m = new Message(me.getRmiName(), p.getRmiName(), rmi.getMyIP(), p.getAddress(), Message.MSG_MOVE, data);
				synchronized(this.getEnvironment().getWriteBuffer()){
					this.getEnvironment().getWriteBuffer().add(m);
					this.getEnvironment().getWriteBuffer().notify();
				}
			}
		}
		this.me.removeLetterFromHand(move.getWord());
		this.nextTurn();
	}
	
	public void nextTurn(){
		do{
			playing_player++;
			if(playing_player>GameState.MAX_N_PLAYERS) playing_player = 1;
		}while(!players.isThereAPlayerWithThisId(playing_player));
		this.gui.getInGameGraphics().playingPlayer(players.getPlayerNameByID(playing_player), this.isMyTurn());
	}
	
	public void removeLetters(String s){
		this.engine.removeLetters(s);
	}
	
	public void refreshPlayersList(Vector<Player> pl){
		players.refresh(pl);
	}
	
	public void refreshHostingScreen(){
		this.gui.getHome().refreshJoiningPlayers();
	}
	
	public void addNewPlayer(Player p){
		this.players.add(p);
	}
	
	public void addPlayersCounter(){
		this.num_players++;
	}
	
	public void addScore(int score){
		me.addToScore(score);
	}
	
	public void abort(){
		hosting = false;
		this.rmi.close();
		this.players = new PlayerList();
		me = null;
		if(!this.env.getConnectioner().isSuspended())this.env.getConnectioner().safeSuspend();
	}
	
	public void resumeToMenu(){
		this.abort();
		players.reset();
		this.state = GameState.STATE_MENU;
		synchronized(this.env){
			this.env.notify();
		}
	}
	
	public void exitGame(){
		TheDistributedScrabble.end();
		if(TheDistributedScrabble.DEBUG)System.out.println("Main class flag switched");
		env.end();
		if(TheDistributedScrabble.DEBUG)System.out.println("Environment instance flag switched");
	}
	
	private void chooseStartingPlayer(){
		this.playing_player = (new Random().nextInt(GameState.MAX_N_PLAYERS))+1;
		if(TheDistributedScrabble.DEBUG)System.out.println("The first playing player will be the number " + this.playing_player);
	}
	
	public void updateScore(int score, int player_id){
		for(Player p : players.getPlayers()){
			if(p.getId()==player_id)p.setScore(score);
		}
	}
	
	public void setMove(Move mv){
		this.gui.getInGameGraphics().setTheMove(mv);
	}
	
	public void setPlayingPlayer(int n){
		this.playing_player = n;
	}
	
	public void setMe(Player p){
		this.me = p;
	}
	
	public Player getMe(){
		return this.me;
	}
	
	public PlayerList getPlayers(){
		return this.players;
	}
	
	public int getState(){
		return this.state;
	}
	
	public String getMyName(){
		return rmi.getName();
	}
	
	public String getMyAddress(){
		return rmi.getMyIP();
	}
	
	public int getNumPlayers(){
		return this.num_players;
	}
	
	public String getPlayerNameByNumber(int n){
		return players.getPlayerNameByID(n);
	}
	
	public Environment getEnvironment(){
		return this.env;
	}
	
	public int getPlayingPlayer(){
		return this.playing_player;
	}
	
	public Engine getEngine(){
		return this.engine;
	}
	
	public boolean isHosting(){
		return this.hosting;
	}
	
	public boolean isMyTurn(){
		return playing_player == me.getId();
	}

	public void nextPlayingPlayer(){
		if(TheDistributedScrabble.DEBUG)System.out.println("Now is playing " + playing_player);
		if(isMyTurn())return;
		if(playing_player==GameState.MAX_N_PLAYERS)playing_player = this.players.getPlayers().elementAt(0).getId();
		else{
			for(Player p: this.players.getPlayers()){
				if(TheDistributedScrabble.DEBUG)System.out.println("Evaluating next player " + p.getId());
				if(p.getId()>playing_player){
					playing_player = p.getId();
					break;
				}
			}
		}
		if(TheDistributedScrabble.DEBUG)System.out.println("But now is playing " + playing_player);
	}
	
	public void refreshHand() {
		this.gui.getInGameGraphics().refreshLetters(this.me.getHand());
		
	}
}
