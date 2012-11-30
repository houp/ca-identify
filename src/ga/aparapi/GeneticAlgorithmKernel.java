package ga.aparapi;

import ga.base.Params;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import com.amd.aparapi.Kernel;

public class GeneticAlgorithmKernel extends Kernel {

	class Statistics {
		public AtomicInteger better;
		public AtomicInteger same;
		public AtomicInteger worse;

		public Statistics() {
			better = new AtomicInteger();
			same = new AtomicInteger();
			worse = new AtomicInteger();
		}

		public synchronized void reset() {
			better.set(0);
			same.set(0);
			worse.set(0);
		}
	}

	private Statistics crossOverStats;
	private Statistics mutationAndCrossStats;
	private Statistics mutationStats;

	public boolean[][] population;
	private boolean[][] tmpBuffer;
	public double[] fitness;

	private boolean[][][] evolution;

	private int populationSize;
	private int offset;
	private int stepLen;
	private int steps;

	public double[] progressiveFitness;

	public int[] ruleLenTable;
	public int[] radiusTable;

	public int[] picked;

	private int maxRuleLen;

	public GeneticAlgorithmKernel(int populationSize, boolean[][][] evolution) {

		this.evolution = evolution;
		this.populationSize = populationSize;

		picked = new int[populationSize * 2];

		stepLen = evolution[0][0].length;
		steps = evolution[0].length;

		fitness = new double[populationSize * 2];
		progressiveFitness = new double[populationSize * 2];
		radiusTable = new int[populationSize * 2];
		ruleLenTable = new int[populationSize * 2];

		maxRuleLen = ruleLen(Params.maxRadius);

		population = new boolean[populationSize * 2][maxRuleLen];
		tmpBuffer = new boolean[populationSize * 2][maxRuleLen];

		for (int i = 0; i < populationSize; i++) {
			radiusTable[i] = Params.minRadius + (int) ceil(Math.random() * (Params.maxRadius - Params.minRadius));

			ruleLenTable[i] = ruleLen(radiusTable[i]);
			for (int j = 0; j < ruleLenTable[i]; j++) {
				this.population[i][j] = Math.random() > 0.5;
			}
			fitness[i] = calculateFitness(i, evolution);
		}

		offset = 0;

		crossOverStats = new Statistics();
		mutationAndCrossStats = new Statistics();
		mutationStats = new Statistics();

	}

	public void reset() {
		crossOverStats.reset();
		mutationAndCrossStats.reset();
		mutationStats.reset();
	}

	@Override
	public void run() {

		int r1 = pickRule();
		int r2 = pickRule(r1);

		int gid = getGlobalId();
		int i1 = offset + (2 * gid);
		int i2 = i1 + 1;

		double sum = fitness[r1] + fitness[r2];

		if (Math.random() < Params.crossProbability) {
			cross(r1, r2, i1, i2);
		} else {
			copyRule(r1, i1);
			copyRule(r2, i2);
		}

		double afterCross = calculateFitness(i1, evolution) + calculateFitness(i2, evolution);
		if (afterCross > sum) {
			crossOverStats.better.addAndGet(1);
		} else if (afterCross == sum) {
			crossOverStats.same.addAndGet(1);
		} else {
			crossOverStats.worse.addAndGet(1);
		}

		mutate(i1);
		mutate(i2);

		fitness[i1] = calculateFitness(i1, evolution);
		fitness[i2] = calculateFitness(i2, evolution);

		double afterCrossAndMut = fitness[i1] + fitness[i2];

		if (afterCrossAndMut > afterCross) {
			mutationStats.better.addAndGet(1);
		} else if (afterCrossAndMut == afterCross) {
			mutationStats.same.addAndGet(1);
		} else {
			mutationStats.worse.addAndGet(1);
		}

		if (afterCrossAndMut > sum) {
			mutationAndCrossStats.better.addAndGet(1);
		} else if (afterCrossAndMut == sum) {
			mutationAndCrossStats.same.addAndGet(1);
		} else {
			mutationAndCrossStats.worse.addAndGet(1);
		}
	}

	private int ruleLen(int radius) {
		return 1 << (2 * radius + 1);
	}

	private void copyRule(int src, int dst) {
		for (int i = 0; i < ruleLenTable[src]; i++) {
			population[dst][i] = population[src][i];
		}
		fitness[dst] = fitness[src];
		ruleLenTable[dst] = ruleLenTable[src];
		radiusTable[dst] = radiusTable[src];
	}

	public void moveElite() {
		moveElite(1);
	}

	public void moveElite(int count) {
		if (count <= 0)
			return;

		int[] minIndex = new int[count];

		for (int i = offset; i < offset + count; i++) {
			minIndex[i - offset] = i;
		}

		for (int i = offset + count; i < offset + populationSize; i++) {
			for (int j = 0; j < count; j++) {
				if (fitness[i] < fitness[minIndex[j]]) {
					minIndex[j] = i;
					break;
				}
			}
		}

		int offset2 = offset == 0 ? populationSize : 0;

		int[] maxIndex = new int[count];
		for (int i = offset2; i < offset2 + count; i++) {
			maxIndex[i - offset2] = i;
		}

		for (int i = offset2 + count; i < offset2 + populationSize; i++) {
			for (int j = 0; j < count; j++) {
				if (fitness[i] > fitness[maxIndex[j]]) {
					maxIndex[j] = i;
					break;
				}
			}
		}

		for (int j = 0; j < count; j++) {
			copyRule(maxIndex[j], minIndex[j]);
		}
	}

	private boolean[] eval(boolean[] input, int rule) {
		boolean[] result = new boolean[input.length];
		int radius = radiusTable[rule];
		for (int i = 0; i < input.length; i++) {
			int pos = 0;
			for (int j = 0; j < 2 * radius + 1; j++) {
				if (input[utils.Math.modulo(i - radius + j, input.length)]) {
					pos += utils.Math.pow2(2 * radius - j);
				}
			}
			result[i] = population[rule][pos];
		}

		return result;
	}

	private double u(double x) {
		return 3 * pow(x, 2) - 2 * pow(x, 3);
	}

	private double smooth(double x) {
		return u(x);
	}

	private double calculateFitness(int rule, boolean[][][] evolution) {
		int errorCount = 0;
		double result = 0;
		for (int e = 0; e < evolution.length; e++) {
			boolean[] tmp = evolution[e][0];

			double error = 0;

			for (int i = 1; i < steps; i++) {
				boolean[] foo = eval(tmp, rule);
				for (int j = 0; j < stepLen; j++) {
					if (foo[j] != evolution[e][i][j])
						errorCount++;
				}
				error += Math.pow(0.5, i) * errorCount;
				errorCount = 0;
				tmp = foo;
			}

			result += 1.0 - error / (stepLen * (1 - Math.pow(0.5, steps)));
		}
		return smooth(result / evolution.length);
	}

	private int pickRule() {
		return pickRule(-1);
	}

	private int pickRule(int notThis) {
		int result = 0;
		int offset2 = offset > 0 ? 0 : populationSize;

		double r = Math.random() * progressiveFitness[populationSize - 1 + offset2];
		for (int i = offset2; i < offset2 + populationSize; i++) {
			if (progressiveFitness[i] > r && (i != notThis)) {
				result = i;
				picked[result]++;
				return result;
			}
		}
		result = notThis == offset2 + populationSize - 1 ? offset2 + populationSize - 2 : offset2 + populationSize - 1;
		picked[result]++;
		return result;

	}

	private int commonRadius(int rule1, int rule2) {
		if (Math.random() > 0.5) {
			return radiusTable[rule1];
		} else {
			return radiusTable[rule2];
		}
	}

	private void decreaseRadius(int src, int dst, int incr) {

		boolean[] source = population[src];
		boolean[] result = tmpBuffer[dst];
		int radius = radiusTable[src];

		if (radius - incr < Params.minRadius) {
			System.out.println(String.format("Error: src=%d, dst=%d, radius[src]=%d, radius[dst]=%d, incr=%d", src, dst, radiusTable[src], radiusTable[dst], incr));
			throw new RuntimeException("increase below minimum");
		}

		for (int iteration = 0; iteration < incr; iteration++) {

			int[] blackCount = new int[(int) utils.Math.pow2(2 * (radius - 1) + 1)];
			for (int i = 0; i < ruleLen(radius); i++) {
				if (source[i]) {
					blackCount[i / 2 % blackCount.length]++;
				}
			}

			for (int i = 0; i < blackCount.length; i++) {
				result[i] = blackCount[i] >= 2;
			}

			source = result;
			result = iteration % 2 == 0 ? population[dst] : tmpBuffer[dst];
			radius--;
		}

		if (incr == 0) {
			result = population[src];
		}

		if (incr % 2 == 1 || incr == 0) {
			for (int i = 0; i < maxRuleLen; i++) {
				population[dst][i] = result[i];
			}
		}

		ruleLenTable[dst] = ruleLen(radius);
		radiusTable[dst] = radius;
	}

	private void increaseRadius(int src, int dst, int incr) {

		boolean[] source = population[src];
		boolean[] result = tmpBuffer[dst];
		int radius = radiusTable[src];

		if (radius + incr > Params.maxRadius) {
			throw new RuntimeException("trying to get radius: " + radius + " + " + incr);
		}

		for (int iteration = 0; iteration < incr; iteration++) {

			int lenBig = (int) utils.Math.pow2(2 * (radius + 1) + 1);
			int lenBigHalf = lenBig / 2;

			for (int i = 0; i < ruleLen(radius); i++) {
				result[2 * i] = source[i];
				result[2 * i + lenBigHalf] = source[i];
				result[2 * i + 1] = source[i];
				result[2 * i + 1 + lenBigHalf] = source[i];
			}

			source = result;
			result = iteration % 2 == 0 ? population[dst] : tmpBuffer[dst];
			radius++;
		}

		if (incr == 0) {
			result = population[src];

		}

		if (incr % 2 == 1 || incr == 0) {
			for (int i = 0; i < maxRuleLen; i++) {
				population[dst][i] = result[i];
			}
		}

		ruleLenTable[dst] = ruleLen(radius);
		radiusTable[dst] = radius;

	}

	private void changeRadius(int src, int dst, int radius) {
		int incr = radius - radiusTable[src];
		if (incr > 0)
			increaseRadius(src, dst, incr);
		else
			decreaseRadius(src, dst, -1 * incr);

	}

	private void cross(int src1, int src2, int dst1, int dst2) {

		int radius = commonRadius(src1, src2);
		changeRadius(src1, dst1, radius);
		changeRadius(src2, dst2, radius);

		int ruleLen = ruleLenTable[dst1];

		int pos = (int) (floor(Math.random() * (ruleLen - 2)) + 1);

		for (int i = 0; i < pos; i++) {
			boolean tmp = population[dst1][i];
			population[dst1][i] = population[dst2][i];
			population[dst2][i] = tmp;
		}

	}

	private void mutate(int ind) {
		for (int i = 0; i < ruleLenTable[ind]; i++) {
			if (Math.random() < Params.mutationProbability) {
				population[ind][i] = !population[ind][i];
			}
		}

		if (Math.random() < Params.downScaleProbability && radiusTable[ind] > Params.minRadius) {
			decreaseRadius(ind, ind, 1);
		}
		if (Math.random() < Params.upScaleProbability && radiusTable[ind] < Params.maxRadius) {
			increaseRadius(ind, ind, 1);
		}

	}

	public int bestFitnessIndex() {
		int result = offset;
		for (int i = offset + 1; i < populationSize + offset; i++) {
			if (fitness[i] > fitness[result]) {
				result = i;
			}
		}
		return result;
	}

	public double bestFitness() {
		return fitness[bestFitnessIndex()];
	}

	public boolean[] bestRule() {
		return population[bestFitnessIndex()];
	}

	public int getOffset() {
		return offset;
	}

	public double minFitness() {
		double result = fitness[offset];
		for (int i = offset + 1; i < offset + populationSize; i++) {
			if (result > fitness[i]) {
				result = fitness[i];
			}
		}

		return result;
	}

	public double avgFitness() {
		double sum = 0;
		for (int i = offset + 1; i < populationSize + offset; i++) {
			sum += fitness[i];
		}
		return sum / populationSize;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public void calculateProgressiveFitness() {
		progressiveFitness[offset] = fitness[offset];
		for (int i = 1 + offset; i < offset + populationSize; i++) {
			progressiveFitness[i] = fitness[i] + progressiveFitness[i - 1];
		}

	}

	private String getRuleString(boolean[] rule, int len) {
		StringBuilder sb = new StringBuilder(len);
		for (int i = 0; i < len; i++) {
			sb.append(rule[i] ? '1' : '0');
		}
		return sb.toString();
	}

	public double getDiversity() {
		Set<String> rules = new HashSet<>();
		for (int i = offset; i < populationSize + offset; i++) {
			rules.add(getRuleString(population[i], ruleLenTable[i]));
		}
		return (double) rules.size() / (double) populationSize;
	}

	public double[] getCrossStats() {
		return getStats(crossOverStats);
	}

	public double[] getMutationAndCrossStats() {
		return getStats(mutationAndCrossStats);
	}

	public double[] getMutationStats() {
		return getStats(mutationStats);
	}

	private double[] getStats(Statistics s) {
		int better = s.better.get();
		int same = s.same.get();
		int worse = s.worse.get();

		int total = better + same + worse;
		return new double[] { (double) better / total, (double) same / total, (double) worse / total };
	}

}
