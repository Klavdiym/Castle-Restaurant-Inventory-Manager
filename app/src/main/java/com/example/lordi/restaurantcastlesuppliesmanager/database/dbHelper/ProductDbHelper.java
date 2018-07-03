package com.example.lordi.restaurantcastlesuppliesmanager.database.dbHelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.lordi.restaurantcastlesuppliesmanager.database.contract.ProductContract.ProductEntry;
import com.example.lordi.restaurantcastlesuppliesmanager.utils.ProductUtility;

import static com.example.lordi.restaurantcastlesuppliesmanager.database.contract.ProductContract.ProductEntry.TABLE_NAME;

//big thanks for the suggestions to the udacity discussion forums to make query like this
public class ProductDbHelper extends SQLiteOpenHelper {
    private static final String Db_NAME = "products.db";
    private static final int Db_VERSION = 1;

    private static final String CREATE_TABLE = "CREATE TABLE ";
    private static final String PRIMARY_KEY = " PRIMARY KEY";
    private static final String AUTOINCREMENT = " AUTOINCREMENT";
    private static final String TEXT = " TEXT";
    private static final String INTEGER = " INTEGER";
    private static final String BLOB = " BLOB";
    private static final String NOT_NULL = " NOT NULL";
    private static final String DEFAULT = " DEFAULT ";
    private static final String OPEN_BRACKET = "(";
    private static final String CLOSE_BRACKET = ")";
    private static final String COMA = ",";
    private static final String SEMI_COLUMN = ";";


    public ProductDbHelper(Context context) {
        super(context, Db_NAME, null, Db_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_TABLE_PRODUCT =
                CREATE_TABLE + TABLE_NAME + OPEN_BRACKET +
                        ProductEntry._ID + INTEGER + PRIMARY_KEY + AUTOINCREMENT + COMA +
                        ProductEntry.COLUMN_PRODUCT_NAME + TEXT + NOT_NULL + COMA +
                        ProductEntry.COLUMN_PRODUCT_CODE + TEXT + NOT_NULL + COMA +
                        ProductEntry.COLUMN_PRODUCT_CATEGORY + TEXT + NOT_NULL + COMA +
                        ProductEntry.COLUMN_PRODUCT_PRICE + INTEGER + NOT_NULL + COMA +
                        ProductEntry.COLUMN_PRODUCT_QUANTITY + INTEGER + NOT_NULL + DEFAULT + ProductUtility.ZERO + COMA +
                        ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME + TEXT + NOT_NULL + COMA +
                        ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE + TEXT + NOT_NULL + COMA +
                        ProductEntry.COLUMN_PRODUCT_DESCRIPTION + TEXT + COMA +
                        ProductEntry.COLUMN_PRODUCT_IMAGE1 + BLOB + COMA +
                        ProductEntry.COLUMN_PRODUCT_IMAGE2 + BLOB + COMA +
                        ProductEntry.COLUMN_PRODUCT_IMAGE3 + BLOB + CLOSE_BRACKET + SEMI_COLUMN;

        db.execSQL(CREATE_TABLE_PRODUCT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

}
