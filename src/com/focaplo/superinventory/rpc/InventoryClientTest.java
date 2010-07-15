package com.focaplo.superinventory.rpc;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.THttpClient;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.junit.Test;

import com.focaplo.supverinventory.rpc.InventoryLookUp;
import com.focaplo.supverinventory.rpc.InventoryLookUpResponse;
import com.focaplo.supverinventory.rpc.InventoryRpcServer;
import com.focaplo.supverinventory.rpc.InventoryRpcServer.Client;

public class InventoryClientTest {

	@Test
	public void testHttpClient() throws TException{
		TTransport transport = new THttpClient("http://localhost:7911");
		TBinaryProtocol binaryProtocol =
	        new TBinaryProtocol(transport);
		InventoryRpcServer.Client c = new Client(binaryProtocol);
		InventoryLookUp req = new InventoryLookUp();
		req.setUid(2222);
		req.setItemNumber("abc2222");
		 transport.open();
		InventoryLookUpResponse res = c.lookUp(req);
		transport.close();
		System.out.println("receveid:" + res);
	}
	
	@Test
	public void testSocketClient() throws TException{
		long totalTime=0;
		for(int i=0;i<10000;i++){
			long t1=System.currentTimeMillis();
		TSocket socket = new TSocket("localhost", 7911);
        socket.setTimeout(3000);
        TTransport transport = socket;
        TBinaryProtocol binaryProtocol =
	        new TBinaryProtocol(transport);
        InventoryRpcServer.Client c = new Client(binaryProtocol);
       
		InventoryLookUp req = new InventoryLookUp();
		req.setUid(2222);
		req.setItemNumber("abc2222");
		 transport.open();
		InventoryLookUpResponse res = c.lookUp(req);
		transport.close();
		totalTime+=(System.currentTimeMillis()-t1);
		}
		System.out.println("total cost:" + totalTime);
//		System.out.println("receveid:" + res);
	}
}
