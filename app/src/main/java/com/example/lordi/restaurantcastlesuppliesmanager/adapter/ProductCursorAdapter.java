package com.example.lordi.restaurantcastlesuppliesmanager.adapter;

import android.content.ContentUris;
import android.content.ContentValues;
import android.net.Uri;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lordi.restaurantcastlesuppliesmanager.R;
import com.example.lordi.restaurantcastlesuppliesmanager.database.contract.ProductContract.ProductEntry;
import com.example.lordi.restaurantcastlesuppliesmanager.utils.ImageUtility;
import com.example.lordi.restaurantcastlesuppliesmanager.utils.ProductUtility;


public class ProductCursorAdapter extends CursorAdapter {


    private final ProductUtility productUtility;

    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
        productUtility = new ProductUtility();
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View cardView = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        ItemViewHolder itemViewHolder = new ItemViewHolder();
        itemViewHolder.productId = cardView.findViewById(R.id.item_id);
        itemViewHolder.productName = cardView.findViewById(R.id.item_name);
        itemViewHolder.productCode = cardView.findViewById(R.id.item_code);
        itemViewHolder.productCategory = cardView.findViewById(R.id.item_category);
        itemViewHolder.productPrice = cardView.findViewById(R.id.item_price);
        itemViewHolder.productQuantity = cardView.findViewById(R.id.item_quantity);
        itemViewHolder.productImage1 = cardView.findViewById(R.id.item_image);

        cardView.setTag(itemViewHolder);
        return cardView;
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        ItemViewHolder itemViewHolder = (ItemViewHolder) view.getTag();

        Button sellButton = view.findViewById(R.id.sell_button);
        int indexProductId = cursor.getColumnIndex(ProductEntry._ID);
        int indexProductName = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
        int indexProductCode = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_CODE);
        int indexProductCategory = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_CATEGORY);
        int indexProductPrice = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);

        int indexImage1 = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_IMAGE1);

        int index = cursor.getInt(indexProductId);
        String productName = cursor.getString(indexProductName).trim();
        String productCode = cursor.getString(indexProductCode).trim();
        String productCategory = cursor.getString(indexProductCategory).trim();
        int productPrice = Integer.parseInt(cursor.getString(indexProductPrice));

        byte[] imageBytes = cursor.getBlob(indexImage1);

        itemViewHolder.productId.setText(String.valueOf(index));
        itemViewHolder.productName.setText(productName);
        itemViewHolder.productCode.setText(productCode);
        itemViewHolder.productCategory.setText(productCategory);
        setPriceTextView(context, itemViewHolder.productPrice, productPrice);

        setItemImage(itemViewHolder.productImage1, imageBytes);

        int indexQuantity = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);
        int quantity = cursor.getInt(indexQuantity);
        setQuantityTextView(context, itemViewHolder.productQuantity, quantity);
        final int quantityIntCurrent = quantity;

        final int productId = cursor.getInt(cursor.getColumnIndex(ProductEntry._ID));

        //Sell button which decrease quantity in storage
        sellButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (quantityIntCurrent >= 1) {
                    int newQuantity = quantityIntCurrent - 1;
                    Uri quantityUri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, productId);

                    ContentValues values = new ContentValues();
                    values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, newQuantity);
                    context.getContentResolver().update(quantityUri, values, null, null);
                } else {
                    Toast.makeText(context, "This product is out of stock!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setQuantityTextView(Context context, TextView textView, int quantity) {
        if (quantity == ProductUtility.ZERO) {
            textView.setText(context.getString(R.string.out_of_stock));
            textView.setBackgroundColor(context.getResources().getColor(R.color.out_off_stock_color));
        } else {
            textView.setText(String.valueOf(quantity));
            textView.setBackgroundColor(context.getResources().getColor(R.color.not_out_off_stock_color));
        }
    }


    private void setPriceTextView(Context context, TextView textView, int price) {
        if (price == ProductUtility.ZERO) {
            textView.setText(context.getString(R.string.free_item));
            textView.setBackgroundColor(context.getResources().getColor(R.color.free_item_color));
        } else {
            String productPrice = String.valueOf(price + productUtility.DUMMY_PRODUCT_PRICE) + productUtility.DOLLAR_SIGN;
            textView.setText(productPrice);
        }if (price > ProductUtility.ZERO){
            textView.setBackgroundColor(context.getResources().getColor(R.color.not_out_off_stock_color));

        }
    }

    private void setItemImage(ImageView imageView, byte[] imageBytes) {
        if (imageBytes == null) { // as default icon
            imageView.setImageResource(R.drawable.inv300);
        } else {
            imageView.setImageBitmap(ImageUtility.byteArrayToBitmap(imageBytes));
        }
    }

    static class ItemViewHolder {
        TextView productId;
        TextView productName;
        TextView productCode;
        TextView productCategory;
        TextView productPrice;
        TextView productQuantity;
        ImageView productImage1;
    }
}
