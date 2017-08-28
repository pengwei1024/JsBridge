package com.apkfuns.jsbridgesample.module;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Toast;

import com.apkfuns.jsbridge.module.JBCallback;
import com.apkfuns.jsbridge.module.JSBridgeMethod;
import com.apkfuns.jsbridge.module.JsModule;
import com.apkfuns.jsbridgesample.R;
import com.apkfuns.jsbridgesample.view.CustomFragmentActivity;
import com.apkfuns.jsbridgesample.view.base.BaseActivity;
import com.apkfuns.jsbridgesample.util.TakePhotoResult;
import com.apkfuns.jsbridgesample.util.WebEvent;
import com.apkfuns.jsbridgesample.view.WebViewActivity;
import com.apkfuns.jsbridgesample.view.fragment.CustomFragment;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

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
            if (location != null) {
                callback.apply(location.getLatitude(), location.getLongitude());
            } else {
                callback.apply();
            }
        }
    }

    @JSBridgeMethod
    public void loadNewPage(String url) {
        Intent it = new Intent(getContext(), WebViewActivity.class);
        it.putExtra("url", url);
        getContext().startActivity(it);
    }

    @JSBridgeMethod
    public void takePhoto(final JBCallback success, final JBCallback errorCallback) {
        if (getContext() != null) {
            WebEvent webEvent = null;
            if (getContext() instanceof WebEvent) {
                webEvent = (WebEvent) getContext();
            } else if (getContext() instanceof CustomFragmentActivity) {
                webEvent = (WebEvent) ((CustomFragmentActivity) getContext())
                        .getSupportFragmentManager().findFragmentByTag(CustomFragmentActivity.TAG);
            }
            if (webEvent == null) {
                return;
            }
            webEvent.takePhoto(new TakePhotoResult() {
                @Override
                public void onSuccess(Bitmap bitmap) {
                    if (bitmap != null) {
                        if (success != null) {
                            String base64 = bitmapToBase64(bitmap);
                            base64 = "data:image/png;base64," + base64;
                            Toast.makeText(mContext, "length=" + base64.length(), Toast.LENGTH_SHORT).show();
                            success.apply(base64);
                        }
                    } else {
                        if (errorCallback != null) {
                            errorCallback.apply("data error");
                        }
                    }
                }

                @Override
                public void onFailure(String error) {
                    if (errorCallback != null) {
                        errorCallback.apply(error);
                    }
                }
            });
        }
    }

    /**
     * 压缩并 转 base64
     * @param bitmap
     * @return
     */
    public static String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        int quality = 10;
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, bos);
        byte[] bytes = bos.toByteArray();
        bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        String result = null;
        try {
            if (bitmap != null) {
                bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                bos.flush();
                bos.close();
                byte[] bitmapBytes = bos.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bos != null) {
                    bos.flush();
                    bos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
