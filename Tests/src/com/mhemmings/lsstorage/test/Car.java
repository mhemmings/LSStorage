package com.mhemmings.lsstorage.test;

import com.mhemmings.lsstorage.LSObject;

public class Car extends LSObject {

	private String name;
	private String colour;
	private Manufacturer manufacturer;
	

	public Car(String name, String colour, Manufacturer manufacturer) {
		this.name = name;
		this.colour = colour;
		this.manufacturer = manufacturer;
	}

	public String getName() {
		return name;
	}

	public String getColour() {
		return colour;
	}

	public Manufacturer getManufacturer() {
		return manufacturer;
	}
	
	public void setManufacturer(Manufacturer manufacturer) {
		this.manufacturer = manufacturer;
	}
	
}
