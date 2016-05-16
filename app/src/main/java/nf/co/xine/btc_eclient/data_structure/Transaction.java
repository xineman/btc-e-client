package nf.co.xine.btc_eclient.data_structure;

/**
 * Created by uragu on 08.05.2016.
 */
public class Transaction {

    public Transaction(int type, String amount, String currency, String desc, int status, String timestamp) {
        this.type = type;
        this.amount = amount;
        this.currency = currency;
        this.desc = desc;
        this.status = status;
        this.timestamp = timestamp;
    }

    private int type;
    private String amount;
    private String currency;
    private String desc;
    private int status;
    private String timestamp;

    public int getType() {
        return type;
    }

    public String getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public String getDesc() {
        return desc;
    }

    public int getStatus() {
        return status;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
