import java.rmi.RemoteException;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Environment extends Thread {
	Vector<Message> read, write;
	Dispatcher_t disp;
	Sender_t send;
	RMINetworkManager inm;
	Connectioner connectioner;
	GameState gs;
	boolean end;
	
	/*****DEBUG*****/
	JFrame f;
	JPanel p;
	JButton bt, bt2;
	
	public Environment(int n){
		if(TheDistributedScrabble.DEBUG)System.out.println("New Environment created.");
		end = false;
		read = new Vector<Message>();
		write = new Vector<Message>();
		rmiInit();
		gs = new GameState(GameState.STATE_MENU, inm, n, this);
		disp = new Dispatcher_t(read, write, gs);
		send = new Sender_t(write,inm, gs);
		disp.start();
		send.start();
		connectioner = new Connectioner(write, gs);
		connectioner.start();
	}
	
	@Override
	public void run(){
		while(!end){
			gs.doThings();
			try{
				synchronized(this){
					wait();
				}
			}catch(InterruptedException e){
				e.printStackTrace();
			}
		}
		if(TheDistributedScrabble.DEBUG)System.out.println("Environment out of cycle");
		try{
			disp.end();
			disp.join();
			if(TheDistributedScrabble.DEBUG)System.out.println("Dispatcher join successful");
			send.end();
			send.join();
			if(TheDistributedScrabble.DEBUG)System.out.println("Sender join successful");
		}catch(InterruptedException e){
			e.printStackTrace();
		}
		synchronized(TheDistributedScrabble.getMonitor()){
			TheDistributedScrabble.getMonitor().notify();
		}
	}
	
	private void rmiInit(){
		try {
			inm = new RMINetworkManager(read, write);
		} catch (RemoteException e) {
			System.out.println("Cannot initialize RMINetworkManager.");
			e.printStackTrace();
		}
	}
	
	public void reinitialize(){
		synchronized (TheDistributedScrabble.getMonitor()) {
			TheDistributedScrabble.getMonitor().notify();
			if(TheDistributedScrabble.DEBUG)System.out.println("Environment closed");
		}
	}
	
	public synchronized void end(){
		end = !end;
		notify();
	}
	
	public Connectioner getConnectioner(){
		return this.connectioner;
	}
	
	public Vector<Message> getWriteBuffer(){
		return this.write;
	}
}
