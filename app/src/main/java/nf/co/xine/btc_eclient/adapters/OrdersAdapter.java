package nf.co.xine.btc_eclient.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import nf.co.xine.btc_eclient.R;
import nf.co.xine.btc_eclient.data_structure.CurrencyOrder;

/**
 * Created by uragu on 03.05.2016.
 */
public class OrdersAdapter extends ArrayAdapter<CurrencyOrder> {
    public OrdersAdapter(Context context, ArrayList orders, String currencyToTrade) {
        super(context, 0, orders);
        this.currencyToTrade = currencyToTrade;
    }

    //private ArrayList
    private String currencyToTrade;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        CurrencyOrder cc = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.order_list_item, parent, false);
        }
        // Lookup view for data population
        TextView value = (TextView) convertView.findViewById(R.id.ask_value);
        TextView amount = (TextView) convertView.findViewById(R.id.ask_amount);
        TextView value2 = (TextView) convertView.findViewById(R.id.bid_value);
        TextView amount2 = (TextView) convertView.findViewById(R.id.bid_amount);
        value.setText(String.valueOf(cc.getAskPrice()) + " " + right(currencyToTrade, 3));
        amount.setText(String.valueOf(cc.getAskAmount()) + " " + currencyToTrade.substring(0, 3));
        value2.setText(String.valueOf(cc.getBidPrice()) + " " + right(currencyToTrade, 3));
        amount2.setText(String.valueOf(cc.getBidAmount()) + " " + currencyToTrade.substring(0, 3));
        return convertView;

    }

    public static String right(String value, int length) {
        // To get right characters from a string, change the begin index.
        return value.substring(value.length() - length);
    }
}
