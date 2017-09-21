---
layout: page
category: "doc"
title: "下载和设置"
order: 1
---

* TOC
{:toc}

### 获取稳定版本
你可以使用 Gradle 从 JCenter 或 JitPack 获取 PLog 的正式发行版本。

#### 使用 JCenter

1. 在 Project 根目录 `build.gradle` 文件中，确保 jcenter 仓库配置存在:
```Groovy
allprojects {
    repositories {
        jcenter()
    }
}
```
2. 在 app module 中， 添加对核心库的依赖, 目前最新版本为 [ ![Download](https://api.bintray.com/packages/muyangmin/org.mym/Android-PLog/images/download.svg) ](https://bintray.com/muyangmin/org.mym/Android-PLog/_latestVersion) ：
```Groovy
compile 'org.mym.plog:plog-core:${VERSION_NAME}'
```
3. 按需依赖 PLog 的各个扩展库，注意需与核心库版本一致：
```Groovy
    compile "org.mym.plog:plog-formatter:${VERSION_NAME}"   //格式化实现包 (强烈推荐使用)
    compile "org.mym.plog:plog-printer:${VERSION_NAME}"     //Printer扩展，如文件支持等 (可选)
    compile "org.mym.plog:plog-timing:${VERSION_NAME}"      //计时日志包 (可选)
```

#### 使用 JitPack
使用 JitPack 获取项目依赖和使用 JCenter 很相似，不同的是你需要添加 JitPack 仓库地址：
```Groovy
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

### 获取快照版本
你可以从 JitPack 获取项目的快照版本，以获得从项目上次 release 之后 push 到 master 分支上的所有修改。

**请注意，快照版本可能包含最新的 bug fix(es) , 但不保证稳定性。**

```Groovy
    // dependency on the latest commit in the master branch
    compile 'com.github.Muyangmin:Andriod-PLog:master-SNAPSHOT'
```