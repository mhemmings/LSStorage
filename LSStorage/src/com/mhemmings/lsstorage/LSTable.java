package com.mhemmings.lsstorage;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import android.content.ContentValues;

/**
 * Ultimately a SQLite table. Each LSTable can store one type of
 * {@link LSObject}, which are mapped into the table using the {@link #in} and
 * mapped back out using {@link #out}
 * 
 * @param <T>
 *            The type of {@link LSObject} to store in this table
 */
public abstract class LSTable<T extends LSObject> {
	public static final String NULL = "NULL";
	public static final String INTEGER = "INTEGER";
	public static final String REAL = "REAL";
	public static final String TEXT = "TEXT";
	public static final String BLOB = "BLOB";
	private ContentValues currentRow;
	private Map<String, String> columns;

	public LSTable() {
		columns = parseColumns();
		currentRow = new ContentValues();
	}

	/**
	 * How a {@link LSObject} is mapped into the table. For each column name and
	 * corresponding object property, call {@link #putValue}
	 * 
	 * @param object
	 *            The object to map to columns
	 */
	protected abstract void in(T object);

	/**
	 * How an object is created when getting it out of the table.
	 * {@link ConrentValues} are passed, each with the key of the column name
	 * and its value
	 * 
	 * @param values
	 *            {@link ContentValues} for an entire row in the table, where
	 *            each key is a column name
	 * @return You must return a new {@code LSObject} which has been created
	 *         using its constructor and the values from the ContentValues
	 */
	protected abstract T out(ContentValues values);

	/**
	 * Populates a row for the table with values. Used inside LSStorage and can
	 * be used externally to check the mappings between columns and object
	 * values
	 * 
	 * @param object
	 *            The {@code LSObject} to populate the row with
	 * @return {@link ContentValues} corresponding to each row name and its
	 *         value
	 */
	public final ContentValues createRow(T object) {
		currentRow.clear();
		in(object);
		return currentRow;
	}

	/**
	 * Get the name of the table
	 * 
	 * @return The name of the table as it will be created in the database
	 */
	public String getName() {
		return this.getClass().getSimpleName();
	}

	/**
	 * Get a map of the column names and their datatypes.
	 * 
	 * @return a Map of columns and datatypes
	 */
	public Map<String, String> getColumns() {
		return columns;
	}

	/**
	 * Get the SQL CREATE statement for this table. Override at your own risk!!
	 * 
	 * @return An SQL CREATE statement
	 */
	public String getCreateStatement() {
		String statement = "CREATE TABLE IF NOT EXISTS ";
		// Add table name
		statement += getName();

		// Add the _id column
		statement += "(_id INTEGER PRIMARY KEY AUTOINCREMENT";

		// Add the columns
		Iterator<Entry<String, String>> it = columns.entrySet().iterator();
		while (it.hasNext()) {
			statement += ", ";
			Entry<String, String> entry = (Entry<String, String>) it.next();
			statement += entry.getKey() + " ";
			statement += entry.getValue();
		}

		statement += ")";
		return statement;
	}

	/**
	 * To be called inside {@link #in}. Maps a column name to a {@code int}
	 * 
	 * @param column
	 *            The column name
	 * @param value
	 *            The {@code int} value
	 */
	protected final void putValue(String column, int value) {
		currentRow.put(column, value);
	}

	/**
	 * To be called inside {@link #in}. Maps a column name to a {@code float}
	 * 
	 * @param column
	 *            The column name
	 * @param value
	 *            The {@code float} value
	 */
	protected final void putValue(String column, float value) {
		currentRow.put(column, value);
	}

	/**
	 * To be called inside {@link #in}. Maps a column name to a {@code string}
	 * 
	 * @param column
	 *            The column name
	 * @param value
	 *            The {@code string} value
	 */
	protected final void putValue(String column, String value) {
		currentRow.put(column, value);
	}

	private Map<String, String> parseColumns() {
		Map<String, String> toReturn = new LinkedHashMap<String, String>();
		Field[] fields = this.getClass().getDeclaredFields();
		for (Field f : fields) {
			Column column = f.getAnnotation(Column.class);
			if (column == null)
				continue;
			try {
				toReturn.put((String) f.get(this),
						columnDatatype(column.value()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return toReturn;
	}

	private String columnDatatype(String string) {
		if (string.toUpperCase(Locale.ENGLISH).contains(NULL))
			return NULL;
		if (string.toUpperCase(Locale.ENGLISH).contains(INTEGER))
			return INTEGER;
		if (string.toUpperCase(Locale.ENGLISH).contains(REAL))
			return REAL;
		if (string.toUpperCase(Locale.ENGLISH).contains(TEXT))
			return TEXT;
		if (string.toUpperCase(Locale.ENGLISH).contains(BLOB))
			return BLOB;

		return string; // This should never happen
	}
}
