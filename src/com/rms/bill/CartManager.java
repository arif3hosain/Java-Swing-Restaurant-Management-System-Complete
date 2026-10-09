package com.rms.bill;

import com.rms.setting.Utils;

import java.util.ArrayList;
import java.util.List;

public class CartManager {

    private final List<CartItem> items = new ArrayList<>();

    public void addToCart(String name, String size, double unitPrice) {
        for (CartItem item : items) {
            if (item.name.equals(name) && item.size.equals(size)) {
                item.quantity++;
                return;
            }
        }
        items.add(new CartItem(name, size, unitPrice, 1));
    }

    public void incrementCart(String name, String size) {
        for (CartItem item : items) {
            if (item.name.equals(name) && item.size.equals(size)) {
                item.quantity++;
                return;
            }
        }
    }

    public void decrementCart(String name, String size) {
        CartItem toRemove = null;
        for (CartItem item : items) {
            if (item.name.equals(name) && item.size.equals(size)) {
                item.quantity--;
                if (item.quantity <= 0) toRemove = item;
                break;
            }
        }
        if (toRemove != null) items.remove(toRemove);
    }

    public void removeFromCart(String name, String size) {
        items.removeIf(item -> item.name.equals(name) && item.size.equals(size));
    }

    public void clearCart() {
        items.clear();
    }

    public List<CartItem> getItems() {
        return new ArrayList<>(items);
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public TotalsResult recalculateTotals(double discountPct) {
        double subtotal = items.stream().mapToDouble(CartItem::lineTotal).sum();
        double discountAmt = (subtotal * discountPct) / 100.0;
        double vatPct = Utils.getDoubleVal(Utils.VAT);
        double vatAmt = ((subtotal - discountAmt) * vatPct) / 100.0;
        double total = subtotal - discountAmt + vatAmt;
        return new TotalsResult(subtotal, discountAmt, vatAmt, total);
    }
}
