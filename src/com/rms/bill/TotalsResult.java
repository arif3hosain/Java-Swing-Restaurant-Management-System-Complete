package com.rms.bill;

public class TotalsResult {
    public final double subtotal;
    public final double discountAmt;
    public final double vatAmt;
    public final double total;

    public TotalsResult(double subtotal, double discountAmt, double vatAmt, double total) {
        this.subtotal = subtotal;
        this.discountAmt = discountAmt;
        this.vatAmt = vatAmt;
        this.total = total;
    }
}
