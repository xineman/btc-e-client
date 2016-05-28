package nf.co.xine.btc_eclient.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;

import nf.co.xine.btc_eclient.R;
import nf.co.xine.btc_eclient.StaticConverter;
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
    public static final int EDITED = 4;
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
        final TextView ask = (TextView) convertView.findViewById(R.id.ask);
        final TextView bid = (TextView) convertView.findViewById(R.id.bid);
        final View dragH = convertView.findViewById(R.id.drag_handle);
        final Switch showPairSwitch = (Switch) convertView.findViewById(R.id.showPairSwitch);
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
        Animation toBrowsing = AnimationUtils.loadAnimation(getContext(), R.anim.to_browsing);
        toBrowsing.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                showPairSwitch.setVisibility(View.INVISIBLE);
                dragH.setVisibility(View.INVISIBLE);
                ask.setVisibility(View.VISIBLE);
                bid.setVisibility(View.VISIBLE);
                mode = BROWSING;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        switch (mode) {
            case BROWSING: {
                name.setText(cc.getName());
                ask.setText(StaticConverter.to7PlacesDouble(cc.getAsk()));
                bid.setText(StaticConverter.to7PlacesDouble(cc.getBid()));
                break;
            }
            case EDITED: {
                name.setText(cc.getName());
                if (cc.isEnabled())
                    showPairSwitch.setChecked(true);
                else
                    showPairSwitch.setChecked(false);
                ask.setText(StaticConverter.to7PlacesDouble(cc.getAsk()));
                bid.setText(StaticConverter.to7PlacesDouble(cc.getBid()));
                showPairSwitch.setVisibility(View.VISIBLE);
                dragH.setVisibility(View.VISIBLE);
                ask.setVisibility(View.VISIBLE);
                ask.setAlpha(0f);
                ask.animate()
                        .alpha(1f)
                        .setDuration(300)
                        .setListener(null);
                bid.setVisibility(View.VISIBLE);
                bid.setAlpha(0f);
                bid.animate()
                        .alpha(1f)
                        .setDuration(300)
                        .setListener(null);
                showPairSwitch.startAnimation(toBrowsing);
                dragH.startAnimation(toBrowsing);
                break;
            }
            case EDIT: {
                name.setText(cc.getName());
                if (cc.isEnabled())
                    showPairSwitch.setChecked(true);
                else
                    showPairSwitch.setChecked(false);
                ask.setText(StaticConverter.to7PlacesDouble(cc.getAsk()));
                bid.setText(StaticConverter.to7PlacesDouble(cc.getBid()));
                ask.setVisibility(View.VISIBLE);
                ask.setAlpha(1f);
                ask.animate()
                        .alpha(0f)
                        .setDuration(300)
                        .setListener(null);
                bid.setVisibility(View.VISIBLE);
                bid.setAlpha(1f);
                bid.animate()
                        .alpha(0f)
                        .setDuration(300)
                        .setListener(null);
                dragH.startAnimation(slideLeft);
                showPairSwitch.startAnimation(slideLeft);
                //name.startAnimation(slideRight);
                showPairSwitch.setVisibility(View.VISIBLE);
                dragH.setVisibility(View.VISIBLE);
                //ask.setVisibility(View.INVISIBLE);
                //bid.setVisibility(View.INVISIBLE);
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


}
