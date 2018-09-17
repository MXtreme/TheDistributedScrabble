

import java.io.Serializable;
import java.util.UUID;
import java.util.Vector;

public class Message implements Serializable{
	private static final long serialVersionUID = 1396239693826474352L;
	static final int MSG_OK = 1;
	static final int MSG_ACK = 2;
	static final int MSG_ERROR = 3;
	static final int MSG_MOVE = 4;
	static final int MSG_END_GAME = 5;
	static final int MSG_JOIN_GAME_REQUEST = 7;
	static final int MSG_IS_ALIVE = 8;
	static final int MSG_START_GAME = 9;
	static final int MSG_STATE_GAME = 10;
	static final int MSG_DRAW = 11;
	static final int MSG_CLIENT_EXITUS = 12;
	static final int MSG_RDY = 13;
	
	private Object id;
	private int type;
	private String sender, dest, address_sender, address_dest;
	private Vector<Object> data;
	
	public Message(String sender, String dest, String address_sender, String address_dest, int type, Vector<Object> data){
		this.id = UUID.randomUUID();
		this.sender = sender;
		this.dest = dest;
		this.address_sender = address_sender;
		this.address_dest = address_dest;
		this.type = type;
		this.data = data;
	}
	
	
	void setID(int another_id){
		this.id = another_id;
	}
	
	void setType(int another_type){
		this.type = another_type;
	}
	
	void setData(Vector<Object> another_data){
		this.data = another_data;
	}
	
	public void setSender(String name){
		this.sender = name;
	}
	
	public void setDest(String dest){
		this.dest = dest;
	}
	
	public void setAddressSender(String address){
		this.address_sender= address;
	}
	
	public void setAddressDest(String address){
		this.address_dest= address;
	}
	
	public String getSender(){
		return this.sender;
	}
	
	public String getDest(){
		return this.dest;
	}
	
	public String getAddressSender(){
		return this.address_sender;
	}
	
	public String getAddressDest(){
		return this.address_dest;
	}
	
	Object gedID(){
		return this.id;
	}
	
	int getType(){
		return this.type;
	}
	
	Vector<Object> getData(){
		return this.data;
	}
}

