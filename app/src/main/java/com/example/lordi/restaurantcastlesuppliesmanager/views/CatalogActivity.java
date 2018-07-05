package com.example.lordi.restaurantcastlesuppliesmanager.views;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lordi.restaurantcastlesuppliesmanager.R;
import com.example.lordi.restaurantcastlesuppliesmanager.adapter.ProductCursorAdapter;
import com.example.lordi.restaurantcastlesuppliesmanager.database.contract.ProductContract.ProductEntry;
import com.example.lordi.restaurantcastlesuppliesmanager.database.dbHelper.ProductDbHelper;
import com.example.lordi.restaurantcastlesuppliesmanager.database.presenter.ProductDbQuery;
import com.example.lordi.restaurantcastlesuppliesmanager.flowingnavigationdrawer.MenuListFragment;
import com.example.lordi.restaurantcastlesuppliesmanager.notes.NoteListActivity;
import com.example.lordi.restaurantcastlesuppliesmanager.utils.ProductUtility;
import com.example.lordi.restaurantcastlesuppliesmanager.utils.ScreenSizeUtility;
import com.mxn.soul.flowingdrawer_core.ElasticDrawer;
import com.mxn.soul.flowingdrawer_core.FlowingDrawer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.channels.FileChannel;
import java.util.Objects;

import butterknife.BindInt;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    @BindView(R.id.warn_image)
    ImageView warnImage;
    @BindView(R.id.btn_show_data)
    Button showData;
    @BindView(R.id.input_number)
    EditText inputNumber;
    @BindView(R.id.input_name)
    EditText inputText;
    @BindView(R.id.count_rows_text_view)
    TextView countRowsTextView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.empty_text_view)
    TextView emptyTextView;
    @BindView(R.id.list)
    ListView listView;
    @BindView(R.id.spinner_ordered_by)
    Spinner spinnerOrderBy;
    @BindView(R.id.spinner_order_search)
    Spinner spinnerOrderOrSearch;
    @BindString(R.string.invalid_input_user_catalog_activity)
    String invalidInput;
    @BindString(R.string.error_text)
    String invalidName;
    @BindString(R.string.empty_text_msg)
    String emptyTextMsg;
    @BindString(R.string.currently_rows)
    String catalogLabel;
    @BindString(R.string.put_value_text_msg)
    String putValueMsg;
    @BindString(R.string.no_data_for_input_value_msg)
    String noDataForInputValue;
    @BindString(R.string.spinner_label_order_by)
    String spinnerLabelOrderBy;
    @BindString(R.string.spinner_label_order_by_default)
    String labelOrderByDefault;
    @BindString(R.string.spinner_label_id)
    String labelId;
    @BindString(R.string.delete_all_entries_in_the_database)
    String deleteAllItems;
    @BindString(R.string.send_database_be_email)
    String emailAllData;
    @BindString(R.string.all_items_deleted)
    String allItemsDeletedMsg;
    @BindString(R.string.insert_test_data)
    String insertDummyItem;
    @BindString(R.string.discard)
    String discardMsg;
    @BindString(R.string.delete_all_items_dialog_msg)
    String deleteAllItemsMsg;
    @BindInt(R.integer.price_max_length)
    int priceMaxLength;
    @BindInt(R.integer.text_min_length)
    int textMinLength;
    @BindInt(R.integer.text_max_length)
    int textMaxLength;
    private String mode;

    private int lastId; // max size of db - to get last id in the table

    private ScreenSizeUtility screenSizeUtility;
    private ProductDbQuery productDbQuery;
    private ProductUtility productUtility;
    public CursorAdapter cursorAdapter;
    private boolean defaultLoaderStateChanged = false;
    private String selection;
    private String moreOrLessSign;
    private String sortOrder;
    private FlowingDrawer mDrawer;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        FragmentManager fm = getSupportFragmentManager();
        MenuListFragment mMenuFragment = (MenuListFragment) fm.findFragmentById(R.id.id_container_menu);
        if (mMenuFragment == null) {
            mMenuFragment = new MenuListFragment();
            fm.beginTransaction().add(R.id.id_container_menu, mMenuFragment).commit();
        }
        return true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);
        ButterKnife.bind(this);
        cursorAdapter = new ProductCursorAdapter(this, null);
        listView.setAdapter(cursorAdapter);
        //this set ups library's navigation drawer with fancy animation
        mDrawer = findViewById(R.id.drawerlayout);
        mDrawer = findViewById(R.id.drawerlayout);
        mDrawer.setTouchMode(ElasticDrawer.TOUCH_MODE_BEZEL);
        //end
        productUtility = new ProductUtility();
        productDbQuery = new ProductDbQuery(this);
        screenSizeUtility = new ScreenSizeUtility(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent editProduct = new Intent(CatalogActivity.this, EditorActivity.class);
                editProduct.setData(ContentUris.withAppendedId(ProductEntry.CONTENT_URI, id));
                startActivity(editProduct);
            }
        });
        setUI();
        //setupMenu();
//TODO: SEND DATABASE BY EMAIL, IF NOTE TITLE MATCHES PRODUCT NAME CHANGE, NAVIGATION DRAWER WITH FANCY ANIMATION (DELETE ALL UNNEEDED CODE FROM THE SAMPLE APP AND THEN COPY)
        getLoaderManager().initLoader(ProductUtility.ZERO, null, this);

        FloatingActionButton notesFab = findViewById(R.id.notesFab);
        notesFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openNotesActivity = new Intent(CatalogActivity.this, NoteListActivity.class);
                startActivity(openNotesActivity);
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_insert_dummy_data:
                setMode(insertDummyItem);
                break;

            case R.id.action_email_all_data:
                setMode(emailAllData);
                break;

            case R.id.action_delete_all_data:
                setMode(deleteAllItems);
                break;
        }
        restartLoaderOnQueryBundleChange(null);

        return super.onOptionsItemSelected(item);
    }


    /**
     * to set all views
     */
    private void setUI() {
        setListView();
        setSpinnerOrderBy();
        setSpinnerOrderOrSearch();
        setFab();
        setInputFieldVisibility(); // to hide it tell user want to enter some values to show certain queries
        setViewsMode(productUtility.DEFAULT_MODE); // as default mode
        Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu_white);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawer.toggleMenu();
            }
        });
    }

    /**
     * to show discard msg if the user clicked back,up or save  buttons
     */
    private void showDiscardMsg(String msg, String discard, String doSomeThing) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(msg);
        builder.setPositiveButton(discard, getDialogInterfaceOnClickListener(discard));
        builder.setNegativeButton(doSomeThing, getDialogInterfaceOnClickListener(doSomeThing));

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private DialogInterface.OnClickListener getDialogInterfaceOnClickListener(String usage) {
        if (usage.equals(discardMsg)) {
            return new DialogInterface.OnClickListener() {
                // user want to edit
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }
            };
        } else if (usage.equals(deleteAllItems)) { // delete all items (used in CatalogActivity)
            return new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    int deletedRows = productDbQuery.deleteAll();
                    Toast.makeText(getBaseContext(), deletedRows + allItemsDeletedMsg, Toast.LENGTH_LONG).show();
                }
            };
        } else { // discard case - user want to exit
            return new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            };
        }
    }

    /**
     * to set the spinnerOrderBy and it's cursorAdapter
     */
    private void setSpinnerOrderBy() {
        final ArrayAdapter<CharSequence> adapterSpinnerOrderBy =
                ArrayAdapter.createFromResource(this, R.array.default_order_by, android.R.layout.simple_spinner_item);

        adapterSpinnerOrderBy.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerOrderBy.setAdapter(adapterSpinnerOrderBy);

        spinnerOrderBy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String itemName = (String) adapterSpinnerOrderBy.getItem(position);
                String[] parts;
                if (mode.equals(spinnerLabelOrderBy) || mode.equals(moreOrLessSign)) {// mode order by
                    if (Objects.requireNonNull(itemName).contains(productUtility.MINUS)) { // like : price/quantity - more / less than
                        parts = itemName.split(productUtility.SPLITTER_REGEX_MINUS);
                        selection = parts[ProductUtility.ZERO];
                        if (parts[ProductUtility.ONE].contains(productUtility.MORE_THAN)) {
                            sortOrder = selection + productUtility.ASC;
                            moreOrLessSign = productUtility.MORE_THAN_SIGN;
                        } else if (parts[ProductUtility.ONE].contains(productUtility.LESS_THAN)) {
                            sortOrder = selection + productUtility.DESC;
                            moreOrLessSign = productUtility.LESS_THAN_SIGN;
                        }
                        setViewsMode(moreOrLessSign);
                        selection = selection + moreOrLessSign;// to add sign to selection whatever was it's case
                        setEmptyTextView(putValueMsg);
                        defaultLoaderStateChanged = true;
                    } else { // like : id , name , quantity ,  price/quantity ASC/DESC
                        if (itemName.equals(labelOrderByDefault)) {
                            // just to prevent restarting
                            // the loader after changing the mode 1st time while the activity still just started
                            defaultLoaderStateChanged = false;
                            sortOrder = null;
                        } else {
                            defaultLoaderStateChanged = true;
                            sortOrder = itemName;
                            setViewsMode(spinnerLabelOrderBy);
                        }
                        if (defaultLoaderStateChanged) {

                            //  must be here to restart the loader if user clicked on Id Item
                            //  and load the default cursor then get last ID while defaultLoaderStateChanged = false;
                            //  and here is the right place for this lines
                            if (itemName.equals(labelId)) {
                                defaultLoaderStateChanged = false;
                                sortOrder = null;
                            }
                            restartLoaderOnQueryBundleChange(productUtility.getBundle(null, null, sortOrder));
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /**
     * to set the setSpinnerOrderOrSearch and it's cursorAdapter
     */
    private void setSpinnerOrderOrSearch() {
        final ArrayAdapter<CharSequence> adapter_spinner_order_search =
                ArrayAdapter.createFromResource(this, R.array.spinner_search, android.R.layout.simple_spinner_item);

        adapter_spinner_order_search.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerOrderOrSearch.setAdapter(adapter_spinner_order_search);

        spinnerOrderOrSearch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String itemName = (String) adapter_spinner_order_search.getItem(position);
                setViewsMode(itemName);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    private void setEmptyTextView(String msg) {

        warnImage.setImageResource(R.drawable.empty1);
        emptyTextView.setText(R.string.no_data_for_input_value_msg);
    }


    private void setViewsMode(String mode) {
        setMode(mode);
        if (mode.equals(productUtility.DEFAULT_MODE) || mode.equals(spinnerLabelOrderBy)) {
            spinnerOrderBy.setVisibility(View.VISIBLE);
            inputNumber.setVisibility(View.GONE);
            inputText.setVisibility(View.GONE);
            showData.setVisibility(View.GONE);
        } else if (mode.equals(moreOrLessSign)) {
            spinnerOrderBy.setVisibility(View.VISIBLE);
            inputNumber.setVisibility(View.VISIBLE);
            inputText.setVisibility(View.GONE);
            showData.setVisibility(View.VISIBLE);
            setEmptyTextView(noDataForInputValue);
        } else { // search for name/category/code
            try {
                spinnerOrderBy.setVisibility(View.GONE);
            } catch (ArrayIndexOutOfBoundsException exception) {
                // Output expected ArrayIndexOutOfBoundsException.
                Toast.makeText(getApplicationContext(), "Error showing results. Out of bounds.", Toast.LENGTH_SHORT).show();
            }

            inputNumber.setVisibility(View.GONE);
            inputText.setVisibility(View.VISIBLE);
            showData.setVisibility(View.VISIBLE);
            setEmptyTextView(noDataForInputValue);
        }
    }

    private void setMode(String mode) {
        this.mode = mode;
    }


    @OnClick(R.id.btn_show_data)
    void onClickShowData() {
        String[] result = new String[0];
        String[] selectionArgs = new String[0];
        String errorMsg;
        errorMsg = String.format(invalidInput, priceMaxLength);
        if (mode.equals(moreOrLessSign)) {// get input number from user
            result = productUtility.checkEnteredNumbers(productUtility.getTextEditText(inputNumber));
            selectionArgs = new String[]{result[ProductUtility.ONE]};
            errorMsg = String.format(invalidInput, priceMaxLength);
        } else { // get input text from user
            try {
                result = productUtility.checkEnteredTextToSearch(productUtility.getTextEditText(inputText));

                String columnName = mode.split(productUtility.SPLITTER_REGEX_COLUMN_NAME,
                        productUtility.TEN)[ProductUtility.ONE];
                selection = columnName + productUtility.LIKE + result[ProductUtility.ONE];
                selectionArgs = null;
                sortOrder = columnName; // to be ordered by the column name
                errorMsg = String.format(invalidName, ProductEntry.COLUMN_PRODUCT_NAME, textMinLength, textMaxLength);
            } catch (ArrayIndexOutOfBoundsException exception) {
                // Output expected ArrayIndexOutOfBoundsException.
                Toast.makeText(getApplicationContext(), "Error showing results. Out of bounds.", Toast.LENGTH_SHORT).show();
            }
        }
        String flag = result[ProductUtility.ZERO];
        if (flag.equals(productUtility.TRUE)) {
            productUtility.showToastMsg(getBaseContext(), errorMsg);
        } else {
            Bundle args = productUtility.getBundle(
                    selection,
                    selectionArgs,
                    sortOrder);
            restartLoaderOnQueryBundleChange(args);
        }
    }

    /**
     * to set fab
     */
    private void setFab() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openEditorActivity = new Intent(CatalogActivity.this, EditorActivity.class);
                openEditorActivity.putExtra(productUtility.LAST_ID_KEY, lastId);
                startActivity(openEditorActivity);
            }
        });
    }

    /**
     * to set list view and it's cursorAdapter
     */
    private void setListView() {
        if (cursorAdapter == null || cursorAdapter.getCount() < 1) {
            return;
        }
        cursorAdapter = new ProductCursorAdapter(this, null);
        listView.setAdapter(cursorAdapter);
        int paddingSidesValue = ProductUtility.ZERO;
        int paddingBottomValue = screenSizeUtility.getHeight() / productUtility.FOUR;
        listView.setEmptyView(emptyTextView);
        listView.setPadding(paddingSidesValue, paddingSidesValue, paddingSidesValue, paddingBottomValue);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent editProduct = new Intent(CatalogActivity.this, EditorActivity.class);
                editProduct.setData(ContentUris.withAppendedId(ProductEntry.CONTENT_URI, id));
                startActivity(editProduct);
            }
        });
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle args) {
        CursorLoader cursorLoader;
        String[] projection = productDbQuery.projection();
        String selection = null;
        String[] selectionArgs = null;
        String sortOrder = null;
        if (args == null) {
            if (mode.equals(insertDummyItem)) {
                productDbQuery.insertData(null); // to insert dummy values
            } else if (mode.equals(deleteAllItems)) {
                showDiscardMsg(deleteAllItemsMsg, discardMsg, deleteAllItems);
            } else if (mode.equals(emailAllData)) {
                File sd = Environment.getExternalStorageDirectory();
                File data = Environment.getDataDirectory();
                FileChannel source = null;
                FileChannel destination = null;
                String currentDBPath = "/data/"+ getPackageName() +"/databases/"+ ProductDbHelper.Db_NAME;
                String retreivedDBPAth = getDatabasePath(ProductDbHelper.Db_NAME).getPath();
                String backupDBPath = "/storage/extSdCard/mydatabase";
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);
                File retrievedDB = new File(retreivedDBPAth);
                Log.d("PATHS", " CurrentDB=" +
                        currentDBPath + "\n\t" + currentDB.getPath() +
                        "\n\tExists=" + String.valueOf(currentDB.exists()) +
                        "\nBackup=" + backupDBPath + "\n\t" + backupDB.getPath() +
                        "\n\tExists=" + String.valueOf(backupDB.exists()) +
                        "\nRetrieved DB=" + retreivedDBPAth + "\n\t" + retrievedDB.getPath() +
                        "\n\tExists=" + String.valueOf(retrievedDB.exists())
                );
                try {
                    source = new FileInputStream(currentDB).getChannel();
                    destination = new FileOutputStream(backupDB).getChannel();
                    //destination.transferFrom(source, 0, source.size());
                    source.close();
                    destination.close();
                } catch(IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Err:"+e, Toast.LENGTH_LONG).show();
                }
            }
                //to escape error because of URI exposure I invoke this lame method, other one with FileProvider a little bit too complex
                if(Build.VERSION.SDK_INT>=24){
                    try{
                        Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                        m.invoke(null);
                    }catch(Exception e){
                        e.printStackTrace(); }
                }

        } else {
            selection = args.getString(productUtility.BUNDLE_KEY_SELECTION);
            selectionArgs = args.getStringArray(productUtility.BUNDLE_KEY_SELECTION_ARGS);
            sortOrder = args.getString(productUtility.BUNDLE_KEY_SORT_ORDER);

        }
        cursorLoader = new CursorLoader(this, ProductEntry.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                sortOrder);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (data.getCount() > ProductUtility.ZERO) {
            if (!defaultLoaderStateChanged) {// to get last id on the default case
                if (data.moveToLast()) {
                    lastId = data.getInt(data.getColumnIndex(ProductEntry._ID));
                    warnImage.setVisibility(View.INVISIBLE);
                }
            }
            countRowsTextView.setText(String.format(catalogLabel, data.getCount()));
            warnImage.setImageResource(ProductUtility.ZERO);
            emptyTextView.setVisibility(View.GONE);
            listView.setEmptyView(emptyTextView);
            warnImage.setImageResource(R.drawable.openboxicon);
            emptyTextView.setText(emptyTextMsg);
        }
        cursorAdapter.swapCursor(data);
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);
    }


    private void restartLoaderOnQueryBundleChange(Bundle args) {
        getLoaderManager().restartLoader(ProductUtility.ZERO, args, this);
    }


    private void setInputFieldVisibility() {
        inputNumber.setVisibility(View.GONE); // as default state till user choose  price/quantity  more/less than value
        inputText.setVisibility(View.GONE);
        showData.setVisibility(View.GONE);
    }

    //private void setupMenu() {
     //   FragmentManager fm = getSupportFragmentManager();
    //    MenuListFragment mMenuFragment = (MenuListFragment) fm.findFragmentById(R.id.id_container_menu);
     //   if (mMenuFragment == null) {
    //        mMenuFragment = new MenuListFragment();
     //       fm.beginTransaction().add(R.id.id_container_menu, mMenuFragment).commit();
     //   }
    //}

    @Override
    public void onBackPressed() {
        //if drawer is occupying the screen close it
        if (mDrawer.isMenuVisible()) {
            mDrawer.closeMenu();
        } else {
            super.onBackPressed();
        }
    }
}
