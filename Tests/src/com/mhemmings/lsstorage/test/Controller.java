package com.mhemmings.lsstorage.test;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;

import com.mhemmings.lsstorage.LSController;

public class Controller extends LSController {

	public Controller(Context context) {
		super(context, new MyDatabase());
	}

	public void addCar(Car car) {
		save(MyDatabase.carTable, car);
		save(MyDatabase.manufacturerTable, car.getManufacturer());
	}

	public void addCars(Car[] cars) {
		saveMany(MyDatabase.carTable, cars);
		ArrayList<Manufacturer> manufacturers = new ArrayList<Manufacturer>();
		for (Car car : cars) {
			manufacturers.add(car.getManufacturer());
		}
		Manufacturer[] manArray = manufacturers
				.toArray(new Manufacturer[manufacturers.size()]);
		saveMany(MyDatabase.manufacturerTable, manArray);
	}

	public Car getCar(String name) {
		Car c = findOne(MyDatabase.carTable, whereEquals(CarTable.name, name));
		c.setManufacturer(findOne(
				MyDatabase.manufacturerTable,
				whereEquals(ManufacturerTable.id,
						Integer.toString(c.getManufacturer().getId()))));
		return c;
	}

	public ArrayList<Car> getAllCars() {
		ArrayList<Car> cars = findAll(MyDatabase.carTable);
		return cars;
	}

	public int changeColour(String name, String colour) {
		ContentValues values = new ContentValues();
		values.put(CarTable.colour, colour);
		String where = whereEquals(CarTable.name, name);
		return update(MyDatabase.carTable, values, where);
	}

	public int delete(String name) {
		String where = whereEquals(CarTable.name, name);
		return delete(MyDatabase.carTable, where);
	}

	public void dropAll() {
		dropItLikeItsHot(MyDatabase.carTable);
		dropItLikeItsHot(MyDatabase.manufacturerTable);
	}

	public Car randomCar() {
		return findRandom(MyDatabase.carTable);
	}

}
