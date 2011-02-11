package org.jini.projects.neon.sample.shop;

public class AvailableItemsDescription {
	private String reference;
	private String name;
	private double unitprice;
	private int quantityAvailable;
	public String getReference() {
		return reference;
	}
	public void setReference(String reference) {
		this.reference = reference;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getUnitprice() {
		return unitprice;
	}
	public void setUnitprice(double unitprice) {
		this.unitprice = unitprice;
	}
	public int getQuantityAvailable() {
		return quantityAvailable;
	}
	public void setQuantityAvailable(int quantityAvailable) {
		this.quantityAvailable = quantityAvailable;
	
	}
	
	public AvailableItemsDescription(){
		
	}
	public AvailableItemsDescription(String reference, String name, double unitprice, int quantityAvailable) {
		super();
		this.reference = reference;
		this.name = name;
		this.unitprice = unitprice;
		this.quantityAvailable = quantityAvailable;
	}
	
}
