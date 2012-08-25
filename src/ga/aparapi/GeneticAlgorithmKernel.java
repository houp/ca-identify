package ga.aparapi;

import ga.base.Params;

import com.amd.aparapi.Kernel;

public class GeneticAlgorithmKernel extends Kernel {

	private boolean[][] population;
	private double[] fitness;
	private boolean[][] evolution;
	private int populationSize;
	private int ruleLen;
	private int offset;
	private int stepLen;
	private int steps;
	private int radius;
	private double[] progressiveFitness;

	public GeneticAlgorithmKernel(int populationSize, int radius,
			boolean[][] evolution) {
		this.evolution = evolution;
		fitness = new double[populationSize * 2];		
		progressiveFitness = new double[populationSize * 2];
		this.populationSize = populationSize;
		this.ruleLen = 1 << (2 * radius + 1);
		this.radius = radius;
		population = new boolean[populationSize*2][ruleLen];
		for (int i = 0; i < populationSize; i++) {
			for (int j = 0; j < ruleLen; j++) {
				this.population[i][j] = Math.random() > 0.5;
			}
			calculateFitness(i);
		}
	
		offset = -1;
		stepLen = evolution[0].length;
		steps = evolution.length;

	}

	@Override
	public void run() {
		int r1 = pickRule();
		int r2 = pickRule(r1);

		int i1 = offset + (2 * getGlobalId());
		int i2 = i1 + 1;

		cross(r1, r2, i1, i2);
		mutate(i1);
		mutate(i2);

		fitness[i1] = calculateFitness(i1);
		fitness[i2] = calculateFitness(i2);
	}

	private boolean[] eval(boolean[] input, int rule) {
		boolean[] result = new boolean[input.length];

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

	private double calculateFitness(int rule) {
		int errorCount = 0;
		boolean[] tmp = evolution[0];

		for (int i = 1; i < steps; i++) {
			boolean[] foo = eval(tmp, rule);
			for (int j = 0; j < stepLen; j++) {
				if (foo[j] != evolution[i][j])
					errorCount++;
			}
			tmp = foo;
		}

		return 1.0 - ((double) errorCount / (steps * stepLen));
	}

	private int pickRule() {
		return pickRule(-1);
	}

	/*private int pickRule(int notThis) {
		int start = (int) floor(Math.random() * (populationSize - 1)) + offset;
		int end = start
				+ 1
				+ (int) floor(Math.random()
						* ((populationSize - 1) - (start - offset)));

		if (start == notThis) {
			start++;
		}
		int off = offset == 0 ? populationSize : 0;

		double max = fitness[start+off];
		
		int maxInd = start;

		for (int i = start + 1; i < end; i++) {
			if (fitness[i+off] > max && i != notThis) {
				max = fitness[i+off];
				maxInd = start;
			}
		}

		return maxInd;
	}*/
	
	private int pickRule(int notThis) {
		double r = Math.random() * progressiveFitness[populationSize-1+offset];
		for(int i=offset;i<offset+populationSize;i++) {
			if(progressiveFitness[i] > r && (i!=notThis)) return i;
		}
		return offset+populationSize-1;
	}

	
	
	
	private void cross(int r1, int r2, int i1, int i2) {
		int pos = (int) (floor(Math.random() * (ruleLen-2))+1);

		for (int i = 0; i < pos; i++) {
			population[i1][i] = population[r1][i];
			population[i2][i] = population[r2][i];
		}

		for (int i = pos; i < ruleLen; i++) {
			population[i2][i] = population[r1][i];
			population[i1][i] = population[r2][i];
		}
	}

	private void mutate(int ind) {
		for (int i = 0; i < ruleLen; i++) {
			if (Math.random() < Params.mutationProbability) {
				population[ind][i] = !population[ind][i];
			}
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
		progressiveFitness[0+offset] = fitness[0+offset];
		for(int i=1+offset;i<offset+populationSize;i++) {
			progressiveFitness[i] = fitness[i] + progressiveFitness[i-1];
		}
	
	}
	
}
