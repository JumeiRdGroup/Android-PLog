---
layout: page
category: "doc"
title: "基本使用"
order: 2
---

* TOC
{:toc}

### 基本使用

在正式打印日志之前，你需要确保你使用了正确的 [Printer][1]. 

#### 输出空消息
所谓"空"消息，指的是你不需要关注它的内容，只需要在这个地方有一行日志输出即可。 空消息在 PLog 中被单列为一个方法，你可以在任何地方直接调用：
```Java
PLog.empty();
```
空消息会以特定的级别和内容输出，你可以在 [设置][5] 中修改它们。

#### 输出普通消息
基础打印方法有一组共五个签名类似的方法：
```Java
v(String msg, Object... params)
d(String msg, Object... params)
i(String msg, Object... params)
w(String msg, Object... params)
e(String msg, Object... params)
```

其中， msg 参数支持标准的 [String Format][2]，你可以在之后跟上对应个数的实际参数：

```Java
PLog.v("This is a verbose log.");
PLog.d("This is a debug log. param is %d, %.2f and %s", 1, 2.413221, "Great");
PLog.i("This is an info log.");
PLog.w("This is a warn log.");
PLog.e("This is an error log.");
```

#### 打印任意对象
有时候你可能不需要任何额外的消息封装，比如单纯打印一个对象的值，或者一串 JSON ：
```Java
PLog.objects(obj); //obj can be any type

PLog.objects("{key:"value", another:"Another"}");
```

### 链式调用
PLog v2 允许你使用链式调用语法来创建任意复杂的日志需求。 你可以通过任意一种方式创建一个 [``LogRequest``][3] ，然后调用其  ``print()`` 方法即可：
```Java
//In PLog v2, auto tag is strongly recommended;
//If you really need specify tag somewhere, you should use builder:
new LogRequest()
        .tag("InfoTag")
        .level(Log.INFO)
        .msg("This is an info log using specified tag.")
        //DO NOT FORGET TO CALL PRINT!
        .print();
```
关于链式请求上所有参数的含义，请参阅 [LogRequest][4]。


[1]: {{ site.baseurl }}/doc/printer.html  
[2]: https://developer.android.com/reference/java/util/Formatter.html  
[3]: {{ site.baseurl }}/javadocs/2.0.0/org/mym/plog/LogRequest.html
[4]: {{ site.baseurl }}/doc/log-request.html
[5]: {{ site.baseurl }}/doc/configuration.html