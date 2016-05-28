package nf.co.xine.btc_eclient.data_structure;

public class Currency {
    public Currency(String name, boolean enabled, String ask, String bid) {
        this.name = name;
        this.enabled = enabled;
        this.ask = ask;
        this.bid = bid;
    }

    private String name;
    private boolean enabled;
    private String ask;
    private String bid;

    public String getName() {
        return name;
    }

    public String getAsk() {
        return ask;
    }

    public void setAsk(String ask) {
        this.ask = ask;
    }

    public String getBid() {
        return bid;
    }

    public void setBid(String bid) {
        this.bid = bid;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String toString() {
        return name;
    }
}
