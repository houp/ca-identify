package ga.base;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ca.base.Rule;

public class TimeStepFittness extends AbstractFittnessFunction<Rule> {

	private final Map<Rule, Double> cache = new HashMap<>();
	
	private final List<TimeStep> steps;

	private int stepCount;
	private int totalSize;
	private int len;
	
	public TimeStepFittness(List<TimeStep> steps) {
		this.steps = steps;
		Collections.sort(this.steps);
		stepCount = steps.size();
		len = steps.get(0).getConfiguration().length;
		totalSize = stepCount * len;			
	}

	@Override
	public double calculate(Rule element) {
		if(cache.containsKey(element)) {
			return cache.get(element);
		}
		
		Iterator<TimeStep> it = steps.iterator();
		TimeStep step = it.next();

		boolean[] current = step.getConfiguration();
		int result = 0;		
		int t = 0;
		
		while(it.hasNext()) {
			step = it.next();
			while (t < step.getTime()) {
				current = element.eval(current);
				t++;
			}
			
			boolean[] stepConf = step.getConfiguration();
			for (int i = 0; i < len; i++) {
				if (current[i] != stepConf[i])
					result++;
			}		
		}

		double res = 1.0 - (double)(result) / (double)totalSize;
		
		cache.put(element, res);
		return res;
	}
}
