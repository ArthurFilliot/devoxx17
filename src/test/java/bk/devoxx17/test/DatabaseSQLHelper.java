package bk.devoxx17.test;

import bk.devoxx17.emulators.databases.DatabaseSQL;

public class DatabaseSQLHelper {
	private static DatabaseSQL db = new DatabaseSQL();
	
	public static DatabaseSQL getDb() {
		return db;
	}
	
	public static void init() { 
		db.openConnection();
		db.openTransaction();
	}
	 
	public static void terminate() {
		db.rollbackTransaction();
		db.closeConnection();
	}
}
