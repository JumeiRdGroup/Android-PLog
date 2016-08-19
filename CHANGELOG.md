#### Version 1.2.0 *(2016-08-19)*
* New: unlimited length log and max length per line support.
* New: convenient method for object params only.
* New: support auto tag *(Disabled by default)*.


* 无限长字符串支持和单行日志长度限制支持, 解决logcat 4K日志长度截断问题。
* 优化无msg打印对象的行为。
* 支持自动设置tag(默认关闭).

#### Version 1.1.0 *(2016-08-11)*
* New: support log stack offset. This is useful when print log in meaningless methods
(typically for code reuse), but often unnecessary.
* New: add a static `newBuilder()` method in `PLogConfig` class.


* 支持日志栈偏移,可以用于跳过指定层级的方法,用于一些特殊场景。
* 新增`newBuilder`方法。

#### Version 1.0.0 *(2016-07-18)*
Initial release.