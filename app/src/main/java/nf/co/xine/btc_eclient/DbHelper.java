package nf.co.xine.btc_eclient;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by uragu on 01.05.2016.
 */
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
                + "NAME TEXT, "
                + "IS_ENABLED NUMERIC, "
                + "PAIR_INDEX INTEGER);");

        insertCurrency(db,"BTC/USD",true,0);
        insertCurrency(db,"BTC/RUR",true,1);
        insertCurrency(db,"BTC/EUR",true,2);
        insertCurrency(db,"LTC/BTC",true,3);
        insertCurrency(db,"LTC/USD",true,4);
        insertCurrency(db,"LTC/RUR",true,5);
        insertCurrency(db,"LTC/EUR",true,6);
        insertCurrency(db,"NMC/BTC",true,7);
        insertCurrency(db,"NMC/USD",true,8);
        insertCurrency(db,"NVC/BTC",true,9);
        insertCurrency(db,"NVC/USD",true,10);
        insertCurrency(db,"USD/RUR",true,11);
        insertCurrency(db,"EUR/USD",true,12);
        insertCurrency(db,"EUR/RUR",true,13);
        insertCurrency(db,"PPC/BTC",true,14);
        insertCurrency(db,"PPC/USD",true,15);
        insertCurrency(db,"DSH/BTC",true,16);
        insertCurrency(db,"ETH/BTC",true,17);
        insertCurrency(db,"ETH/USD",true,18);
        System.out.println("created");

    }

    public static void insertCurrency(SQLiteDatabase db, String name, boolean enabled, int index) {
        ContentValues values = new ContentValues();
        values.put("NAME", name);
        values.put("IS_ENABLED", enabled);
        values.put("PAIR_INDEX", index);
        db.insert("CURRENCIES",null,values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        System.out.println("updated");

    }
}
