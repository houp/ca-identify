package ga.base.test;

import org.junit.Test;

import ca.base.Rule;

public class RuleTests {

	@Test
	public void NumberLutConversion() throws Exception {
		long i = (long)Integer.MAX_VALUE*2L+1;
		Rule r = new Rule(i, 2);
		if (r.getNumber() != i)
			throw new Exception("fuck: " + i);

	}
}
