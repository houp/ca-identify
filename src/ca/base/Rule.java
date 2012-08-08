package ca.base;

public class Rule implements Cloneable {
	private long number;
	private int radius;

	private boolean[] lut;
	
	public Rule() {
	}

	public Rule(long number, int radius) {
		this.number = number;
		this.radius = radius;
		lut = getLookupTable();
	}

	public Rule(Rule that) {
		this(that.number, that.radius);
	}

	public static Rule random(int radius) {
		long number = (long)(Math.random() * (double)utils.Math.pow2((int)utils.Math.pow2(2*radius+1)));
		return new Rule(number, radius);
	}
	
	public long getNumber() {
		return number;
	}

	public void setNumber(long number) {
		if(number != this.number) {
			this.number = number;
			this.lut = getLookupTable();
		}
	}

	
	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof Rule))
			return false;
		Rule that = (Rule) o;
		return that.number == number && that.radius == radius;
	}

	@Override
	public int hashCode() {
		return (int) (31 * (17 + number) + radius);
	}

	public Rule decreaseRadius() {
		if (radius > 1) {
			boolean[] lutBig = getLookupTable();
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

			return new Rule(getNumberFromLookupTable(lutSmall), radius - 1);
		}
		return new Rule(this);
	}

	public Rule increaseRadius() {
		boolean[] lutSmall = getLookupTable();
		int lenBig = (int) utils.Math.pow2(2 * (radius + 1) + 1);
		int lenBigHalf = lenBig / 2;
		boolean[] lutBig = new boolean[lenBig];
		for (int i = 0; i < lutSmall.length; i++) {
			lutBig[2 * i] = lutSmall[i];
			lutBig[2 * i + lenBigHalf] = lutSmall[i];
			lutBig[2 * i + 1] = lutSmall[i];
			lutBig[2 * i + 1 + lenBigHalf] = lutSmall[i];
		}

		return new Rule(getNumberFromLookupTable(lutBig), radius + 1);
	}

	public Rule assignRadius(int radius) {
		return changeRadius(radius - this.radius);
	}

	public Rule changeRadius(int offset) {
		
		Rule result = new Rule(this);

		if(offset > 0) {
			for (int i = 0; i < Math.abs(offset); i++) {
				result = offset > 0 ? result.increaseRadius() : result.decreaseRadius();
			}
		}
		
		return result;
	}

	private boolean[] getLookupTable() {
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

	public void setRadius(int radius) {
		this.radius = radius;
	}

	public int getRuleBitLenght() {
		return (int) utils.Math.pow2(2 * radius + 1);
	}

	@Override
	public String toString() {
		return "Rule: " + number + " @ radius: " + radius;
	}
}
