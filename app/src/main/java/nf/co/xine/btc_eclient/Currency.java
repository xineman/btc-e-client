package nf.co.xine.btc_eclient;

/**
 * Created by uragu on 01.05.2016.
 */
public class Currency {
    Currency(String name, boolean enabled) {
        this.name = name;
        this.enabled = enabled;
    }

    private String name;
    private boolean enabled;
    private double ask;
    private double bid;

    public String getName() {
        return name;
    }

    public double getAsk() {
        return ask;
    }

    public void setAsk(double ask) {
        this.ask = ask;
    }

    public double getBid() {
        return bid;
    }

    public void setBid(double bid) {
        this.bid = bid;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
