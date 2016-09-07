#PLog  [ ![Download](https://api.bintray.com/packages/muyangmin/org.mym/Android-PLog/images/download.svg) ](https://bintray.com/muyangmin/org.mym/Android-PLog/_latestVersion)

这个项目的部分灵感来源于开源项目[Logger](https://github.com/orhanobut/logger)和[KLog](https://github.com/ZhaoKaiQiang/KLog)。
但区别于前述两个项目，PLog的设计理念是**实用性**：回归Log本身，在保持强大功能的同时取消花哨的修饰，特别是行分割线和花边等功能。

PLog追求极简，因此也在尽量避免冗余，目前还是**零依赖**状态。

PLog在设计的时候还充分考虑实际项目中对功能的需求，提供丰富的设置项可供定制而无需修改库本身的实现。通过使用`Builder`模式，使用者无需任何设置也可以工作，在需要定制时也只用关心自己要修改的设置项即可。

请参阅[Wiki](https://github.com/Muyangmin/Android-PLog/wiki)以获取使用说明和高级特性, 欢迎完善Wiki、改进排版等。

## 功能特点
* **支持JCenter, 轻巧零依赖**
* **支持空消息打印（通常用于观察某处是否执行）**
* **支持无Tag、全局/局部Tag、自动Tag打印**
* **支持变长参数和自动格式化**
* **可打印Throwable参数, 不影响过滤器**
* **支持显示行号,Android Studio控制台点击可跳转到源文件对应行, 还可以通过栈帧偏移控制跳转位置**
* **支持粒度可控的的loggable控制**
* **支持重定向Logger（通常用于本地文件打印、第三方日志接入等）**
* **无限长字符串打印, 同时支持自动换行**

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