package com.mhemmings.lsstorage.test;

import android.content.ContentValues;

import com.mhemmings.lsstorage.Column;
import com.mhemmings.lsstorage.LSTable;

public class ManufacturerTable extends LSTable<Manufacturer> {

	@Column("INTEGER")
	public final static String id = "id";

	@Column("TEXT")
	public final static String name = "name";

	@Column("TEXT")
	public final static String country = "country";

	@Override
	protected void in(Manufacturer manufacturer) {
		putValue(id, manufacturer.getId());
		putValue(name, manufacturer.getName());
		putValue(country, manufacturer.getCountry());
	}

	@Override
	protected Manufacturer out(ContentValues values) {
		return new Manufacturer(values.getAsInteger(id),
				values.getAsString(name), values.getAsString(country));
	}

}
