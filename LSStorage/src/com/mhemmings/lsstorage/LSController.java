package com.mhemmings.lsstorage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * A controller for all the CRUD operations on your database. You should have
 * one class which extends LSController which abstracts away the database
 * interface.
 * 
 */
public abstract class LSController {
	private SQLiteOpenHelper dbHelper;
	private SQLiteDatabase database;

	public LSController(Context context, LSDatabase database) {
		dbHelper = new LSDatabaseHelper(context, database).helper();
	}

	/**
	 * Saves an {@link LSObject} into a {@link LSTable}. Under the hood,
	 * {@link SQLiteDatabase#insert} is called
	 * 
	 * @param table
	 *            the {@link LSTable} in which to save the object
	 * @param object
	 *            the {@link LSObject} to save
	 * @return the row ID of the newly inserted row, or -1 if an error occurred
	 */
	protected <T extends LSObject> long save(LSTable<T> table, T object) {
		open();
		database.beginTransaction();
		long row = -1L;
		try {
			row = database.insert(table.getName(), null,
					table.createRow(object));
			database.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			database.endTransaction();
			close();
		}
		return row;
	}

	/**
	 * Saves an array of {@link LSObject}s into a {@link LSTable}. Under the
	 * hood, {@link SQLiteDatabase#insert} is called multiple times within a
	 * single transaction
	 * 
	 * @param table
	 *            the {@link LSTable} in which to save the object
	 * @param object
	 *            an array of the {@link LSObject}s to save
	 */
	protected <T extends LSObject> void saveMany(LSTable<T> table, T[] objects) {
		open();
		database.beginTransaction();
		try {
			for (T object : objects) {
				database.insert(table.getName(), null, table.createRow(object));
			}
			database.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			database.endTransaction();
			close();
		}
	}

	/**
	 * Finds {@link LSObject}s within a {@link LSTable} that match a given
	 * criteria
	 * 
	 * @param table
	 *            the {@link LSTable} in which to search for the objects
	 * @param where
	 * 
	 *            A filter declaring which rows to return, formatted as an SQL
	 *            WHERE clause (excluding the WHERE itself). There are where
	 *            helper methods to do this for you. See {@link #whereEquals},
	 *            {@link #whereLessThan}, and {@link #whereGreaterThan}. Passing
	 *            {@code null} will return all rows for the given table.
	 * @param orderBy
	 *            How to order the rows, formatted as an SQL ORDER BY clause
	 *            (excluding the ORDER BY itself). Passing {@code null} will use
	 *            the default sort order, which may be unordered.
	 * @param limit
	 *            Limits the number of rows returned by the query, formatted as
	 *            LIMIT clause. Passing {@code null} denotes no LIMIT clause.
	 * @return An {@link ArrayList} of {@link LSObjects} that match the criteria
	 */
	protected <T extends LSObject> ArrayList<T> find(LSTable<T> table,
			String where, String orderBy, String limit) {
		open();
		Cursor cursor = database.query(false, table.getName(), null, where,
				null, null, null, orderBy, limit);
		cursor.getCount();
		ArrayList<T> objects = new ArrayList<T>();

		if (cursor.moveToFirst()) {
			do {
				ContentValues values = new ContentValues();
				Iterator<Entry<String, String>> iterator = table.getColumns()
						.entrySet().iterator();

				while (iterator.hasNext()) {
					Map.Entry<String, String> column = (Entry<String, String>) iterator
							.next();
					int index = cursor.getColumnIndex(column.getKey());
					values.putAll(columnContentValue(column.getKey(),
							column.getValue(), cursor, index));
				}
				objects.add(table.out(values));
			} while (cursor.moveToNext());
		}
		cursor.close();
		close();

		return objects;
	}

	/**
	 * Finds a single {@link LSObject} within a {@link LSTable} that matches a
	 * given criteria (the first match)
	 * 
	 * @param table
	 *            the {@link LSTable} in which to search for the object
	 * @param where
	 *            A filter declaring which rows to return, formatted as an SQL
	 *            WHERE clause (excluding the WHERE itself). There are where
	 *            helper methods to do this for you. See {@link #whereEquals},
	 *            {@link #whereLessThan}, and {@link #whereGreaterThan}. Passing
	 *            {@code null} will return all rows for the given table.
	 * @return A {@link LSObject} that matches the criteria
	 */
	protected <T extends LSObject> T findOne(LSTable<T> table, String where) {
		open();
		Cursor cursor = database.query(false, table.getName(), null, where,
				null, null, null, null, "1");

		cursor.getCount();
		T object = null;

		if (cursor.moveToFirst()) {
			ContentValues values = new ContentValues();
			Iterator<Entry<String, String>> iterator = table.getColumns()
					.entrySet().iterator();

			while (iterator.hasNext()) {
				Map.Entry<String, String> column = (Entry<String, String>) iterator
						.next();
				int index = cursor.getColumnIndex(column.getKey());
				values.putAll(columnContentValue(column.getKey(),
						column.getValue(), cursor, index));
			}
			object = table.out(values);
		}
		cursor.close();
		close();

		return object;
	}

	/**
	 * Helper method to get all {@link LSObject}s from a {@link LSTable}. Same
	 * as calling {@code find(table, null, null, null)}
	 * 
	 * @param table
	 *            the {@link LSTable} in which to search for the object
	 * @return An {@link ArrayList} of {@link LSObjects} that match the criteria
	 */
	protected <T extends LSObject> ArrayList<T> findAll(LSTable<T> table) {
		return find(table, null, null, null);
	}

	/**
	 * Helper method to return a random {@link LSObject} from a {@link LSTable}
	 * 
	 * @param table
	 *            the {@link LSTable} in which to get the object
	 * @return A random {@link LSObject}
	 */
	protected <T extends LSObject> T findRandom(LSTable<T> table) {
		open();
		Cursor cursor = database.query(table.getName()
				+ " Order BY RANDOM() LIMIT 1", new String[] { "*" }, null,
				null, null, null, null);

		cursor.getCount();
		T object = null;

		if (cursor.moveToFirst()) {
			ContentValues values = new ContentValues();
			Iterator<Entry<String, String>> iterator = table.getColumns()
					.entrySet().iterator();

			while (iterator.hasNext()) {
				Map.Entry<String, String> column = (Entry<String, String>) iterator
						.next();
				int index = cursor.getColumnIndex(column.getKey());
				values.putAll(columnContentValue(column.getKey(),
						column.getValue(), cursor, index));
			}
			object = table.out(values);
		}
		cursor.close();
		close();

		return object;
	}

	/**
	 * Updates a record (or records) in a table
	 * 
	 * @param table
	 *            The {@link LSTable} to update
	 * @param values
	 *            A set of {@link ContentValues} to update in a record(s)
	 * @param where
	 *            the optional WHERE clause to apply when updating. There are
	 *            where helper methods to do this for you. See
	 *            {@link #whereEquals}, {@link #whereLessThan}, and
	 *            {@link #whereGreaterThan}. Passing {@code null} will update
	 *            all rows.
	 * @return The number of records changed, as an {@code int}
	 */
	protected <T extends LSObject> int update(LSTable<T> table,
			ContentValues values, String where) {
		open();
		database.beginTransaction();
		int affected = 0;
		try {
			affected = database.update(table.getName(), values, where, null);
			database.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			database.endTransaction();
			close();
		}
		return affected;
	}

	/**
	 * Deletes a record (or records) from a table
	 * 
	 * @param table
	 *            The {@link LSTable} to delete from
	 * @param where
	 *            the optional WHERE clause to apply when deleting. There are
	 *            where helper methods to do this for you. See
	 *            {@link #whereEquals}, {@link #whereLessThan}, and
	 *            {@link #whereGreaterThan}. Passing {@code null} will delete
	 *            all rows.
	 * @return
	 */
	protected <T extends LSObject> int delete(LSTable<T> table, String where) {
		int deleted = 0;
		open();
		database.beginTransaction();
		try {
			deleted = database.delete(table.getName(), where, null);
			database.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			database.endTransaction();
			close();
		}
		return deleted;
	}

	/**
	 * Helper to delete all rows in a {@link LSTable}
	 * 
	 * @param table
	 *            The {@link LSTable} to delete from
	 */
	protected <T extends LSObject> void dropItLikeItsHot(LSTable<T> table) {
		delete(table, null);
	}

	/**
	 * Helper to create a formatted WHERE string. In the format of
	 * "{column}='{value}'"
	 * 
	 * @param column
	 *            The column
	 * @param value
	 *            The value
	 * @return A SQL WHERE String
	 */
	protected String whereEquals(String column, String value) {
		return where(column, value, "=");
	}

	/**
	 * Helper to create a formatted WHERE string. In the format of
	 * "{column}>'{value}'"
	 * 
	 * @param column
	 *            The column
	 * @param value
	 *            The value
	 * @return A SQL WHERE String
	 */
	protected String whereGreaterThan(String column, String value) {
		return where(column, value, ">");
	}

/**
	 * Helper to create a formatted WHERE string. In the format of
	 * "{column}<'{value}'"
	 * 
	 * @param column
	 *            The column
	 * @param value
	 *            The value
	 * @return A SQL WHERE String
	 */
	protected String whereLessThan(String column, String value) {
		return where(column, value, "<");
	}

	private void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	private void close() {
		dbHelper.close();
	}

	private String where(String column, String value, String operator) {
		return column + operator + "'" + value + "'";
	}

	private ContentValues columnContentValue(String column, String type,
			Cursor cursor, int index) {
		ContentValues values = new ContentValues();
		if (type.equals(LSTable.NULL))
			values.putNull(column);
		else if (type.equals(LSTable.INTEGER))
			values.put(column, cursor.getInt(index));
		else if (type.equals(LSTable.REAL))
			values.put(column, cursor.getDouble(index));
		else if (type.equals(LSTable.TEXT))
			values.put(column, cursor.getString(index));
		else if (type.equals(LSTable.BLOB))
			values.put(column, cursor.getBlob(index));

		return values;
	}

	private final class LSDatabaseHelper {
		private LSDatabase database;
		private DatabaseHelper dbHelper;

		private LSDatabaseHelper(Context context, LSDatabase database) {
			this.database = database;
			dbHelper = new DatabaseHelper(context);
		}

		public SQLiteOpenHelper helper() {
			return dbHelper;
		}

		private class DatabaseHelper extends SQLiteOpenHelper {

			public DatabaseHelper(Context context) {
				super(context, database.getDbName(), null, database
						.getDbVersion());
			}

			@Override
			public void onCreate(SQLiteDatabase db) {
				for (LSTable<LSObject> table : database.tables()) {
					db.execSQL(table.getCreateStatement());
				}
			}

			@Override
			public void onUpgrade(SQLiteDatabase db, int oldVersion,
					int newVersion) {
				String[] sql = database.onUpgrade(oldVersion);
				for (String command : sql) {
					db.execSQL(command);
				}
			}

		}

	}

}