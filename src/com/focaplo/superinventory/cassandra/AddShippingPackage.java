package com.focaplo.superinventory.cassandra;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.cassandra.service.Column;
import org.apache.cassandra.service.ColumnOrSuperColumn;
import org.apache.cassandra.service.ConsistencyLevel;
import org.apache.cassandra.service.InvalidRequestException;
import org.apache.cassandra.service.SuperColumn;
import org.apache.cassandra.service.TimedOutException;
import org.apache.cassandra.service.UnavailableException;
import org.apache.thrift.TException;

public class AddShippingPackage implements BatchInsertable{
	String orderNumber;
	String packageNumber; 
	String shippedItemNumber; 
	int shippedQuantityInPackage;
	
	public AddShippingPackage(String packageNumber, String shippedItemNumber,
			int shippedQuantityInPackage) {
		super();
		this.packageNumber = packageNumber;
		this.shippedItemNumber = shippedItemNumber;
		this.shippedQuantityInPackage = shippedQuantityInPackage;
	}

	public void exec() throws UnsupportedEncodingException, InvalidRequestException, UnavailableException, TimedOutException, TException{
		CassandraClientTemplate.instance().batchInsert(this);
	}

	@Override
	public int getConsistenceLevel() {
		return ConsistencyLevel.ALL;
	}

	@Override
	public String getKeyspace() {
		return "SuperInventory";
	}

	@Override
	public Map<String, List<ColumnOrSuperColumn>> getRowsColumnFamilyMap() throws UnsupportedEncodingException {
		//
		Map<String, List<ColumnOrSuperColumn>> oneRowPerColumnFamily = new HashMap<String, List<ColumnOrSuperColumn>>();
		List<ColumnOrSuperColumn> row = new ArrayList<ColumnOrSuperColumn>();
		
		long timestamp = System.currentTimeMillis();
		{
			
			Column itemNumber = new Column("item-number".getBytes("utf-8"), shippedItemNumber.getBytes("utf-8"),timestamp);
			Column quantityInPackage = new Column("quantityInPackage".getBytes("utf-8"), Integer.toString(shippedQuantityInPackage).getBytes("utf-8"),timestamp);
			SuperColumn shippedItem = new SuperColumn();
			shippedItem.setName(shippedItemNumber.getBytes());
			shippedItem.addToColumns(itemNumber);
			shippedItem.addToColumns(quantityInPackage);
			ColumnOrSuperColumn c = new ColumnOrSuperColumn();
			c.setSuper_column(shippedItem);
			row.add(c);
		}

		//add this row to column family order-shipping-packages, keyed by package number
		oneRowPerColumnFamily.put("OrderShippingPackages", row);
		return oneRowPerColumnFamily;
	}

	@Override
	public String getUniversalRowKey() {
		return packageNumber;
	}
}
