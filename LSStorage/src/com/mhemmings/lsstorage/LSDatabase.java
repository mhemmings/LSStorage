package com.mhemmings.lsstorage;

public abstract class LSDatabase {
	private String dbName;
	private int dbVer;

	/**
	 * How you tell LSStorage about your tables. This method has to return an
	 * array of {@link LSTable}s
	 * 
	 * @return An array of the {@link LSTable}s to use in the database
	 */
	public abstract LSTable<LSObject>[] tables();

	/**
	 * Use this method to upgrade your database (e.g. adding new tables).
	 * Currently you need to write the raw SQL yourself per version upgrade. The
	 * SQL will get executed in the order it is in the array. The easiest way to
	 * do this is to switch on the old db number and have a case per upgrade,
	 * without using {@code break}. This has the effect of users being able to
	 * skip db versions but not miss those upgrades.
	 * 
	 * @param oldVersion
	 *            The version the db is upgrading FROM
	 * @return An array of SQL statements, to be executed in order
	 */
	public abstract String[] onUpgrade(int oldVersion);

	/**
	 * Must be called in the database constructor
	 * 
	 * @param databaseName
	 *            The name of the database, without the .db extension
	 * @param databaseVersion
	 *            The version of the database as an {@code int}
	 */
	public LSDatabase(String databaseName, int database_version) {
		this.dbName = databaseName + ".db";
		this.dbVer = database_version;
	}

	/**
	 * Gets the full database name (including the .db extension)
	 * 
	 * @return The database name
	 */
	public String getDbName() {
		return dbName;
	}

	/**
	 * Gets the database version
	 * 
	 * @return The database version
	 */
	public int getDbVersion() {
		return dbVer;
	}

}
