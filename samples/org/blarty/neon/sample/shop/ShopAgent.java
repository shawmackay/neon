package org.jini.projects.neon.sample.shop;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.jini.projects.neon.agents.AbstractAgent;
import org.jini.projects.neon.host.NoSuchAgentException;
import org.jini.projects.neon.neontests.tutorial.jdbc.JDBCAgent;
import org.jini.projects.neon.neontests.tutorial.support.JDBCProcessor;
import org.jini.projects.neon.render.BadFormatException;
import org.jini.projects.neon.render.EngineInstruction;
import org.jini.projects.neon.render.PresentableAgent;
import org.jini.projects.neon.sample.shop.dto.Address;
import org.jini.projects.neon.web.WebUtils;
import org.jini.projects.neon.web.XmlCombinerLocation;

public class ShopAgent extends AbstractAgent implements Shop, PresentableAgent {

	private transient Map<String, AvailableItemsDescription> availItems;
	private transient JDBCAgent dbConnector;

	private transient Address deliveryAddress = new Address();

	public ShopAgent() {
		this.namespace = "example.shop";
		this.name = "Frontend";
	}

	@Override
	public boolean init() {
		availItems = new TreeMap<String, AvailableItemsDescription>();
		try {
			dbConnector = (JDBCAgent) getAgentContext().getAgent("example.Jdbc");
			JDBCProcessor proc = new ShopJDBCProcessor();
			dbConnector.InvokeJDBCProcessor(proc);
			List l = (List) proc.getResults();
			for (Object item : l) {
				AvailableItemsDescription desc = (AvailableItemsDescription) item;
				availItems.put(desc.getReference(), desc);
			}
			return true;
		} catch (NoSuchAgentException e) {
			// TODO Auto-generated catch block
			getAgentLogger().severe("JDBCAgent not found: aborting agent deployment");
			e.printStackTrace();
			return false;
		}
	}

	public void addItemToAvailableList(String reference, String name, double unitprice, int availquantity) {
		availItems.put(reference, new AvailableItemsDescription(reference, name, unitprice, availquantity));
	}

	public EngineInstruction getPresentableFormat(String type, String action, Map params, boolean getTemplate) {
		StringBuffer buffer = new StringBuffer();
		BufferedReader reader = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("main.xml")));
		try {
			String line = reader.readLine();
			while (line != null) {
				buffer.append(line);
				line = reader.readLine();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String xml = null;
		try {
			String itemsxml = WebUtils.convertObjectToXML("items", availItems);
			String addressxml = WebUtils.convertObjectToXML("delivery", deliveryAddress);
			List<XmlCombinerLocation> docs = new ArrayList<XmlCombinerLocation>();
			docs.add(new XmlCombinerLocation("entities", itemsxml));
			docs.add(new XmlCombinerLocation("addresses", addressxml));

			xml = WebUtils.combineDocuments("shop", docs);
			System.out.println("xml:" + xml);
		} catch (BadFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new EngineInstruction("xsl", getTemplate("xsl", action), xml);
	}

	public URL getTemplate(String type, String action) {
		// TODO Auto-generated method stub
		return this.getClass().getResource("main.xsl");
	}

	public void addItems(SimpleDetailsForm item) {
		// TODO Auto-generated method stub
		System.out.println("Adding Item: " + item.getItems() + "quantity: " + item.getQuantities());

	}

	public void deleteItems(String reference) {
		// TODO Auto-generated method stub
		System.out.println("Doing delete");
	}

	public void setAddress(Address delivery) {
		System.out.println("Setting address to:" + delivery);
		this.deliveryAddress = delivery;
	}

	public void updateDeliveryAddress(Address newAddress) {
		System.out.println("Updating address");
		this.deliveryAddress.setLine1(newAddress.getLine1());
		this.deliveryAddress.setLine2(newAddress.getLine2());
		this.deliveryAddress.setTown(newAddress.getTown());
		this.deliveryAddress.setStateOrCounty(newAddress.getStateOrCounty());
		this.deliveryAddress.setZipOrPostalCode(newAddress.getZipOrPostalCode());
	}
}
