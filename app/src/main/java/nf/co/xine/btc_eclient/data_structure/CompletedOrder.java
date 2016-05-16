package nf.co.xine.btc_eclient.data_structure;

public class CompletedOrder {

    public CompletedOrder(String pair, String type, String amount, String rate, String order_id, int is_your_order, String timestamp) {
        this.pair = pair;
        this.type = type;
        this.amount = amount;
        this.rate = rate;
        this.order_id = order_id;
        this.is_your_order = is_your_order;
        this.timestamp = timestamp;
    }

    private String pair;
    private String type;
    private String amount;
    private String rate;
    private String order_id;
    private int is_your_order;
    private String timestamp;

    public int getIs_your_order() {
        return is_your_order;
    }

    public String getOrder_id() {
        return order_id;
    }

    public String getRate() {
        return rate;
    }

    public String getAmount() {
        return amount;
    }

    public String getType() {
        return type;
    }

    public String getPair() {
        return pair;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
