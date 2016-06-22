package nf.co.xine.btc_eclient.data_structure;

/**
 * Created by uragu on 22.06.2016.
 */
public class Parameter {
    private String name;
    private String value;

    public Parameter(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
