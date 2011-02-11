package org.jini.projects.neon.sample.ajax;

public class DisplayableItem {
	private String identifier;
	private String name;
	private String description;
	private double unitprice;
	private int quantityavailable;
	
	public DisplayableItem(String description, String identifier, String name, int quantityavailable, double unitprice) {
		super();
		this.description = description;
		this.identifier = identifier;
		this.name = name;
		this.quantityavailable = quantityavailable;
		this.unitprice = unitprice;
	}

	public DisplayableItem(){
		
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public double getUnitprice() {
		return unitprice;
	}

	public void setUnitprice(double unitprice) {
		this.unitprice = unitprice;
	}

	public int getQuantityavailable() {
		return quantityavailable;
	}

	public void setQuantityavailable(int quantityavailable) {
		this.quantityavailable = quantityavailable;
	}
}
