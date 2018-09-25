

import java.util.Vector;

public class Connectioner extends Thread {
	private final int DEFAULT_TIMEOUT = 3000;
	private Vector<Message> write;
	private GameState gs;
	private boolean end, thread_suspended;
	
	public Connectioner(Vector<Message> write, GameState gs){
		this.write = write;
		this.gs = gs;
		end = false;
		thread_suspended = true;
	}
	
		@Override
	public synchronized void run(){
			while (!end) {
				try {
	               	synchronized(this) {
	               	     while (thread_suspended)
	               		wait();
	                    	if(TheDistributedScrabble.DEBUG)System.out.println("Out of suspend");
	               	}
	            	}catch (InterruptedException e) {
					e.printStackTrace();
				}
				try {
					this.wait(DEFAULT_TIMEOUT);
					if(TheDistributedScrabble.DEBUG)System.out.println(TheDistributedScrabble.ANSI_YELLOW + "Connectioner: wait finished" + TheDistributedScrabble.ANSI_RESET);
				}catch (InterruptedException e) {
					e.printStackTrace();
					//if(TheDistributedScrabble.DEBUG)System.out.println("Hosting " + this.gs.isHosting() + " Host is null " + this.gs.getPlayers().getTheHost()!=null);
				}
				pingToNext();
			}
	}
		
	private void pingToNext(){
          PlayerList pl = this.gs.getPlayers();
          if(TheDistributedScrabble.DEBUG)System.out.println(TheDistributedScrabble.ANSI_YELLOW + "Pinging to the next" + TheDistributedScrabble.ANSI_RESET);
          synchronized(pl){
          	if(TheDistributedScrabble.DEBUG)System.out.println(TheDistributedScrabble.ANSI_YELLOW + "In the critical section" + TheDistributedScrabble.ANSI_RESET);
			if(!pl.getPlayers().isEmpty()){
				if(this.gs.getState() == GameState.STATE_HOST){
					for(Player p : pl.getPlayers()){
						if(TheDistributedScrabble.DEBUG)System.out.println("In the hosting for " + p.getName());
						if(!p.getRmiName().equals(this.gs.getMe().getRmiName()) && !this.gs.isAlive(p)){
							this.gs.peerIsDead(p);
						}
					}	
					if(TheDistributedScrabble.DEBUG)this.gs.getPlayers().listPlayers();
				}
				else{
					if(TheDistributedScrabble.DEBUG)System.out.println(TheDistributedScrabble.ANSI_YELLOW + "There are players" + TheDistributedScrabble.ANSI_RESET);
					Player p = this.gs.getPlayers().getNextToMe(this.gs.getMe().getId());
					if(TheDistributedScrabble.DEBUG)pl.listPlayers();
					if(p!= null && TheDistributedScrabble.DEBUG)System.out.println(TheDistributedScrabble.ANSI_YELLOW + "The next to me is " + p.getRmiName() + " @ " + p.getAddress() + TheDistributedScrabble.ANSI_RESET);
					if(p!=null && p!=this.gs.getMe()){
						boolean b = this.gs.isAlive(p);
						if(TheDistributedScrabble.DEBUG)System.out.println(TheDistributedScrabble.ANSI_YELLOW + "Connectioner: The next is " + b + TheDistributedScrabble.ANSI_RESET);
						if(!b){
							this.gs.peerIsDead(p);
							if(TheDistributedScrabble.DEBUG)this.gs.getPlayers().listPlayers();
						}
					}
				}
			}
		}
	}
		
		/*
		@Override
	public synchronized void run() {
		Vector<Player> pl = this.gs.getPlayers().getPlayers();
		while (!end) {
			try {
                synchronized(this) {
                    while (thread_suspended)
                        wait();
                    if(TheDistributedScrabble.DEBUG)System.out.println("Out of suspend");
                }
            } catch (InterruptedException e) {
				e.printStackTrace();
			}
			try {
				this.wait(DEFAULT_TIMEOUT);
				if(TheDistributedScrabble.DEBUG)System.out.println("Connectioner: wait finished");
				pl = this.gs.getPlayers().getPlayers();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}synchronized(pl){
				if(TheDistributedScrabble.DEBUG)System.out.println("Hosting " + this.gs.isHosting() + " Host is null " + this.gs.getPlayers().getTheHost()!=null);
				if (gs.isHosting() && !pl.isEmpty()) {
					while(checkForPeersAlive());
				} else if (!gs.isHosting() && this.gs.getPlayers().getTheHost()!=null) {
					Player host = this.gs.getPlayers().getTheHost();
					Vector<Object> payload = new Vector<Object>();
					payload.add(host.getName());
					Message m = new Message(gs.getMyName(), host.getRmiName(), gs.getMyAddress(), host.getAddress(), Message.MSG_IS_ALIVE, payload);
					synchronized(write){
						write.add(m);
						write.notify();
					}
				}
			}
		}
	}
		*/
		
		/*
	private void pingToBack(PlayerList pl){
		Player me = this.gs.getMe();
		Player p = pl.getPreviousToMe(me.getId());
		if(TheDistributedScrabble.DEBUG)pl.listPlayers();
		if(p!= null && TheDistributedScrabble.DEBUG)System.out.println("The previous is " + p.getRmiName() + "@" + p.getAddress());
		if(p!=null && p!=this.gs.getMe()){
			Message m = new Message(me.getRmiName(), p.getRmiName(), me.getAddress(), p.getAddress(), Message.MSG_IS_ALIVE, null);
			synchronized(this.gs.getEnvironment().getWriteBuffer()){
				this.gs.getEnvironment().getWriteBuffer().add(m);
				this.gs.getEnvironment().getWriteBuffer().notify();
			}
		}
	}
		
	private void checkForNextAlive(){
		Player p = this.gs.getPlayers().getNextToMe(this.gs.getMe().getId());
		if(p!=null){
			long time = System.currentTimeMillis() - p.getLastSeen();
			if (time >= (2*DEFAULT_TIMEOUT)) { // peer is probably dead
				gs.peerIsDead(p);
			}
		}
	}	
	*/	
	/*	
	private boolean checkForPeersAlive(){
		Vector<Player> pl = this.gs.getPlayers().getPlayers();
		for(Player p : pl){
			if(TheDistributedScrabble.DEBUG)System.out.println("Connectioner: There are players");
			if(p!=gs.getMe()){
				if(TheDistributedScrabble.DEBUG)System.out.println("Connectioner: And at least one it's not me.");
				long time = System.currentTimeMillis() - p.getLastSeen();
				if (time >= (2*DEFAULT_TIMEOUT)) { // peer is probably dead
					gs.peerIsDead(p);
					return true;
				}
			}
		}
		return false;
	}*/
		
	public synchronized void end(){
		end = true;
	}
	
	public boolean isSuspended(){
		return this.thread_suspended;
	}
	
	public synchronized void safeSuspend(){
		thread_suspended = !thread_suspended;
		if(TheDistributedScrabble.DEBUG){
			if(thread_suspended)System.out.println("Connectioner not working");
			else{
				System.out.println("Connectioner working");
			}
		}

        if (!thread_suspended)
            notify();
	}
	
	public synchronized void go(){
		end = false;
	}
	
	public void setGS(GameState gs){
		this.gs = gs;
	}
}

