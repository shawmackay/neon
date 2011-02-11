/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jini.projects.neon.sample.shop;

import java.util.ArrayList;

/**
 *
 * @author calum
 */
public class SimpleDetailsForm {
    private ArrayList items = new ArrayList();
private ArrayList quantity = new ArrayList();
    
    public void addItem(String item){
        System.out.println("Adding item: " + item);
        this.items.add(item);
    }
    
    public void addQuantity(int quantity){
        System.out.println("Adding Quantity: " + quantity);
        this.quantity.add(quantity);
    }
    
    public String getItems(){
        return items.toString();
    }
    
    public String getQuantities(){
        return quantity.toString();
    }
}

