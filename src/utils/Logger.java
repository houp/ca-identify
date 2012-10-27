package utils;

public class Logger {

	public static boolean showDebug = true;
	
	public static void debug(String s) {
		if(showDebug)
			System.out.println("DEBUG: "+s);
	}
	
	public static void info(String s) {
		System.out.println("INFO: "+s);
	}
	
	public static void result(String s) {
		System.out.println("RESULT: "+s);
	}
	
}
