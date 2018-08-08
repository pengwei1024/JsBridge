#指定代码的压缩级别
-optimizationpasses 5

#包明不混合大小写
-dontusemixedcaseclassnames

#不去忽略非公共的库类
-dontskipnonpubliclibraryclasses

 #优化  不优化输入的类文件
-dontoptimize

 #预校验
-dontpreverify

 #混淆时是否记录日志
-verbose

 # 混淆时所采用的算法
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
-keepattributes Signature,Deprecated,*Annotation*,EnclosingMethod,Exceptions
-keepattributes SourceFile,LineNumberTable,InnerClasses,*JavascriptInterface*

-keep public class com.apkfuns.jsbridge.**{*;}
-keep class * extends com.apkfuns.jsbridge.module.JsModule{*;}





