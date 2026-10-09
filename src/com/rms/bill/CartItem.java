package com.rms.bill;

public class CartItem {
    public String name;
    public String size;
    public double unitPrice;
    public int quantity;

    public CartItem(String name, String size, double unitPrice, int quantity) {
        this.name = name;
        this.size = size;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }

    public double lineTotal() {
        return unitPrice * quantity;
    }
}
