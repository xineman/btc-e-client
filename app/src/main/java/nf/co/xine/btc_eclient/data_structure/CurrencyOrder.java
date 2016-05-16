package nf.co.xine.btc_eclient.data_structure;

/**
 * Created by uragu on 03.05.2016.
 */
public class CurrencyOrder {
    public CurrencyOrder(double askPrice, double askAmount, double bidPrice, double bidAmount) {
        this.askPrice = askPrice;
        this.askAmount = askAmount;
        this.bidPrice = bidPrice;
        this.bidAmount = bidAmount;
    }

    private double askPrice;
    private double askAmount;
    private double bidPrice;
    private double bidAmount;


    public double getAskPrice() {
        return askPrice;
    }

    public double getAskAmount() {
        return askAmount;
    }

    public double getBidPrice() {
        return bidPrice;
    }

    public double getBidAmount() {
        return bidAmount;
    }
}
