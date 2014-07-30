package com.mhemmings.lsstorage.test;

import android.content.ContentValues;

import com.mhemmings.lsstorage.Column;
import com.mhemmings.lsstorage.LSTable;

public class CarTable extends LSTable<Car> {

	@Column("TEXT")
	public final static String name = "name";

	@Column("TEXT")
	public final static String colour = "colour";

	@Column("INTEGER")
	public final static String manufacturer_id = "manufacturer_id";

	@Override
	protected void in(Car car) {
		putValue(name, car.getName());
		putValue(colour, car.getColour());
		putValue(manufacturer_id, car.getManufacturer().getId());
	}

	@Override
	protected Car out(ContentValues values) {
		return new Car(values.getAsString(name), values.getAsString(colour),
				new Manufacturer(values.getAsInteger(manufacturer_id), null,
						null));
	}

}
