package ga.aparapi;

import ga.base.Params;
import ca.base.Rule;

public class AparapiRunner {
	private static boolean[][] getRandomTestCase(Rule r, int spaceLen,
			int timeLen) {
		boolean[][] result = new boolean[timeLen][spaceLen];

		for (int i = 0; i < spaceLen; i++) {
			result[0][i] = Math.random() > 0.5;
		}

		for (int t = 1; t < timeLen; t++) {
			result[t] = r.eval(result[t - 1]);
		}

		return result;
	}

	private static boolean[][] getTestCase(Rule r, int spaceLen, int timeLen) {
		boolean[][] result = new boolean[timeLen][spaceLen];

		result[0][spaceLen / 2] = true;

		for (int t = 1; t < timeLen; t++) {
			result[t] = r.eval(result[t - 1]);
		}

		return result;
	}

	private static boolean[][] getTestCase2(Rule r, int spaceLen, int timeLen) {
		boolean[][] result = new boolean[timeLen][spaceLen];

		for (int i = 0; i < spaceLen; i++) {
			result[0][i] = true;
		}

		result[0][spaceLen / 2] = false;

		for (int t = 1; t < timeLen; t++) {
			result[t] = r.eval(result[t - 1]);
		}

		return result;
	}

	public static void main(String[] args) {

		Rule r = new Rule(Params.rule, Params.radius);

		boolean[][] evolution1 = getRandomTestCase(r, 79, 80);
		boolean[][] evolution2 = getTestCase2(r, 79, 80);
		boolean[][] evolution5 = getTestCase(r, 79, 80);

		boolean[][][] e = { evolution2, evolution1, evolution5 };

		GeneticAlgorithmKernel kernel = new GeneticAlgorithmKernel(Params.populationCount, e);

		double bestFittnes = 0;
		Rule bestRule = null;
		int i = 0;
		for (i = 0; i < Params.maxRunCount; i++) {
			long t1 = System.currentTimeMillis();

			
			kernel.calculateProgressiveFitness();
			kernel.setOffset(kernel.getOffset() == 0 ? Params.populationCount : 0);
			
		/*	for(int f=0;f<Params.populationCount*2;f++) {
				System.out.println("progressive["+f+"] = "+kernel.progressiveFitness[f] + " fittness["+f+"] = "+kernel.fitness[f]);
			}
			*/
			
			kernel.execute(Params.populationCount / 2);
			kernel.moveElite();

			/*
			for(int f=0;f<Params.populationCount*2;f++) {
				System.out.println("picked["+f+"] = "+kernel.picked[f]);
			}
			*/
			
			double tmpBest = kernel.bestFitness();
			if (bestFittnes < tmpBest) {
				bestFittnes = tmpBest;
				//System.out.println("new best!");
				bestRule = new Rule(kernel.bestRule());
			}

			int best = kernel.bestFitnessIndex();
			
			System.err.println(String.format(
					"t=%d, max_f=%f, avg_f=%f, rule=%s", i, tmpBest,
					kernel.avgFitness(), new Rule(kernel.population[best],kernel.ruleLenTable[best])));

			int[] dist = new int[Params.maxRadius+1];
			for(int rad=0;rad<Params.populationCount;rad++) {
				dist[kernel.radiusTable[rad+kernel.getOffset()]]++;
			}
			
			String s = "t="+i+", ";
			for(int rad=1;rad<Params.maxRadius+1;rad++) {
				s+= String.format("dist[%d] = %f, ",rad,(double)dist[rad]/Params.populationCount);
			}
			System.out.println(s);
			
			if (Double.compare(1.0, tmpBest) == 0) {
				System.out.println("Got ideal solution!");
				break;
			}
		}

		System.out.println("-----------------------------------------");
		System.out.println("Test rule: " + r);
		System.out.println("Iterations: " + i);
		System.out.println("Found rule: " + bestRule);
		System.out.println("Best fit: " + bestFittnes);
	}
}
