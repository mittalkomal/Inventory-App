package com.example.komal.inventoryapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.support.design.widget.FloatingActionButton;
import com.example.komal.inventoryapp.data.ItemContract.ItemEntry;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private ItemAdapter mCursorAdapter;
    public static final int LOADER_ITEM = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab1 = findViewById(R.id.fab);
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        ListView ListViewitems = findViewById(R.id.list_view);
        View emptyView = findViewById(R.id.empty_view);
        ListViewitems.setEmptyView(emptyView);
        mCursorAdapter = new ItemAdapter(this);
        ListViewitems.setAdapter(mCursorAdapter);

        ListViewitems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id)
            {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                Uri UricurrentItem = ContentUris.withAppendedId(ItemEntry.CONTENT_URI, id);
                intent.setData(UricurrentItem);
                startActivity(intent);
            }
        });

        getLoaderManager().initLoader(LOADER_ITEM, null, this);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.delete_all_items:
                showDeleteAllDialog();
                return true;
            case R.id.insert_data:
                insertdummyItem();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }
    public void clickOnSale(long id, int quantity)
    {
        int Quantity = 0;
        if (quantity > 0)
        {
            Quantity = quantity - 1;
        }
        String selection = ItemEntry._ID + "=?";
        String[] selectionArgs = new  String[]{String.valueOf(id)};

        ContentValues values = new ContentValues();
        values.put(ItemEntry.COLUMN_ITEM_QUANTITY, Quantity);
        getContentResolver().update(ItemEntry.CONTENT_URI, values, selection, selectionArgs);
    }

    private void showDeleteAllDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.deleteall_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteAllProducts();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void insertdummyItem() {

        ContentValues values = new ContentValues();
        values.put(ItemEntry.COLUMN_ITEM_NAME, "Cocacola");
        values.put(ItemEntry.COLUMN_ITEM_PRICE, "35");
        values.put(ItemEntry.COLUMN_ITEM_QUANTITY, "20");
        values.put(ItemEntry.COLUMN_SUPPLIERS_NAME, "PEPSICO");
        values.put(ItemEntry.COLUMN_SUPPLIERS_NUMBER, "0123456789");
        values.put(ItemEntry.COLUMN_IMAGE, "android.resource://com.example.komal.inventoryapp/drawable/colacola");

        getContentResolver().insert(ItemEntry.CONTENT_URI, values);

    }

    private void deleteAllProducts() {
         getContentResolver().delete(ItemEntry.CONTENT_URI, null, null);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                ItemEntry._ID, ItemEntry.COLUMN_ITEM_NAME,
                ItemEntry.COLUMN_ITEM_PRICE, ItemEntry.COLUMN_ITEM_QUANTITY,
                ItemEntry.COLUMN_SUPPLIERS_NAME, ItemEntry.COLUMN_SUPPLIERS_NUMBER,
                ItemEntry.COLUMN_IMAGE,
        };

        return new CursorLoader(this,
                ItemEntry.CONTENT_URI, projection, null, null, null);
    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }
}
