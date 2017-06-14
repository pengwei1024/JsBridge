package com.apkfuns.jsbridgesample.view;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.apkfuns.jsbridgesample.HiApplication;

import java.util.List;

/**
 * Created by pengwei on 2017/6/11.
 */

public class BaseActivity extends AppCompatActivity {

    private LocationManager mLocationManager;
    private LocationListener locationListener;
    private Menu menu;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        return true;
    }

    public void setMenu(String title, final Runnable r) {
        if (menu != null) {
            menu.clear();
            MenuItem item = menu.add(Menu.NONE, 0, Menu.NONE, title);
            item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    r.run();
                    return true;
                }
            });
            MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
        }
    }

    public Location getLocation(LocationListener listener) {
        locationListener = listener;
        mLocationManager = (LocationManager) HiApplication.getInstance().getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        String locationProvider = null;
        if (providers.contains(LocationManager.GPS_PROVIDER)) {
            //如果是GPS
            locationProvider = LocationManager.GPS_PROVIDER;
        } else if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
            //如果是Network
            locationProvider = LocationManager.NETWORK_PROVIDER;
        }
        Location location = mLocationManager.getLastKnownLocation(locationProvider);
        mLocationManager.requestLocationUpdates(locationProvider, 3000, 1, locationListener);
        return location;
    }

    @Override
    protected void onDestroy() {
        if (mLocationManager != null && locationListener != null) {
            mLocationManager.removeUpdates(locationListener);
        }
        super.onDestroy();
    }
}
