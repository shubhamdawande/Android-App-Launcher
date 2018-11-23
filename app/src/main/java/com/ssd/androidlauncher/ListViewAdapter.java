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

    private Context mContext;
    private ArrayList<AppDetail> appList;
    static ArrayList<AppDetail> searchedItems;
    private static ArrayList<AppDetail> allItems;
    private LayoutInflater inflater;

    ListViewAdapter(Context context, ArrayList<AppDetail> array1List, ArrayList<AppDetail> array2List){

        this.mContext = context;
        inflater = LayoutInflater.from(mContext);
        this.appList = array1List;
        searchedItems = (ArrayList<AppDetail>) array1List.clone(); // copy app list first

        // Create a merged items arraylist -> apps + contacts
        allItems = new ArrayList<>();
        allItems.addAll(array1List);
        allItems.addAll(array2List);
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

        TextView appName = convertView.findViewById(R.id.item_app_name);
        appName.setText(searchedItems.get(position).name);

        TextView appInfo = convertView.findViewById(R.id.item_app_info);
        appInfo.setText(searchedItems.get(position).info);

        ImageView appCallIcon = convertView.findViewById(R.id.item_app_callIcon);
        appCallIcon.setImageDrawable(searchedItems.get(position).callIcon);

        return convertView;
    }

    /**
     * Search filter
     */
    void filter(String text){
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
