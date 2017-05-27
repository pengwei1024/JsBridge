package com.apkfuns.jsbridge;

/**
 * Created by pengwei on 16/5/13.
 */
class JSBridgeReadyRun implements JsMethodRun {

    JsBridgeConfigImpl config = JsBridgeConfigImpl.getInstance();

    @Override
    public String execJs() {
        StringBuilder builder = new StringBuilder("try{");
        builder.append("var ready = window." + config.getReadyFuncName() + ";");
        builder.append("if(ready && typeof(ready) === 'function'){ready()}");
        builder.append("else {window.addEventListener('load',function(){ready()}, false)}");
        builder.append("}catch(e){console.error(e)};");
        return builder.toString();
    }
}
