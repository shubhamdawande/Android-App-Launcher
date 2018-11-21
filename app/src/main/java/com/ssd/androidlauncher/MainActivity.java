package com.ssd.androidlauncher;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

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

        // Load apps to adapter view
        listView = findViewById(R.id.list_view);
        loadApps();
        adapter = new ListViewAdapter(this, apps);
        listView.setAdapter(adapter);

        // listen for searched text and provide results
        searchView.setOnQueryTextListener(this);
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
            apps.add(app);
        }
    }
}