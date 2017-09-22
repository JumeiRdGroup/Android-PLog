---
layout: default
title: "Home"
---

### 关于 Android-PLog
这是一个专注于为 Android 开发者提供的日志库，旨在提供纯粹、高效、强大、灵活的日志功能。

#### 容易打印
* **支持无Tag打印**
* **支持空消息打印（通常用于观察某处是否执行）**
* **支持任意参数类型**
* **支持变长参数和自动格式化**
* **支持Builder方式打印**

#### 消息丰富
* **无 LogCat 4K 长度限制**
* **可打印线程信息**
* **可打印行号信息**(AS LogCat **自动日志定位**)

#### 输出可控
* **支持多维度的输出拦截**
* **支持自定义输出装饰样式**
* **支持多通道同时输出**
* **支持自动换行（SoftWrap）**

#### 筛选容易
* **支持全局Tag**(可以区分不同应用)
* **支持自动Tag**(可以区分不同类名)
* **支持保留堆栈**(可以区分不同文件和方法)
* **支持分组打印**(可以区分不同开发者, etc)

### 如何使用
请按照 [下载和设置][1] 指南页面添加依赖，然后阅读 [基本使用][2] 以熟悉基本用法与链式调用 [日志请求][5] 。

你可以使用 PLog 强大的 [配置选项][3] 来实现大部分需求。对于一些特殊场景，你可能需要阅读 [Printer][4]或其他文档（参见侧边栏）。

[1]: {{ site.baseurl }}/doc/download-setup.html  
[2]: {{ site.baseurl }}/doc/basic-usage.html  
[3]: {{ site.baseurl }}/doc/configuration.html  
[4]: {{ site.baseurl }}/doc/printer.html  
[5]: {{ site.baseurl }}/doc/log-request.html  
