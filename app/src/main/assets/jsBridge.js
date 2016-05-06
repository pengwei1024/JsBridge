/**
 * Created by pengwei on 16/5/6.
 */

(function (win) {
    var hasOwnProperty = Object.prototype.hasOwnProperty;
    var JSBridge = win.JSBridge || (win.JSBridge = {});
    var PROTOCOL = 'JSBridge';
    var timeoutSec = 8000;
    var Inner = {
        callbacks: {},
        timers: {},
        call: function (obj, method, params, callback) {
            var port = Util.getPort();
            this.callbacks[port] = callback;
            var params = params;
            var uri = Util.getUri(obj, method, params, port);
            var timeout = arguments[4];
            if (!timeout) {
                timeout = timeoutSec;
            }
            if (timeout > 0) {
                this.timers[port] = setTimeout(function () {
                    JSBridge.onFailure(port, "timeout")
                }, timeout);
            }
            if (/(iPhone|iPad|iPod|iOS)/i.test(navigator.userAgent)) {
                window.location.href = uri;
            } else if (/(Android)/i.test(navigator.userAgent)) {
                window.prompt(uri, "");
            } else {
                JSBridge.onFailure(port, "platform error.");
            }
        },
        onFinish: function (port, jsonObj) {
            var result = {'success': true, data: jsonObj};
            var callback = this.callbacks[port];
            callback && callback(result);
            delete this.callbacks[port];
            clearTimeout(this.timers[port])
            delete this.timers[port];
        },
        onFailure: function (port, jsonObj) {
            var result = {'success': false, data: jsonObj};
            var callback = this.callbacks[port];
            callback && callback(result);
            delete this.callbacks[port];
            clearTimeout(this.timers[port])
            delete this.timers[port];
        }
    };
    var Util = {
        getPort: function () {
            return Math.floor(Math.random() * (1 << 30));
        },
        getUri: function (obj, method, params, port) {
            params = this.getParam(params);
            var uri = PROTOCOL + '://' + obj + ':' + port + '/' + method + '?' + params;
            return uri;
        },
        getParam: function (obj) {
            if (obj && typeof obj === 'object') {
                return JSON.stringify(obj);
            } else {
                return obj || '';
            }
        }
    };
    for (var key in Inner) {
        if (!hasOwnProperty.call(JSBridge, key)) {
            JSBridge[key] = Inner[key];
        }
    }
})(window);






