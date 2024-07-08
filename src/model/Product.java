/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author User
 */
public class Product {
    
     private String id;
    private String name;
    private double price;
    private float qty;

    public Product(String id, String name, double price, float qty) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.qty = qty;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public float getQty() {
        return qty;
    }
    
}
