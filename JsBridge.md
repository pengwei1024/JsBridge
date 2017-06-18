项目中为了减少端上开发量，通常会使用一些跨平台的解决方案，而 web 就是最简单、兼容性最强的方案，但 web 又受制于浏览器，不能直接访问系统的一些属性，而且我们也需要 web 调用 native 的一些方法，所以我们需要一套 web 和 native 双向交互的方案。

目前，Android 要实现与 web 交互有以下几种常用方案：

* WebView addJavascriptInterface方法
* 拦截自定义协议链接实现数据交换
* 实现 prompt,console等原生方法来数据交互 

`方案一`是官方推荐实现方案，但是在 android 4.2以下存在严重安全漏洞，而且和 JS 交换的数据仅仅局限于基本类型(int,float,double,String 等)，不支持直接 JS 函数调用和回调(需要通过注入 JS 支持), 案例：[wendux/DSBridge-Android](https://github.com/wendux/DSBridge-Android)

`方案二`是兼容 iOS 的方案, 一般情况下前端需要依赖 JS 文件 或者 端上注入 JS, 调用方法固定，方法参数一般为: 函数名, 传递参数和回调函数, 传输数据长度就是 url 长度限制, 不支持同步回调，案例：[lzyzsd / JsBridge](https://github.com/lzyzsd/JsBridge) 和 
 [marcuswestin / WebViewJavascriptBridge](https://github.com/marcuswestin/WebViewJavascriptBridge)
 
 `方案三`是通过实现 Android WebView 原生方法来交互数据, 执行效率高,不限传输数据, 支持同步和异步传输，但也有弊端, 占据了系统函数，意味着前端使用这个函数就没效果了。
 
我们今天要介绍的库就是基于第三种方案的改进，基于 prompt 方法来实现 Android 与 Javascript 双向交互。
开源地址：[https://github.com/pengwei1024/JsBridge](https://github.com/pengwei1024/JsBridge)

为什么说它功能强大呢？它可以实现你要想的任意 JS 方法，支持 JS 函数，对象，数组等所有基础类型的解析和回调。我们先来看个示例吧。![](http://img.t.sinajs.cn/t4/appstyle/expression/ext/normal/5c/huanglianwx_thumb.gif)

如果要实现一个分享功能要怎么做呢？

```java
public class ServiceModule extends JsModule {
    @Override
    public String getModuleName() {
        return "service";
    }

    @JSBridgeMethod
    public void share(String msg, final JBCallback success, final JBCallback failure) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, msg);
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            getContext().startActivity(Intent.createChooser(intent, "share"));
            success.apply("success");
        } else {
            failure.apply("failure");
        }
    }

```

JS 怎么调用呢？

```javascript
JsBridge.service.share('分享内容', 
	function(){
	    console.log('分享成功')
	}, 
	function(){
		console.log('分享失败')
	}
)
```
不用传递函数名, 调用的方法名和原生定义的方法名一致，参数支持所有 JS 类型(对象，数组等), 支持多个回调函数，回调函数参数也可以支持 JS 的所有类型，一看就和其他妖艳xx 不一样 ![](http://img.t.sinajs.cn/t4/appstyle/expression/ext/normal/40/hearta_thumb.gif)

你说 JS 可能没有成功失败的回调？没关系，我们支持缺省参数,下面这样也可以哟，不过原生方法 `share` 收到的 JBCallback 参数就为空咯 

```javascript
JsBridge.service.share('分享内容');
```
数据还不够复杂？实际开发情况数据复杂多了。 ![](http://img.t.sinajs.cn/t4/appstyle/expression/ext/normal/e9/sk_thumb.gif) 那你看下面这样还行吗？JS 数组里基本包含了所有的类型

```javascrip
JsBridge.share.test(
[ - 1111111111111111111, 1.235, 'hello world', true,
function(args) {
    alert(args)
},
{
    a: 100101,
    b: function() {
        alert('执行复杂回调函数')
    }
},
[1, 2, 3, 4]]);
```
原生怎么解析这种情况呢？so easy, 不就一个 JBArray嘛 ![](http://img.t.sinajs.cn/t4/appstyle/expression/ext/normal/37/moren_chongjing_thumb.png)

```java
@JSBridgeMethod
public void test(JBArray array) {
    for (int i = 0; i < array.size(); i++) {
            String output = "" + array.get(i);
            if (array.get(i) != null) {
                output += "##" + array.get(i).getClass();
            }
            Log.d(JsBridge.TAG, output);
     }
     array.getCallback(4).apply("xxx");
     array.getMap(5).getCallback("b").apply();
}
```
轻松回调 JS 数组里面的 function。JS 对象也不怕，JBMap全部搞定。

调用方法只能三个层级？我们项目不需要 module！！就想 JsBridge.test 直接调用。老铁当然没问题啦，只要继承`JsStaticModule`, 里面的方法分分钟变成静态，对象名直接调用。 

```java
public class ServiceModule extends JsStaticModule {
	...
}
```
我们项目要求多个层级呢？

需求还真多，多个层级是需要多少层级呢？4级够了吗？不行再多点？那你自己按需求定制吧！！![](http://img.t.sinajs.cn/t4/appstyle/expression/ext/normal/40/cool_thumb.gif) 

```java
public class ServiceModule extends JsModule {
    @Override
    public String getModuleName() {
        return "a.b.c.d.e.f.g";
    }
}    
```
现在调用路径如下，![](http://img.t.sinajs.cn/t4/appstyle/expression/ext/normal/3c/pcmoren_wu_thumb.png) 我不敢保证 FE 看到这个方法会不会拿刀去找你

```javascript
JsBridge.a.b.c.d.e.f.g.xx(...);
```
iOS 要实现类似功能怎么办？![](http://img.t.sinajs.cn/t4/appstyle/expression/ext/normal/af/kl_thumb.gif)

我们推荐使用 [marcuswestin / WebViewJavascriptBridge](https://github.com/marcuswestin/WebViewJavascriptBridge)，JsBridge 是可以兼容这个库的。怎么兼容呢？iOS 的 `WebViewJavascriptBridge `不主要就是一个方法嘛？

```javascript
bridge.registerHandler("getCurrentPageUrl", function(data,responseCallback) {
	responseCallback(document.location.toString())
})
```
一个静态方法实现足以 ![](http://img.t.sinajs.cn/t4/appstyle/expression/ext/normal/2c/moren_yunbei_thumb.png) 怎么都觉得配合 iOS 这么强大的库就算杀鸡用牛刀了！

介绍完功能，接下来我们从源码层面来介绍下JsBridge实现原理！

在分析之前，我们先来了解几个相关知识点

#### 关键一: WebView 怎么执行 JS

在 API 19+, 可以用系统方法

```javascript
 WebView.evaluateJavascript("alert(1)", null)
```

兼容所有版本的方案:

```javascript
WebView.loadUrl("javascript:alert(1)")
```
有一点需要明确，JS的执行是异步进行的

怎么利用执行 JS 注入一个对象呢？比如我需要一个对象`JsBridge`, 它包含一个 `print()`方法, JS 的语法是这样：

```javasceipt
var JsBridge = {
	print:function(msg){
		console.log(msg);
	}
}
```
WebView 只需要执行这段 JS 就好了

```java
WebView.loadUrl("javascript:var JsBridge = {print:function(msg){console.log(msg);}")
```
在 JS 中就可以直接用`JsBridge.print(1)`来调用这个方法了

#### 关键二: onJsPrompt的参数了解
`onJsPrompt` 是 WebChromeClient 接口的一个回调方法，用来处理`prompt`方法的回调。

```java
@Override
public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
     return true;
}
```
我们再看下 `prompt`的使用, 下面的代码网页中是什么效果呢？

```javascript
prompt("输入你的名字", "张三");
```
![](http://qiniu.apkfuns.com/qiniu-20170618151335-217.png)
prompt 第一个参数是标题，对应`onJsPrompt`方法的 message, 第二个参数是需要输入的内容的默认值, 对应``onJsPrompt`的defaultValue, 那`JsPromptResult`是用来干啥的呢？我们实现同步回调就完全靠它了，用来设置 prompt 方法的返回值，加入我们端上执行`JsPromptResult.confirm("12")`, 那么 prompt(xx) 的返回值就是12

基本的知识点我们都了解了，怎么串联起来上面的两条来实现WebView 和 JS 的双向交互呢？首先我们需要给 JS 提供调用方法, 如最早的`JsBridge.service.share(...)`, 大概你已经知道了要用注入 JS 的方式。

```
var JsBridge = {
	service: {
		share: function(title, success, error) {
			// title 是 string， success 和 error 是 function
		}
	}
}
```

这样，在 JS 就可以执行上面的方法，接下来的问题就是我们怎么把 JS 的数据传递给 Android 呢？prompt 要上场了!

```javascript
var JsBridge = {
	service: {
		share: function(title, success, error) {
			prompt(title)
		}
	}
}
```
这样的话，在`onJsPrompt `的回调里message 就取到了 title。你就要问了，success 和 error 怎么传递给 Android 呢？这两个类型是 function, Android 和 JS 的 变量并不能共享或者相互转换，所以是做不到把 JS 的变量传递到 Android 的！那怎么去解决这个问题呢？

既然不能转换，我们能不能换种思路，在分享执行完成的时候，我们需要执行 success 或者 error 来告诉 JS 分享结果，我们可以执行这个方法来实现呀，和 注入 JS 是一个套路，但是问题是对我们来说 success 和 error 是一个匿名方法(和 Java 的匿名内部类对象相似)，我们并不知道怎么去调用它，这个容易解决呀，把这个函数赋值给一个已知名称的函数，然后我们在 Android 端调用已知名称的函数不就都解决了？我们来看下实现方式！

```javascript
var JsBridge = {
	service: {
		share: function(title, success, error) {
		    JsBridge.service.shareCallback1 = success; 
		    JsBridge.service.shareCallback2 = error; 
			prompt(title)
		}
	}
}
```

Android 端实现

```java
public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
     // 省略分享操作
     view.loadUrl("javascript:JsBridge.service.shareCallback1()");
     return true;
}
```
这样 JS 就可以收到 success 回调了。

以上 Android 和 JS 的双向交互原理都讲明白了，我们来看下`JsBridge`这个库是怎么实现的！就是两步走，注入 JS 和 接受和处理 JS 参数并回调。

先看怎么注入的呢？
JsBridge 需要继承`JsModule`来创建模块，然后模块里对方法添加`JSBridgeMethod` 注解的就是需要注入的方法。Java 方法的参数有一点的要求，和 JS 的参数有一个映射表

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

然后通过`JsBridgeConfig.getSetting().registerDefaultModule(NativeModule.class)` 或者 `JsBridge.loadModule(NativeModule.class)` 来注册 module。

我们来看下注册的代码

```java
private void loadingModule(Class<? extends JsModule>... modules) {
        try {
            for (Class<? extends JsModule> moduleCls : config.getDefaultModule()) {
                loadModule.add(moduleCls.newInstance());
            }
            if (modules != null) {
                for (Class<? extends JsModule> moduleCls : modules) {
                    loadModule.add(moduleCls.newInstance());
                }
            }
            if (!loadModule.isEmpty()) {
                Collections.sort(loadModule, new ModuleComparator());
                for (JsModule module : loadModule) {
                    if (module != null && !TextUtils.isEmpty(module.getModuleName())) {
                        HashMap<String, JsMethod> methodsMap = JBUtils.getAllMethod(
                                module, module.getClass());
                        if (!methodsMap.isEmpty()) {
                            exposedMethods.put(module, methodsMap);
                        }
                    }
                }
            }
        } catch (Exception e) {
            JBLog.e("loadingModule error", e);
        }
    }
```
将 module.class 通过反射实例化对象，并获取这个类里面的所有有效注册方法存在 `Map<JsModule, Map<String, JsMethod>>` 对象里面，注册就完成了

```java
private String getInjectJsString() {
        StringBuilder builder = new StringBuilder();
        builder.append("var " + className + " = function () {");
        // 注入通用方法
        builder.append(JBUtilMethodFactory.getUtilMethods());
        // 注入默认方法
        for (JsModule module : loadModule) {
            if (module == null || TextUtils.isEmpty(module.getModuleName())) {
                continue;
            }
            HashMap<String, JsMethod> methods = exposedMethods.get(module);
            if (module instanceof JsStaticModule) {
                for (String method : methods.keySet()) {
                    JsMethod jsMethod = methods.get(method);
                    builder.append(jsMethod.getInjectJs());
                }
            } else {
                List<String> moduleGroup = JBUtils.moduleSplit(module.getModuleName());
                if (moduleGroup.isEmpty()) {
                    continue;
                } else {
                    for (int i = 0; i < moduleGroup.size() - 1; ++i) {
                        if (!moduleLayers.contains(moduleGroup.get(i))) {
                            for (int k = i; k < moduleGroup.size() - 1; ++k) {
                                builder.append(className + ".prototype." + moduleGroup.get(k) + " = {};");
                                moduleLayers.add(moduleGroup.get(k));
                            }
                            break;
                        }
                    }
                    builder.append(className + ".prototype." + module.getModuleName() + " = {");
                    moduleLayers.add(module.getModuleName());
                }
                for (String method : methods.keySet()) {
                    JsMethod jsMethod = methods.get(method);
                    builder.append(jsMethod.getInjectJs());
                }
                builder.append("};");
            }
        }
        builder.append("};");
        builder.append("window." + config.getProtocol() + " = new " + className + "();");
        builder.append(config.getProtocol() + ".OnJsBridgeReady();");
        return builder.toString();
    }
```
从`Map<JsModule, Map<String, JsMethod>>`对象依次取出 Module, 并划分静态 module 和非静态， 原理就是把需要的 JS 对象和方法注入进去，这里复杂的问题是多级调用层级的实现，如前面提到的`JsBridge.a.b.c.d.e.f.g.xx(...);`, 这也是注册 module 时为什么要对 module 进行排序的原因，这里需要有一定 JS 基础才能看得明白些，就不细讲了，有兴趣的大家去看源码吧。



更多功能和介绍欢迎查看官方教程：[https://github.com/pengwei1024/JsBridge](https://github.com/pengwei1024/JsBridge)