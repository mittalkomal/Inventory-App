package com.example.komal.inventoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Komal on 15-04-2018.
 */

public class ItemContract {

    public static final String CONTENT_AUTHORITY = "com.example.komal.inventoryapp";
    public static final String PATH_PRODUCTS = "items";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    private ItemContract() {
    }

    public static final class ItemEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUCTS);

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

        public final static String TABLE_NAME = "items";

        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_ITEM_NAME = "name";
        public final static String COLUMN_ITEM_PRICE = "price";
        public final static String COLUMN_SUPPLIERS_NAME = "supplier_name";
        public final static String COLUMN_SUPPLIERS_NUMBER = "supplier_phone_number";
        public final static String COLUMN_ITEM_QUANTITY = "quantity";

        public final static String COLUMN_IMAGE = "image";

    }
}

