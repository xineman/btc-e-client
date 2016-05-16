package nf.co.xine.btc_eclient.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import nf.co.xine.btc_eclient.R;
import nf.co.xine.btc_eclient.data_structure.CurrencyBalance;

public class BalanceAdapter extends ArrayAdapter<CurrencyBalance> {
    public BalanceAdapter(Context context, ArrayList balances) {
        super(context, 0, balances);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        CurrencyBalance item = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.balance_item, parent, false);
        }
        // Lookup view for data population
        TextView name = (TextView) convertView.findViewById(R.id.balance_currency_name);
        TextView value = (TextView) convertView.findViewById(R.id.balance_currency_value);

        name.setText(item.getCurrencyName());
        value.setText(item.getValue());
        // Return the completed view to render on screen
        return convertView;
    }
}
