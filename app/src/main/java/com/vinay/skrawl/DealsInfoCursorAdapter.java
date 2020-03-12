package com.vinay.skrawl;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;


public class DealsInfoCursorAdapter extends CursorAdapter {
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private SearchView searchView;

    public DealsInfoCursorAdapter(Context context, Cursor cursor, SearchView sv) {
        super(context, cursor, false);
        mContext = context;
        searchView = sv;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View v = mLayoutInflater.inflate(R.layout.deal_simple_item_layout, parent, false);
        return v;
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
        String desc = cursor.getString(cursor.getColumnIndexOrThrow("desc"));

        TextView dealsTv = (TextView) view.findViewById(R.id.tv_deal);
        dealsTv.setText(title);

        TextView cashbackTv = (TextView) view.findViewById(R.id.tv_cashback);
        cashbackTv.setText(desc);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //take next action based user selected item
                TextView dealText = (TextView) view.findViewById(R.id.tv_deal);
                searchView.setIconified(true);
                Toast.makeText(context, "Selected suggestion " + dealText.getText(),
                        Toast.LENGTH_LONG).show();

            }
        });

    }
}