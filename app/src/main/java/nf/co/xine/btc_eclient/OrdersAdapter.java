package nf.co.xine.btc_eclient;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by uragu on 03.05.2016.
 */
public class OrdersAdapter extends ArrayAdapter<CurrencyOrder> {
    public OrdersAdapter(Context context, ArrayList orders) {
        super(context, 0, orders);
    }

    //private ArrayList

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        CurrencyOrder cc = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.order_list_item, parent, false);
        }
        // Lookup view for data population
        TextView value = (TextView) convertView.findViewById(R.id.order_value);
        TextView amount = (TextView) convertView.findViewById(R.id.order_amount);
        value.setText(String.valueOf(cc.getPrice()));
        amount.setText(String.valueOf(cc.getAmount()));
        return convertView;

    }
}
