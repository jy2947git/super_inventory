package com.focaplo.superinventory.cassandra;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import org.apache.cassandra.service.ColumnOrSuperColumn;

public interface BatchInsertable {

	String getKeyspace();

	String getUniversalRowKey();

	int getConsistenceLevel();

	Map<String, List<ColumnOrSuperColumn>> getRowsColumnFamilyMap() throws UnsupportedEncodingException;

}
