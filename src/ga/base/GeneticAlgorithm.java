package ga.base;

import java.util.ArrayList;
import java.util.List;

import utils.Pair;
import ca.base.Rule;

public class GeneticAlgorithm {

	private AbstractFittnessFunction<Rule> fittnes;
	private Params params;
	private Population<Rule> population;

	private Rule best = null;
	private int iterations = 0;
	private double bestFittness = 0;
	
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

		int ruleLen = (int) utils.Math.pow2(2 * c.getRadius() + 1);

		long foo = 1;
		long number = result.getNumber();
		for (int i = 0; i < ruleLen; i++) {
			if (Math.random() < params.getMutationProbability()) {
				number = number ^ foo;
			}
			foo *= 2;
		}

		if(result.getNumber()!=number) {
			result.setNumber(number);
		}
		
		if (Math.random() < params.getUpScaleProbability() && result.getRadius() < params.getMaxRadius()) {
			result = result.increaseRadius();
		}
		
		if (Math.random() < params.getDownScaleProbability()) {
			result = result.decreaseRadius();
		}
		
		if(Math.random()<params.getHardMutationProbability()) {
			result = Rule.random(params.getMinRadius());
		}
		
		return result;
	}

	public Pair<Rule, Rule> cross(Rule c1, Rule c2) {
		if(c1.equals(c2)) {
			return new Pair<Rule, Rule> (new Rule(c1), new Rule(c2));
		}
		
		Pair<Rule, Rule> result = alignRadius(c1, c2);

		int radius = result.getFirst().getRadius();

		int ruleLen1 = 1 + (int)Math.floor(Math.log(Math.abs(1 + result.getFirst().getNumber())));
		int ruleLen2 = 1 + (int)Math.floor(Math.log(Math.abs(1 + result.getSecond().getNumber())));
		
		int ruleLen = ruleLen1 > ruleLen2 ? ruleLen1 : ruleLen2;
	
		int cross = (int) Math.round(ruleLen * Math.random());

		long m2 = (1 << cross) - 1;
		long m1 = Long.MAX_VALUE - m2;

		Pair<Rule,Rule> pair = new Pair<Rule, Rule>(new Rule((result.getFirst().getNumber() & m1) + (result.getSecond().getNumber() & m2), radius), new Rule((result.getSecond().getNumber() & m1) + (result.getFirst().getNumber() & m2), radius));
		return pair;
	}

	public Pair<Rule, Rule> alignRadius(Rule c1, Rule c2) {
		Rule r1,r2;

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
		if(params.getUpScaleProbability() > params.getDownScaleProbability())
			return Math.max(c1.getRadius(), c2.getRadius());
		else
			return Math.min(c1.getRadius(), c2.getRadius());
	}

	private Population<Rule> evolve(Population<Rule> p) {
		List<Rule> l = new ArrayList<Rule>(p.count());

		for (int i = 0; i < (p.count() / 2); i++) {
			Rule r1 = p.getElement();
			Rule r2 = p.getElement(r1);

			Pair<Rule, Rule> pair = cross(r1, r2);
			l.add(mutate(pair.getFirst()));
			l.add(mutate(pair.getSecond()));
		}

		return new Population<Rule>(l);
	}

	public GeneticAlgorithm(Params p, AbstractFittnessFunction<Rule> fittness, Rule r) {
		this.params = p;
		this.fittnes = fittness;

		population = buildPopulation(r);
	}

	private Population<Rule> buildPopulation(Rule ommit) {
		List<Rule> rules = new ArrayList<Rule>(params.getPopulationCount());
		
		int radiusDiff = params.getMaxRadius() - params.getMinRadius();
		
		for(int i=0;i<params.getPopulationCount();i++) {
			int radius = params.getMinRadius();
			
			if(radiusDiff > 0) {
				radius += (int)Math.round((Math.random() * radiusDiff));
			}
			
			int rule = (int)Math.floor(Math.random() * utils.Math.pow2(2*radius+1));
			
			if(radius == ommit.getRadius()) {
				while(rule == ommit.getNumber()) {
					rule = (int)Math.floor(Math.random() * utils.Math.pow2(2*radius+1));			
				}
			}
			
			Rule r = new Rule(rule, radius);
			rules.add(r);
		}
		
		return new Population<Rule>(rules);
	}

	public void run() {
		Population<Rule> p = population;
		Chromosome<Rule> best = p.best();

		
		for (int i = 0; i < params.getMaxRunCount(); i++) {
			p.realculateFittnes(fittnes);
			
			Chromosome<Rule> newBest = p.best();
			
			if (newBest.getFittnes() > best.getFittnes()) {
				best = newBest;
			}
			
			System.err.println(String.format("DEBUG: %d; %f; %f; %f; rule: %s", i, best.getFittnes(), population.avgFittness(), population.minFittness(), best.getElement().toString()));
			
			if(newBest.getFittnes() == 1.0) {
				this.best = newBest.getElement();
				this.iterations = i;
				this.bestFittness = 1.0;
				return;
			}
			
			p = evolve(p);
		}

		this.best = best.getElement();
		this.iterations = params.getMaxRunCount();
		this.bestFittness = best.getFittnes();
		
	}

}
