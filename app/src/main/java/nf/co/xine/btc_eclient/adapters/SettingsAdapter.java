package nf.co.xine.btc_eclient.adapters;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;

import nf.co.xine.btc_eclient.R;
import nf.co.xine.btc_eclient.StaticConverter;
import nf.co.xine.btc_eclient.data_structure.CurrencyOrder;
import nf.co.xine.btc_eclient.data_structure.Parameter;

public class SettingsAdapter extends ArrayAdapter<Parameter> {
    private ArrayList<Parameter> parameters;

    public SettingsAdapter(Context context, ArrayList parameters) {
        super(context, 0, parameters);
        this.parameters = parameters;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final Parameter parameter = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.settings_item, parent, false);
        }
        // Lookup view for data population
        final TextView name = (TextView) convertView.findViewById(R.id.settings_item_name);
        final TextView value = (TextView) convertView.findViewById(R.id.settings_item_control);
        name.setText(parameter.getName());
        value.setText(parameter.getValue());
        ((EditText) convertView.findViewById(R.id.settings_item_control)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (parameter.getName().equals(name.getText().toString())) {
                    parameters.get(parameters.indexOf(parameter)).setValue(value.getText().toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return convertView;

    }


}
