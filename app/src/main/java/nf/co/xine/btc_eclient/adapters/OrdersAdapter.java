package nf.co.xine.btc_eclient.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;

import nf.co.xine.btc_eclient.R;
import nf.co.xine.btc_eclient.StaticConverter;
import nf.co.xine.btc_eclient.data_structure.CurrencyOrder;

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
        value.setText(StaticConverter.doubleToString(cc.getAskPrice()) + " " + StaticConverter.right(currencyToTrade, 3));
        amount.setText(StaticConverter.doubleToString(cc.getAskAmount()) + " " + currencyToTrade.substring(0, 3));
        value2.setText(StaticConverter.doubleToString(cc.getBidPrice()) + " " + StaticConverter.right(currencyToTrade, 3));
        amount2.setText(StaticConverter.doubleToString(cc.getBidAmount()) + " " + currencyToTrade.substring(0, 3));
        return convertView;

    }


}
