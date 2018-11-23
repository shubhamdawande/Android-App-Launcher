package com.ssd.androidlauncher;

import android.Manifest;
import android.app.WallpaperManager;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
    private ArrayList<AppDetail> contacts;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchView = findViewById(R.id.search_view);

        /*
         * Set view background
         */
        setBackground();

        /*
         * Get apps with category launcher
         */
        listView = findViewById(R.id.list_view);
        loadApps();

        /*
         * Get contacts list
         */
        getAllContacts();

        /*
         * Load listview with apps
         */
        adapter = new ListViewAdapter(this, apps, contacts);
        listView.setAdapter(adapter);

        /*
         * listen for searched text and provide filtered results
         */
        searchView.setOnQueryTextListener(this);
        addClickListener();
    }

    /**
     * Retrive all contacts from phone
     */
    private void getAllContacts() {

        /*
         * Request read permission
         */
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.READ_CONTACTS)) {
            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_CONTACTS}, 200);
            }
        }

        /*
         * Request call permission
         */
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.CALL_PHONE)) {
            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.CALL_PHONE}, 200);
            }
        }

        /*
         * Run query and get contact information from cursor into contacts<ArrayList>
         */
        contacts = new ArrayList<>();
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        assert cur != null;
        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                AppDetail cd = new AppDetail();
                String contactId = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));

                // Get contact name
                cd.name = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                // Get phone number
                Cursor phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId,
                        null, null);
                assert phones != null;
                while (phones.moveToNext()){
                    cd.info = phones.getString(phones.getColumnIndex(
                            ContactsContract.CommonDataKinds.Phone.NUMBER));
                }
                phones.close();
                if(cd.info != null){
                    cd.icon = resize(ContextCompat.getDrawable(getApplicationContext(), R.drawable.contacts));
                    cd.callIcon = resize(ContextCompat.getDrawable(getApplicationContext(),
                            R.drawable.callicon));
                    cd.type = "contact";
                    contacts.add(cd);
                }
            }
        }
        cur.close();
    }

    /**
     * Search text change listener
     */
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

    /**
     * Load list of launcher category apps
     */
    private void loadApps() {
        manager = getPackageManager();
        apps = new ArrayList<>();

        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> availableActivities = manager.queryIntentActivities(intent, 0);
        for (ResolveInfo act : availableActivities) {
            AppDetail app = new AppDetail();
            app.name = act.loadLabel(manager);
            app.info = null;
            app.callIcon = null;
            app.packageName = act.activityInfo.packageName;
            app.icon = act.activityInfo.loadIcon(manager);
            if(app.icon.getIntrinsicHeight() != 96){
                app.icon = resize(app.icon);
            }
            app.type = "application";
            apps.add(app);
        }
    }

    /**
     * Launch corresponding activity after click
     */
    private void addClickListener(){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {

                AppDetail item = ListViewAdapter.searchedItems.get(pos);
                // Open selected application
                if(item.type.equals("application")) {
                    MainActivity.this.startActivity(manager.getLaunchIntentForPackage(
                            ListViewAdapter.searchedItems.get(pos).packageName.toString()));
                }
                // Call selected person
                else if(item.type.equals("contact")){
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + item.info));
                    startActivity(callIntent);
                }
            }
        });
    }

    /**
     * Utility: To resize app icons
     */
    private Drawable resize(Drawable image) {
        Bitmap b = ((BitmapDrawable)image).getBitmap();
        Bitmap bitmapResized = Bitmap.createScaledBitmap(b, 96, 96, false);
        return new BitmapDrawable(getResources(), bitmapResized);
    }

    /**
     * set device wallpaper as background
     */
    private void setBackground(){

        WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
        final Drawable wallpaperDrawable = wallpaperManager.getDrawable();
        RelativeLayout mainScreen = findViewById(R.id.main_activity);
        mainScreen.setBackground(wallpaperDrawable);
    }
}