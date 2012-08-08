package ga.base.test;

import ga.base.TimeStep;
import ga.base.TimeStepFittness;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.*;

import ca.base.Rule;
public class FittnessTests {

	@Test
	public void verifyFittnessCalc() {
		
		Rule r = new Rule(18, 1);
		boolean[] initial = new boolean[17];
		initial[17/2] = true;
		List<TimeStep> steps = new ArrayList<TimeStep>();
		steps.add(new TimeStep(0,initial));
		
		for(int i=1;i<20;i++) {
			initial = r.eval(initial);
			steps.add(new TimeStep(i,initial));
		}
		
		TimeStepFittness fit = new TimeStepFittness(steps);
		double d = fit.calculate(r);
		if(d<1) fail();	
		
		d = fit.calculate(new Rule(13,1));
		if(d==1) fail();	
	}

}
