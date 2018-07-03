package com.example.lordi.restaurantcastlesuppliesmanager.utils;

import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import com.example.lordi.restaurantcastlesuppliesmanager.database.contract.ProductContract;

import java.util.Random;

//Big thanks to the InventoryApp-Master creator on showing how to neatly organize database/calls and packages
public class ProductUtility {

    private static final String DUMMY_SUPPLIER_PHONE = "0100111100";
    private static final String WHITE_SPACE_REGEX = ".*\\s.*";
    private static final int LIMIT_DESCRIPTION_LENGTH = 1000;
    public final String LAST_ID_KEY = "last id";
    public final String DEFAULT_MODE = "default mode";
    public final String BUNDLE_KEY_SELECTION = "selection";
    public final String BUNDLE_KEY_SELECTION_ARGS = "selectionArgs";
    public final String BUNDLE_KEY_SORT_ORDER = "sortOrder";
    public final String SIGN_ID = " =?";
    public final String DOLLAR_SIGN = " $";
    public final String MORE_THAN_SIGN = " >=?";
    public final String LESS_THAN_SIGN = " <=?";
    public final String MORE_THAN = "more than";
    public final String LESS_THAN = "less than";
    public final String DESC = " DESC";
    public final String ASC = " ASC";
    public final String TEXT = "TEXT";
    public final String NUMERIC = "NUMERIC";
    public final String LONG_TEXT = "LONG_TEXT";
    public final String PHONE = "PHONE";
    public final String TRUE = "TRUE";
    public final String FALSE = "FALSE";
    public final String LIKE = " LIKE ";
    public final String SPLITTER_REGEX_COLUMN_NAME = "(for )";
    public final String SPLITTER_REGEX_MINUS = "(-)";
    public final String MINUS = "-";
    public final String INTENT_TYPE_IMAGE = "image/*";
    public final float DUMMY_PRODUCT_PRICE = 0.99f;
    public final int LIMIT_RANDOM_VALUE = 100;
    public static final int ZERO = 0;
    public static final int ONE = 1;
    public static final int TWO = 2;
    public final int NOT_NULLABLE_COLUMNS_COUNT = 7;
    public final int INVALID = -1;
    public final int VALID = 1;
    public final int THREE = 3;
    public final int FOUR = 4;
    public final int FIVE = 5;
    private static final String DUMMY_PRODUCT_NAME = "product";
    private static final String DUMMY_PRODUCT_CODE = "code";
    private static final String DUMMY_SUPPLIER_NAME = "name";
    public final int SIX = 6;
    private static final String DUMMY_PRODUCT_DESCRIPTION = "no description found";
    private static final String DUMMY_PRODUCT_CATEGORY = "category";
    private static final String EMPTY_STRING = "";
    private static final String TEXT_REGEX = "^[A-Za-z][A-Za-z0-9]*(?:_[A-Za-z0-9]+)*$";
    public final int SEVEN = 7;
    private static final String SEARCH_TEXT_1ST_SINGLE_QUOTE = "'%";
    private static final String SEARCH_TEXT_2ND_SINGLE_QUOTE = "%'";
    public final int TEN = 10;
    private static final int COLUMNS_COUNT = 8;
    private final Random random = new Random();

    public ProductUtility() {

    }

    /**
     * to generate dummy values and set them to Edit Text fields
     */
    public String[] getDummyStringValues() {
        // to change dummy data values according to  the random value
        String[] dummyValues = new String[COLUMNS_COUNT];
        dummyValues[ZERO] = DUMMY_PRODUCT_NAME + getRandomValue();
        dummyValues[ONE] = DUMMY_PRODUCT_CODE + getRandomValue();
        dummyValues[TWO] = DUMMY_PRODUCT_CATEGORY + getRandomValue();
        dummyValues[THREE] = String.valueOf(getRandomValue());
        dummyValues[FOUR] = String.valueOf(getRandomValue());
        dummyValues[FIVE] = DUMMY_SUPPLIER_NAME + getRandomValue();
        dummyValues[SIX] = DUMMY_SUPPLIER_PHONE + getRandomValue();
        dummyValues[SEVEN] = DUMMY_PRODUCT_DESCRIPTION;
        return dummyValues;
    }

    /**
     * to generate dummy values for testing purpose
     * used when want to insert dummy content values to db directly
     */
    public ContentValues getDummyContentValues() {
        String[] dummyValues = getDummyStringValues();

        ContentValues values = new ContentValues();
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME, dummyValues[ZERO]);
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_CODE, dummyValues[ONE]);
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_CATEGORY, dummyValues[TWO]);
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE, dummyValues[THREE]);
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY, dummyValues[FOUR]);
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME, dummyValues[FIVE]);
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE, dummyValues[SIX]);
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_DESCRIPTION, dummyValues[SEVEN]);
        return values;
    }


    /**
     * to show toast Msg
     *
     * @param context for toast context
     * @param msg     to be shown
     */
    public void showToastMsg(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    public int getRandomValue() {
        return random.nextInt(LIMIT_RANDOM_VALUE);
    }

    private boolean textHasWhiteSpace(String text) {
        return text.matches(WHITE_SPACE_REGEX);
    }

    /**
     * to check user input - if empty or doesn't match the regex
     */
    public String[] checkEnteredTextToSearch(String name) {
        String invalid = TRUE; // invalid value
        String nameButNotMatched = EMPTY_STRING;
        String checkedValue = EMPTY_STRING;
        if (!TextUtils.isEmpty(name) && !textHasWhiteSpace(name)) {
            if (name.matches(TEXT_REGEX)) {
                invalid = FALSE;
                checkedValue = SEARCH_TEXT_1ST_SINGLE_QUOTE + name +
                        SEARCH_TEXT_2ND_SINGLE_QUOTE;
            } else nameButNotMatched = name;
        }
        return new String[]{invalid, checkedValue, nameButNotMatched};
    }

    private String[] checkEnteredText(String name) {
        String flag = TRUE; // invalid value
        String nameButNotMatched = EMPTY_STRING;
        String checkedValue = EMPTY_STRING;
        if (!TextUtils.isEmpty(name) && !textHasWhiteSpace(name)) {
            if (name.length() >= THREE) {
                if (name.matches(TEXT_REGEX)) {
                    flag = FALSE;
                    checkedValue = name;
                } else nameButNotMatched = name;
            }
        }
        return new String[]{flag, checkedValue, nameButNotMatched};
    }

    private String[] checkEnteredLongText(String longText) {
        String invalid = TRUE; // invalid value
        String nameButNotMatched = EMPTY_STRING;
        String checkedValue = EMPTY_STRING;
        if (!TextUtils.isEmpty(longText)) {
            if (longText.length() < LIMIT_DESCRIPTION_LENGTH) {
                invalid = FALSE;
                checkedValue = longText;
            } else nameButNotMatched = longText;
        }
        return new String[]{invalid, checkedValue, nameButNotMatched};
    }

    public String[] checkEnteredNumbers(String number) {
        String invalid = TRUE; // invalid value
        String checkedValue = EMPTY_STRING;
        if (!TextUtils.isEmpty(number)) {
            int numberButNotMatched = (int) Math.floor(Integer.parseInt(number));
            if (numberButNotMatched >= ZERO) {
                invalid = FALSE; // invalid value
                checkedValue = String.valueOf(numberButNotMatched);
            }
        }//String value of the number it will be converted to int in next steps
        return new String[]{invalid, checkedValue, number};
    }

    private String[] checkEnteredPhoneNumber(String phone) {
        String invalid = TRUE; // not valid value as default case
        String phoneButNotMatched = EMPTY_STRING;
        String checkedValue = EMPTY_STRING;
        if (!TextUtils.isEmpty(phone)) {
            if (phone.length() >= TEN) {
                invalid = FALSE;
                checkedValue = phone;
            } else phoneButNotMatched = phone;
        }
        return new String[]{invalid, checkedValue, phoneButNotMatched};
    }

    public String[] checkIfFieldIsNull(EditText editText, String type) {
        String[] flagAndValues;
        switch (type) {
            case TEXT: // name , code .. etc
                flagAndValues = checkEnteredText(getTextEditText(editText));
                break;
            case NUMERIC: // price , quantity
                flagAndValues = checkEnteredNumbers(getTextEditText(editText));
                break;
            case PHONE: // phone number
                flagAndValues = checkEnteredPhoneNumber(getTextEditText(editText));
                break;
            case LONG_TEXT:// long text (description)
            default:
                flagAndValues = checkEnteredLongText(getTextEditText(editText));
        }
        return flagAndValues;
    }

    public String getTextEditText(EditText editText) {
        return editText.getText().toString().trim();
    }

    public Bundle getBundle(String selection, String[] selectionArgs, String sortOrder) {
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_KEY_SELECTION, selection);
        bundle.putStringArray(BUNDLE_KEY_SELECTION_ARGS, selectionArgs);
        bundle.putString(BUNDLE_KEY_SORT_ORDER, sortOrder);
        return bundle;
    }

    public boolean noNullValues(ContentValues values) {
        // check which values are empty
        return (values.containsKey(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME)) &&
                (values.containsKey(ProductContract.ProductEntry.COLUMN_PRODUCT_CODE)) &&
                (values.containsKey(ProductContract.ProductEntry.COLUMN_PRODUCT_CATEGORY)) &&
                (values.containsKey(ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE)) &&
                (values.containsKey(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY)) &&
                (values.containsKey(ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME)) &&
                (values.containsKey(ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE));
    }
}
