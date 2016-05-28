package nf.co.xine.btc_eclient;

import android.widget.EditText;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class StaticConverter {

    public static String doubleToString(double val) {
        DecimalFormat decimalFormat = (DecimalFormat) DecimalFormat.getInstance(Locale.getDefault());
        decimalFormat.setMaximumFractionDigits(8);
        return decimalFormat.format(val);
    }

    public static String to7PlacesDouble(String value) {

        double val = Double.valueOf(value);
        int integerPlaces = value.indexOf('.');
        if (integerPlaces == -1) {
            integerPlaces = value.length();
            switch (integerPlaces) {
                case 1: {
                    DecimalFormat df = new DecimalFormat("0.000000");
                    df.setRoundingMode(RoundingMode.CEILING);
                    return df.format(val);
                }
                case 2: {
                    DecimalFormat df = new DecimalFormat("#.00000");
                    df.setRoundingMode(RoundingMode.CEILING);
                    return df.format(val);
                }
                case 3: {
                    DecimalFormat df = new DecimalFormat("#.0000");
                    df.setRoundingMode(RoundingMode.CEILING);
                    return df.format(val);
                }
                case 4: {
                    DecimalFormat df = new DecimalFormat("#.000");
                    df.setRoundingMode(RoundingMode.CEILING);
                    return df.format(val);
                }
                case 5: {
                    DecimalFormat df = new DecimalFormat("#.00");
                    df.setRoundingMode(RoundingMode.CEILING);
                    return df.format(val);
                }
                case 6: {
                    DecimalFormat df = new DecimalFormat("#.0");
                    df.setRoundingMode(RoundingMode.CEILING);
                    return df.format(val);
                }
                default: {
                    DecimalFormat df = new DecimalFormat("#");
                    df.setRoundingMode(RoundingMode.CEILING);
                    return df.format(val);
                }
            }
        } else
            switch (integerPlaces) {
                case 1: {
                    DecimalFormat df = new DecimalFormat("0.000000");
                    df.setRoundingMode(RoundingMode.CEILING);
                    return df.format(val);
                }
                case 2: {
                    DecimalFormat df = new DecimalFormat("#.00000");
                    df.setRoundingMode(RoundingMode.CEILING);
                    return df.format(val);
                }
                case 3: {
                    DecimalFormat df = new DecimalFormat("#.0000");
                    df.setRoundingMode(RoundingMode.CEILING);
                    return df.format(val);
                }
                case 4: {
                    DecimalFormat df = new DecimalFormat("#.000");
                    df.setRoundingMode(RoundingMode.CEILING);
                    return df.format(val);
                }
                case 5: {
                    DecimalFormat df = new DecimalFormat("#.00");
                    df.setRoundingMode(RoundingMode.CEILING);
                    return df.format(val);
                }
                case 6: {
                    DecimalFormat df = new DecimalFormat("#.0");
                    df.setRoundingMode(RoundingMode.CEILING);
                    return df.format(val);
                }
                default: {
                    DecimalFormat df = new DecimalFormat("#");
                    df.setRoundingMode(RoundingMode.CEILING);
                    return df.format(val);
                }
            }
    }

    public static String currencyNameToUrlFormat(String name) {
        return (name.substring(0, 3) + "_" + name.substring(4, 7)).toLowerCase();
    }

    public static String right(String value, int length) {
        // To get right characters from a string, change the begin index.
        return value.substring(value.length() - length);
    }

    public static double getDoubleFromEditText(EditText editText) {
        DecimalFormat format = (DecimalFormat) DecimalFormat.getInstance(Locale.getDefault());
        DecimalFormatSymbols symbols = format.getDecimalFormatSymbols();
        char sep = symbols.getDecimalSeparator();
        Number number;
        double d = 0;
        try {
            number = format.parse(editText.getText().toString());
            d = number.doubleValue();
        } catch (Exception ignored) {
        }
        if (editText.getText().toString().equals("") || editText.getText().toString().equals(String.valueOf(sep))) {
            return 0;
        } else return d;
    }
}
