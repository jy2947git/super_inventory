package com.focaplo.superinventory.cassandra;

import java.util.List;

import org.apache.cassandra.service.Cassandra;
import org.apache.cassandra.service.ColumnOrSuperColumn;
import org.apache.cassandra.service.ColumnParent;
import org.apache.cassandra.service.ConsistencyLevel;
import org.apache.cassandra.service.InvalidRequestException;
import org.apache.cassandra.service.SlicePredicate;
import org.apache.cassandra.service.SliceRange;
import org.apache.cassandra.service.TimedOutException;
import org.apache.cassandra.service.UnavailableException;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

public class SearchShippingPackage {

	public List<ColumnOrSuperColumn> search(String packageNumber, String itemNumber) throws InvalidRequestException, UnavailableException, TimedOutException, TException{
		TTransport transport = null;
		try{
			transport = new TSocket("localhost", 9160);
			TProtocol protocol = new TBinaryProtocol(transport);
			Cassandra.Client client = new Cassandra.Client(protocol);
			transport.open();
			//client.batch_insert("SuperInventory", packageNumber, oneRowPerColumnFamily, ConsistencyLevel.ALL);
			SlicePredicate slicePredicate = new SlicePredicate();


			List
			result =
			client.get_slice("SuperInventory", packageNumber, new ColumnParent("OrderShippingPackages",itemNumber.getBytes()), slicePredicate, ConsistencyLevel.ONE);
			return result;
		}
		finally{
			transport.flush();
			transport.close();
		}
	}
}
