package com.mobile.safe.enity;

public class ContactInfo {

	private String number;
	private String name;
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public String toString() {
		return "ContactInfo [number=" + number + ", name=" + name + "]";
	}
	
	
}
