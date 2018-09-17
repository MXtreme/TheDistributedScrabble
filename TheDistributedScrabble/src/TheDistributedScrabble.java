public class TheDistributedScrabble {
	private static final Object monitor = new Object();
	private static boolean end = false;
	static boolean DEBUG = true;
	
	public static void main(String[] args) {
		System.out.println("Welcome to The Distributed Scrabble");
		int n = 4;
		if(args.length>0){
			for(String s : args){
				TheDistributedScrabble.DEBUG = s=="debug" ? true : false;
				if(isInteger(s)) n = Integer.parseInt(s);
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
