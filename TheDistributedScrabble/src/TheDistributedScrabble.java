public class TheDistributedScrabble {
	private static final Object monitor = new Object();
	private static boolean end = false;
	static boolean DEBUG = false;
	
	public static void main(String[] args) {
		System.out.println("Welcome to The Distributed Scrabble");
		int n = 4;
		if(args.length==1){
			if(isInteger(args[0])) n = Integer.parseInt(args[0]);
			else TheDistributedScrabble.DEBUG = args[0].equals("debug") ? true : false;
		}else if(args.length==2) {
			if(isInteger(args[0])) {
				n = Integer.parseInt(args[0]);
				TheDistributedScrabble.DEBUG = args[1].equals("debug") ? true : false;
			}else if(isInteger(args[1])) {
				n = Integer.parseInt(args[1]);
				TheDistributedScrabble.DEBUG = args[0].equals("debug") ? true : false;
			}
		}
		while(!end){
			Environment environment = new Environment(n);
			environment.start();
			try{
				synchronized(TheDistributedScrabble.monitor){
					TheDistributedScrabble.monitor.wait();
				}
				if(TheDistributedScrabble.end)environment.join();
			}
			catch(InterruptedException e){
				e.printStackTrace();
			}
		}
		System.out.println("Thank you for playing! Bye bye!");
		System.exit(0);
	}
	
	public static Object getMonitor(){
		return TheDistributedScrabble.monitor;
	}
	
	public static void end(){
		TheDistributedScrabble.end = !end;
	}

	private static boolean isInteger(String str) {
	    if (str == null) {
	        return false;
	    }
	    if (str.isEmpty()) {
	        return false;
	    }
	    int i = 0;
	    if (str.charAt(0) == '-') {
	        if (str.length() == 1) {
	            return false;
	        }
	        i = 1;
	    }
	    for (; i < str.length(); i++) {
	        char c = str.charAt(i);
	        if (c < '0' || c > '9') {
	            return false;
	        }
	    }
	    return true;
	}
	
}
