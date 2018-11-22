package com.ssd.androidlauncher;

import android.content.Context;
import android.util.Log;
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
    public static ArrayList<AppDetail> searchedItems, allItems;
    private LayoutInflater inflater;

    ListViewAdapter(Context context, ArrayList<AppDetail> array1List, ArrayList<AppDetail> array2List){

        this.mContext = context;
        inflater = LayoutInflater.from(mContext);
        this.appList = array1List;
        this.searchedItems = (ArrayList<AppDetail>) array1List.clone(); // copy app list first

        // Create a merged items arraylist -> apps + contacts
        allItems = new ArrayList<>();
        this.allItems.addAll(array1List);
        this.allItems.addAll(array2List);
        Log.i("s","s");
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

        TextView appLabel = convertView.findViewById(R.id.item_app_name);
        appLabel.setText(searchedItems.get(position).name);

        TextView appName = convertView.findViewById(R.id.item_app_info);
        appName.setText(searchedItems.get(position).info);

        return convertView;
    }

    // Search filter
    public void filter(String text){
        text = text.toLowerCase(Locale.getDefault());
        searchedItems.clear();

        if(text.length() > 0){
            for(AppDetail item: allItems){
                CharSequence appName = item.name;
                if(appName.toString().toLowerCase(Locale.getDefault()).contains(text)){
                    searchedItems.add(item);
                }
            }
        }
        else{
            searchedItems = (ArrayList<AppDetail>) appList.clone();
        }
        notifyDataSetChanged();
    }
}
