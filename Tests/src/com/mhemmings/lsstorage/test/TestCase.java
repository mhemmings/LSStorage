package com.mhemmings.lsstorage.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.test.AndroidTestCase;
import android.util.Log;

public class TestCase extends AndroidTestCase {

	private Controller controller;
	private CarTable carTable;
	private Car billie;
	private Car jim;

	@Override
	protected void setUp() throws Exception {
		controller = new Controller(this.getContext());
		carTable = new CarTable();
		billie = new Car("Billie", "Red",
				new Manufacturer(12, "Mazda", "Japan"));
		jim = new Car("Jim", "Blue", new Manufacturer(10, "Ford", "UK"));
	}

	@Override
	protected void tearDown() throws Exception {
		controller.dropAll();
		super.tearDown();
	}

	public void testTableCreateStatement() {
		String statement = "CREATE TABLE IF NOT EXISTS CarTable(_id INTEGER PRIMARY KEY AUTOINCREMENT, colour TEXT, manufacturer_id INTEGER, name TEXT)";
		assertEquals(statement, carTable.getCreateStatement());
	}

	public void testAddCar() {
		controller.addCar(billie);
	}

	public void testAddCars() {
		controller.addCars(new Car[] { billie, jim });
		ArrayList<Car> cars = controller.getAllCars();
		assertEquals(2, cars.size());
	}

	public void testGetCar() {
		controller.addCar(billie);
		Car c = controller.getCar("Billie");
		assertEquals(billie.getColour(), c.getColour());
		assertEquals(billie.getManufacturer().getCountry(), c.getManufacturer()
				.getCountry());
	}

	public void testGetAllCars() {
		controller.addCar(billie);
		controller.addCar(jim);
		ArrayList<Car> cars = controller.getAllCars();
		assertEquals(2, cars.size());
	}

	public void testUpdateCar() {
		controller.addCars(new Car[] { billie, jim });
		int changed = controller.changeColour("Billie", "pink");
		assertEquals(1, changed);
		Car car = controller.getCar("Billie");
		assertEquals("pink", car.getColour());
		car = controller.getCar("Jim");
		assertEquals(jim.getColour(), car.getColour());
	}

	public void testDelete() {
		controller.addCars(new Car[] { billie, jim });
		int deleted = controller.delete("Jim");
		assertEquals(1, deleted);
		ArrayList<Car> cars = controller.getAllCars();
		int remaining = cars.size();
		assertEquals(1, remaining);
		Car car = cars.get(0);
		assertEquals(car.getName(), billie.getName());
	}

	public void testRandom() {
		controller.addCars(new Car[] { billie, jim });
		List<String> cars = new ArrayList<String>();
		for (int i = 0; i < 50; i++) {
			cars.add(controller.randomCar().getName());
		}
		int billies = Collections.frequency(cars, billie.getName());
		int jims = Collections.frequency(cars, jim.getName());
		assertTrue(billies > 0 && billies < 35);
		assertTrue(jims > 0 && jims < 35);
		Log.e("billie", billies + "");
		Log.e("jim", jims + "");
	}
}
