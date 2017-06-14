English | [简体中文](./README_CN.md)

# JsBridge
A simpler, extendable bidirectional communication Frame between Android WebView and Javascript

## Features
- supports parsing and callback for JS primitive types
- Modular management
- support System WebView and Custom WebView
- permission authentication is implemented by Native, JS do not need to depend other file
- support Android API 8+, avoid addJavascriptInterface Vulnerability
- compatible with iOS [WebViewJavascriptBridge](https://github.com/marcuswestin/WebViewJavascriptBridge)

## Getting Started
Download [the latest JAR](./jars) or Gradle:

```
compile 'com.apkfuns.jsbridge:jsbridge:2.0.0'
```
The library dependen on `support-annotations` and` fastjson`, if your project already exists, please exclude

```
compile('com.apkfuns.jsbridge:jsbridge:2.0.0') {
    exclude module: 'support-annotations'
    exclude module: 'fastjson'
}
```

## Examples
We use JS to call the original module to achieve `ajax cross-domain request` to briefly introduce the use of the library

#### 1.Create Module
Create a module that needs to inherit `JsModule` and implement the` getModuleName `method, the module naming request is the same as the java variable naming, must not be empty, only allow `underline(_)` `Letters` 和`Number`, if is `static Module` (not contain module name)，need to inherit `JsStaticModule`, the following creates a Native module

 ```java
 public class NativeModule extends JsModule {
     @Override
     public String getModuleName() {
         return "native";
     }
 }
 ```
 
#### 2.Create Native Method
Module inside the creation method requires the use of annotations `@ JSBridgeMethod`, by default Java method name is JS call method name, also specify the name of the calling method by `@JSBridgeMethod (methodName = "xx")`。Method can not be `static` or` abstract`, method can contain the return type, if the return type is the object, default return string to JS, the method parameters for the following types, you can directly map to their corresponding JS type

Java Types | Mapping the JS type
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

for more convenient to call, we define the method parameters as ajax, we look at the ajax request structure

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
Ajax method parameter is a JS object, the object contains type, url, dataType three string parameters, data parameter is an object, success and error is JS callback method, let's define the Java method.

```java
@JSBridgeMethod
public void ajax(JBMap dataMap) {
        String type = dataMap.getString("type");
        String url = dataMap.getString("url");
        JBMap data = dataMap.getJBMap("data");
        JBCallback successCallback = dataMap.getCallback("success");
        JBCallback errorCallback = dataMap.getCallback("error");
        // Omit the request code
        if (request success) {
              successCallback.apply("success");
        } else {
              errorCallback.apply("failure");
        }
}
```
 must add annotations `@JSBridgeMethod`，the parameter is JBMap
 
 `JBCallback.apply` callback JS callback method，variable parameter，support for Java basic types，Array (WritableJBArray)，and Object(WritableJBMap), if for other objects, the default converted to string
     
#### 3.Register Module
There are two ways to register a Module, `Default registration`, `Dynamic registration`

 ```java
JsBridgeConfig.getSetting().registerDefaultModule(NativeModule.class);

// or

JsBridge.loadModule(NativeModule.class)
 ```  
 JsBridgeConfig parameter:
 
Method | Type | Description | Default
----|------ |------|------
setProtocol|string|The name of the object that JS calls| JsBridge
setLoadReadyMethod|string|Load the completion of the callback function| onJsBridgeReady
registerDefaultModule|JsModule|Common module, default load| None
debugMode| bool | In debug mode, the output TAG is the JsBridgeDebug log| false 
 
#### 4.WebView inject method & listen callback
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
                // listen callback
                jsBridge.callJsPrompt(message, result);
                return true;
            }
        });

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // inject JS
                jsBridge.injectJs(view);
            }
        });
    }

    @Override
    protected void onDestroy() {
        // Avoid memory leaks
        jsBridge.release();
        super.onDestroy();
    }
}

 ```
 Now, in JS code can call this method:
 
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
If it is like calling `JsBridge.ajax({...})`, change the parent class from `JsModule` to `JsStaticModule`

There are some important information, because JS execution is asynchronous, in order to ensure that injection JS has been completed, please implement the method in the callback, or judge JsBridge object exists

```javascript
window.onJsBridgeReady = function () {
    JsBridge.native.ajax({...});
}

// or
document.addEventListener('onJsBridgeReady', function(){
    JsBridge.native.ajax({...});
})

// or
if (JsBridge) {
    JsBridge.native.ajax({...});
}

```

For documentation and additional information see [wiki](https://github.com/pengwei1024/JsBridge/wiki) and [sample](./sample)

## Proguard
```
-keep class com.apkfuns.jsbridge.**{*;}
-keep class * extends com.apkfuns.jsbridge.module.JsModule
```

## License
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