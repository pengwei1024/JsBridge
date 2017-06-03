package com.apkfuns.jsbridge;

/**
 * Created by pengwei on 2017/5/31.
 */

class JBUtilMethodFactory {
    private static StringBuffer injectFunc;

    /**
     * 注入工具类
     *
     * @return
     */
    public static String getUtilMethods() {
        if (injectFunc == null) {
            injectFunc = new StringBuffer();
            injectFunc.append(new GetType().getMethod());
            injectFunc.append(new ParseFunction().getMethod());
            injectFunc.append(new OnJsBridgeReady().getMethod());
        }
        return injectFunc.toString();
    }

    static class OnJsBridgeReady extends JsRunMethod {
        JsBridgeConfigImpl config = JsBridgeConfigImpl.getInstance();

        @Override
        protected String executeJS() {
            StringBuilder builder = new StringBuilder("(){try{");
            builder.append("var ready = window." + config.getReadyFuncName() + ";");
            builder.append("if(ready && typeof(ready) === 'function'){ready()}");
            builder.append("else {var readyEvent = document.createEvent('Events');");
            builder.append("readyEvent.initEvent('" + config.getReadyFuncName() + "');");
            builder.append("document.dispatchEvent(readyEvent);");
            builder.append("}");
            builder.append("}catch(e){console.error(e)};}");
            return builder.toString();
        }

        @Override
        protected boolean isPrivate() {
            return false;
        }

        @Override
        public String methodName() {
            return "OnJsBridgeReady";
        }
    }

    static class GetType extends JsRunMethod {
        @Override
        protected String executeJS() {
            return "(args){var type=0;if(typeof args==='string'){type=1}else if(typeof args==='number'){type=2}else if(typeof args==='boolean'){type=3}else if(typeof args==='function'){type=4}else if(args instanceof Array){type=6}else if(typeof args==='object'){type=5}return type}";
        }

        @Override
        public String methodName() {
            return "_getType";
        }
    }

    static class ParseFunction extends JsRunMethod {
        @Override
        protected String executeJS() {
            return "(obj,name,callback){if(typeof obj==='function'){callback[name]=obj;obj='[Function]::'+name;return}if(typeof obj!=='object'){return}for(var p in obj){switch(typeof obj[p]){case'object':var ret=name?name+'_'+p:p;parseFunction(obj[p],ret,callback);break;case'function':var ret=name?name+'_'+p:p;callback[ret]=(obj[p]);obj[p]='[Function]::'+ret;break;default:break}}}";
        }

        @Override
        public String methodName() {
            return "_parseFunction";
        }
    }
}
