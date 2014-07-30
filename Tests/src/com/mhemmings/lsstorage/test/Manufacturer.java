package com.mhemmings.lsstorage.test;

import com.mhemmings.lsstorage.LSObject;

public class Manufacturer extends LSObject {

	private int id;
	private String name;
	private String country;

	public Manufacturer(int id, String name, String country) {
		this.id = id;
		this.name = name;
		this.country = country;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getCountry() {
		return country;
	}

}
