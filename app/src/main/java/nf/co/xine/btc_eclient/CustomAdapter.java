package nf.co.xine.btc_eclient;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;


public class CustomAdapter extends ArrayAdapter<HashMap<String, String>> {
    public CustomAdapter(Context context, ArrayList currencies, int mode) {
        super(context, 0, currencies);
        this.mode = mode;
        this.currencies = currencies;

    }

    static final int BROWSING = 1;
    static final int EDIT = 2;
    static final int DROPPING = 3;
    private int mode;
    private ArrayList<HashMap<String, String>> currencies;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        HashMap<String, String> map = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }
        // Lookup view for data population
        final TextView name = (TextView) convertView.findViewById(R.id.currency_name);
        TextView ask = (TextView) convertView.findViewById(R.id.ask);
        TextView bid = (TextView) convertView.findViewById(R.id.bid);
        View dragH = convertView.findViewById(R.id.drag_handle);
        Switch showPairSwitch = (Switch) convertView.findViewById(R.id.showPairSwitch);
        Animation slideLeft = AnimationUtils.loadAnimation(getContext(), R.anim.slide_left);
        Animation slideRight = AnimationUtils.loadAnimation(getContext(), R.anim.slide_right);
        switch (mode) {
            case BROWSING: {
                name.setText(map.get("name"));
                ask.setText(map.get("ask"));
                bid.setText(map.get("bid"));
                break;
            }
            case EDIT: {
                name.setText(map.get("name"));
                if (map.get("enabled").equals("1"))
                    showPairSwitch.setChecked(true);
                else
                    showPairSwitch.setChecked(false);
                dragH.startAnimation(slideLeft);
                showPairSwitch.startAnimation(slideLeft);
                //name.startAnimation(slideRight);
                showPairSwitch.setVisibility(View.VISIBLE);
                dragH.setVisibility(View.VISIBLE);
                ask.setVisibility(View.INVISIBLE);
                bid.setVisibility(View.INVISIBLE);
                break;
            }
            case DROPPING: {
                name.setText(map.get("name"));
                if (map.get("enabled").equals("1"))
                    showPairSwitch.setChecked(true);
                else
                    showPairSwitch.setChecked(false);
                showPairSwitch.setVisibility(View.VISIBLE);
                dragH.setVisibility(View.VISIBLE);
                ask.setVisibility(View.INVISIBLE);
                bid.setVisibility(View.INVISIBLE);
                break;
            }
        }
        ((Switch) convertView.findViewById(R.id.showPairSwitch)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int nn = 0;
                if (isChecked) nn = 1;
                for (HashMap<String, String> tmp : currencies) {
                    if (tmp.get("name").equals(name.getText().toString()))
                        tmp.put("enabled", String.valueOf(nn));
                }
            }
        });
        // Return the completed view to render on screen
        return convertView;
    }

    void setMode(int mode) {
        this.mode = mode;
    }
}
