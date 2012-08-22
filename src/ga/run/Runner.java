package ga.run;

import ga.base.Chromosome;
import ga.base.GeneticAlgorithm;
import ga.base.Params;
import ga.base.TimeStep;
import ga.base.TimeStepFittness;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ca.base.Rule;

public class Runner {
	
	private static List<TimeStep> getTestCase(Rule r, int spaceLen, int timeLen) {
		List<TimeStep> result = new ArrayList<>();
		
		boolean[] initialState = new boolean[spaceLen];

		for(int i = 0; i<spaceLen;i++) {
			initialState[i] = Math.random() > 0.5;
		}

		initialState[spaceLen/2] = true;
		
		result.add(new TimeStep(0, initialState));
		
		int t = 0;
		boolean[] current = initialState;
		
		for(t=1;t<timeLen;t++) {
			current = r.eval(current);
			result.add(new TimeStep(t, current));
		}
		
		return result;
	}
	
	public static void main(String args[]) {
		int radius = Integer.parseInt(args[0]);
		
		Rule r = new Rule(1234567890,radius);
		TimeStepFittness fittness = new TimeStepFittness(getTestCase(r, 79, 80));
		GeneticAlgorithm ga = new GeneticAlgorithm(fittness, r);
		ga.run();
			
		System.out.println("-----------------------------------------");
		System.out.println("Result for test case: " + r);
		System.out.println("Rule: " + ga.getBest());
		System.out.println("Iterations: " + ga.getIterations());
		System.out.println("Best fit: " + ga.getBestFittness());
	}

}
