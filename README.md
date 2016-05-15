# JsBridge
提供android和javascript双向交互的框架<br/>

### 示例:通过js设置右上角菜单，并将点击事件传回给js处理
<img src='./screenshot/ss01.gif'/>

## 使用方法

#### 导入JSBridge
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
JSBridge.getConfig().setProtocol("MyBridge").setSdkVersion(1).registerModule(ServiceModule.class);
```
方法 | 值 | 描述  
------- | ------- | -------  
setProtocol | string | 使用协议，也是JS调用的对象名,如MyBridge.sdk.getInfo(options);
setSdkVersion | int | 设置sdk版本，用于解决客户端
registerModule | JsModule | 注册js和java交互方法  





