package com.mhemmings.lsstorage.test;

import com.mhemmings.lsstorage.LSDatabase;
import com.mhemmings.lsstorage.LSObject;
import com.mhemmings.lsstorage.LSTable;

public class MyDatabase extends LSDatabase {

	public static final LSTable<Car> carTable = new CarTable();
	public static final LSTable<Manufacturer> manufacturerTable = new ManufacturerTable();

	public MyDatabase() {
		super("CarsDatabase", 1);
	}

	@Override
	public LSTable<LSObject>[] tables() {
		return new LSTable[] { carTable, manufacturerTable };
	}

	@Override
	public String[] onUpgrade(int oldVersion) {
		String sql[] = new String[0];

		switch (oldVersion) {
		case 1:
		}

		return sql;
	}

}
