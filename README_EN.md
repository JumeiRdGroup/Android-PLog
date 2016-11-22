#Android-PLog  [![Build Status](https://travis-ci.org/Muyangmin/Android-PLog.svg?branch=master)](https://travis-ci.org/Muyangmin/Android-PLog)[ ![Download](https://api.bintray.com/packages/muyangmin/org.mym/Android-PLog/images/download.svg) ](https://bintray.com/muyangmin/org.mym/Android-PLog/_latestVersion)<a href="http://www.methodscount.com/?lib=org.mym.plog%3Aandroid-plog%3A1.5.0"><img src="https://img.shields.io/badge/Methods and size-201 | 20 KB-e91e63.svg"/></a>

#### [中文版文档](./README.md)

## Summary
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
Only one step!
```Groovy
    compile 'org.mym.plog:android-plog:${latestVersion}'
```
Sample codes below:
```Java
    PLog.empty();
    PLog.v("This is a verbose log.");
    PLog.d("DebugTag", "This is a debug log.");
    PLog.e("This is an error log.");
```
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

## Sample Screen Shot
![ScreenShot](./ScreenShot.png)

## Thanks
* [Logger](https://github.com/orhanobut/logger)
* [KLog](https://github.com/ZhaoKaiQiang/KLog)

## Contribution & Contact
Thanks for using PLog, this library is still in active development.**Any contribution or material
bonus are appreciated and welcome.**
Please feel free to contact me by using following way:

**Email: muyangmin@foxmail.com**

## Licence
```
Copyright 2016 Muyangmin

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