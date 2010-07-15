package com.focaplo.superinventory.cassandra;

import java.io.UnsupportedEncodingException;

import org.apache.cassandra.service.Cassandra;
import org.apache.cassandra.service.ConsistencyLevel;
import org.apache.cassandra.service.InvalidRequestException;
import org.apache.cassandra.service.TimedOutException;
import org.apache.cassandra.service.UnavailableException;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

public class CassandraClientTemplate {
	private static CassandraClientTemplate instance;
	private CassandraClientTemplate(){
		
	}
	
	public static CassandraClientTemplate instance(){
		if(instance==null){
			instance = new CassandraClientTemplate();
		}
		return instance;
	}
	public void batchInsert(BatchInsertable bi) throws InvalidRequestException, UnavailableException, TimedOutException, TException, UnsupportedEncodingException{
		TTransport transport = null;
		try{
			transport = new TSocket("localhost", 9160);
			TProtocol protocol = new TBinaryProtocol(transport);
			Cassandra.Client client = new Cassandra.Client(protocol);
			transport.open();
			//client.batch_insert("SuperInventory", packageNumber, oneRowPerColumnFamily, ConsistencyLevel.ALL);
			client.batch_insert(bi.getKeyspace(), bi.getUniversalRowKey(), bi.getRowsColumnFamilyMap(), bi.getConsistenceLevel());
		}
		finally{
			transport.flush();
			transport.close();
		}
	}
}
