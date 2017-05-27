package com.apkfuns.jsbridge;

import android.text.TextUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by pengwei on 16/5/13.
 */
class JsBridgeConfigImpl implements JsBridgeConfig {

    private Map<JsModule, HashMap<String, JsMethod>> exposedMethods = new HashMap<>();
    private Set<Class<? extends JsMethodRun>> methodRuns = new HashSet<>();
    private String protocol;
    private String readyFuncName;

    private JsBridgeConfigImpl() {
    }

    private static JsBridgeConfigImpl singleton;

    public static JsBridgeConfigImpl getInstance() {
        if (singleton == null) {
            synchronized (JsBridgeConfigImpl.class) {
                if (singleton == null) {
                    singleton = new JsBridgeConfigImpl();
                }
            }
        }
        return singleton;
    }

    public Set<Class<? extends JsMethodRun>> getMethodRuns() {
        return methodRuns;
    }

    @Override
    public JsBridgeConfig registerModule(Class<? extends JsModule>... modules) {
        if (modules != null && modules.length > 0) {
            for (Class<? extends JsModule> moduleCls : modules) {
                try {
                    JsModule module = moduleCls.newInstance();
                    if (module != null) {
                        HashMap<String, JsMethod> methodsMap = Utils.getAllMethod(
                                module.getModuleName(), moduleCls);
                        if (!methodsMap.isEmpty()) {
                            exposedMethods.put(module, methodsMap);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return this;
    }

    @Override
    public JsBridgeConfig registerJsMethod(Class<? extends JsMethodExt>... methods) {
        if (methods != null && methods.length > 0) {
            for (Class<? extends JsMethodExt> cls: methods) {
                try {
                    JsMethodExt ext = cls.newInstance();
                    if (ext == null || TextUtils.isEmpty(ext.getJsMethod())) {
                        throw new NullPointerException("methodName must not be empty");
                    }
                    String module = ext.getModuleName();
                    if (TextUtils.isEmpty(module)) {
                        module = JsBridgeConfig.MODULE_NONE;
                    }
                    if (exposedMethods.containsKey(module) && exposedMethods.get(module) != null) {
                        HashMap<String, JsMethod> maps = exposedMethods.get(module);
                        maps.put(ext.getJsMethod(), ext);
                    } else {
                        HashMap<String, JsMethod> maps = new HashMap<>();
                        maps.put(ext.getJsMethod(), ext);
                        exposedMethods.put(module, maps);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return this;
    }

    @Override
    public JsBridgeConfig registerMethodRun(Class<? extends JsMethodRun>... methodRuns) {
        if (methodRuns != null && methodRuns.length > 0) {
            for (Class<? extends JsMethodRun> run : methodRuns) {
                this.methodRuns.add(run);
            }
        }
        return this;
    }


    public Map<JsModule, HashMap<String, JsMethod>> getExposedMethods() {
        return exposedMethods;
    }

    @Override
    public JsBridgeConfig setProtocol(String protocol) {
        this.protocol = protocol;
        return this;
    }

    public String getProtocol() {
        return TextUtils.isEmpty(protocol) ? DEFAULT_PROTOCOL : protocol;
    }

    @Override
    public JsBridgeConfig setLoadReadyFuncName(String readyName) {
        this.readyFuncName = readyName;
        return this;
    }

    public String getReadyFuncName() {
        if (TextUtils.isEmpty(this.readyFuncName)) {
            return String.format("on%sReady", getProtocol());
        }
        return readyFuncName;
    }
}
