package ca.base;

import java.util.Arrays;

public class Rule implements Cloneable {
	private boolean[] lut;
	private int radius;
	
	public Rule() {
	}

	public Rule(long number, int radius) {
		
		this.radius = radius;
		lut = getLookupTable(number);
	}

	public Rule(Rule that) {
		this(that.lut);
	}

	public static Rule random(int radius) {
		boolean[] lut = new boolean[(int)utils.Math.pow2(2*radius+1)];
	
		for(int i=0;i<lut.length;i++) {
			lut[i] = Math.random() > 0.5;
		}
		
		return new Rule(lut);
	}
	
	public Rule(boolean[] lut) {
		this.lut = Arrays.copyOf(lut, lut.length);
		radius = (utils.Math.log2(lut.length)-1)/2;
	}
	
	public Rule(boolean[] lut, int len) {
		this.lut = Arrays.copyOf(lut, len);
		radius = (utils.Math.log2(len)-1)/2;
	}
	
	public long getNumber() {
		return getNumberFromLookupTable(lut);
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof Rule))
			return false;
		Rule that = (Rule) o;
		
		if(that.lut.length != this.lut.length) { 
			return false; 
		}
		
		for(int i=0;i<lut.length;i++) {
			if(lut[i]!=that.lut[i]) return false;
		}
		
		return true;
	}

	public Rule decreaseRadius() {
		if (radius > 1) {
			boolean[] lutBig = lut;
			int[] blackCount = new int[(int) utils.Math.pow2(2 * (radius - 1) + 1)];
			for (int i = 0; i < lutBig.length; i++) {
				if (lutBig[i]) {
					blackCount[i / 2 % blackCount.length]++;
				}
			}

			boolean[] lutSmall = new boolean[blackCount.length];
			for (int i = 0; i < lutSmall.length; i++) {
				lutSmall[i] = blackCount[i] >= 2;
			}

			return new Rule(lutSmall);
		}
		return new Rule(this);
	}

	public Rule increaseRadius() {
		boolean[] lutSmall = lut;
		int lenBig = (int) utils.Math.pow2(2 * (radius + 1) + 1);
		int lenBigHalf = lenBig / 2;
		boolean[] lutBig = new boolean[lenBig];
		for (int i = 0; i < lutSmall.length; i++) {
			lutBig[2 * i] = lutSmall[i];
			lutBig[2 * i + lenBigHalf] = lutSmall[i];
			lutBig[2 * i + 1] = lutSmall[i];
			lutBig[2 * i + 1 + lenBigHalf] = lutSmall[i];
		}

		return new Rule(lutBig);
	}

	public Rule assignRadius(int radius) {
		return changeRadius(radius - this.getRadius());
	}

	public Rule changeRadius(int offset) {
		
		Rule result = new Rule(this);

		if(offset != 0) {
			for (int i = 0; i < Math.abs(offset); i++) {
				result = offset > 0 ? result.increaseRadius() : result.decreaseRadius();
			}
		}
		
		return result;
	}

	private boolean[] getLookupTable(long number) {
		boolean[] result = new boolean[(int) utils.Math.pow2(2 * radius + 1)];

		for (int i = 0; i < result.length; i++) {
			result[i] = ((number >> i) & 1) == 1;
		}

		return result;
	}

	private long getNumberFromLookupTable(boolean[] lut) {
		long result = 0;
		for (int i = 0; i < lut.length; i++) {
			if (lut[i]) {
				result += utils.Math.pow2(i);
			}
		}
		return result;
	}

	public boolean[] eval(boolean[] input) {
		boolean[] result = new boolean[input.length];

		for (int i = 0; i < input.length; i++) {
			int pos = 0;
			for (int j = 0; j < 2 * radius + 1; j++) {
				if (input[utils.Math.modulo(i - radius + j, input.length)]) {
					pos += utils.Math.pow2(2 * radius - j);
				}
			}

			//result[i] = ((number >> pos) & 1) == 1;
			result[i] = lut[pos];
		}

		return result;
	}

	public int getRadius() {
		return radius;
	}

	public int getRuleBitLenght() {
		return (int) utils.Math.pow2(2 * radius + 1);
	}

	public boolean[] getLut() {
		return lut;
	}
	
	@Override
	public String toString() {
		return "Rule: " + getNumber() + " @ radius: " + getRadius();
	}
}
