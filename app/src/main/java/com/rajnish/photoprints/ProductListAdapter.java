package com.rajnish.photoprints;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import java.util.ArrayList;

public class ProductListAdapter extends BaseAdapter {
Context context;
 ArrayList ProductName;
 ArrayList ProductPrice;
 LayoutInflater inflater;

    public ProductListAdapter(Context context, ArrayList ProductName, ArrayList ProductPrice) {
        this.context = context;
        this.ProductPrice = ProductPrice;
        this.ProductName = ProductName;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return ProductName.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }


    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.buy_coins_row,null);
        TextView tv_productName = view.findViewById(R.id.ProductName);
        TextView tv_productPrice = view.findViewById(R.id.ProductPrice);
        tv_productName.setText(ProductName.get(i).toString());
        tv_productPrice.setText(ProductPrice.get(i).toString());

        return view;
    }
}
