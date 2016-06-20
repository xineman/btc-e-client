package nf.co.xine.btc_eclient;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.assist.TradeApi;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

import nf.co.xine.btc_eclient.adapters.CustomAdapter;
import nf.co.xine.btc_eclient.adapters.OrdersAdapter;
import nf.co.xine.btc_eclient.data_structure.Currency;
import nf.co.xine.btc_eclient.data_structure.CurrencyOrder;

public class CurrencyFragment extends Fragment {

    private CurrencyFragmentListener mListener;
    private ListView ordersView;
    private TextView min24;
    private TextView max24;
    private TextView last;
    private Button buyOrderButton;
    private Button sellOrderButton;
    private Button createOrder;
    private TextView totalCurrency;
    private TextView priceCurrency;
    private TextView amountCurrency;
    private RadioButton buyRadio;
    private RadioButton sellRadio;
    private RadioGroup orderTypeRadioGroup;
    private TextView buyBalance;
    private TextView sellBalance;
    private TextView buyBalanceHeader;
    private TextView sellBalanceHeader;
    private EditText amount;
    private EditText price;
    private TextView total_value;
    private View placeOrderDialog;
    private SwipeRefreshLayout swipeRefreshLayout;
    private View summaryLayout;
    private TextView priceLabel;
    private TextView amountLabel;
    private AVLoadingIndicatorView progressBar;

    private UpdateCurrencyBalance updateCurrencyBalance;
    private UpdateOrders updateOrders;
    private UpdateSummary updateSummary;

    private ArrayList<CurrencyOrder> orders;
    private ArrayList<String> summary;
    private String currencyToTrade;
    private String orderType = "buy";
    private TradeApi tradeApi;

    public CurrencyFragment() {
        // Required empty public constructor
    }


    public static void expand(final View v) {
        v.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? ViewGroup.LayoutParams.WRAP_CONTENT
                        : (int) (targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        //(int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density)
        a.setDuration((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public void updateCurrencyToTrade() {
        currencyToTrade = mListener.getCurrencyToTrade();
        if (mListener.isPublicInfoReceived()) {
            tradeApi.info.setCurrentPair(StaticConverter.currencyNameToUrlFormat(currencyToTrade));
            price.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(tradeApi.info.getCurrentDecimalPlaces())});
        }
        amount.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(8)});
        updateInfo();
    }

    public class DecimalDigitsInputFilter implements InputFilter {

        private final int decimalDigits;

        /**
         * Constructor.
         *
         * @param decimalDigits maximum decimal digits
         */
        public DecimalDigitsInputFilter(int decimalDigits) {
            this.decimalDigits = decimalDigits;
        }

        @Override
        public CharSequence filter(CharSequence source,
                                   int start,
                                   int end,
                                   Spanned dest,
                                   int dstart,
                                   int dend) {


            int dotPos = -1;
            int len = dest.length();
            for (int i = 0; i < len; i++) {
                char c = dest.charAt(i);
                if (c == '.' || c == ',') {
                    dotPos = i;
                    break;
                }
            }
            if (dotPos >= 0) {

                // protects against many dots
                if (source.equals(".") || source.equals(",")) {
                    return "";
                }
                // if the text is entered before the dot
                if (dend <= dotPos) {
                    return null;
                }
                if (len - dotPos > decimalDigits) {
                    return "";
                }
            }

            return null;
        }

    }

    public String getCurrencyToTrade() {
        return currencyToTrade;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d("Currency", "OnCreateView");
        return inflater.inflate(R.layout.fragment_currency, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d("Currency", "OnAttach");
        if (context instanceof CurrencyFragmentListener) {
            mListener = (CurrencyFragmentListener) context;

        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    //Initializing TradeApi, inflating all the views
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            tradeApi = mListener.getApi();
            currencyToTrade = mListener.getCurrencyToTrade();
        } catch (Exception e) {
            e.printStackTrace();
        }
        inflateAllTheViews();
        Log.d("Currency", "OnActivityCreated");
    }

    //Starting AsyncTask to save data to DB, canceling all the active AsyncTasks
    @Override
    public void onPause() {
        super.onPause();
        /*if (orders != null && summary != null)
            new SaveInfoToDb().execute(getActivity());*/
        updateCurrencyBalance.cancel(true);
        updateOrders.cancel(true);
        updateSummary.cancel(true);
        mListener.saveMarketOrders(orders, summary);
        Log.d("Currency", "Paused");
    }

    @Override
    public void onResume() {
        super.onResume();
        orders = mListener.getOrders();
        summary = mListener.getSummary();
        //updateInfo();
        if (orders != null && summary != null
                && StaticConverter.currencyNameToUrlFormat(summary.get(0)).equals(StaticConverter.currencyNameToUrlFormat(mListener.getCurrencyToTrade()))) {
            updateUI();
            TypedValue typed_value = new TypedValue();
            getActivity().getTheme().resolveAttribute(android.support.v7.appcompat.R.attr.actionBarSize, typed_value, true);
            swipeRefreshLayout.setProgressViewOffset(false, 0, getResources().getDimensionPixelSize(typed_value.resourceId));
            swipeRefreshLayout.setRefreshing(true);
            updateInfo();
        } else progressBar.setVisibility(View.VISIBLE);
        setupUI(getView().findViewById(R.id.currency_parent));
        Log.d("Currency", "Resumed");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void inflateAllTheViews() {
        if (getView() != null) {
            progressBar = (AVLoadingIndicatorView) getView().findViewById(R.id.progress_bar);
            swipeRefreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.orders_list_refresh);
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    updateInfo();
                }
            });
            ordersView = (ListView) getView().findViewById(R.id.orders_list);
            min24 = (TextView) getView().findViewById(R.id.min_24h);
            max24 = (TextView) getView().findViewById(R.id.max_24h);
            last = (TextView) getView().findViewById(R.id.last_deal);
            placeOrderDialog = getView().findViewById(R.id.place_order_dialog);
            buyOrderButton = (Button) getView().findViewById(R.id.new_buy_order);
            sellOrderButton = (Button) getView().findViewById(R.id.new_sell_order);
            createOrder = (Button) getView().findViewById(R.id.place_order_button);
            buyBalance = (TextView) getView().findViewById(R.id.buy_balance);
            sellBalance = (TextView) getView().findViewById(R.id.sell_balance);
            buyBalanceHeader = (TextView) getView().findViewById(R.id.buy_balance_header);
            sellBalanceHeader = (TextView) getView().findViewById(R.id.sell_balance_header);
            totalCurrency = (TextView) getView().findViewById(R.id.total_new_order_currency);
            priceCurrency = (TextView) getView().findViewById(R.id.price_edit_currency);
            amountCurrency = (TextView) getView().findViewById(R.id.amount_edit_currency);
            amount = (EditText) getView().findViewById(R.id.amount_edit);
            price = (EditText) getView().findViewById(R.id.price_edit);
            amountLabel = (TextView) getView().findViewById(R.id.amount_label);
            priceLabel = (TextView) getView().findViewById(R.id.price_label);
            SpannableString ss = new SpannableString(getResources().getString(R.string.set_max));
            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(View textView) {
                    setAmountMax();
                    Log.d("Amount", "max");
                }
            };
            ss.setSpan(clickableSpan, 1, ss.length() - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            amountLabel.setText(ss);
            amountLabel.setMovementMethod(LinkMovementMethod.getInstance());
            amountLabel.setHighlightColor(Color.TRANSPARENT);
            ss = new SpannableString(getResources().getString(R.string.set_max));
            clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    setPriceToMax();
                    Log.d("Price", "current");
                }

            };
            ss.setSpan(clickableSpan, 1, ss.length() - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            priceLabel.setText(ss);
            priceLabel.setMovementMethod(LinkMovementMethod.getInstance());
            priceLabel.setHighlightColor(Color.TRANSPARENT);
            total_value = (TextView) getView().findViewById(R.id.total_new_order_value);
            buyRadio = (RadioButton) getView().findViewById(R.id.radio_buy);
            sellRadio = (RadioButton) getView().findViewById(R.id.radio_sell);
            orderTypeRadioGroup = (RadioGroup) getView().findViewById(R.id.order_type_radiogroup);
            orderTypeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    switch (checkedId) {
                        case R.id.radio_buy: {
                            orderType = "buy";
                            break;
                        }
                        case R.id.radio_sell: {
                            orderType = "sell";
                            break;
                        }
                    }
                    Log.d("Order type", orderType);
                }
            });
            summaryLayout = getView().findViewById(R.id.currency_summary);
            buyOrderButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    buyOrderButton.setVisibility(View.GONE);
                    sellOrderButton.setVisibility(View.GONE);
                    expand(placeOrderDialog);
                    buyRadio.setChecked(true);
                    setPriceToMax();
                    setAmountMax();
                }
            });
            sellOrderButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    buyOrderButton.setVisibility(View.GONE);
                    sellOrderButton.setVisibility(View.GONE);
                    expand(placeOrderDialog);
                    sellRadio.setChecked(true);
                    setPriceToMax();
                    setAmountMax();
                }
            });
            amount.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    total_value.setText(StaticConverter.doubleToString(StaticConverter.getDoubleFromEditText(price) * StaticConverter.getDoubleFromEditText(amount)));
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            amount.setSelection(amount.getText().length());
            price.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    total_value.setText(StaticConverter.doubleToString(StaticConverter.getDoubleFromEditText(price) * StaticConverter.getDoubleFromEditText(amount)));
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            price.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    hideSoftKeyboard(getActivity());
                    if (summaryLayout.getVisibility() == View.GONE) expand(summaryLayout);
                    return false;
                }
            });
            View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (b) {
                        ((EditText) view).selectAll();
                    }
                }
            };
            price.setOnFocusChangeListener(onFocusChangeListener);
            amount.setOnFocusChangeListener(onFocusChangeListener);
            Button cancel = (Button) getView().findViewById(R.id.collapse_order_menu);
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideSoftKeyboard(getActivity());
                    buyOrderButton.setVisibility(View.VISIBLE);
                    sellOrderButton.setVisibility(View.VISIBLE);
                    if (summaryLayout.getVisibility() == View.GONE) expand(summaryLayout);
                    collapse(placeOrderDialog);
                }
            });
            createOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener.isConnectedToNetwork() && mListener.isPublicInfoReceived()) {
                        if (StaticConverter.getDoubleFromEditText(price) > tradeApi.info.getCurrentMaxPrice() || StaticConverter.getDoubleFromEditText(price) < tradeApi.info.getCurrentMinPrice()) {
                            Toast toast = Toast.makeText(getActivity(), "Enter price between " + tradeApi.info.getCurrentMinPrice().toString() + " and " + tradeApi.info.getCurrentMaxPrice().toString()
                                            + " " + StaticConverter.right(currencyToTrade, 3),
                                    Toast.LENGTH_SHORT);
                            toast.show();
                        } else if (StaticConverter.getDoubleFromEditText(amount) < tradeApi.info.getCurrentMinAmount()) {
                            Toast toast = Toast.makeText(getActivity(), "Minimal amount is " + tradeApi.info.getCurrentMinAmount() + " " + currencyToTrade.substring(0, 3),
                                    Toast.LENGTH_SHORT);
                            toast.show();
                        } else {
                            tradeApi.trade.resetParams();
                            tradeApi.trade.setType(orderType);
                            tradeApi.trade.setPair(StaticConverter.currencyNameToUrlFormat(currencyToTrade));
                            tradeApi.trade.setAmount(amount.getText().toString());
                            tradeApi.trade.setRate(price.getText().toString());
                            new CreateOrder().execute();
                            hideSoftKeyboard(getActivity());
                            buyOrderButton.setVisibility(View.VISIBLE);
                            sellOrderButton.setVisibility(View.VISIBLE);
                            if (summaryLayout.getVisibility() == View.GONE) expand(summaryLayout);
                            collapse(placeOrderDialog);
                        }
                    } else {
                        showNoNetworkMessage();
                    }
                }
            });

        }
    }

    private void setAmountMax() {
        if (orders != null && mListener.isPublicInfoReceived() && tradeApi.getInfo.isSuccess()) {
            if (orderType.equals("buy")) {
                double amountVal = Double.parseDouble(tradeApi.getInfo.getBalance(currencyToTrade.substring(0, 3))) / orders.get(0).getAskPrice();
                tradeApi.info.setCurrentPair(StaticConverter.currencyNameToUrlFormat(currencyToTrade));
                if (amountVal < tradeApi.info.getCurrentMinAmount())
                    amount.setText("0");
                else
                    amount.setText(StaticConverter.doubleToString(amountVal));
            } else {
                double amountVal = Double.parseDouble(tradeApi.getInfo.getBalance(currencyToTrade.substring(0, 3)));
                tradeApi.info.setCurrentPair(StaticConverter.currencyNameToUrlFormat(currencyToTrade));
                if (amountVal < tradeApi.info.getCurrentMinAmount())
                    amount.setText("0");
                else
                    amount.setText(StaticConverter.doubleToString(amountVal));
            }
        } else showNoNetworkMessage();
    }

    private void showNoNetworkMessage() {
        Toast.makeText(getActivity(), getResources().getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
    }

    private void setPriceToMax() {
        if (orders != null) {
            if (orderType.equals("buy")) {
                price.setText(StaticConverter.doubleToString(orders.get(0).getAskPrice()));
            } else {
                price.setText(StaticConverter.doubleToString(orders.get(0).getBidPrice()));
            }
        } else showNoNetworkMessage();
    }

    public void setupUI(View view) {

        //Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText) && view.getId() != R.id.amount_label && view.getId() != R.id.price_label
                && view.getId() != R.id.radio_sell && view.getId() != R.id.radio_buy) {

            view.setOnTouchListener(new View.OnTouchListener() {

                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(getActivity());
                    if (summaryLayout.getVisibility() == View.GONE) expand(summaryLayout);
                    return false;
                }

            });
        }
        if ((view instanceof EditText)) {

            view.setOnTouchListener(new View.OnTouchListener() {

                public boolean onTouch(View v, MotionEvent event) {
                    collapse(summaryLayout);
                    return false;
                }

            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {

            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {

                View innerView = ((ViewGroup) view).getChildAt(i);

                setupUI(innerView);
            }
        }
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (activity.getCurrentFocus() != null)
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }


    private void updateUI() {
        OrdersAdapter ordersAdapter = new OrdersAdapter(getActivity(), orders, mListener.getCurrencyToTrade());
        ordersView.setAdapter(ordersAdapter);
        min24.setText(StaticConverter.doubleToString(Double.parseDouble(summary.get(1))));
        max24.setText(StaticConverter.doubleToString(Double.parseDouble(summary.get(2))));
        last.setText(StaticConverter.doubleToString(Double.parseDouble(summary.get(3))));
    }

    //starting asyncTasks to fetch data from server
    private void updateInfo() {
        totalCurrency.setText(StaticConverter.right(currencyToTrade, 3));
        priceCurrency.setText(StaticConverter.right(currencyToTrade, 3));
        amountCurrency.setText(currencyToTrade.substring(0, 3));
        currencyToTrade = mListener.getCurrencyToTrade();
        tradeApi.getInfo.resetParams();
        tradeApi.depth.resetParams();
        tradeApi.ticker.resetParams();
        tradeApi.depth.addPair(StaticConverter.currencyNameToUrlFormat(currencyToTrade));
        tradeApi.depth.setLimit(20);
        (updateOrders = new UpdateOrders()).execute(tradeApi);
        tradeApi.ticker.addPair(StaticConverter.currencyNameToUrlFormat(mListener.getCurrencyToTrade()));
        (updateSummary = new UpdateSummary()).execute(tradeApi);
        (updateCurrencyBalance = new UpdateCurrencyBalance()).execute(tradeApi);
    }

    private class CreateOrder extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            tradeApi.trade.runMethod();
            Log.d("Trade", "finished");
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (tradeApi.trade.isSuccess()) {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                View layout = inflater.inflate(R.layout.dialog_layout,
                        (ViewGroup) getView().findViewById(R.id.dialog_root));

                TextView received = (TextView) layout.findViewById(R.id.received_value);
                TextView remains = (TextView) layout.findViewById(R.id.remains_value);
                TextView receivedLabel = (TextView) layout.findViewById(R.id.received_label);
                if (orderType.equals("buy")) receivedLabel.setText("Bought:");
                else receivedLabel.setText("Sold:");
                TextView receivedCurrency = (TextView) layout.findViewById(R.id.received_currency);
                TextView remainsCurrency = (TextView) layout.findViewById(R.id.remains_currency);
                receivedCurrency.setText(currencyToTrade.substring(0, 3).toUpperCase());
                remainsCurrency.setText(currencyToTrade.substring(0, 3).toUpperCase());
                received.setText(tradeApi.trade.getReceived());
                remains.setText(tradeApi.trade.getRemains());

                Toast toast = new Toast(getActivity());
                //toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setView(layout);
                toast.show();
            } else {
                Toast toast = Toast.makeText(getActivity(), "Error: " + tradeApi.trade.getErrorMessage(), Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    //    private class SaveInfoToDb extends AsyncTask<Context, Void, Void> {
//
//        DbHelper helper;
//        ContentValues values = new ContentValues();
//        StringBuilder ordersList = new StringBuilder();
//        StringBuilder summaryList = new StringBuilder();
//        String valSeparator = " ";
//        String itemSeparator = ";";
//
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            for (CurrencyOrder order : orders) {
//                ordersList.append(order.getAskPrice());
//                ordersList.append(valSeparator);
//                ordersList.append(order.getAskAmount());
//                ordersList.append(valSeparator);
//                ordersList.append(order.getBidPrice());
//                ordersList.append(valSeparator);
//                ordersList.append(order.getBidAmount());
//                ordersList.append(itemSeparator);
//            }
//            for (String s : summary) {
//                summaryList.append(s).append(" ");
//            }
//            values.put("CURRENCY_NAME", StaticConverter.currencyNameToUrlFormat(currencyToTrade));
//            values.put("ORDERS_LIST", ordersList.toString());
//            values.put("SUMMARY_LIST", summaryList.toString());
//        }
//
//        @Override
//        protected Void doInBackground(Context... context) {
//            helper = new DbHelper(context[0]);
//            SQLiteDatabase db = helper.getWritableDatabase();
//            db.insertWithOnConflict("ORDERS_VAL", null, values, SQLiteDatabase.CONFLICT_REPLACE);
//            //}
//            db.close();
//            Log.d("Orders", "Successfully inserted!!!");
//            return null;
//        }
//
//    }
//    private class InitValuesFromDb extends AsyncTask<Void, Void, Void> {
//
//        @Override
//        protected Void doInBackground(Void... voids) {
//            try {
//                ArrayList<CurrencyOrder> ordersTmp = new ArrayList<>();
//                ArrayList<String> summaryTmp = new ArrayList<>();
//                DbHelper helper = new DbHelper(getActivity());
//                SQLiteDatabase db = helper.getWritableDatabase();
//                Cursor cursor = db.query("ORDERS_VAL", new String[]{"CURRENCY_NAME", "ORDERS_LIST", "SUMMARY_LIST"}, "CURRENCY_NAME='" + StaticConverter.currencyNameToUrlFormat(mListener.getCurrencyToTrade()) + "'", null, null, null, null);
//                if (cursor.getCount() == 0) {
//                    cancel(true);
//                    return null;
//                }
//                cursor.moveToNext();
//                String[] ordersStr = cursor.getString(1).split(";");
//                for (String s : ordersStr) {
//                    String[] vals = s.split(" ");
//                    ordersTmp.add(new CurrencyOrder(Double.parseDouble(vals[0]), Double.parseDouble(vals[1]), Double.parseDouble(vals[2]), Double.parseDouble(vals[3])));
//                }
//                String[] summaryStr = cursor.getString(2).split(" ");
//                cursor.close();
//                db.close();
//                Collections.addAll(summaryTmp, summaryStr);
//                orders = ordersTmp;
//                summary = summaryTmp;
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//            if (isAdded())
//                updateUI();
//        }
//    }

    private class UpdateCurrencyBalance extends AsyncTask<TradeApi, Void, Void> {

        @Override
        protected Void doInBackground(TradeApi... apis) {
            if (mListener.isConnectedToNetwork()) apis[0].getInfo.runMethod();
            return null;
        }

        @Override
        protected void onPostExecute(Void res) {
            if (isAdded() && tradeApi.getInfo.isSuccess()) {
                sellBalanceHeader.setText(currencyToTrade.substring(0, 3).toUpperCase() + " balance");
                sellBalance.setText(StaticConverter.doubleToString(Double.parseDouble(tradeApi.getInfo.getBalance(currencyToTrade.substring(0, 3)))));
                buyBalanceHeader.setText(StaticConverter.right(currencyToTrade, 3).toUpperCase() + " balance");
                buyBalance.setText(StaticConverter.doubleToString(Double.parseDouble(tradeApi.getInfo.getBalance(StaticConverter.right(currencyToTrade, 3)))));
            }
        }
    }

    private class UpdateSummary extends AsyncTask<TradeApi, Void, Void> {
        ArrayList<String> s = new ArrayList<>();

        @Override
        protected Void doInBackground(TradeApi... apis) {
            if (mListener.isConnectedToNetwork()) {
                apis[0].ticker.runMethod();
                apis[0].ticker.switchNextPair();
                Log.d("Test", String.valueOf(apis[0].ticker.isSuccess()));
                s.add(tradeApi.ticker.getCurrentPairName());
                s.add(tradeApi.ticker.getCurrentLow());
                s.add(tradeApi.ticker.getCurrentHigh());
                s.add(tradeApi.ticker.getCurrentLast());
                summary = s;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void res) {
            if (isAdded() && tradeApi.ticker.isSuccess()) {
                try {
                    min24.setText(StaticConverter.doubleToString(Double.parseDouble(tradeApi.ticker.getCurrentLow())));
                    max24.setText(StaticConverter.doubleToString(Double.parseDouble(tradeApi.ticker.getCurrentHigh())));
                    last.setText(StaticConverter.doubleToString(Double.parseDouble(tradeApi.ticker.getCurrentLast())));

                } catch (Exception ignored) {
                    ignored.printStackTrace();
                }
            }
        }
    }

    private class UpdateOrders extends AsyncTask<TradeApi, Void, Void> {

        ArrayList<CurrencyOrder> ordersTmp = new ArrayList<>();

        @Override
        protected Void doInBackground(TradeApi... apis) {
            if (mListener.isConnectedToNetwork()) {
                apis[0].depth.runMethod();
                if (isAdded() && tradeApi.depth.isSuccess()) {
                    tradeApi.depth.switchNextPair();
                    while (tradeApi.depth.hasNextAsk() || tradeApi.depth.hasNextBid()) {
                        tradeApi.depth.switchNextAsk();
                        tradeApi.depth.switchNextBid();
                        ordersTmp.add(new CurrencyOrder(Double.parseDouble(tradeApi.depth.getCurrentAskPrice()),
                                Double.parseDouble(tradeApi.depth.getCurrentAskAmount()),
                                Double.parseDouble(tradeApi.depth.getCurrentBidPrice()),
                                Double.parseDouble(tradeApi.depth.getCurrentBidAmount())));
                    }
                    if (ordersTmp.size() != 0) orders = ordersTmp;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void res) {
            Parcelable stateA = ordersView.onSaveInstanceState();
            if (isAdded() && tradeApi.depth.isSuccess() && orders != null) {
                OrdersAdapter ordersAdapter = new OrdersAdapter(getActivity(), orders, currencyToTrade);
                ordersView.setAdapter(ordersAdapter);
                ordersView.onRestoreInstanceState(stateA);
                Log.d("Orders", "updated");
            } else {
                showNoNetworkMessage();
            }
            if (isAdded()) {
                swipeRefreshLayout.setRefreshing(false);
                progressBar.setVisibility(View.GONE);
            }
        }
    }

    public interface CurrencyFragmentListener {
        String getCurrencyToTrade();

        TradeApi getApi();

        void saveMarketOrders(ArrayList<CurrencyOrder> orders, ArrayList<String> summary);

        ArrayList<CurrencyOrder> getOrders();

        ArrayList<String> getSummary();

        ArrayList<Currency> getCurrencies();

        boolean isConnectedToNetwork();

        boolean isPublicInfoReceived();
    }
}

