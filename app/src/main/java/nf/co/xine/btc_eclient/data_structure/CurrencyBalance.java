package nf.co.xine.btc_eclient.data_structure;

public class CurrencyBalance {
    private String currencyName;
    private String value;

    public CurrencyBalance(String currencyName, String value) {
        this.currencyName = currencyName;
        this.value = value;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public String getValue() {
        return value;
    }
}
