import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ExportException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Enumeration;
import java.util.Vector;

public class RMINetworkManager implements INetworkManager {
	private Vector<Message> buffer, write;
	private String myIp;
	private INetworkManager stub;
	private Registry myRegistry;
	private String myName;
	
	public RMINetworkManager(Vector<Message> v, Vector<Message> write) throws RemoteException{
		super();
		this.buffer = v;
		this.write = write;
		this.myIp = getIP();
		System.setProperty("java.rmi.server.hostname", myIp);
		System.setProperty("sun.rmi.transport.connectionTimeout", new Integer(3000).toString());
		this.myRegistry = LocateRegistry.createRegistry(2336);
		if(TheDistributedScrabble.DEBUG)System.out.println("Registry created.");
	}

	@Override
	public void manageMsg(Message m) {
		synchronized(buffer){
			buffer.addElement(m);
			buffer.notify();
		}
	}
	
	public boolean selfExport(){
		System.out.println("Exporting myself.");
		try {
			stub = (INetworkManager)UnicastRemoteObject.exportObject(this, 2336);
			if(TheDistributedScrabble.DEBUG)System.out.println("Stub created.");
			myRegistry.rebind(myName, stub);
			if(TheDistributedScrabble.DEBUG)System.out.println(myName + " bound.");
			System.out.println("Exporting was successful");
			return true;
		} catch (AccessException e) {
			e.printStackTrace();
		} 
		catch(ExportException e){
			if(TheDistributedScrabble.DEBUG)System.out.println("The object is already exported. So we just rebind the name.");
			try {
				myRegistry.rebind(myName, stub);
			} catch (AccessException e1) {
				e1.printStackTrace();
			} catch (RemoteException e1) {
				e1.printStackTrace();
			}
			if(TheDistributedScrabble.DEBUG)System.out.println(myName + " bound at the second try.");
			//e.printStackTrace();
		}
		catch (RemoteException e) {	
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean connect(String host_ip, String name){
		try{	        
			System.out.println("Trying to locate INetworkManager...");
			if(!validIP(host_ip))throw new RemoteException();
			System.out.println("Registry found.");
            System.out.println("INetworkManager found.");
            Vector<Object> data = new Vector<Object>();
            Message m = new Message(myName, name, myIp, host_ip, Message.MSG_JOIN_GAME_REQUEST, data);
            synchronized(write){
            	write.add(m);
            	write.notify();
            }
            if(TheDistributedScrabble.DEBUG)System.out.println("Join request put in the write buffer");
            return true;
		}catch(RemoteException e){
			System.out.println("Can't connect to " + host_ip + ". Check the IP address or your connection");
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean close(){
		try {
			System.out.println("Closing the connection.");
			myRegistry.unbind(myName);
			if(TheDistributedScrabble.DEBUG) System.out.println("Unbinded from the registry");
			UnicastRemoteObject.unexportObject(this, false);
			if(TheDistributedScrabble.DEBUG) System.out.println("RMINetworkManager unexported");
			System.out.println("Connection closed.");
			return true;
		} catch (RemoteException e) {
			e.printStackTrace();
		}
        catch (NotBoundException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	
	public boolean sendMessage(Message m){
		Registry registry;
		String dest = m.getDest();
		String ip = m.getAddressDest();
		try {
			if(TheDistributedScrabble.DEBUG)System.out.println("Sending to "+ dest + " type: " + m.getType() + " at address " + ip);
			registry = LocateRegistry.getRegistry(ip, 2336);
			if(TheDistributedScrabble.DEBUG)System.out.println("Registry of " + dest + " located.");
	        INetworkManager rminm = (INetworkManager) registry.lookup(dest);
	        if(TheDistributedScrabble.DEBUG)System.out.println("Found rmi " + dest + ".");
	        rminm.manageMsg(m);
	        if(TheDistributedScrabble.DEBUG)System.out.println("Sent message.");
	        if(TheDistributedScrabble.DEBUG)if(m.getType()==Message.MSG_ACK)close();
			return true;
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (NotBoundException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	private String getIP(){
		try {
            for (Enumeration<NetworkInterface> ni = NetworkInterface.getNetworkInterfaces(); ni.hasMoreElements();) {
                NetworkInterface current_interface = (NetworkInterface) ni.nextElement();
                for (Enumeration<InetAddress> current_ip_address = current_interface.getInetAddresses(); current_ip_address.hasMoreElements();) {
                    InetAddress inet_address = (InetAddress) current_ip_address.nextElement();
                    if (!inet_address.isLoopbackAddress() && inet_address instanceof Inet4Address) {
                        String ip_address = inet_address.getHostAddress().toString();
                        return ip_address;
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
            return null;
        }
		return null;
	}
	
	public String getMyIP(){
		return this.myIp;
	}
	
	public String getName(){
		return this.myName;
	}
	
	public void setName(String name){
		this.myName = name;
	}
	
	public boolean validIP (String ip) {
	    try {
	        if ( ip == null || ip.isEmpty() ) {
	            return false;
	        }

	        String[] parts = ip.split( "\\." );
	        if ( parts.length != 4 ) {
	            return false;
	        }

	        for ( String s : parts ) {
	            int i = Integer.parseInt( s );
	            if ( (i < 0) || (i > 255) ) {
	                return false;
	            }
	        }
	        if ( ip.endsWith(".") ) {
	            return false;
	        }

	        return true;
	    } catch (NumberFormatException nfe) {
	        return false;
	    }
	}
}

