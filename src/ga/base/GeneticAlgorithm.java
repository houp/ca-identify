package ga.base;

import java.util.ArrayList;
import java.util.List;
import utils.Pair;
import ca.base.Rule;
import java.util.concurrent.*;

public class GeneticAlgorithm {

	private AbstractFittnessFunction<Rule> fittnes;
	private Params params;
	private Population<Rule> population;
	private ExecutorService exec;

	private Rule best = null;
	private int iterations = 0;
	private double bestFittness = 0;

	public GeneticAlgorithm(AbstractFittnessFunction<Rule> fittness, Rule r) {
		this.fittnes = fittness;
		population = buildPopulation(r);
	}
	
	public double getBestFittness() {
		return bestFittness;
	}

	public Rule getBest() {
		return best;
	}

	public int getIterations() {
		return iterations;
	}

	public Rule mutate(Rule c) {
		Rule result = new Rule(c);
		boolean[] lut = result.getLut();

		for (int i = 0; i < lut.length; i++) {
			if (Math.random() < Params.mutationProbability) {
				lut[i] = !lut[i];
			}
		}

		if (Math.random() < Params.upScaleProbability
				&& result.getRadius() < Params.maxRadius) {
			return result.increaseRadius();
		}

		if (Math.random() < Params.downScaleProbability
				&& result.getRadius() > Params.minRadius) {
			return result.decreaseRadius();
		}

		return result;
	}

	public Pair<Rule, Rule> cross(Rule c1, Rule c2) {

		if (c1.equals(c2)) {
			return new Pair<Rule, Rule>(new Rule(c1), new Rule(c2));
		}

		Pair<Rule, Rule> result = alignRadius(c1, c2);

		boolean[] lut1 = result.getFirst().getLut();
		boolean[] lut2 = result.getSecond().getLut();

		int pos = (int) Math.floor(lut1.length * Math.random());

		for (int i = pos; i < lut1.length; i++) {
			boolean tmp = lut1[i];
			lut1[i] = lut2[i];
			lut2[i] = tmp;
		}

		return result;
	}

	public Pair<Rule, Rule> alignRadius(Rule c1, Rule c2) {
		Rule r1, r2;

		if (c1.getRadius() != c2.getRadius()) {
			int radius = commonRadius(c1, c2);
			r1 = c1.assignRadius(radius);
			r2 = c2.assignRadius(radius);
		} else {
			r1 = new Rule(c1);
			r2 = new Rule(c2);
		}

		return new Pair<Rule, Rule>(r1, r2);
	}

	private int commonRadius(Rule c1, Rule c2) {
		return (c1.getRadius() + c2.getRadius()) / 2;
	}

	private Population<Rule> evolve(final Population<Rule> p) {
		int count = p.count();
		List<Rule> l = new ArrayList<Rule>(count);

		for (int i = 0; i < count; i++) {
			Rule r1 = p.getElement();
			Rule r2 = p.getElement(r1);

			Pair<Rule, Rule> pair = cross(r1, r2);
			l.add(mutate(pair.getFirst()));
			l.add(mutate(pair.getSecond()));
		}

		return new Population<Rule>(l);
	}

	private Population<Rule> buildPopulation(Rule ommit) {
		List<Rule> rules = new ArrayList<Rule>(Params.populationCount);

		int radiusDiff = Params.maxRadius - Params.minRadius;

		for (int i = 0; i < Params.populationCount; i++) {
			int radius = Params.minRadius;

			if (radiusDiff > 0) {
				radius += (int) Math.round((Math.random() * radiusDiff));
			}

			Rule r = Rule.random(radius);

			if (ommit != null) {
				while (r.equals(ommit)) {
					r = Rule.random(radius);
				}
			}

			rules.add(r);
		}

		return new Population<Rule>(rules);
	}

	public void run() {

		Chromosome<Rule> best = population.best();

		for (int i = 0; i < Params.maxRunCount; i++) {
			long t1 = System.currentTimeMillis();
			population.realculateFittnes(fittnes);

			Chromosome<Rule> newBest = population.best();

			if (newBest.getFittnes() > best.getFittnes()) {
				best = newBest;
			}

			System.err.println(String.format("DEBUG: %d; %f; %f; %f; rule: %s",
					i, best.getFittnes(), population.avgFittness(),
					population.minFittness(), best.getElement().toString()));

			if (newBest.getFittnes() == 1.0) {
				break;
			}

			population = evolve(population);
			System.out.println("Time = " + (System.currentTimeMillis() - t1));
		}

		this.best = best.getElement();
		this.iterations = Params.maxRunCount;
		this.bestFittness = best.getFittnes();
	}

}
