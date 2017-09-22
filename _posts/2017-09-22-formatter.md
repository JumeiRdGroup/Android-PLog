---
layout: page
category: "ext"
title: "格式化(Formatter)"
order: 1
---

#### 设计目标

Formatter 库提供标准的格式化操作，目前支持：
* JSON 串和 JSONObject/JSONArray
* 任意 Collection 和 Map
* 任意 单一 对象

#### 使用
> 请注意：使用该扩展需要你添加额外的依赖，参见 [下载和设置][3]。请放心，该扩展是轻量级的。

在加入依赖之后，``DebugPrinter`` 和其他继承 [``AbsPrinter``][4] 的打印器会自动生效；其他自定义的 ``Printer``，则需要你手动设置：
```Java
    @Override
    public Formatter getFormatter() {
        return mFormatter;
    }
```

#### 递归深度
要完整输出一个对象是很困难的事情。 如果只输出基本类型及String，则很多情形下的 POJO 的嵌套引用都没办法格式化，但是完全递归，则会在发生循环引用或单例对象上发生栈溢出和OOM（该问题的 [``原始 Issue``][1] ）。

PLog 对此的解决办法是引入了一个配置项：**递归深度**。 通过限制递归深度，既可以避免上述问题，也可以顺便解决一些递归深度过长导致日志冗余的情况。

#### 原始代码
该扩展库的原始代码在 [这里][2] 。

[1]: {{ site.codeurl }}/issues/30  
[2]: {{ site.codeurl }}/tree/master/plog-formatter  
[3]: {{ site.baseurl }}/doc/download-setup.html  
[4]: {{ site.baseurl }}/javadocs/2.0.0/org/mym/plog/AbsPrinter.html  