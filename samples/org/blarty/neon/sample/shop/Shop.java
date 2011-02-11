package org.jini.projects.neon.sample.shop;

import java.util.Map;

import org.jini.projects.neon.annotations.Render;
import org.jini.projects.neon.collaboration.Collaborative;
import org.jini.projects.neon.sample.shop.dto.Address;

public interface Shop extends Collaborative	{
	@Render(action="add", populate={SimpleDetailsForm.class})
    public void addItems(SimpleDetailsForm item);
	@Render(action="delete", populate={String.class})
    public void deleteItems(String reference);
	
	@Render(action="updateAddress",populate={Address.class})
	public void updateDeliveryAddress(Address newAddress);
}
