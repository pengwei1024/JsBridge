package com.apkfuns.jsbridgesample.module;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import com.apkfuns.jsbridge.module.JBCallback;
import com.apkfuns.jsbridge.module.JSBridgeMethod;
import com.apkfuns.jsbridge.module.JsModule;
import com.apkfuns.jsbridgesample.view.BaseActivity;

/**
 * Created by pengwei on 16/5/15.
 */
public class NativeModule extends JsModule {
    @Override
    public String getModuleName() {
        return "native";
    }

    @JSBridgeMethod
    public boolean setMenu(String title, final JBCallback callback) {
        if (getContext() != null && getContext() instanceof BaseActivity) {
            ((BaseActivity) getContext()).setMenu(title, new Runnable() {
                @Override
                public void run() {
                    callback.apply(System.currentTimeMillis());
                }
            });
        }
        return false;
    }

    @JSBridgeMethod
    public void alertDialog(String title, String msg, final JBCallback sure, final JBCallback cancel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                .setTitle(title)
                .setMessage(msg)
                .setNegativeButton("Sure", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sure.apply();
                    }
                })
                .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cancel.apply();
                    }
                });
        builder.create().show();
    }

    @JSBridgeMethod
    public void getLocation(final JBCallback callback, final JBCallback change) {
        if (getContext() != null && getContext() instanceof BaseActivity) {
            Location location = ((BaseActivity) getContext()).getLocation(new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    change.apply(location.getLatitude(), location.getLongitude());
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            });
            if (location == null) {
                callback.apply(location.getLatitude(), location.getLongitude());
            } else {
                callback.apply();
            }
        }
    }
}
