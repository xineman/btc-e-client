package nf.co.xine.btc_eclient.data_structure;


public class MarketTrade {
    private String type;
    private String amount;
    private String price;
    private String timestamp;

    public MarketTrade(String type, String amount, String price, String timestamp) {
        this.type = type;
        this.amount = amount;
        this.price = price;
        this.timestamp = timestamp;
    }

    public String getPrice() {
        return price;
    }

    public String getType() {
        return type;
    }

    public String getAmount() {
        return amount;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
