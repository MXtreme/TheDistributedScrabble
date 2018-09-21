import java.util.Vector;

public class Sender_t extends Thread {
	private Vector<Message> buffer;
	private Message current;
	private RMINetworkManager rmi;
	private GameState gs;
	private boolean end = false;
	
	public Sender_t(Vector<Message> v, RMINetworkManager rmi, GameState gs){
		this.buffer = v;
		this.rmi = rmi;
		this.gs = gs;
	}
	
	@Override
	public void run(){
		if(TheDistributedScrabble.DEBUG)System.out.println("Sender started.");
		while(!end){
			if(buffer.isEmpty()){
				try{
					synchronized(buffer){
						buffer.wait();
					}
				}catch(InterruptedException e){
					e.printStackTrace();
				}
			}else{
				current = buffer.firstElement();
				doThings();
				synchronized (buffer) {
					buffer.remove(0);
				}
				current = null;
			}
		}
	}
	
	private void doThings(){
		if(current!=null){
			if(!rmi.sendMessage(current))cantSend();
		}
	}
	
	private void cantSend(){
		if(TheDistributedScrabble.DEBUG)System.out.println("Couldn't send message to " + current.getAddressDest() + " of type " + current.getType());
		switch(current.getType()){
			case Message.MSG_OK:break;
			case Message.MSG_ACK:break;
			case Message.MSG_ERROR:break;
			case Message.MSG_MOVE:break;
			case Message.MSG_END_GAME:break;
			case Message.MSG_JOIN_GAME_REQUEST:break;
			case Message.MSG_IS_ALIVE:	// a peer failed to check the connectivity with the host
				gs.peerIsDead(this.gs.getPlayers().getNextToMe(this.gs.getMe().getId()));
				break;
			case Message.MSG_START_GAME:break;
			case Message.MSG_STATE_GAME:break;
			case Message.MSG_DRAW:break;
			case Message.MSG_CLIENT_EXITUS:break;
			case Message.MSG_RDY:break;
		}
	}
	
	public void end(){
		end = !end;
		synchronized(buffer){
			buffer.notify();
		}
	}
}
