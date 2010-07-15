package com.focaplo.superinventory.cassandra;

import static me.prettyprint.cassandra.utils.StringUtils.bytes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.cassandra.service.Column;
import org.apache.cassandra.service.ColumnOrSuperColumn;
import org.apache.cassandra.service.ColumnParent;
import org.apache.cassandra.service.ColumnPath;
import org.apache.cassandra.service.ConsistencyLevel;
import org.apache.cassandra.service.SlicePredicate;
import org.apache.cassandra.service.SliceRange;
import org.apache.cassandra.service.SuperColumn;
import org.junit.Test;

import me.prettyprint.cassandra.service.CassandraClient;
import me.prettyprint.cassandra.service.CassandraClientPool;
import me.prettyprint.cassandra.service.CassandraClientPoolFactory;
import me.prettyprint.cassandra.service.Keyspace;
import me.prettyprint.cassandra.service.PoolExhaustedException;

public enum HectorClient {

	instance;
	
	public void addShippingPackge(String orderNumber, String packageTrackingNumber, String[] items, String[] quantities) throws IllegalStateException, PoolExhaustedException, Exception{
		CassandraClientPool pool = CassandraClientPoolFactory.INSTANCE.get();
	    CassandraClient client = pool.borrowClient("localhost", 9160);
	    try {
	    	 Keyspace keyspace = client.getKeyspace("SuperInventory");
	         //
				Map<String, List<SuperColumn>> orderShippedPackagesRow = new HashMap<String, List<SuperColumn>>();
				Map<String, List<SuperColumn>> shippedPackageRow = new HashMap<String, List<SuperColumn>>();
				List<SuperColumn> shippedPackageRowColumns = new ArrayList<SuperColumn>();
				List<SuperColumn> orderPackageRow = new ArrayList<SuperColumn>();
				
				long timestamp = System.currentTimeMillis();
			
					//add items
					for(int j=0;j<items.length;j++){
						System.out.println("processing " + packageTrackingNumber + " " + items[j] + " " + quantities[j]);
						Column itemNumber = new Column(bytes("item-number"), bytes(items[j]),timestamp);
						Column quantityInPackage = new Column(bytes("quantityInPackage"), bytes(quantities[j]),timestamp);
						SuperColumn itemInPackage = new SuperColumn();
						itemInPackage.setName(bytes(items[j]));
						itemInPackage.addToColumns(itemNumber);
						itemInPackage.addToColumns(quantityInPackage);
						shippedPackageRowColumns.add(itemInPackage);
					}
					{
						//add the order number to the row too
						Column orderNumberColumn = new Column(bytes("order-number"),bytes(orderNumber),timestamp);
						SuperColumn superOrderNumber = new SuperColumn();
						superOrderNumber.setName(bytes(orderNumber));
						superOrderNumber.addToColumns(orderNumberColumn);
						shippedPackageRowColumns.add(superOrderNumber);
					}
					{
						//add the order number
						Column shipDateColumn = new Column(bytes("shipped-date"),bytes("2/2/2010"),timestamp);
						Column carrierColumn = new Column(bytes("carrier"),bytes("UPS"),timestamp);
						SuperColumn orderPackageShipped = new SuperColumn();
						orderPackageShipped.setName(bytes(packageTrackingNumber));
						orderPackageShipped.addToColumns(shipDateColumn);
						orderPackageShipped.addToColumns(carrierColumn);
						orderPackageRow.add(orderPackageShipped);
					}
					//add this row to column family order-shipping-packages, keyed by package number
					shippedPackageRow.put("ShippedPackages", shippedPackageRowColumns);
					keyspace.batchInsert(packageTrackingNumber, null, shippedPackageRow);

				//now add the order-shipped-package relationship
					orderShippedPackagesRow.put("OrderShippedPackages", orderPackageRow);	
				keyspace.batchInsert(orderNumber, null, orderShippedPackagesRow);
				
	    } finally {
	        // return client to pool. do it in a finally block to make sure it's executed
	        pool.releaseClient(client);
	      }
	}
	
	public void searchShippedPackagesForOrder(String orderNumber) throws IllegalStateException, PoolExhaustedException, Exception{
		CassandraClientPool pool = CassandraClientPoolFactory.INSTANCE.get();
	    CassandraClient client = pool.borrowClient("localhost", 9160);
	    try {
	    	 Keyspace keyspace = client.getKeyspace("SuperInventory", CassandraClient.DEFAULT_CONSISTENCY_LEVEL, CassandraClient.DEFAULT_FAILOVER_POLICY);
	         //
	    	
				SlicePredicate slicePredicate = new SlicePredicate();
				SliceRange sliceRange = new SliceRange("".getBytes(),"".getBytes(),false,100);
				slicePredicate.setSlice_range(sliceRange);
				List<SuperColumn> results =  keyspace.getSuperSlice(orderNumber,new ColumnParent("OrderShippedPackages",null), slicePredicate);
				
				System.out.println("found " + results.size());
				for(SuperColumn sc:results){
					
						String trackingNumber = new String(sc.getName(),"utf-8");
						System.out.print("tracking number:" + trackingNumber);
						List<Column> attributes = sc.getColumns();
						for(Column attribute:attributes){
							System.out.print(new String(attribute.getName(),"utf-8") + "=" + new String(attribute.getValue(),"utf-8"));
						}
						System.out.println();
			
				}
	    } finally {
	        // return client to pool. do it in a finally block to make sure it's executed
	        pool.releaseClient(client);
	      }
	}
}
