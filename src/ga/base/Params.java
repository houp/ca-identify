package ga.base;

import java.util.Properties;
import static utils.Logger.*;

public class Params {
	public static double mutationProbability = 0.07;
	public static int populationCount = 500;
	public static int minRadius = 1;
	public static int maxRadius = 4;
	public static double upScaleProbability = 0.07;
	public static double downScaleProbability = 0.07;
	public static int maxRunCount = 5000;
	public static long rule = 2294967295L;
	public static int radius = 2;
	public static int spaceLen = 79;
	public static int timeLen = 80;
	public static boolean keepBest = true;
	public static int elite = 1;

	public static void readConf() {
		Properties p = System.getProperties();

		if (p.containsKey("ca.keepBest")) {
			keepBest = Boolean.parseBoolean((String) p.get("ca.keepBest"));
		}

		if (p.containsKey("ca.elite")) {
			elite = Integer.parseInt((String) p.get("ca.elite"));
		}

		if (p.containsKey("ca.mutationProbability")) {
			mutationProbability = Double.parseDouble((String) p.get("ca.mutationProbability"));
		}

		if (p.containsKey("ca.upScaleProbability")) {
			upScaleProbability = Double.parseDouble((String) p.get("ca.upScaleProbability"));
		}

		if (p.containsKey("ca.downScaleProbability")) {
			downScaleProbability = Double.parseDouble((String) p.get("ca.downScaleProbability"));
		}

		if (p.containsKey("ca.populationCount")) {
			populationCount = Integer.parseInt((String) p.get("ca.populationCount"));
		}

		if (p.containsKey("ca.minRadius")) {
			minRadius = Integer.parseInt((String) p.get("ca.minRadius"));
		}

		if (p.containsKey("ca.maxRadius")) {
			maxRadius = Integer.parseInt((String) p.get("ca.maxRadius"));
		}

		if (p.containsKey("ca.maxRunCount")) {
			maxRunCount = Integer.parseInt((String) p.get("ca.maxRunCount"));
		}

		if (p.containsKey("ca.radius")) {
			radius = Integer.parseInt((String) p.get("ca.radius"));
		}

		if (p.containsKey("ca.spaceLen")) {
			spaceLen = Integer.parseInt((String) p.get("ca.spaceLen"));
		}

		if (p.containsKey("ca.timeLen")) {
			timeLen = Integer.parseInt((String) p.get("ca.timeLen"));
		}

		if (p.containsKey("ca.rule")) {
			rule = Long.parseLong((String) p.get("ca.rule"));
		}
	}

	public static void printConf() {
		info("Looking for rule: " + rule + " at radius: " + radius);
		info("Radius range: [" + minRadius + ", " + maxRadius + "]");
		info("Upscale probability: " + upScaleProbability);
		info("Downsacle probability: " + downScaleProbability);
		info("Population count: " + populationCount);
		info("Mutation probability: " + mutationProbability);
		info("Max iteration count: " + maxRunCount);
		info("Space size: " + spaceLen);
		info("Time frames: " + timeLen);
		info("Keep elite: " + keepBest);
		info("Elite ratio: " + elite);
		
	}

}
