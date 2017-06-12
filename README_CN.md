简体中文 | [English](./README.md)

# JsBridge
轻量可扩展 Android WebView 和 Javascript 双向交互框架

### Features
- 支持 JS 基本类型的解析和回调
- 模块化管理
- 支持系统 WebView 和 自定义 WebView
- 权限鉴定由端上实现, H5 端不需要依赖 js 文件
- 支持 Android API 8+, 避免 addJavascriptInterface 4.2以下漏洞
- 兼容 iOS [WebViewJavascriptBridge](https://github.com/marcuswestin/WebViewJavascriptBridge) 库

### Getting Started
下载 [the latest JAR](#) 或 Gradle依赖:

```
compile 'com.apkfuns.jsbridge:jsbridge:2.0.0'
```

### Examples
我们以 JS 调用原生模块来实现 `ajax 跨域请求`来简单介绍下库的使用

##### 1.创建模块
创建模块需要继承 `JsModule` 并实现`getModuleName `方法, 模块命名要求和 java 变量命名一致, 不允许为空, 只允许 `下划线(_)` `字母` 和`数字`, 如果是`静态模块`(不包含模块名)，需要继承`JsStaticModule`, 下面创建了一个 Native 模块

 ```java
 public class NativeModule extends JsModule {
     @Override
     public String getModuleName() {
         return "native";
     }
 }
 ```
 
##### 2.创建调用方法
Module 里面创建方法需要使用注解`@JSBridgeMethod`, 默认情况下 Java 方法名就是 JS 调用的方法名, 也可以通过`@JSBridgeMethod(methodName = "xx")`来指定调用方法名。方法要求不能为 `static` 或者 `abstract`, 可以包含返回类型，如果返回类型是对象默认转为 String 后返回给 JS, 方法的参数要求为以下类型，可以直接映射到它们对应的JS类型

Java 类型 | 映射的 JS 类型
----|------
Boolean / boolean | Bool  
Integer/ int | Number
Float / float | Number
Double / double | Number
Long / long | Number
String | String
JBCallback | function
JBMap | Object
JBArray | Array

为了 JS 更方便调用, 我们把方法参数定义成ajax一样, 我们先看下 ajax 请求结构

```javascript
$.ajax({
	type:'GET',
	url:'xxx.com',
	dataType:'text'
	data:{a:1, b:'xx'},
	success:function(data){
	},
	error:function(err){
	}
})
```
ajax 方法参数是一个 JS 对象, 对象包含type、url、dataType 三个string 参数，data 参数也是一个对象，success 和 error 是 JS 回调方法，下面我们来定义 Java 方法。

```java
@JSBridgeMethod
public void ajax(JBMap dataMap) {
        String type = dataMap.getString("type");
        String url = dataMap.getString("url");
        JBMap data = dataMap.getJBMap("data");
        JBCallback successCallback = dataMap.getCallback("success");
        JBCallback errorCallback = dataMap.getCallback("error");
        // 省略请求代码
        if (请求成功) {
        	  successCallback.apply(返回数据);
        } else {
        	  errorCallback.apply(错误);
        }
}
```
 必须要添加`@JSBridgeMethod`注解，参数是JBMap 映射到 JS 的对象
 
 `JBCallback.apply`用来回调 JS 的回调方法，不定参数，支持 Java 基本类型，数组(WritableJBArray)，和 对象(WritableJBMap), 如果为其他对象，默认转为 string转递给 JS
 
     
##### 3.注册 Module
有两种方式可以注册 module, `默认注册`, `动态注册`

 ```java
JsBridgeConfig.getSetting().registerDefaultModule(NativeModule.class);

// 或者

JsBridge.loadModule(NativeModule.class)
 ```  
 JsBridgeConfig 配置下如下:
 
方法 | 类型 | 描述 | 默认
----|------ |------|------
setProtocol|string|使用的协议，也是JS调用的对象名| JsBridge
setLoadReadyMethod|string|加载完成回调| onJsBridgeReady
registerDefaultModule|JsModule|公用模块|无
debugMode| bool | 是否调试,输入 TAG 为JsBridgeDebug 日志| false 
 
##### 4.WebView 注入方法 & 设置回调
 ```java
 public class WebViewActivity extends BaseActivity {
    private JsBridge jsBridge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
		  ...
        jsBridge = JsBridge.loadModule();
        ...
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
                // 监听回调
                jsBridge.callJsPrompt(message, result);
                return true;
            }
        });

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // 注入 JS
                jsBridge.injectJs(view);
            }
        });
    }

    @Override
    protected void onDestroy() {
        // 避免内存泄露
        jsBridge.release();
        super.onDestroy();
    }
}

 ```
 现在，在 JS 代码中可以这样调用这个方法：
 
```javascript
JsBridge.native.ajax({
	type:'GET',
	url:'xxx.com',
	dataType:'text'
	data:{a:1, b:'xx'},
	success:function(data){
	},
	error:function(err){
	}
})
```
如果想直接JsBridge.ajax, 把父类从`JsModule` 改成 `JsStaticModule`就好了

还有一个问题值得注意，因为 JS 执行是异步的，为了确保注入 JS 已经完成，请在回调里执行方法，或者判断 JsBridge 对象存在

```javascript
window.onJsBridgeReady = function () {
    JsBridge.native.ajax({...});
}

或者
document.addEventListener('onJsBridgeReady', function(){
    JsBridge.native.ajax({...});
})

或者
if (JsBridge) {
	JsBridge.native.ajax({...});
}

```

更多的文档和示例请参考 [wiki](#) 和 [sample](./sample)

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