package camelartifact.test;

import org.junit.Assert;
import org.junit.Test;

import camelartifact.test.App;

public class UnitTestApp {
	
	@Test
	public void testRunSimulation() throws InterruptedException, Exception {

		Assert.assertEquals(App.runSimulation(), 0);

	}	
	
}
