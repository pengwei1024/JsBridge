# JsBridge
a bridge for android webView Interaction with JS(javascript)

## 使用方法

#### 导入JSBridge框架
```
compile 'com.apkfuns.jsbridge:jsbridge:1.1.0'
```

#### 设置prompt监听和注入JS方法
```java
# 设置prompt监听
webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
                result.confirm(JSBridge.callJsPrompt(MainActivity.this, webView, message));
                return true;
            }
});

# 注入JS
webView.setWebViewClient(new WebViewClient() {
             @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                JSBridge.injectJs(webView);
            }
        });
```

#### 设置协议和可调用方法
```java
JSBridge.getConfig().setProtocol("MyJSBridge").setSdkVersion(1).registerModule(ServiceModule.class);
```


