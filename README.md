# JsBridge
android WebView和Javascript双向交互的框架,更简单的Hybrid实现方案。[ios实现方案请看这里](./doc/IOS_SOLUTION.md)

### 优势
* 接入简单、开发简单、调用简单
* 支持同步异步回调，直接注入JS对象，无需考虑平台，调用简单
* 避免WebView addJavascriptInterface漏洞，兼容API 8+

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

#### android实现注册右上角菜单
继承JsModule类, 所谓module，就是提供一个划分模块的功能，不通的功能放在不同的模块实现更方便的管理
```java
public class NativeModule implements JsModule {
    @Override
    public String getModuleName() {
        return "native";
    }

    public static void setNavMenu(MainActivity activity, String options, final JSCallback callback) {
        activity.addMenu(options, new Runnable() {
            @Override
            public void run() {
                callback.apply(options);
            }
        });
    }
}
```
MainActivity中addMenu()实现
```java
public class MainActivity extends ActionBarActivity {
    private Menu menu;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        return true;
    }
    /**
     * 添加右上角菜单
     * @param title
     * @param r
     */
    public void addMenu(String title, final Runnable r) {
        if (menu != null) {
            menu.clear();
            MenuItem item = menu.add(Menu.NONE, 0, Menu.NONE, title).setIcon(R.mipmap.ic_launcher);
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
}

```

#### 设置协议和可调用方法
```java
JSBridge.getConfig().setProtocol("MyBridge").setSdkVersion(1).registerModule(NativeModule.class);
```
方法 | 值 | 描述  
------- | ------- | -------  
setProtocol | string | 使用协议，也是JS调用的对象名,如MyBridge.sdk.getInfo(options);
setSdkVersion | int | 设置sdk版本，用于解决客户端不断迭代导致早起版本方法不存在的问题
registerModule | JsModule | 注册js和java交互方法  
registerMethodRun | JsMethodRun | 注册可直接运行的js脚本

这样android端就完成注入了，网页端怎么访问呢？
```javascript
MyBridge.native.setNavMenu({
        data: '我是来自网页的按钮', onListener: function (res) {
            alert('点击按钮了，数据是:' + res)
        }
    });
```

### 历史版本
[查看历史版本](./doc/HISTORY.md)

### 建议 & 反馈
该框架尚且存在很多不足之处，如有建议或者反馈请提[issue](https://github.com/pengwei1024/JsBridge/issues)或者邮件[pengwei1024@gmail.com](http://mail.qq.com/cgi-bin/qm_share?t=qm_mailme&email=pengwei1024@gmail.com)


### 致谢
* <a target='_blank' href='http://blog.csdn.net/sbsujjbcy/article/details/50752595'>http://blog.csdn.net/sbsujjbcy/article/details/50752595</a>

### License
<pre>
Copyright pengwei1024

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
</pre>
