package com.example.komal.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.komal.inventoryapp.data.ItemContract.ItemEntry;
import static com.example.komal.inventoryapp.data.ItemContract.ItemEntry.TABLE_NAME;



/**
 * Created by Komal on 15-04-2018.
 */

class ItemDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "items.db";
    private static final int DATABASE_VERSION = 1;


    public ItemDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String ITEMS_TABLE = "CREATE TABLE "
                + ItemEntry.TABLE_NAME + " (" + ItemEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ItemEntry.COLUMN_ITEM_NAME + " TEXT NOT NULL, " + ItemEntry.COLUMN_ITEM_PRICE + " FLOAT NOT NULL, "
                + ItemEntry.COLUMN_ITEM_QUANTITY + " INTEGER NOT NULL DEFAULT 0, " + ItemEntry.COLUMN_SUPPLIERS_NAME + " TEXT NOT NULL, "
                + ItemEntry.COLUMN_SUPPLIERS_NUMBER + " TEXT NOT NULL, "
                + ItemEntry.COLUMN_IMAGE + " TEXT NOT NULL" + ");";

        db.execSQL(ITEMS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}