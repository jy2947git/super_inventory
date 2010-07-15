package com.focaplo.superinventory.rpc;

import org.apache.thrift.TException;

import com.focaplo.superinventory.InventoryServiceImpl;
import com.focaplo.supverinventory.rpc.InventoryLookUp;
import com.focaplo.supverinventory.rpc.InventoryLookUpResponse;
import com.focaplo.supverinventory.rpc.QuantityRefresh;
import com.focaplo.supverinventory.rpc.QuantityRefreshResponse;
import com.focaplo.supverinventory.rpc.QuantityUpdate;
import com.focaplo.supverinventory.rpc.QuantityUpdateResponse;
import com.focaplo.supverinventory.rpc.itemReservation;
import com.focaplo.supverinventory.rpc.itemReservationResponse;
import com.focaplo.supverinventory.rpc.InventoryRpcServer.Iface;

public class InventoryService implements Iface {

	private void onStartUp(){
		//load all inventory data from Cassandra
	}
	@Override
	public InventoryLookUpResponse lookUp(InventoryLookUp request)
			throws TException {
		InventoryServiceImpl impl = new InventoryServiceImpl();
		return impl.lookUp(request);
	}
	@Override
	public QuantityRefreshResponse refreshQuantity(QuantityRefresh request)
			throws TException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public itemReservationResponse reserve(itemReservation request)
			throws TException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public QuantityUpdateResponse updateQuantity(QuantityUpdate request)
			throws TException {
		// TODO Auto-generated method stub
		return null;
	}

}
