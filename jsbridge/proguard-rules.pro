# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/baidu/android/android-sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-keep public class com.apkfuns.jsbridge.JBUtils{*;}
-keep public class com.apkfuns.jsbridge.JsBridge{*;}
-keep public class com.apkfuns.jsbridge.JsBridgeConfig{*;}
-keep public class com.apkfuns.jsbridge.module.JBArray{*;}
-keep public class com.apkfuns.jsbridge.module.JBMap{*;}
-keep public class com.apkfuns.jsbridge.module.JBCallback{*;}
-keep public class com.apkfuns.jsbridge.module.JSArgumentType{*;}
-keep public class com.apkfuns.jsbridge.module.JSBridgeMethod{*;}
-keep public class com.apkfuns.jsbridge.module.JsModule{*;}
-keep public class com.apkfuns.jsbridge.module.JsStaticModule{*;}
-keep public class com.apkfuns.jsbridge.module.WritableJBArray{*;}
-keep public class com.apkfuns.jsbridge.module.WritableJBMap{*;}
-keep public class com.apkfuns.jsbridge.common.**{*;}

-keep public class com.apkfuns.jsbridge.**{*;}
-keep class * extends com.apkfuns.jsbridge.module.JsStaticModule
-keep class * extends com.apkfuns.jsbridge.module.JsModule





