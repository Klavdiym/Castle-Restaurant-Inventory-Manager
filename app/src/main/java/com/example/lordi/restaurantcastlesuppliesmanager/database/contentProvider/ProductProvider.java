package com.example.lordi.restaurantcastlesuppliesmanager.database.contentProvider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.lordi.restaurantcastlesuppliesmanager.R;
import com.example.lordi.restaurantcastlesuppliesmanager.database.contract.ProductContract;
import com.example.lordi.restaurantcastlesuppliesmanager.database.dbHelper.ProductDbHelper;
import com.example.lordi.restaurantcastlesuppliesmanager.utils.ProductUtility;
import com.example.lordi.restaurantcastlesuppliesmanager.database.contract.ProductContract.ProductEntry;

import java.util.Objects;


public class ProductProvider extends ContentProvider {
    /**
     * Tag for the log messages
     */
    private static final String LOG_TAG = ProductProvider.class.getSimpleName() + " : ";
    private ProductUtility productUtility;

    private static final int PRODUCT = 100; // code for table path uri
    private static final int PRODUCT_ID = 101; // code for table's rows uri
    // to create 2 uris od table and rows and add them to MATCHER object
    private static final UriMatcher MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        MATCHER.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PRODUCT_PATH, PRODUCT);

        MATCHER.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PRODUCT_PATH + "/#", PRODUCT_ID);
    }

    private ProductDbHelper dbHelper; // object link between database and contentProvider

    @Override
    public boolean onCreate() {
        dbHelper = new ProductDbHelper(getContext());
        productUtility = new ProductUtility();
        return true;
    }


    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = dbHelper.getReadableDatabase(); //retrieves data to display it

        int match = MATCHER.match(uri); // to know which uri is in the MATCHER
        Cursor cursor; // cursor which will contain the database / row as the MATCHER gets table or row uri
        switch (match) {
            case PRODUCT:
                cursor = db.query(
                        ProductEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null, null, sortOrder);
                break;
            case PRODUCT_ID:
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(
                        ProductEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException(LOG_TAG + Objects.requireNonNull(getContext()).getString(R.string.error_displaying_data) + uri);
        }
        cursor.setNotificationUri(Objects.requireNonNull(getContext()).getContentResolver(), uri);
        return cursor;
    }


    @Override
    public String getType(@NonNull Uri uri) {
        int match = MATCHER.match(uri);

        if (match == PRODUCT) {
            return ProductEntry.PRODUCT_LIST_TYPE;
        } else { // if match == PRODUCT_ID
            return ProductEntry.PRODUCT_ITEM_TYPE;
        }
    }


    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        int match = MATCHER.match(uri);

        if (match == PRODUCT_ID) {
            throw new IllegalArgumentException(LOG_TAG + Objects.requireNonNull(getContext()).getString(R.string.error_inserting_data) + uri);
        }
        Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null);
        return insertData(uri, values);
    }

    /**
     * to insert the data to the table
     *
     * @param uri    of the table
     * @param values which well be inserted
     * @return newRowUri
     */
    private Uri insertData(Uri uri, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase(); // get the writable db to insert the data
        int newRowId = (int) db.insert(ProductEntry.TABLE_NAME, null, values);
        if (newRowId == productUtility.INVALID) { // if not inserted throw this error
            Log.e(LOG_TAG, Objects.requireNonNull(getContext()).getString(R.string.error_inserting_data) + uri);
            return null;
        }
        // else return new uri with the id of inserted data
        return Uri.withAppendedPath(uri, String.valueOf(newRowId));
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int match = MATCHER.match(uri);
        int deletedRows;
        switch (match) {
            case PRODUCT:
                deletedRows = db.delete(ProductEntry.TABLE_NAME, null, null); // to delete all rows but still increment according to last id
                break;
            case PRODUCT_ID:
                deletedRows = db.delete(ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                Log.e(LOG_TAG, Objects.requireNonNull(getContext()).getString(R.string.error_deleting_data) + uri);

                throw new IllegalArgumentException(LOG_TAG + getContext().getString(R.string.error_deleting_data) + uri);
        }
        Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null);
        return deletedRows;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = MATCHER.match(uri);

        switch (match) {
            case PRODUCT:
                return updateProduct(uri, contentValues, selection, selectionArgs);
            case PRODUCT_ID:
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateProduct(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateProduct(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.containsKey(ProductEntry.COLUMN_PRODUCT_NAME)) {
            String name = values.getAsString(ProductEntry.COLUMN_PRODUCT_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Product requires a name");
            }
        }
        // check if the provided quantity is equal or greater than to 0
        Integer quantity = values.getAsInteger(ProductEntry.COLUMN_PRODUCT_QUANTITY);
        if (quantity != null && quantity < 0) {
            throw new IllegalArgumentException("Quantity has to be equal or greater than 0");
        }
        if (values.size() == 0) {
            return 0;
        }
        // Otherwise, get writable database to update the data
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update(ProductEntry.TABLE_NAME, values, selection, selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows updated
        return rowsUpdated;
    }


}

