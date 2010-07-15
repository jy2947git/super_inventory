package com.focaplo.superinventory.cassandra;

import me.prettyprint.cassandra.service.PoolExhaustedException;

import org.junit.Test;

public class HectorClientTest {
	@Test
	public void testAddShippingPackage() throws IllegalStateException, PoolExhaustedException, Exception{
	
		HectorClient.instance.addShippingPackge("order02", "FEDEX03", new String[]{"computer101","chair033"}, new String[]{"1","2"});
		
	}
	
	@Test
	public void testSearchPackagesForOrder() throws IllegalStateException, PoolExhaustedException, Exception{
		
		HectorClient.instance.searchShippedPackagesForOrder("order02");
	}
}
