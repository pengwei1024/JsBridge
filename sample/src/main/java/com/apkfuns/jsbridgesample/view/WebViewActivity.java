package com.apkfuns.jsbridgesample.view;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.JsPromptResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.apkfuns.jsbridge.JsBridge;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;


/**
 * Created by pengwei on 2017/6/11.
 */

public class WebViewActivity extends BaseActivity implements WebEvent{
    private JsBridge jsBridge;
    private TakePhotoResult result;
    private Uri imageUri;

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("System WebView");
        jsBridge = JsBridge.loadModule();
        WebView webView = new WebView(this);
        setContentView(webView);
        webView.getSettings().setJavaScriptEnabled(true);
        WebView.setWebContentsDebuggingEnabled(true);
        String url = getIntent().getStringExtra("url");
        if (TextUtils.isEmpty(url)) {
            url = "file:///android_asset/index.html";
        }
        webView.loadUrl(url);
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
                if (jsBridge.callJsPrompt(message, result)) {
                    return true;
                }
                return super.onJsPrompt(view, url, message, defaultValue, result);
            }

            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                Log.d(JsBridge.TAG, consoleMessage.message());
                return true;
            }
        });

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.d(JsBridge.TAG, "start load JsBridge");
                jsBridge.injectJs(view);
            }
        });
    }

    @Override
    protected void onDestroy() {
        jsBridge.release();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Bitmap photo = null;
            try {
                photo = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
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

    @Override
    public void takePhoto(TakePhotoResult result) {
        this.result = result;
        File outputImage = new File(getExternalCacheDir(), "output_image.jpg");
        if (outputImage.exists()) {
            outputImage.delete();
        }
        try {
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT >= 24) {
            imageUri = FileProvider.getUriForFile(this,
                    "com.gyq.cameraalbumtest.fileprovider", outputImage);
        } else {
            imageUri = Uri.fromFile(outputImage);
        }
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, 0);
    }
}
