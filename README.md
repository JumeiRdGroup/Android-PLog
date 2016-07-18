#PLog
这个项目的灵感部分来源于开源项目[Logger](https://github.com/orhanobut/logger)和[KLog](https://github.com/ZhaoKaiQiang/KLog)。  
但区别于前述两个项目，PLog的设计理念是**实用性**：回归Log本身，在保持强大功能的同时取消花哨的修饰，特别是行分割线和花边等功能。

PLog追求极简，因此也在尽量避免冗余，目前还是**零依赖**状态。

## 目前功能
* 支持显示行号
* 支持精确的loggable控制，甚至可以精确到没个Tag
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
欢迎试用PLog，如有功能上不爽的地方欢迎指出，并请参考KLog和Logger能否满足需求。  
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