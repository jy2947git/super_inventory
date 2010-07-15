package com.focaplo.superinventory.cassandra;

import java.util.List;

import org.apache.cassandra.service.ColumnOrSuperColumn;
import org.apache.cassandra.service.InvalidRequestException;
import org.apache.cassandra.service.TimedOutException;
import org.apache.cassandra.service.UnavailableException;
import org.apache.thrift.TException;
import org.junit.Test;

public class SearchShippingPackageTest {

	@Test
	public void testSearchByPackageItem() throws InvalidRequestException, UnavailableException, TimedOutException, TException{
		SearchShippingPackage c = new SearchShippingPackage();
		List<ColumnOrSuperColumn> results  = c.search("p9292902", "item30212");
		System.out.println(results.size());
		for(ColumnOrSuperColumn cs:results){
			System.out.println(new String(cs.getColumn().getName()) + "=" + new String(cs.getColumn().getValue()));
		}
	}
}
