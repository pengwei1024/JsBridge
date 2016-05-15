# IOS JSBridge解决方案

鉴于本人对ios也不是特别熟悉，仅仅提供实现思路。如果有`ioser`能实现成更简单调用的框架就更赞了。<br/><br/>
不多说了，基本思路如下:<br/>

#### 注入JS
将下述代码在webViewDidFinishLoad()回调中注入到UIWebView中
```javascript
window.JSBrdige = {
    service: {
        getName: function (opt) {
            // 将参数和回调函数等保存到本地容易获取的对象中
            JSBrdige.service.getNameOption = opt;
            var param = opt && opt.data ? opt.data : '';
            location.href = 'JSBrdige://service:0/getName?' + param;
            // 拦截Url并执行响应的逻辑,利用JSBridge.service.getNameOption执行回调
        }
    }
};
// 注入加载完成后的回调
if (window.onJSBrdigeReady && typeof(window.onJSBrdigeReady) === 'function') {
    setTimeout(window.onJSBrdigeReady(), 100);
}
```
#### 拦截请求并执行操作
在shouldStartLoadWithRequest拦截请求JSBrdige://service:0/getName?param所有请求，并获取参数，执行操作
```swift
func webView(webView: UIWebView, shouldStartLoadWithRequest request: NSURLRequest, navigationType: UIWebViewNavigationType) -> Bool {
        let url:NSURL = request.URL!
        let u:String = String(url.absoluteString)
        print("load Url = " + u)
        if(url.scheme == "jsbridge"){
            if (url.host! == "service") {
                print("path=" + url.path!)
                if (url.path! == "/getName") {
                     // 获取到参数param，执行相应操作
                    let param:String = String(request.URL!.query!)
                     // 执行回调
                    let x = "JSBrdige.service.getNameOption.onSuccess('success')"
                    webView.stringByEvaluatingJavaScriptFromString(x)
                }
            }
            return false
        }
        return true
    }

```

#### 在js中怎么调用?
```javascript
// 确保jsBridge已经注入完成，类似window.onload 网页已经加载完成
window.onJSBrdigeReady = function () {
    JSBrdige.service.getName({
        data: 'apple',
        onSuccess: function (res) {
            alert(res)
        }
    })
}
```
