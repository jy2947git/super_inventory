package com.focaplo.superinventory.cassandra;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.cassandra.service.Cassandra;
import org.apache.cassandra.service.Column;
import org.apache.cassandra.service.ColumnOrSuperColumn;
import org.apache.cassandra.service.ColumnParent;
import org.apache.cassandra.service.ColumnPath;
import org.apache.cassandra.service.ConsistencyLevel;
import org.apache.cassandra.service.InvalidRequestException;
import org.apache.cassandra.service.SlicePredicate;
import org.apache.cassandra.service.SliceRange;
import org.apache.cassandra.service.SuperColumn;
import org.apache.cassandra.service.TimedOutException;
import org.apache.cassandra.service.UnavailableException;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.junit.Test;

public class CassandraClientTest {

	@Test
	public void testAddShippingPackage() throws UnsupportedEncodingException, InvalidRequestException, UnavailableException, TimedOutException, TException{
		{
			AddShippingPackage client = new AddShippingPackage("p9292902", "item30702",9);
			client.exec();
		}
		{
			AddShippingPackage client = new AddShippingPackage("p9292902", "item30212",7);
			client.exec();
		}
		{
			AddShippingPackage client = new AddShippingPackage("p9292902", "item30242",91);
			client.exec();
		}
	}
	
	
	@Test
	public void testAddOneShippedPackage() throws UnsupportedEncodingException, InvalidRequestException, UnavailableException, TimedOutException, TException{
		TTransport transport = null;
		try{
			transport = new TSocket("localhost", 9160);
			TProtocol protocol = new TBinaryProtocol(transport);
			Cassandra.Client client = new Cassandra.Client(protocol);
			transport.open();
			//client.batch_insert("SuperInventory", packageNumber, oneRowPerColumnFamily, ConsistencyLevel.ALL);
			String orderNumber = "order488";
			String[] packageTracking = {"UPS69"};
			String[][] itemsInPackage = {{"toaster2","hair-drier-2"},{"coffe-maker-2"}};
			String[][] quantityOfItems = {{"1","2"},{"3"}};
			//
			Map<String, List<ColumnOrSuperColumn>> orderShippedPackages = new HashMap<String, List<ColumnOrSuperColumn>>();
			Map<String, List<ColumnOrSuperColumn>> oneRowPerColumnFamily = new HashMap<String, List<ColumnOrSuperColumn>>();
			List<ColumnOrSuperColumn> shippedPackageRow = new ArrayList<ColumnOrSuperColumn>();
			List<ColumnOrSuperColumn> orderPackageRow = new ArrayList<ColumnOrSuperColumn>();
			
			long timestamp = System.currentTimeMillis();
			for(int i=0;i<packageTracking.length;i++){
				//add items
				for(int j=0;j<itemsInPackage[i].length;j++){
					System.out.println("processing " + packageTracking[i] + " " + itemsInPackage[i][j] + " " + quantityOfItems[i][j]);
					Column itemNumber = new Column("item-number".getBytes("utf-8"), itemsInPackage[i][j].getBytes("utf-8"),timestamp);
					Column quantityInPackage = new Column("quantityInPackage".getBytes("utf-8"), quantityOfItems[i][j].getBytes("utf-8"),timestamp);
					SuperColumn itemInPackage = new SuperColumn();
					itemInPackage.setName(itemsInPackage[i][j].getBytes("utf-8"));
					itemInPackage.addToColumns(itemNumber);
					itemInPackage.addToColumns(quantityInPackage);
					ColumnOrSuperColumn c = new ColumnOrSuperColumn();
					c.setSuper_column(itemInPackage);
					shippedPackageRow.add(c);
				}
				{
					//add the order number to the row too
					Column orderNumberColumn = new Column("order-number".getBytes("utf-8"),orderNumber.getBytes("utf-8"),timestamp);
					SuperColumn superOrderNumber = new SuperColumn();
					superOrderNumber.setName(orderNumber.getBytes("utf-8"));
					superOrderNumber.addToColumns(orderNumberColumn);
					ColumnOrSuperColumn c = new ColumnOrSuperColumn();
					c.setSuper_column(superOrderNumber);
					shippedPackageRow.add(c);
				}
				{
					//add the order number
					Column shipDateColumn = new Column("shipped-date".getBytes("utf-8"),"2/2/2010".getBytes("utf-8"),timestamp);
					Column carrierColumn = new Column("carrier".getBytes("utf-8"),"UPS".getBytes("utf-8"),timestamp);
					SuperColumn orderPackageShipped = new SuperColumn();
					orderPackageShipped.setName(packageTracking[i].getBytes("utf-8"));
					orderPackageShipped.addToColumns(shipDateColumn);
					orderPackageShipped.addToColumns(carrierColumn);
					ColumnOrSuperColumn c = new ColumnOrSuperColumn();
					c.setSuper_column(orderPackageShipped);
					orderPackageRow.add(c);
				}
				//add this row to column family order-shipping-packages, keyed by package number
				oneRowPerColumnFamily.put("ShippedPackages", shippedPackageRow);
				client.batch_insert("SuperInventory", packageTracking[i], oneRowPerColumnFamily, ConsistencyLevel.ALL);
			}

			//now add the order-shipped-package relationship
			orderShippedPackages.put("OrderShippedPackages", orderPackageRow);
			client.batch_insert("SuperInventory", orderNumber, orderShippedPackages, ConsistencyLevel.ALL);

		}
		finally{
			transport.flush();
			transport.close();
		}
	}
	
	@Test
	public void testAddAllShippedPackagesTogether() throws UnsupportedEncodingException, InvalidRequestException, UnavailableException, TimedOutException, TException{
		TTransport transport = null;
		try{
			transport = new TSocket("localhost", 9160);
			TProtocol protocol = new TBinaryProtocol(transport);
			Cassandra.Client client = new Cassandra.Client(protocol);
			transport.open();
			//client.batch_insert("SuperInventory", packageNumber, oneRowPerColumnFamily, ConsistencyLevel.ALL);
			String orderNumber = "order998";
			String[] packageTracking = {"UPS345","UPS678"};
			String[][] itemsInPackage = {{"toaster1","hair-drier-1"},{"coffe-maker-1"}};
			String[][] quantityOfItems = {{"1","2"},{"3"}};
			//
			Map<String, List<ColumnOrSuperColumn>> shippedPackages = new HashMap<String, List<ColumnOrSuperColumn>>();
			Map<String, List<ColumnOrSuperColumn>> orderShippedPackages = new HashMap<String, List<ColumnOrSuperColumn>>();
			List<ColumnOrSuperColumn> shippedPackageRow = new ArrayList<ColumnOrSuperColumn>();
			List<ColumnOrSuperColumn> orderPackageRow = new ArrayList<ColumnOrSuperColumn>();
			
			long timestamp = System.currentTimeMillis();
			for(int i=0;i<packageTracking.length;i++){
				//add items
				for(int j=0;j<itemsInPackage[i].length;j++){
					System.out.println("processing " + packageTracking[i] + " " + itemsInPackage[i][j] + " " + quantityOfItems[i][j]);
					Column itemNumber = new Column("item-number".getBytes("utf-8"), itemsInPackage[i][j].getBytes("utf-8"),timestamp);
					Column quantityInPackage = new Column("quantityInPackage".getBytes("utf-8"), quantityOfItems[i][j].getBytes("utf-8"),timestamp);
					SuperColumn itemInPackage = new SuperColumn();
					itemInPackage.setName(itemsInPackage[i][j].getBytes("utf-8"));
					itemInPackage.addToColumns(itemNumber);
					itemInPackage.addToColumns(quantityInPackage);
					ColumnOrSuperColumn c = new ColumnOrSuperColumn();
					c.setSuper_column(itemInPackage);
					shippedPackageRow.add(c);
				}
				{
					//add the order number
					Column shipDateColumn = new Column("shipped-date".getBytes("utf-8"),"2/2/2010".getBytes("utf-8"),timestamp);
					Column carrierColumn = new Column("carrier".getBytes("utf-8"),"UPS".getBytes("utf-8"),timestamp);
					SuperColumn orderPackageShipped = new SuperColumn();
					orderPackageShipped.setName(packageTracking[i].getBytes("utf-8"));
					orderPackageShipped.addToColumns(shipDateColumn);
					orderPackageShipped.addToColumns(carrierColumn);
					ColumnOrSuperColumn c = new ColumnOrSuperColumn();
					c.setSuper_column(orderPackageShipped);
					orderPackageRow.add(c);
				}
				//add this row to column family order-shipping-packages, keyed by package number
				shippedPackages.put("ShippedPackages", shippedPackageRow);
				client.batch_insert("SuperInventory", packageTracking[i], shippedPackages, ConsistencyLevel.ALL);
				
			}
			orderShippedPackages.put("OrderShippedPackages", orderPackageRow);
			client.batch_insert("SuperInventory", orderNumber, orderShippedPackages, ConsistencyLevel.ALL);

		}
		finally{
			transport.flush();
			transport.close();
		}
	}
	
	@Test
	public void testSearchOrderShippedPackages() throws InvalidRequestException, UnavailableException, TimedOutException, TException, UnsupportedEncodingException{
		TTransport transport = null;
		try{
			transport = new TSocket("localhost", 9160);
			TProtocol protocol = new TBinaryProtocol(transport);
			Cassandra.Client client = new Cassandra.Client(protocol);
			transport.open();
			//client.batch_insert("SuperInventory", packageNumber, oneRowPerColumnFamily, ConsistencyLevel.ALL);
			String orderNumber = "order484";
			SlicePredicate slicePredicate = new SlicePredicate();
			SliceRange sliceRange = new SliceRange("".getBytes(),"".getBytes(),false,100);
			slicePredicate.setSlice_range(sliceRange);
			List<ColumnOrSuperColumn> results = client.get_slice("SuperInventory", orderNumber, new ColumnParent("OrderShippedPackages",null), slicePredicate, ConsistencyLevel.ONE);
			System.out.println("found " + results.size());
			for(ColumnOrSuperColumn r:results){
				if(r.isSetSuper_column()){
					SuperColumn sc = r.getSuper_column();
					String trackingNumber = new String(sc.getName(),"utf-8");
					System.out.print("tracking number:" + trackingNumber);
					List<Column> attributes = sc.getColumns();
					for(Column attribute:attributes){
						System.out.print(new String(attribute.getName(),"utf-8") + "=" + new String(attribute.getValue(),"utf-8"));
					}
					System.out.println();
				}
			}
		}
		finally{
			transport.flush();
			transport.close();
		}
	}
	
	@Test
	public void testShippedPackageContents() throws InvalidRequestException, UnavailableException, TimedOutException, TException, UnsupportedEncodingException{
		TTransport transport = null;
		try{
			transport = new TSocket("localhost", 9160);
			TProtocol protocol = new TBinaryProtocol(transport);
			Cassandra.Client client = new Cassandra.Client(protocol);
			transport.open();
			//client.batch_insert("SuperInventory", packageNumber, oneRowPerColumnFamily, ConsistencyLevel.ALL);
			String packageNumber = "UPS69";
			SlicePredicate slicePredicate = new SlicePredicate();
			SliceRange sliceRange = new SliceRange("".getBytes(),"".getBytes(),false,100);
			slicePredicate.setSlice_range(sliceRange);
			List<ColumnOrSuperColumn> results = client.get_slice("SuperInventory", packageNumber, new ColumnParent("ShippedPackages",null/*"toaster33333".getBytes("utf-8")*/), slicePredicate, ConsistencyLevel.ONE);
			System.out.println("found " + results.size());
			for(ColumnOrSuperColumn r:results){
				if(r.isSetSuper_column()){
					SuperColumn sc = r.getSuper_column();
					String superName = new String(sc.getName(),"utf-8");
					System.out.print("superName:" + superName);
					List<Column> attributes = sc.getColumns();
					for(Column attribute:attributes){
						System.out.print(new String(attribute.getName(),"utf-8") + "=" + new String(attribute.getValue(),"utf-8") + " at " + attribute.getTimestamp());
					}
					System.out.println();
				}else{
					Column sc = r.getColumn();

					System.out.println(new String(sc.getName(),"utf-8") + "=" + new String(sc.getValue(),"utf-8"));

				}
			}
		}
		finally{
			transport.flush();
			transport.close();
		}
	}
	
	@Test
	public void testRemoveItemFromPackage() throws InvalidRequestException, UnavailableException, TimedOutException, TException, UnsupportedEncodingException{
		TTransport transport = null;
		try{
			transport = new TSocket("localhost", 9160);
			TProtocol protocol = new TBinaryProtocol(transport);
			Cassandra.Client client = new Cassandra.Client(protocol);
			transport.open();
			//client.batch_insert("SuperInventory", packageNumber, oneRowPerColumnFamily, ConsistencyLevel.ALL);
			String packageNumber = "UPS69";
			ColumnPath columnPath = new ColumnPath("ShippedPackages","hair-drier-2".getBytes("utf-8"),null);
			client.remove("SuperInventory", packageNumber, columnPath, 1270064964044L, ConsistencyLevel.ALL);
		}
		finally{
			transport.flush();
			transport.close();
		}
	}
	
}
