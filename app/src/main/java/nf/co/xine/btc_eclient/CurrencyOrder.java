package nf.co.xine.btc_eclient;

/**
 * Created by uragu on 03.05.2016.
 */
public class CurrencyOrder {
    public CurrencyOrder(double price, double amount) {
        this.amount = amount;
        this.price = price;
    }

    private double price;
    private double amount;

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
