---
layout: page
category: "doc"
title: "配置选项"
order: 4
---

* TOC
{:toc}

PLog 定义了大量的配置项，以允许你尽可能在不修改代码的情况下扩展需求。

### 装饰性配置选项
有一些配置项是修改日志内容，使其更方便筛选和定位的，包括：
* 自动标签
* 全局标签
* 强制拼接全局标签
* 行号信息
* 线程信息

#### autoTag (自动标签)
PLog 可以智能推断日志的标签，为你每行日志自动设置代码所在的类名为标签。 该选项默认为开启，如果你确信不需要这个特性，你可以手动关闭它。 

某些情况下自动标签得到的值可能不是你想要的，这时候你需要检查你的栈帧偏移是否设置正确。

#### GlobalTag (全局标签)
全局标签可以让你很容易区分你的应用日志与其他应用日志，你可以直接在 LogCat 中设置一个过滤器。

理论上你可以设置任何非空的字符串作为全局标签；从设计初衷出发，我们建议你使用你的应用名称作为全局标签，以具有较高的辨识度和可读性。 

#### forceConcatGlobalTag (强制拼接)
假如你设置了全局标签，但是又想保留自动标签功能，或者你的日志显式指定了某个标签，怎么办呢？将这个标记设置为 `true` 即可将两个标签拼接输出，这样可以兼顾全局标签和自动标签的筛选功能。 该选项默认关闭，但我们强烈推荐在设置全局标签时一并开启。

#### needLineNumber (行号信息)
使用该选项可以输出日志所在行，方便定位。输出格式使用的是标准格式，确保大部分情况下在 Android Studio 中都能直接点击链接跳转。

该选项默认关闭，需要手动开启。

#### needThreadInfo (线程信息)
使用该选项可以直接输出当前线程的信息，目前是采用的标准 toString() 输出，可以看到线程名、线程组、优先级信息。

该选项默认关闭，需要手动开启。

#### 示例

下面是一个完整的日志输出：
```Text
D/Playground-MainActivity: [(MainActivity.java:58):onCreate] [Thread[main,5,main]] Here executed.
```
其中，`Playground` 是全局标签，`MainActivity`是自动标签， `[(MainActivity.java:58):onCreate]`表日志行所在行号信息， `[Thread[main,5,main]]`是线程信息。

### 控制性配置选项
另外还有一些配置项与日志内容无关，它们主要做控制方面的工作，包括：
* globalInterceptor
* Printer
* Formatter

#### globalInterceptor
你可以设置最多一个全局拦截器。在每次日志请求到来时，该拦截器可以决定原始日志能否被打印，假如它的 ``onIntercept`` 方法返回 `true`， 所有的 `printer` 都不会收到这次日志的调用：
```Java
    @Override
	public boolean onIntercept(int level, String tag, Category category, String msg){
	    return true;
	}
```

#### Printer 
参见 [Printer][2] 页面。

#### Formatter
你可以自定义要使用的格式化工具。

你也可以直接使用 PLog 实现的标准格式化工具（需要添加 `formatter` 依赖，参见 [下载和设置][4]）：
```Java
config.setFormatter(new DefaultFormatter());
```

#### maxRecursiveDepth
该选项仅在使用 [Formatter扩展][5] 时生效，关于该选项的更多介绍请参见 [递归深度][6] 。

该选项默认值为 ``2``.

### 常见问题

#### 零配置启动
在成功 [下载依赖][1] 后，你可以直接调用 PLog 的任意API，但在第一行日志之前你可能会看到类似这样的一行警告（为突出起见，实际上用的是ERROR级别）：
> E/Playground-LogEngine: [(LogEngine.java:60):handleLogRequest] [Thread[main,5,main]] No printer prepared, did you forgot it?

这个警告是为了提醒开发者不要忘记定义自己的 [Printer][2]。 当你看到这行日志以后，PLog 会自动创建一个默认的 [``DebugPrinter``][3]，你的所有日志都将不加过滤地直接打印到控制台(LogCat)中。如果这就是你想要的（即，不加过滤或开关，也不需要额外输出到某些媒介），那么你可以忽略这个警告。

#### 重复配置与顺序
如果必要，你可以反复地调用 [``PLog.init()``][4]， 但是只有最后一个配置会生效。

``init()`` 方法与 ``prepared()`` 方法并没有固定的先后顺序。但我们推荐先初始化配置项再准备打印机。

[1]: {{ site.baseurl }}/doc/download-setup.html  
[2]: {{ site.baseurl }}/doc/printer.html  
[3]: {{ site.baseurl }}/javadocs/2.0.0/org/mym/plog/DebugPrinter.html  
[4]: {{ site.baseurl }}/javadocs/2.0.0/org/mym/plog/PLog.html#init  
[5]: {{ site.baseurl }}/ext/formatter.html  
[6]: {{ site.baseurl }}/ext/formatter.html#递归深度