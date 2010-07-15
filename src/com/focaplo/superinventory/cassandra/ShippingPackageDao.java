package com.focaplo.superinventory.cassandra;

import static me.prettyprint.cassandra.utils.StringUtils.bytes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.cassandra.service.Column;
import org.apache.cassandra.service.SuperColumn;

import me.prettyprint.cassandra.dao.Command;
import me.prettyprint.cassandra.service.Keyspace;

public class ShippingPackageDao {
	  private final static String CASSANDRA_KEYSPACE = "SuperInventory";
	  private final static int CASSANDRA_PORT = 9160;
	  private final static String CASSANDRA_HOST = "localhost";
	  
	public void insert(final String orderNumber, final String packageTrackingNumber, final String[] items, final String[] quantities) throws Exception{
		execute(new Command<Void>(){

			@Override
			public Void execute(Keyspace ks) throws Exception {
				 //
				Map<String, List<SuperColumn>> oneRowPerColumnFamily = new HashMap<String, List<SuperColumn>>();
				List<SuperColumn> shippedPackageRow = new ArrayList<SuperColumn>();
				
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
						shippedPackageRow.add(itemInPackage);
					}
					{
						//add the order number to the row too
						Column orderNumberColumn = new Column(bytes("order-number"),bytes(orderNumber),timestamp);
						SuperColumn superOrderNumber = new SuperColumn();
						superOrderNumber.setName(bytes(orderNumber));
						superOrderNumber.addToColumns(orderNumberColumn);
						shippedPackageRow.add(superOrderNumber);
					}
					//add this row to column family order-shipping-packages, keyed by package number
					oneRowPerColumnFamily.put("ShippedPackages", shippedPackageRow);
					ks.batchInsert(packageTrackingNumber, null, oneRowPerColumnFamily);
				return null;
			}
			
		});
	}
	
	  protected static <T> T execute(Command<T> command) throws Exception {
		    return command.execute(CASSANDRA_HOST, CASSANDRA_PORT, CASSANDRA_KEYSPACE);
		  }
}
