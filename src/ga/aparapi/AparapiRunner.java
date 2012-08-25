package ga.aparapi;

import ga.base.Params;
import ca.base.Rule;

public class AparapiRunner {
	private static boolean[][] getTestCase(Rule r, int spaceLen, int timeLen) {
		boolean[][] result = new boolean[timeLen][spaceLen];

		for (int i = 0; i < spaceLen; i++) {
			result[0][i] = Math.random() > 0.5;
		}

		result[0][spaceLen / 2] = true;

		for (int t = 1; t < timeLen; t++) {
			result[t] = r.eval(result[t - 1]);
		}

		return result;
	}

	public static void main(String[] args) {
		int radius = Integer.parseInt(args[0]);

		Rule r = new Rule(1234567890, radius);

		boolean[][] evolution = getTestCase(r, 79, 80);

		GeneticAlgorithmKernel kernel = new GeneticAlgorithmKernel(
				Params.populationCount, radius, evolution);

		double bestFittnes = 0;
		Rule bestRule = null;
		int i = 0;
		for (i = 0; i < Params.maxRunCount; i++) {
			long t1 = System.currentTimeMillis();

			kernel.setOffset(kernel.getOffset() == 0 ? Params.populationCount
					: 0);
			kernel.calculateProgressiveFitness();
			kernel.execute(Params.populationCount / 2);

			double tmpBest = kernel.bestFitness();
			if (bestFittnes < tmpBest) {
				bestFittnes = tmpBest;
				System.out.println("new best!");
				bestRule = new Rule(kernel.bestRule());
			}

			System.err.println(String.format(
					"t=%d, max_f=%f, avg_f=%f, rule=%s", i, tmpBest,
					kernel.avgFitness(), new Rule(kernel.bestRule())));
			System.err.println("Time = " + (System.currentTimeMillis() - t1));

			if (tmpBest == 1) {
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
