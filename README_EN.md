#Android-PLog  [![Build Status](https://travis-ci.org/Muyangmin/Android-PLog.svg?branch=master)](https://travis-ci.org/Muyangmin/Android-PLog)[ ![Download](https://api.bintray.com/packages/muyangmin/org.mym/Android-PLog/images/download.svg) ](https://bintray.com/muyangmin/org.mym/Android-PLog/_latestVersion)

#### [中文版文档](./README.md)

## Summary
PLog is a logging library for Android, which has following features:
#### Easy to print
* print without tag
* print without message
* accept anny type of arguments
* accept variable length arguments
* auto formatting
* print with builder style code

#### Controllable output
* intercept with multi dimension
* customizable output decoration (style)
* multi channel output
* soft wrap supported

#### Easy to filter
* global tag (for filtering apps)
* auto tag (for filtering classes)
* keep stacktrace (for filtering files and/or methods)
* category (for filtering developers or groups, etc)

#### Dependency on demand
You can use `plog-core` module for simple scenarios, but you can also import other modules for
advanced usage. As you like.

#### Flexible Setting/Extension
You can use built-in options or just a little interface implementation to satisfy almost all
 your needs.

PLog is a logging library for Android, follows following principle:
#### Applicability
 > Focus on log itself, discard divider lines and other complicated decors but still keep being
 powerful.

#### Light weight
 > PLog is **zero dependency**!

#### Flexibility and expandability
> PLog provides multi configs to fit your custom needs.

#### Convention over configuration
> Although PLog provides `init` method，your program will still work by using default logging
configurations. And you can user `Builder` class to simply build your own config.
Bonus: `recommend` method maybe help you much!

## Usage
Please view [Wiki](https://github.com/Muyangmin/Android-PLog/wiki) to get usage details and advanced
features.

## Features
* **Jcenter support, ZERO DEPENDENCY**
* **Empty method support *(useful for observe somewhere executed )***
* **Log without tag/ global tag/ auto tag**
* **Varargs support and auto formatting**
* **JSON format support**
* **Throwable format support**
* **Timing log support**
* **Line number and stackOffset support**
* **Loggable controller with different level**
* **Local file logger is available**
* **Logger redirect support（useful for adapt a 3-party logging, etc）**
* **Very long log content support, either auto line wrap(soft wrap)**

## Compare With Other Libs
| Library Name | [Logger](https://github.com/orhanobut/logger) | [Timber](https://github.com/JakeWharton/timber) | [KLog](https://github.com/ZhaoKaiQiang/KLog) | [Android-PLog](https://github.com/Muyangmin/Android-PLog)
| ------| ------ | ------ | ------ | ----- |
| Star/Fork | 5.7K+/1.0K+ | 3.5K+/366 | 1.1K+/251 | **Welcome!**|
| Easy To Print | √ | √ | √ | √ |
| Easy To Use | √ | √ | √ | √ |
| Flexible Settings | ☆ | ☆ | ☆ | ☆ |
| Light Weight | ☆ | ☆☆ | ☆ | ☆☆ |
| Locating in IDE | √ | × | √ | √ |
| Thread Info | √ | × | × | √ |
| Easy To Filter | ☆ | ×  | ☆ | ☆☆ |
| Beautify | ☆ | × | ☆ | ☆☆ |
| Controllable and Multi Output | × | ☆☆ | ☆ | ☆☆ |

## Sample Screen Shot
![ScreenShot](./ScreenShot.png)


## Contribution & Contact
Thanks for using PLog, this library is still in active development.**Any contribution and suggestions are welcome.**
Please feel free to contact me by using following way:

**Email: muyangmin@foxmail.com**

## Licence
```
Copyright 2016-2017 Muyangmin

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```