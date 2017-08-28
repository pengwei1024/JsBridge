package com.apkfuns.jsbridgesample.view.fragment;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JsPromptResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.apkfuns.jsbridge.JsBridge;
import com.apkfuns.jsbridgesample.HiApplication;
import com.apkfuns.jsbridgesample.util.TakePhotoResult;
import com.apkfuns.jsbridgesample.util.WebEvent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;

/**
 * Created by pengwei on 2017/8/14.
 */

public class CustomFragment extends Fragment implements WebEvent {
    private WebView webView;
    private JsBridge jsBridge;
    private TakePhotoResult result;
    private Uri imageUri;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        webView = new WebView(getContext());
        return webView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initWebView();
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void initWebView() {
        jsBridge = JsBridge.loadModule();
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("file:///android_asset/fragment.html");
        WebView.setWebContentsDebuggingEnabled(true);
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
                if (jsBridge.callJsPrompt(message, result)) {
                    return true;
                }
                return super.onJsPrompt(view, url, message, defaultValue, result);
            }
        });
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                jsBridge.injectJs(view);
            }
        });
    }

    @Override
    public void takePhoto(TakePhotoResult result) {
        this.result = result;
        File outputImage = new File(HiApplication.getInstance().getExternalCacheDir(), "output_image.jpg");
        if (outputImage.exists()) {
            outputImage.delete();
        }
        try {
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        imageUri = Uri.fromFile(outputImage);
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, 0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Bitmap photo = null;
            try {
                photo = BitmapFactory.decodeStream(getContext().getContentResolver().openInputStream(imageUri));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            if (result != null) {
                result.onSuccess(photo);
            }
        } else {
            if (result != null) {
                result.onFailure("user cancel");
            }
        }
    }
}
