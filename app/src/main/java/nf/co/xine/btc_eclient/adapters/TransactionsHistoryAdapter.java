package nf.co.xine.btc_eclient.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import nf.co.xine.btc_eclient.data_structure.CompletedOrder;
import nf.co.xine.btc_eclient.R;
import nf.co.xine.btc_eclient.data_structure.Transaction;


public class TransactionsHistoryAdapter extends ArrayAdapter<Transaction> {
    public TransactionsHistoryAdapter(Context context, ArrayList transactions) {
        super(context, 0, transactions);
        //this.activeOrders = activeOrders;

    }

    //private ArrayList<MyOrder> activeOrders;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Transaction transaction = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.transaction_item, parent, false);
        }
        // Lookup view for data population
        TextView date = (TextView) convertView.findViewById(R.id.transaction_date);
        TextView time = (TextView) convertView.findViewById(R.id.transaction_time);
        TextView amount = (TextView) convertView.findViewById(R.id.transaction_amount);
        TextView desc = (TextView) convertView.findViewById(R.id.transaction_desc);

        String timestamp = transaction.getTimestamp();
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(new Date(Long.parseLong(timestamp)*1000L));
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String dateFormatted = formatter.format(calendar.getTime());
        date.setText(dateFormatted);
        formatter = new SimpleDateFormat("hh:mm:ss", Locale.getDefault());
        dateFormatted = formatter.format(calendar.getTime());
        time.setText(dateFormatted);

        amount.setText(transaction.getAmount().concat(" ").concat(transaction.getCurrency()));
        //total.setText(Double.toString(Double.parseDouble(order.getAmount()) * Double.parseDouble(order.getRate())));
        desc.setText(transaction.getDesc());
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
