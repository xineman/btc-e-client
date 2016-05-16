package nf.co.xine.btc_eclient.adapters;

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

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;

import nf.co.xine.btc_eclient.R;
import nf.co.xine.btc_eclient.data_structure.Currency;


public class CustomAdapter extends ArrayAdapter<Currency> {
    public CustomAdapter(Context context, ArrayList currencies, int mode) {
        super(context, 0, currencies);
        this.mode = mode;
        this.currencies = currencies;

    }

    public static final int BROWSING = 1;
    public static final int EDIT = 2;
    public static final int DROPPING = 3;
    private int mode;
    private ArrayList<Currency> currencies;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Currency cc = getItem(position);
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


        slideLeft.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mode = DROPPING;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        Animation slideRight = AnimationUtils.loadAnimation(getContext(), R.anim.slide_right);
        switch (mode) {
            case BROWSING: {
                name.setText(cc.getName());
                ask.setText(convertDouble(cc.getAsk()));
                bid.setText(convertDouble(cc.getBid()));
                break;
            }
            case EDIT: {
                name.setText(cc.getName());
                if (cc.isEnabled())
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
                name.setText(cc.getName());
                if (cc.isEnabled())
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
                for (Currency tmp : currencies) {
                    if (tmp.getName().equals(name.getText().toString()))
                        tmp.setEnabled(isChecked);
                }
            }
        });
        // Return the completed view to render on screen
        return convertView;
    }

    void setMode(int mode) {
        this.mode = mode;
    }

    public static String convertDouble(double val) {

        String text = Double.toString(val);
        int integerPlaces = text.indexOf('.');
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
}
