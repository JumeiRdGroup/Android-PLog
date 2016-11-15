![logo](./app/src/main/res/mipmap-xxhdpi/ic_launcher.png)
#Android-PLog  [![Build Status](https://travis-ci.org/Muyangmin/Android-PLog.svg?branch=master)](https://travis-ci.org/Muyangmin/Android-PLog)[ ![Download](https://api.bintray.com/packages/muyangmin/org.mym/Android-PLog/images/download.svg) ](https://bintray.com/muyangmin/org.mym/Android-PLog/_latestVersion)<a href="http://www.methodscount.com/?lib=org.mym.plog%3Aandroid-plog%3A1.5.0"><img src="https://img.shields.io/badge/Methods and size-201 | 20 KB-e91e63.svg"/></a>

[English Version](README_EN.md)

## Summary
PLog是一个Android专用的日志封装库，遵从以下的设计哲学：
#### 追求实用性
 > 回归Log本身，在保持强大功能的同时取消花哨的修饰，特别是行分割线和花边等功能。

#### 轻巧，极简
 > PLog为降低使用成本和依赖负担，尽量避免冗余，目前还是**零依赖**状态。

#### 充分考虑可扩展性
> PLog提供丰富的设置项可供定制而无需修改库本身的实现。

#### 约定大于配置
> PLog虽然提供了`init`方法，但是实际上即使没有调用该方法也可以正常打印日志。并且由于提供了Builder，使用者在需要定制时也只用关心自己要修改的设置项即可。

## Usage
只需一步即可打印日志! Jcenter依赖如下:
```Groovy
    compile 'org.mym.plog:android-plog:${latestVersion}'
```
打印样例:
```Java
    PLog.empty();
    PLog.v("This is a verbose log.");
    PLog.d("DebugTag", "This is a debug log.");
    PLog.e("This is an error log.");
```
请参阅[Wiki](https://github.com/Muyangmin/Android-PLog/wiki)以获取详细的使用说明和高级特性。

## Features
* **支持JCenter, 轻巧零依赖**
* **支持空消息打印（通常用于观察某处是否执行）**
* **支持无Tag、全局/局部Tag、自动Tag打印**
* **支持变长参数和自动格式化**
* **支持JSON格式化打印**
* **支持Throwable格式化打印**
* **任意类智能格式化打印**
* **支持打点计时日志(TimingLog)**
* **支持显示行号,Android Studio控制台点击可跳转到源文件对应行, 还可以通过栈帧偏移控制跳转位置**
* **支持粒度可控的的loggable控制**
* **支持重定向Logger（通常用于本地文件打印、第三方日志接入等）**
* **无限长字符串打印, 支持自动换行(Soft Wrap)**

## Sample Screen Shot
![ScreenShot](./ScreenShot.png)

## Contribution & Contact
Thanks for using PLog, this library is still in active development.
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