package nf.co.xine.btc_eclient;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.assist.TradeApi;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabClickListener;

import java.util.ArrayList;

import nf.co.xine.btc_eclient.data_structure.Currency;
import nf.co.xine.btc_eclient.data_structure.CurrencyBalance;
import nf.co.xine.btc_eclient.data_structure.CurrencyOrder;
import nf.co.xine.btc_eclient.data_structure.MyOrder;
import nf.co.xine.btc_eclient.data_structure.Transaction;


public class MainActivity extends AppCompatActivity implements QuotesFragment.OnFragmentInteractionListener,
        CurrencyFragment.CurrencyFragmentListener,
        ProfileFragment.OnFragmentInteractionListener,
        ActiveOrdersFragment.OnFragmentInteractionListener,
        TransactionsHistoryFragment.OnFragmentInteractionListener,
        BalanceFragment.OnFragmentInteractionListener,
        MarketFragment.OnFragmentInteractionListener,
        MarketHistoryFragment.OnFragmentInteractionListener,
        ChartsFragment.OnFragmentInteractionListener,
        SettingsFragment.OnFragmentInteractionListener {

    private BottomBar mBottomBar;
    private QuotesFragment quotesFragment = new QuotesFragment();
    private CurrencyFragment currencyFragment = new CurrencyFragment();
    private MarketFragment marketFragment = new MarketFragment();
    private ProfileFragment profileFragment;
    private SettingsFragment settingsFragment;
    private String currencyToTrade = "BTC/USD";
    private Menu menu;
    private Spinner spinner;
    private ArrayAdapter spinnerAdapter;
    private String aKey = null; //API-key
    private String aSecret = null; //SECRET-key

    private TradeApi api;
    private boolean isConnectedToNetwork;
    private NetworkReceiver receiver = new NetworkReceiver(this);
    private boolean isPublicInfoReceived = false;
    private int timeout = 5000;
    private boolean isSignedIn = false;
    private Context context = this;
    private int selected;
    private int requestLimit = 20;

    private ArrayList<Transaction> transactions;
    private ArrayList<MyOrder> orders;
    private ArrayList<String> pairs;
    private ArrayList<String> types;
    private ArrayList<CurrencyBalance> balance;
    private ArrayList<CurrencyOrder> marketOrders;
    private ArrayList<String> marketSummary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        isConnectedToNetwork = activeNetwork != null;
        Log.d("Is connected to network", String.valueOf(isConnectedToNetwork));
        try {
            api = new TradeApi(aKey, aSecret);
        } catch (Exception e) {
            Log.d("Main activity", "Error while initializing API");
            e.printStackTrace();
        }

        mBottomBar = BottomBar.attach(this, savedInstanceState);
        mBottomBar.useFixedMode();
        mBottomBar.setItemsFromMenu(R.menu.activity_main_drawer, new OnMenuTabClickListener() {
            @Override
            public void onMenuTabSelected(@IdRes int menuItemId) {
                if (menuItemId == R.id.nav_quotes) {
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.main_content, quotesFragment, "Quotes");
                    transaction.commit();
                    if (menu != null) {
                        menu.clear();
                        getMenuInflater().inflate(R.menu.main, menu);
                        if (!isConnectedToNetwork)
                            menu.add(Menu.NONE, 25, 1, getString(R.string.no_connection)).setIcon(R.drawable.ic_error_outline_white_24dp).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
                    }
                    selected = mBottomBar.getCurrentTabPosition();
                }
                if (menuItemId == R.id.nav_trade) {
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.main_content, marketFragment, "Trade");
                    //transaction.addToBackStack(null);
                    transaction.commit();
                    getSupportFragmentManager().executePendingTransactions();
                    menu.clear();
                    getMenuInflater().inflate(R.menu.trade_menu, menu);
                    if (!isConnectedToNetwork)
                        menu.add(Menu.NONE, 25, 1, getString(R.string.no_connection)).setIcon(R.drawable.ic_error_outline_white_24dp).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
                    MenuItem item = menu.findItem(R.id.spinner);
                    spinner = (Spinner) MenuItemCompat.getActionView(item);
                    spinnerAdapter = new ArrayAdapter(getApplicationContext(), R.layout.currency_spinner_item, quotesFragment.getCurrencies());
                    spinner.setAdapter(spinnerAdapter);
                    spinner.setOnItemSelectedListener(currencyChangeListener);
                    spinner.setSelection(getIndex(spinner, currencyToTrade));
                    selected = mBottomBar.getCurrentTabPosition();
                }

                if (menuItemId == R.id.nav_profile) {
                    if (isSignedIn) {
                        if (profileFragment == null)
                            profileFragment = new ProfileFragment();
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.main_content, profileFragment, "Profile");
                        //transaction.addToBackStack(null);
                        transaction.commit();
                        menu.clear();
                        getMenuInflater().inflate(R.menu.main, menu);
                        if (!isConnectedToNetwork)
                            menu.add(Menu.NONE, 25, 1, getString(R.string.no_connection)).setIcon(R.drawable.ic_error_outline_white_24dp).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
                        selected = mBottomBar.getCurrentTabPosition();
                    } else {
                        Toast.makeText(context, "You have to sign in", Toast.LENGTH_SHORT).show();
                        mBottomBar.selectTabAtPosition(selected, false);
                    }
                }

                if (menuItemId == R.id.nav_settings) {
                    if (settingsFragment == null)
                        settingsFragment = new SettingsFragment();
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.main_content, settingsFragment, "Settings");
                    //transaction.addToBackStack(null);
                    transaction.commit();
                    menu.clear();
                    getMenuInflater().inflate(R.menu.main, menu);
                    if (!isConnectedToNetwork)
                        menu.add(Menu.NONE, 25, 1, getString(R.string.no_connection)).setIcon(R.drawable.ic_error_outline_white_24dp).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
                    selected = mBottomBar.getCurrentTabPosition();
                }
            }

            @Override
            public void onMenuTabReSelected(@IdRes int menuItemId) {
                if (menuItemId == R.id.nav_trade) {
                    // The user reselected item number one, scroll your content to top.
                }
            }
        });

        /*
        ActionBar actionBar = getSupportActionBar();
        Toolbar toolbar = new Toolbar(this);
        setSupportActionBar(toolbar);

        actionBar.setSubtitle("mytest");
        actionBar.setTitle("vogella.com");*/

    }


    private class NetworkReceiver extends BroadcastReceiver {
        NetworkReceiver(Context context) {
            this.context = context;
        }

        Context context;

        @Override
        public void onReceive(Context context, Intent intent) {
            isConnectedToNetwork = !intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
            Log.w("Network Listener", String.valueOf(isConnectedToNetwork));
            if (isConnectedToNetwork) {
                if (api != null && !api.info.isSuccess()) new GetPublicInfo().execute(context);
                if (menu != null) {
                    menu.removeItem(25);
                }
                if (quotesFragment.isAdded()) {
                    quotesFragment.stopRequests();
                    quotesFragment.startRequests();
                }
            } else {
                if (menu != null)
                    menu.add(Menu.NONE, 25, 1, getString(R.string.no_connection)).setIcon(R.drawable.ic_error_outline_white_24dp).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
                Toast.makeText(context, getResources().getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
                if (quotesFragment.isAdded()) {
                    quotesFragment.stopRequests();
                }
            }
        }
    }

    // TODO: 21.05.2016 Save received info to DB, ensure that info available before using it
    private class GetPublicInfo extends AsyncTask<Context, Void, Void> {

        Context context;

        @Override
        protected Void doInBackground(Context... params) {
            context = params[0];
            api.setTimeouts(timeout, timeout);
            api.info.runMethod();
            /*try {
            } catch (Exception ex) {
            }*/

            //api.info.switchNextPair();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (!api.info.isSuccess()) {
                new AlertDialog.Builder(context)
                        .setTitle("Error")
                        .setMessage("Couldn't receive data!")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                            }
                        })/*
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })*/
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            } else
                isPublicInfoReceived = true;
        }
    }

    public static int getIndex(Spinner spinner, String myString) {
        int index = 0;

        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)) {
                index = i;
                break;
            }
        }
        return index;
    }
/*
    View.OnClickListener buttonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (placeOrderDialog.getVisibility() == View.GONE) expand(placeOrderDialog);
        }
    };
*/


    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(receiver, filter);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (!isConnectedToNetwork)
            menu.add(Menu.NONE, 25, 1, getString(R.string.no_connection)).setIcon(R.drawable.ic_error_outline_white_24dp).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.trade_menu, menu);
        MenuItem item = menu.findItem(R.id.spinner);
        spinner = (Spinner) MenuItemCompat.getActionView(item);
        menu.clear();
        getMenuInflater().inflate(R.menu.main, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_edit) {
            if (getSupportFragmentManager().findFragmentByTag("Quotes") != null && getSupportFragmentManager().findFragmentByTag("Quotes").isVisible()) {
                if (quotesFragment.toggleEditMode()) {
                    menu.getItem(0).setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_done_white_24dp, null));
                } else {
                    menu.getItem(0).setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_mode_edit_white_24dp, null));
                }
                spinnerAdapter = new ArrayAdapter(getApplicationContext(), R.layout.currency_spinner_item, quotesFragment.getCurrencies());
                spinner.setAdapter(spinnerAdapter);
            }
            if (getSupportFragmentManager().findFragmentByTag("Settings") != null && getSupportFragmentManager().findFragmentByTag("Settings").isVisible()) {

            }
            return true;
        }
        if (id == 25) {
            Toast.makeText(this, getResources().getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public String getCurrencyToTrade() {
        return currencyToTrade;
    }

    public AdapterView.OnItemSelectedListener currencyChangeListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            currencyToTrade = parent.getSelectedItem().toString();
            if (getSupportFragmentManager().findFragmentByTag("Trade") != null && getSupportFragmentManager().findFragmentByTag("Trade").isVisible())
                marketFragment.updateCurrencyToTrade();
            //currencyFragment.getUpdateCurrency().execute();
            Log.d("Changed", currencyToTrade);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    @Override
    public ArrayList<Currency> getCurrencies() {
        return quotesFragment.getCurrencies();
    }

    @Override
    public void showCurrencyFragment(String name) {
        /*if (!currencyToTrade.equals(name))
            currencyFragment = new CurrencyFragment();*/
        currencyToTrade = name;
        mBottomBar.selectTabAtPosition(1, true);
    }

    public TradeApi getApi() {
        return api;
    }

    public String getKey() {
        return aKey;
    }

    public String getSecret() {
        return aSecret;
    }

    public void setKey(String aKey) {
        this.aKey = aKey;
    }

    public void setSecret(String aSecret) {
        this.aSecret = aSecret;
    }

    public void signOut() {
        aKey = null;
        aSecret = null;
        isSignedIn = false;
    }

    public void signIn(String key, String secret) {
        aKey = key;
        aSecret = secret;
        isSignedIn = true;
        try {
            api = new TradeApi(aKey, aSecret);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (api != null && !api.info.isSuccess()) new GetPublicInfo().execute(context);
    }

    public boolean isSignedIn() {
        return isSignedIn;
    }

    public int getRequestLimit() {
        return requestLimit;
    }

    public void setRequestLimit(int requestLimit) {
        this.requestLimit = requestLimit;
    }

    public boolean isConnectedToNetwork() {
        return isConnectedToNetwork;
    }

    public boolean isPublicInfoReceived() {
        return isPublicInfoReceived;
    }

    @Override
    public void saveMarketOrders(ArrayList<CurrencyOrder> orders, ArrayList<String> summary) {
        marketOrders = orders;
        marketSummary = summary;
    }

    @Override
    public ArrayList<CurrencyOrder> getOrders() {
        return marketOrders;
    }

    @Override
    public ArrayList<String> getSummary() {
        return marketSummary;
    }

    @Override
    public void saveBalance(ArrayList<CurrencyBalance> balances) {
        balance = balances;
    }

    @Override
    public ArrayList<CurrencyBalance> getBalance() {
        return balance;
    }

    public void saveTransactions(ArrayList<Transaction> transactions) {
        this.transactions = transactions;
    }

    public ArrayList<Transaction> getTransactions() {
        return transactions;
    }

    public void saveActiveOrders(ArrayList<MyOrder> orders, ArrayList<String> pairs, ArrayList<String> types) {
        this.orders = orders;
        this.types = types;
        this.pairs = pairs;
    }

    public ArrayList<MyOrder> getActiveOrders() {
        return orders;
    }

    public ArrayList<String> getPairs() {
        return pairs;
    }

    public ArrayList<String> getTypes() {
        return types;
    }
}
