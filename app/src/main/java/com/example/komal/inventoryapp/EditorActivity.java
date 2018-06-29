package com.example.komal.inventoryapp;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.komal.inventoryapp.data.ItemContract.ItemEntry;


/**
 * Created by Komal on 15-04-2018.
 */

public class EditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    public static final int REQUEST_READ_EXTERNAL_STORAGE_PERMISSION = 1;
    public static final int EXISTING_LOADER = 0;
    private static final int CHOOSE_IMAGE_REQUEST = 0;

    private Uri mImageUri;

    private Uri mCurrentItemsUri;
    private EditText mProductName;
    private EditText mSupplierName;
    private EditText mSupplierPhone;
    private Button mImageButton;
    public EditText mProductPrice;
    private EditText mProductQuantity;

    private ImageView mProductimageImageView;
    private boolean mProductHasChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editor_activity);
        Intent intent = getIntent();
        mCurrentItemsUri = intent.getData();

        if (mCurrentItemsUri == null) {
            setTitle(getString(R.string.editor_activity_title_edit_product));
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.editor_activity_title_edit_product));
            getLoaderManager().initLoader(EXISTING_LOADER, null, this);
        }
        mSupplierName = findViewById(R.id.edit_supplier_name);
        mSupplierPhone = findViewById(R.id.edit_supplier_phone);
        mProductName = findViewById(R.id.edit_product_name);
        mProductPrice = findViewById(R.id.edit_product_price);
        mProductQuantity = findViewById(R.id.edit_product_quantity);
        mImageButton = findViewById(R.id.select_image);
        mProductimageImageView = findViewById(R.id.image_view);
        ImageButton mdecreaseQuantity = findViewById(R.id.decrease_quantity);
        ImageButton mincreaseQuantity = findViewById(R.id.increase_quantity);

        mincreaseQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addToQuantity();
                mProductHasChanged = true;
            }
        });

        mdecreaseQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                subtractToQuantity();
                mProductHasChanged = true;
            }
        });

        mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission();
                mProductHasChanged = true;
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_READ_EXTERNAL_STORAGE_PERMISSION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openImageSelector();
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {

        if (requestCode == CHOOSE_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {

            if (resultData != null) {
                mImageUri = resultData.getData();
                Log.i("ImageUri", mImageUri.toString());
                mProductimageImageView.setImageURI(mImageUri);
                mProductimageImageView.setTag(mImageUri.toString());
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mCurrentItemsUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);

            MenuItem orderItem = menu.findItem(R.id.action_order);
            orderItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_save:
                if (!saveProduct()) {
                    return true;
                }
                finish();
                return true;

            case R.id.action_order:
                orderConfirmationDialog();
                return true;

            case R.id.action_delete:
                DeleteConfirmationDialog();
                return true;

            case android.R.id.home:
                if (!mProductHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                UnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        if (!mProductHasChanged) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        finish();
                    }
                };
        UnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                ItemEntry._ID,
                ItemEntry.COLUMN_ITEM_NAME, ItemEntry.COLUMN_ITEM_PRICE, ItemEntry.COLUMN_ITEM_QUANTITY,
                ItemEntry.COLUMN_SUPPLIERS_NAME, ItemEntry.COLUMN_SUPPLIERS_NUMBER, ItemEntry.COLUMN_IMAGE};

        return new CursorLoader(this,
                mCurrentItemsUri, projection, null, null, null);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mProductName.setText(" ");
        mProductPrice.setText(" ");
        mProductQuantity.setText(" ");
        mSupplierName.setText(" ");
        mSupplierPhone.setText(" ");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_NAME);

            int priceColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_PRICE);

            int quanitityColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_QUANTITY);

            int supplierNameColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_SUPPLIERS_NAME);

            int supplierPhoneColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_SUPPLIERS_NUMBER);
            int productImageIndex = cursor.getColumnIndex(ItemEntry.COLUMN_IMAGE);

            String name = cursor.getString(nameColumnIndex);
            String price = cursor.getString(priceColumnIndex);
            int quanitity = cursor.getInt(quanitityColumnIndex);
            String supplierName = cursor.getString(supplierNameColumnIndex);
            String supplierPhone = cursor.getString(supplierPhoneColumnIndex);
            Uri image = Uri.parse(cursor.getString(productImageIndex));

            mProductName.setText(name);
            mProductPrice.setText(price);
            mProductQuantity.setText(Integer.toString(quanitity));
            mSupplierName.setText(supplierName);
            mSupplierPhone.setText(supplierPhone);
            mProductimageImageView.setImageURI(image);
            mProductimageImageView.setTag(image.toString());
        }
    }


    private void addToQuantity() {
        String previousValueString = mProductQuantity.getText().toString();
        int previousValue;
        if (previousValueString.isEmpty()) {
            previousValue = 0;
        } else {
            previousValue = Integer.parseInt(previousValueString);
        }
        mProductQuantity.setText(String.valueOf(previousValue + 1));
    }

    private void subtractToQuantity() {
        String previousValueString = mProductQuantity.getText().toString();
        int previousValue;
        if (previousValueString.isEmpty() || previousValueString.equals("0")) {
        } else {
            previousValue = Integer.parseInt(previousValueString);
            mProductQuantity.setText(String.valueOf(previousValue - 1));
        }
    }


    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions
                    (this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_READ_EXTERNAL_STORAGE_PERMISSION);
            return;
        }
        openImageSelector();
    }


    private void openImageSelector() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        } else {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        }
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), CHOOSE_IMAGE_REQUEST);
    }

    public void deleteProduct() {
        // Only perform the delete if this is an existing product.
        if (mCurrentItemsUri != null) {

            int rowsDeleted = getContentResolver().delete(mCurrentItemsUri, null, null);

            if (rowsDeleted == 0) {

            } else {

            }
        }

        finish();
    }

    private boolean saveProduct() {

        String nameString = mProductName.getText().toString().trim();
        String priceString = mProductPrice.getText().toString().trim();
        String quantityString = mProductQuantity.getText().toString().trim();
        Integer quantity = 0;
        if (!TextUtils.isEmpty(quantityString))
            quantity = Integer.parseInt(quantityString);
        String supplierNameString = mSupplierName.getText().toString().trim();
        String supplierPhoneString = mSupplierPhone.getText().toString().trim();
        try {
            String path = mProductimageImageView.getTag().toString();
            mImageUri = Uri.parse(path);
        } catch (Exception e) {
            Log.d("saveProduct", e.toString());
        }


        if (mImageUri == null &&
                TextUtils.isEmpty(nameString) && TextUtils.isEmpty(priceString) &&
                TextUtils.isEmpty(quantityString) && TextUtils.isEmpty(supplierNameString) &&
                TextUtils.isEmpty(supplierPhoneString)) {

            return true;
        }

        boolean isAllOk = true;
        if (!checkIfValueSet(mProductName, nameString, "name")) {
            isAllOk = false;
        }
        if (!checkIfValueSet(mProductPrice, priceString, "price")) {
            isAllOk = false;
        }
        if (!checkIfValueSet(mProductQuantity, quantityString, "quantity")) {
            isAllOk = false;
        }
        if (!checkIfValueSet(mSupplierName, supplierNameString, "supplier name")) {
            isAllOk = false;
        }
        if (!checkIfValueSet(mSupplierPhone, supplierPhoneString, "supplier phone")) {
            isAllOk = false;
        }

        if (mImageUri == null) {
            isAllOk = false;
            mImageButton.setError("Missing image");
        }

        if (!isAllOk) {
            return false;
        }

        ContentValues values = new ContentValues();
        values.put(ItemEntry.COLUMN_ITEM_NAME, nameString);
        values.put(ItemEntry.COLUMN_ITEM_PRICE, priceString);
        values.put(ItemEntry.COLUMN_ITEM_QUANTITY, quantity);
        values.put(ItemEntry.COLUMN_SUPPLIERS_NAME, supplierNameString);
        values.put(ItemEntry.COLUMN_SUPPLIERS_NUMBER, supplierPhoneString);
        values.put(ItemEntry.COLUMN_IMAGE, mImageUri.toString());

        if (mCurrentItemsUri == null) {

            Uri newUri = getContentResolver().insert(ItemEntry.CONTENT_URI, values);
            if (newUri == null) {


            } else {

            }
        } else {

            int rowsAffected = getContentResolver().update(mCurrentItemsUri, values, null, null);

            if (rowsAffected == 0) {
            } else {
            }
        }
        return true;

    }

    private boolean checkIfValueSet(EditText box, String text, String description) {
        if (TextUtils.isEmpty(text)) {
            box.setError("Missing product " +description);
            Toast.makeText(this, getString(R.string.fill),
                    Toast.LENGTH_SHORT).show();
            return false;
        } else {
            box.setError(null);
            return true;
        }
    }

    private void orderConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.order_message);
        builder.setPositiveButton(R.string.phone, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // intent to phone
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + mSupplierPhone.getText().toString().trim()));
                startActivity(intent);
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



    private void UnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (dialog != null) {
                    dialog.dismiss() ;
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void DeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.order_message);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (dialog != null) {dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}



