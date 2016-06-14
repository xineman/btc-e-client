package nf.co.xine.btc_eclient.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import nf.co.xine.btc_eclient.R;
import nf.co.xine.btc_eclient.StaticConverter;
import nf.co.xine.btc_eclient.data_structure.MarketTrade;
import nf.co.xine.btc_eclient.data_structure.MyOrder;


public class MarketHistoryAdapter extends ArrayAdapter<MarketTrade> {
    public MarketHistoryAdapter(Context context, ArrayList marketTrades) {
        super(context, 0, marketTrades);
        //this.activeOrders = activeOrders;

    }

    //private ArrayList<MyOrder> activeOrders;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        MarketTrade trade = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.market_order_item, parent, false);
        }
        // Lookup view for data population
        TextView date = (TextView) convertView.findViewById(R.id.order_date);
        TextView time = (TextView) convertView.findViewById(R.id.order_time);
        TextView amount = (TextView) convertView.findViewById(R.id.order_amount);
        TextView price = (TextView) convertView.findViewById(R.id.order_price);
        TextView type = (TextView) convertView.findViewById(R.id.order_type);
        TextView total = (TextView) convertView.findViewById(R.id.order_total);

        String timestamp = trade.getTimestamp();
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(new Date(Long.parseLong(timestamp) * 1000L));
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String dateFormatted = formatter.format(calendar.getTime());
        date.setText(dateFormatted);
        formatter = new SimpleDateFormat("hh:mm:ss", Locale.getDefault());
        dateFormatted = formatter.format(calendar.getTime());
        time.setText(dateFormatted);

        amount.setText(trade.getAmount());
        price.setText(trade.getPrice());
        total.setText(StaticConverter.doubleToString(Double.parseDouble(trade.getAmount()) * Double.parseDouble(trade.getPrice())));
        type.setText(trade.getType().toUpperCase());
        Animation slideLeft = AnimationUtils.loadAnimation(getContext(), R.anim.slide_left);


        slideLeft.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        Animation slideRight = AnimationUtils.loadAnimation(getContext(), R.anim.slide_right);
        // Return the completed view to render on screen
        return convertView;
    }
}
