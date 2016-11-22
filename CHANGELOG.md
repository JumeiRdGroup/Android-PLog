#### Version 1.7.0 *(Coming soon)*
**File logger is available now!**
Fixed #19.

#### Version 1.6.0 *(2016-10-21)*
* Empty method with stack offset param #14
* Enhanced object format for List #15
* Configurable timing logger #16

#### Version 1.5.0 *(2016-09-13)*
Some bug fixes. (#11, #12, #13)

#### Version 1.5.0 *(2016-09-09)*
* **Refactored library! more extensible and flexible.**
* **NEW:JSON Support**
* Fix a redundant space when formatting a normal object.

#### Version 1.4.0 *(2016-09-08)*
* Improved soft wrap line rule.
* Timing log is printed as single log now (#8)
* **NEW**:`globalStackOffset` setting is provided to support wrapper usage(#7)

#### Version 1.3.0 *(2016-09-07)*
* **NEW** sample app redesigned (#3)
* **NEW**: TimingLogger support (#4)
* Better Objects and **Throwable support**(#2， #5)
* Better wrap line strategy (#6)
* Better support for inner classes (#1)


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