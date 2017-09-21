---
layout: page
category: "doc"
title: "日志请求"
order: 3
---

* TOC
{:toc}

### 链式调用
在 PLog 1.x 版本的实际使用中，方法的过度重载带来了很多问题，其中最麻烦的是不易增加 feature 和难于维护。 因此，PLog 的 2.x 版本决定改为链式调用。 调用链称为一个 ``LogRequest``，它遵循标准的 Builder 模式，可以接受任意简单和复杂的参数。**参数之间没有先后顺序；对同一个参数重复设值，则后设置的值会覆盖先前值。**

此页面用于解释日志请求各参数的含义。 

### 参数列表

#### tag (标签)
指定日志 tag 。请注意：
1. 一般情况下推荐使用自动tag；
2. 使用该方法时传 ``null`` 或 ``""`` 将不生效；
3. 假如在全局 [``设置``][1] 中设置了 [``globalTag``][2] 且 [``forceConcat``][3]，则你设置的 tag 会跟在全局 tag 之后。示例：

| globalTag | forceConcat | LogRequest.tag | Output |
| aaa | false | XXX | XXX |
| aaa | true | XXX | aaa-XXX |
| null | false | XXX | XXX |

#### level (级别)
指定日志等级，只接受标准日志库定义的以下level: 
* VERBOSE
* DEBUG
* INFO
* WARN
* ERROR

如果你在链式调用中没有设置 level ，则它默认为 ``Log.DEBUG``。

#### msg (内容)
指定消息内容，该参数支持标准的 [String Format][4] ，假如你使用了格式符，你需要配合使用 param 参数。

#### param (参数)
如上条所说，假如你传入了 ``msg`` 参数且含有格式化符号，那么你需要使用该方法指定实际参数，请务必保证参数的个数和顺序完全匹配。
```Java
PLog.d("This is a debug log. param is %d, %.2f and %s", 1, 2.413221, "Great");
```

此外，假如你没有调用 ``msg`` 方法，你仍然可以直接向 ``param()`` 方法传入一个任意的对象，此时的输出与直接调用 [``PLog.objects()``][5] 等效，区别在于链式调用时你可以修改它的 level 和 category 等参数。

#### category (分组)
你可以设定该条日志属于哪个日志组。 日志组主要是作为 [``Printer``][6] 筛选日志的一个维度，因此假如你并没有设置过任何特殊的 ``Printer``，可能你不是很需要这个选项。

目前日志组基本都是字符串类型，但是为后期扩展，定义了一个接口 [``Category`][7]。

#### stackOffset (栈帧偏移)
你可以设定日志的堆栈是否要做特定的偏移值，该参数主要影响自动 Tag 和方法行数的结果。

*ProTip: 无法确定你需要的栈在哪一帧？你可以调用 [``PLog.printStackTraceHere()`][8] 以获得帮助。*

[1]: {{ site.baseurl }}/doc/cofiguration.html  
[2]: {{ site.baseurl }}/doc/configuration.html#globalTag  
[3]: {{ site.baseurl }}/doc/configuration.html#forceConcat  
[4]: https://developer.android.com/reference/java/util/Formatter.html  
[5]: {{ site.baseurl }}/javadocs/2.0.0/org/mym/plog/PLog.html#objects  
[6]: {{ site.baseurl }}/doc/printer.html  
[7]: {{ site.baseurl }}/javadocs/2.0.0/org/mym/plog/Category.html  
[8]: {{ site.baseurl }}/javadocs/2.0.0/org/mym/plog/PLog.html#printStackTraceHere