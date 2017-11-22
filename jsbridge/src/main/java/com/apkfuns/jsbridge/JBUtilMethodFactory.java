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
    public static String getUtilMethods(String loadReadyMethod) {
        if (injectFunc == null) {
            JsRunMethod[] methods = new JsRunMethod[]{new GetType(), new ParseFunction(),
                    new OnJsBridgeReady(loadReadyMethod), new CallJava(),
                    new Printer(), new CallMethod()
            };
            injectFunc = new StringBuffer();
            for (JsRunMethod method : methods) {
                injectFunc.append(method.getMethod());
            }
        }
        return injectFunc.toString();
    }

    static class OnJsBridgeReady extends JsRunMethod {
        private String loadReadyMethod;

        public OnJsBridgeReady(String loadReadyMethod) {
            this.loadReadyMethod = loadReadyMethod;
        }

        @Override
        protected String executeJS() {
            StringBuilder builder = new StringBuilder("(){try{");
            builder.append("var ready = window." + loadReadyMethod + ";");
            builder.append("if(ready && typeof(ready) === 'function'){ready()}");
            builder.append("else {var readyEvent = document.createEvent('Events');");
            builder.append("readyEvent.initEvent('" + loadReadyMethod + "');");
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
            return "(obj,name,callback){if(typeof obj==='function'){callback[name]=obj;obj='[Function]::'+name;return}if(typeof obj!=='object'){return}for(var p in obj){switch(typeof obj[p]){case'object':var ret=name?name+'_'+p:p;_parseFunction(obj[p],ret,callback);break;case'function':var ret=name?name+'_'+p:p;callback[ret]=(obj[p]);obj[p]='[Function]::'+ret;break;default:break}}}";
        }

        @Override
        public String methodName() {
            return "_parseFunction";
        }
    }

    static class CallJava extends JsRunMethod {
        @Override
        protected String executeJS() {
            return "(id,module,method,args){var req={id:id,module:module,method:method,parameters:args};return JSON.parse(prompt(JSON.stringify(req)));};";
        }

        @Override
        public String methodName() {
            return "_callJava";
        }
    }

    static class Printer extends JsRunMethod {

        @Override
        protected String executeJS() {
            return "(s){console.error(s)}";
        }

        @Override
        public String methodName() {
            return "d";
        }
    }

    static class CallMethod extends JsRunMethod {

        @Override
        protected String executeJS() {
            StringBuffer buffer = new StringBuffer();
            buffer.append("(callback,methodArg,ret,moduleName,methodName){");
            buffer.append("try{");
            buffer.append("var id=Math.floor(Math.random()*(1 << 10)),args = [];");
            buffer.append("for (var i in methodArg) {");
            buffer.append("var name = id + '_a' + i, item = methodArg[i], l = {};");
            buffer.append("_parseFunction(item, name, l);");
            buffer.append("for (var k in l) {callback[k] = l[k];}");
            buffer.append("args.push({type: _getType(item), name: name, value: item})");
            buffer.append("}");
            buffer.append("var r = _callJava(id, moduleName, methodName, args);");
            buffer.append("if (r && r.success){");
            buffer.append("if(ret)return r.msg");
            buffer.append("}else{");
            buffer.append("d(r.msg)");
            buffer.append("}");
            buffer.append("}catch(e){d(e)}");
            return buffer.append("}").toString();
        }

        @Override
        public String methodName() {
            return "_method";
        }
    }
}
