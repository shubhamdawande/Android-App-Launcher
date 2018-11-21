package com.ssd.androidlauncher;

import android.app.WallpaperManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{

    private SearchView searchView;
    private ListView listView;
    private PackageManager manager;

    private ArrayList<AppDetail> apps;
    private ListViewAdapter adapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchView = findViewById(R.id.search_view);

        // set device wallpaper as background
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
        final Drawable wallpaperDrawable = wallpaperManager.getDrawable();
        RelativeLayout mainScreen = findViewById(R.id.main_activity);
        mainScreen.setBackground(wallpaperDrawable);

        // Load launcher category apps to adapter view
        listView = findViewById(R.id.list_view);
        loadApps();
        adapter = new ListViewAdapter(this, apps);
        listView.setAdapter(adapter);

        // listen for searched text and provide filtered results
        searchView.setOnQueryTextListener(this);
        addClickListener();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        adapter.filter(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        adapter.filter(newText);
        return false;
    }

    // Load list of launcher category apps
    private void loadApps() {
        manager = getPackageManager();
        apps = new ArrayList<>();

        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> availableActivities = manager.queryIntentActivities(intent, 0);
        for (ResolveInfo act : availableActivities) {
            AppDetail app = new AppDetail();
            app.label = act.loadLabel(manager);
            app.name = act.activityInfo.packageName;
            app.icon = act.activityInfo.loadIcon(manager);
            if(app.icon.getIntrinsicHeight() != 96){
                app.icon = resize(app.icon);
            }
            apps.add(app);
        }
    }

    // Launch app after click
    private void addClickListener(){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                MainActivity.this.startActivity(manager.getLaunchIntentForPackage(ListViewAdapter.searchedItems.get(pos).name.toString()));
            }
        });
    }

    // To resize app icons
    private Drawable resize(Drawable image) {
        Bitmap b = ((BitmapDrawable)image).getBitmap();
        Bitmap bitmapResized = Bitmap.createScaledBitmap(b, 96, 96, false);
        return new BitmapDrawable(getResources(), bitmapResized);
    }
}