package com.apkfuns.jsbridge.sample;

public class GeolocationJsMethodListener extends JsMethodListener {
    private static GeolocationJsMethodListener geolocationJsMethodListener = new GeolocationJsMethodListener();

    private GeolocationJsMethodListener() {
    }

    public static GeolocationJsMethodListener get() {
        return geolocationJsMethodListener;
    }

    @Override
    public String getPlatform() {
        return "device";
    }

    @Override
    public String getModule() {
        return "geolocation";
    }

    @Override
    public String getJsMethod() {
        return "geolocationListener";
    }
}

