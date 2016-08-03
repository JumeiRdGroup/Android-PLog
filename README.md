#PLog  [ ![Download](https://api.bintray.com/packages/muyangmin/org.mym/Android-PLog/images/download.svg) ](https://bintray.com/muyangmin/org.mym/Android-PLog/_latestVersion)

这个项目的灵感部分来源于开源项目[Logger](https://github.com/orhanobut/logger)和[KLog](https://github.com/ZhaoKaiQiang/KLog)。  
但区别于前述两个项目，PLog的设计理念是**实用性**：回归Log本身，在保持强大功能的同时取消花哨的修饰，特别是行分割线和花边等功能。

PLog追求极简，因此也在尽量避免冗余，目前还是**零依赖**状态。

PLog在设计的时候还充分考虑实际项目中对功能的需求，提供丰富的设置项可供定制而无需修改库本身的实现。通过使用`Builder`模式，使用者无需任何设置也可以工作，在需要定制时也只用关心自己要修改的设置项即可。

## Basic Usage
1. Import via gradle:  
```Groovy
    compile 'org.mym.plog:android-plog:1.0.0'
```
2. Initialize PLog before any log（**Optional**）:  
```Java
        PLog.init(new PLogConfig.Builder()
                .globalTag("GlobalTag")
                .forceConcatGlobalTag(true)
                .keepInnerClass(true)
                .keepLineNumber(true)
                .build());
```  
All settings are **optional**, and here just show a few part of options.Please see doc for 
advanced usage.  
3. Use `PLog` class to print log, via `v()`, `d()`, `i()` method, and so on. Sample:  
```Java
        PLog.empty();
        PLog.v("This is a verbose log.");
        PLog.d("DebugTag", "This is a debug log.");
        PLog.i("InfoTag", "This is an info log.");
        PLog.w("This is a warn log.");
        PLog.e("This is an error log.");
```

For full change logs, please see [Here](https://github.com/Muyangmin/Android-PLog/releases).

## 功能特点
* 支持JCenter依赖
* 支持显示行号
* 支持精确的loggable控制，只要你愿意，甚至可以精确到每条Tag、每行Msg
* 支持Android Studio控制台点击可跳转到源文件对应行
* 支持无Tag快捷打印
* 支持设置全局Tag，同时支持每条日志指定Tag
* 支持空消息打印（通常用于观察某处是否执行）
* 支持变长参数和自动格式化，从此不用手动拼接参数
* **支持第三方日志接入（通常用于上传日志）**

## 预备加入/考虑是否需要的功能
* 保存日志到文件
* 无限长字符串打印

## Contribution
欢迎试用PLog，如有功能上不爽的地方欢迎指出，并请参考KLog和Logger能否暂时满足需求。  
另外也欢迎Star/Fork/PR，但PR我会视情况决定是否接受，请大家见谅^_^

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