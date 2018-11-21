package com.ssd.androidlauncher;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

public class ListViewAdapter extends BaseAdapter {

    Context mContext;
    private ArrayList<AppDetail> appList;
    public static ArrayList<AppDetail> searchedItems;
    private LayoutInflater inflater;

    ListViewAdapter(Context context, ArrayList<AppDetail> arrayList){

        this.mContext = context;
        inflater = LayoutInflater.from(mContext);
        this.appList = arrayList;
        this.searchedItems = (ArrayList<AppDetail>) arrayList.clone();
    }

    @Override
    public int getCount() {
        return searchedItems == null ? 0 : searchedItems.size();
    }

    @Override
    public Object getItem(int position) {
        return searchedItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = inflater.inflate(R.layout.list_items, null);
        }

        ImageView appIcon = convertView.findViewById(R.id.item_app_icon);
        appIcon.setImageDrawable(searchedItems.get(position).icon);

        TextView appLabel = convertView.findViewById(R.id.item_app_label);
        appLabel.setText(searchedItems.get(position).label);

        //TextView appName = convertView.findViewById(R.id.item_app_name);
        //appName.setText(searchedItems.get(position).name);

        return convertView;
    }

    // Search filter
    public void filter(String text){
        text = text.toLowerCase(Locale.getDefault());
        searchedItems.clear();

        if(text.length() > 0){
            for(AppDetail app: appList){
                CharSequence appName = app.label;
                if(appName.toString().toLowerCase(Locale.getDefault()).contains(text)){
                    searchedItems.add(app);
                }
            }
        }
        else{
            searchedItems = (ArrayList<AppDetail>) appList.clone();
        }
        notifyDataSetChanged();
    }
}
