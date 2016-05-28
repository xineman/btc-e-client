package nf.co.xine.btc_eclient;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "settings";

    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE CURRENCIES ("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "NAME TEXT UNIQUE, "
                + "ASK_VAL TEXT, "
                + "BID_VAL TEXT, "
                + "IS_ENABLED NUMERIC, "
                + "PAIR_INDEX INTEGER);");
        db.execSQL("CREATE TABLE ORDERS_VAL ("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "CURRENCY_NAME TEXT UNIQUE, "
                + "ORDERS_LIST TEXT, "
                + "SUMMARY_LIST TEXT);");

        insertCurrency(db, "BTC/USD", true, 0, "0", "0");
        insertCurrency(db, "BTC/RUR", true, 1, "0", "0");
        insertCurrency(db, "BTC/EUR", true, 2, "0", "0");
        insertCurrency(db, "LTC/BTC", true, 3, "0", "0");
        insertCurrency(db, "LTC/USD", true, 4, "0", "0");
        insertCurrency(db, "LTC/RUR", true, 5, "0", "0");
        insertCurrency(db, "LTC/EUR", true, 6, "0", "0");
        insertCurrency(db, "NMC/BTC", true, 7, "0", "0");
        insertCurrency(db, "NMC/USD", true, 8, "0", "0");
        insertCurrency(db, "NVC/BTC", true, 9, "0", "0");
        insertCurrency(db, "NVC/USD", true, 10, "0", "0");
        insertCurrency(db, "USD/RUR", true, 11, "0", "0");
        insertCurrency(db, "EUR/USD", true, 12, "0", "0");
        insertCurrency(db, "EUR/RUR", true, 13, "0", "0");
        insertCurrency(db, "PPC/BTC", true, 14, "0", "0");
        insertCurrency(db, "PPC/USD", true, 15, "0", "0");
        insertCurrency(db, "DSH/BTC", true, 16, "0", "0");
        insertCurrency(db, "ETH/BTC", true, 17, "0", "0");
        insertCurrency(db, "ETH/USD", true, 18, "0", "0");
        System.out.println("created");

    }

    public static void insertCurrency(SQLiteDatabase db, String name, boolean enabled, int index, String ask, String bid) {
        ContentValues values = new ContentValues();
        values.put("NAME", name);
        values.put("ASK_VAL", ask);
        values.put("BID_VAL", bid);
        values.put("IS_ENABLED", enabled);
        values.put("PAIR_INDEX", index);
        db.insertWithOnConflict("CURRENCIES", null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        System.out.println("updated");

    }
}
