package ca.base.test;

import ca.base.*;

import static org.junit.Assert.*;

import org.junit.Test;

public class RuleTest {

	@Test
	public void evalTest1() {
		boolean[] input = new boolean[] { false, true, false, true, false };
		Rule r = new Rule(0,1);
		boolean[] output = r.eval(input);
		
		for(boolean b: output) {
			if(b) fail("All values should be false!");
		}
	}
	
	@Test
	public void increaseDecreseTest() {
		for(int i=0;i<256;i++) {
			Rule r = new Rule(i, 1);
			Rule r2 = r.increaseRadius().increaseRadius();
			Rule r3 = r2.decreaseRadius().decreaseRadius();
			if(!r.equals(r3)) {
				fail();
			}

		}
	}

}
