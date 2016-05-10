/**
 * Created by pengwei on 16/5/6.
 * version : 1.1.0
 * 协议格式: schema://module:port/method?param
 */

(function (win) {
    var hasOwnProperty = Object.prototype.hasOwnProperty;
    var JSBridge = win.JSBridge || (win.JSBridge = {});
    var PROTOCOL = 'JSBridge';
    var timeoutSec = 8000;
    var callbacks = {};
    var timers = {};
    timers.name = {}
    var Core = {
        call: function (module, method, params, callback) {
            var port = Utils.getPort();
            callbacks[port] = callback;
            var params = params;
            var uri = Utils.getUri(module, method, params, port);
            var timeout = arguments[4];
            if (!timeout) {
                timeout = timeoutSec;
            }
            if (timeout > 0) {
                timers[port] = setTimeout(function () {
                    JSBridge.onFailure(port, Utils.getResultMsg(-1, 'timeout', null));
                }, timeout);
            }
            if (Utils.getDevice() === 'ios') {
                window.location.href = uri;
            } else if (Utils.getDevice() === 'android') {
                window.prompt(uri, "");
            } else {
                JSBridge.onFailure(port, Utils.getResultMsg(-1, 'platform error', null));
            }
        },
        get: function (module, method, params) {
            var uri = Utils.getUri(module, method, params, 0);
            if (Utils.getDevice() === 'android') {
                return window.prompt(uri, "");
            } else if (Utils.getDevice() === 'ios') {
                window.location.href = uri;
                return localStorage.getItem(uri);
            }
            return null;
        },
        onFinish: function (port, jsonObj) {
            var result = Utils.getResultMsg(0, '', jsonObj);
            var callback = callbacks[port];
            callback && callback(result);
            onComplete(port);
        },
        onFailure: function (port, jsonObj) {
            var result = Utils.getResultMsg(101, jsonObj, null);
            if (jsonObj && jsonObj.errorCode !== undefined && jsonObj.errorMsg != undefined
                && jsonObj.data !== undefined) {
                result = jsonObj;
            }
            var callback = callbacks[port];
            callback && callback(result);
            onComplete(port);
        }
    };
    var onComplete = function (port) {
        delete callbacks[port];
        clearTimeout(timers[port]);
        delete timers[port];
    };
    var Utils = {
        getPort: function () {
            return Math.floor(Math.random() * (1 << 30));
        },
        getUri: function (module, method, params, port) {
            params = this.getParam(params);
            var uri = PROTOCOL + '://' + module + ':' + port + '/' + method + '?' + params;
            return uri;
        },
        getParam: function (obj) {
            if (obj && typeof obj === 'object') {
                return JSON.stringify(obj);
            } else {
                return obj || '';
            }
        },
        getDevice: function () {
            if (/(iPhone|iPad|iPod|iOS)/i.test(navigator.userAgent)) {
                return 'ios';
            } else if (/(Android)/i.test(navigator.userAgent)) {
                return 'android';
            }
            return "other";
        },
        getResultMsg: function (errorCode, errorMsg, data) {
            return {'errorCode': errorCode, 'errorMsg': errorMsg, data: data};
        }
    };
    for (var key in Core) {
        if (!hasOwnProperty.call(JSBridge, key)) {
            JSBridge[key] = Core[key];
        }
    }
})(window);



