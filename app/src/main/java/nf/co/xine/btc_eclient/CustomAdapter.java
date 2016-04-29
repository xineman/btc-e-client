package nf.co.xine.btc_eclient;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;


public class CustomAdapter extends ArrayAdapter<HashMap<String, String>> {
    public CustomAdapter(Context context, ArrayList currencies) {
        super(context, 0, currencies);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        HashMap<String, String> map = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }
        // Lookup view for data population
        TextView name = (TextView) convertView.findViewById(R.id.currency_name);
        TextView ask = (TextView) convertView.findViewById(R.id.ask);
        TextView bid = (TextView) convertView.findViewById(R.id.bid);
        // Populate the data into the template view using the data object
        name.setText(map.get("name"));
        ask.setText(map.get("ask"));
        bid.setText(map.get("bid"));
        // Return the completed view to render on screen
        return convertView;
    }
}
