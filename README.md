[ ![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-Android--PLog-brightgreen.svg?style=flat) ](https://android-arsenal.com/details/1/4884)[![](https://jitpack.io/v/Muyangmin/Android-PLog.svg)](https://jitpack.io/#Muyangmin/Android-PLog)[ ![Download](https://api.bintray.com/packages/muyangmin/org.mym/Android-PLog/images/download.svg) ](https://bintray.com/muyangmin/org.mym/Android-PLog/_latestVersion)

# PLog = Pure, Pretty, Powerful logging tool [![Build Status](https://travis-ci.org/Muyangmin/Android-PLog.svg?branch=master)](https://travis-ci.org/Muyangmin/Android-PLog)

![logo](./app/src/main/res/mipmap-xxhdpi/ic_launcher.png)

[English Version](README_EN.md)

## 概述
Android-PLog 项目(简称 PLog )是一个专为 Android 
应用程序设计的开源日志封装库，追求纯粹、易用、强大，帮助开发者更好地使用日志调试程序，提高开发效率。概括来讲，它具有以下特性：
#### 容易打印
* **支持无Tag打印**
* **支持空消息打印（通常用于观察某处是否执行）**
* **支持任意参数类型**
* **支持变长参数和自动格式化**
* **支持Builder方式打印**

#### 输出可控
* **支持多维度的输出拦截**
* **支持自定义输出装饰样式**
* **支持多通道同时输出**
* **支持自动换行（SoftWrap）**

#### 筛选容易
* **支持全局Tag**(可以区分不同应用)
* **支持自动Tag**(可以区分不同类名)
* **支持保留堆栈**(可以区分不同文件和方法，并且实现在AS中**自动日志定位**)
* **支持分组打印**(可以区分不同开发者, etc)

#### 按需依赖
核心功能模块和定制特性完全分离，体积轻巧，并且全部支持Jcenter依赖。

#### 扩展灵活
核心功能全部接口化，通过简单的设置和接口注入就可以完成绝大部分日志需求。


**关于项目的更多信息，包括下载、使用和注意事项，请访问[完整文档地址](https://jumeirdgroup.github.io/Android-PLog/)。**

## Contribution & Contact
如果您在使用这个库的时候遇到困难，或者有任何的反馈、建议，都可以通过GitHub Issue 功能或下面的邮箱联系我：
**<muyangmin@foxmail.com>**

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
