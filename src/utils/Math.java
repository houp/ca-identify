package utils;

public class Math {
	public static int modulo(int i, int len) {
		while (i < 0) {
			i += len;
		}
		
		return i % len;
	}

	public static long pow2(int a) {
		return 1L << a;
		
	}

}
