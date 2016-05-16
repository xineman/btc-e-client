package nf.co.xine.btc_eclient.data_structure;


public class MyOrder {

    public MyOrder(String pair, String type, String amount, String rate, String timestamp_created) {
        this.pair = pair;
        this.type = type;
        this.amount = amount;
        this.rate = rate;
        this.timestamp_created = timestamp_created;
    }

    private String pair;
    private String type;
    private String amount;
    private String rate;
    private String timestamp_created;

    public String getPair() {
        return pair;
    }

    public String getType() {
        return type;
    }

    public String getAmount() {
        return amount;
    }

    public String getRate() {
        return rate;
    }

    public String getTimestamp_created() {
        return timestamp_created;
    }
}