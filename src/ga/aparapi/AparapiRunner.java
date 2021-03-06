package ga.aparapi;

import ga.base.Params;
import ca.base.Rule;
import static utils.Logger.*;

public class AparapiRunner {

	private static boolean[][] getRandomTestCase(Rule r) {
		int spaceLen = Params.spaceLen;
		int timeLen = Params.timeLen;

		boolean[][] result = new boolean[timeLen][spaceLen];

		for (int i = 0; i < spaceLen; i++) {
			result[0][i] = Math.random() > 0.5;
		}

		for (int t = 1; t < timeLen; t++) {
			result[t] = r.eval(result[t - 1]);
		}

		return result;
	}

	private static boolean[][] getTestCase(Rule r) {
		int spaceLen = Params.spaceLen;
		int timeLen = Params.timeLen;
		boolean[][] result = new boolean[timeLen][spaceLen];

		result[0][spaceLen / 2] = true;

		for (int t = 1; t < timeLen; t++) {
			result[t] = r.eval(result[t - 1]);
		}

		return result;
	}

	private static boolean[][] getTestCase2(Rule r) {
		int spaceLen = Params.spaceLen;
		int timeLen = Params.timeLen;
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

	private static String printArray(double[] a) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < a.length; i++) {
			sb.append(a[i] + ", ");
		}
		return sb.toString();
	}

	public static void main(String[] args) {

		Params.readConf();
		Params.printConf();

		Rule r = new Rule(Params.rule, Params.radius);

		boolean[][][] e = { getRandomTestCase(r), getTestCase(r), getTestCase2(r) };

		GeneticAlgorithmKernel kernel = new GeneticAlgorithmKernel(Params.populationCount, e);

		double bestFitness = 0;
		Rule bestRule = null;
		int i = 0;
		for (i = 0; i < Params.maxRunCount; i++) {

			kernel.calculateProgressiveFitness();
			kernel.setOffset(kernel.getOffset() == 0 ? Params.populationCount : 0);
			kernel.reset();
			kernel.execute(Params.populationCount / 2);

			if (Params.keepBest) {
				kernel.moveElite(Params.elite);
			}

			double tmpBest = kernel.bestFitness();
			if (bestFitness < tmpBest) {
				bestFitness = tmpBest;
				bestRule = new Rule(kernel.bestRule());
			}

			int best = kernel.bestFitnessIndex();

			StringBuilder sb = new StringBuilder(String.format("t=%d, max_f=%f, avg_f=%f, min_f=%f, div=%f, best_rule=%s, {cs=%s}, {ms=%s}, {cms=%s}", i, tmpBest, kernel.avgFitness(), kernel.minFitness(), kernel.getDiversity(), new Rule(
					kernel.population[best], kernel.ruleLenTable[best]), printArray(kernel.getCrossStats()), printArray(kernel.getMutationStats()), printArray(kernel.getMutationAndCrossStats())));

			int[] dist = new int[Params.maxRadius + 1];
			for (int rad = 0; rad < Params.populationCount; rad++) {
				dist[kernel.radiusTable[rad + kernel.getOffset()]]++;
			}

			if (Params.minRadius != Params.maxRadius) {
				StringBuilder db = new StringBuilder(", dist:");
				double avgRad = 0;
				for (int rad = Params.minRadius; rad < Params.maxRadius + 1; rad++) {
					double foo = (double) dist[rad] / Params.populationCount;
					db.append(String.format("%d = %f, ", rad, foo));
					avgRad += rad * foo;
				}
				sb.append(String.format(", avg_rad=%f", avgRad));
				sb.append(db.toString());
			}
			result(sb.toString());

			if (Double.compare(1.0, tmpBest) == 0) {
				info("Got ideal solution!");
				break;
			}
		}

		info("Simulation finished!");
		info("Iterations : " + i);
		info("Test rule: " + r);
		info("Found rule: " + bestRule);
		info("Best fitness: " + bestFitness);
	}
}
