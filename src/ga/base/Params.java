package ga.base;

public class Params {
	private double mutationProbability = 0.45;

	public double getMutationProbability() {
		return mutationProbability;
	}

	private int populationCount = 100;
	private int minRadius = 1;
	private int maxRadius = 2;
	
	public double getHardMutationProbability() {
		return hardMutationProbability;
	}

	public void setHardMutationProbability(double hardMutationProbability) {
		this.hardMutationProbability = hardMutationProbability;
	}

	private double hardMutationProbability = 0.1;

	public void setMutationProbability(double mutationProbability) {
		this.mutationProbability = mutationProbability;
	}

	public void setPopulationCount(int populationCount) {
		this.populationCount = populationCount;
	}

	public void setMinRadius(int minRadius) {
		this.minRadius = minRadius;
	}

	public void setMaxRadius(int maxRadius) {
		this.maxRadius = maxRadius;
	}

	public void setUpScaleProbability(double upScaleProbability) {
		this.upScaleProbability = upScaleProbability;
	}

	public void setDownScaleProbability(double downScaleProbability) {
		this.downScaleProbability = downScaleProbability;
	}

	public void setMaxRunCount(int maxRunCount) {
		this.maxRunCount = maxRunCount;
	}

	private double upScaleProbability = 0.5;
	private double downScaleProbability = 0.35;

	private int maxRunCount = 1500;

	public int getPopulationCount() {
		return populationCount;
	}

	public int getMinRadius() {
		return minRadius;
	}

	public int getMaxRadius() {
		return maxRadius;
	}

	public double getUpScaleProbability() {
		return upScaleProbability;
	}

	public double getDownScaleProbability() {
		return downScaleProbability;
	}

	public int getMaxRunCount() {
		return maxRunCount;
	}

}
