package com.example.komal.inventoryapp;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.komal.inventoryapp.data.ItemContract.ItemEntry;


/**
 * Created by Komal on 15-04-2018.
 */

class ItemAdapter extends CursorAdapter {

    private final MainActivity activity;
    public ItemAdapter(MainActivity context) {
        super(context, null, 0 );
        this.activity = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_view, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor)
    {
        TextView nameTextView = view.findViewById(R.id.name);
        TextView priceTextView = view.findViewById(R.id.price);
        TextView quantityTextView = view.findViewById(R.id.quantity);
        ImageView image = view.findViewById(R.id.image_view);
        ImageView order = view.findViewById(R.id.order);
        int nameColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_NAME);
        int priceColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_QUANTITY);

        image.setImageURI(Uri.parse(cursor.getString(cursor.getColumnIndex(ItemEntry.COLUMN_IMAGE))));

        String productName = cursor.getString(nameColumnIndex);
        String productPrice = "\u20B9" + " " + cursor.getString(priceColumnIndex);
        final Integer productQuantity = cursor.getInt(quantityColumnIndex);

        nameTextView.setText(productName);
        priceTextView.setText(productPrice);
        quantityTextView.setText(productQuantity.toString());

        final long id = cursor.getLong(cursor.getColumnIndex(ItemEntry._ID));
        String p = "i" + id;
        Log.i("id", p);
        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.clickOnSale(id,
                        productQuantity);
            }
        });

    }
}


