---
layout: page
category: "dev"
title: "贡献代码或文档"
order: 1
---

我们非常欢迎你为 Android-PLog 的完善做出贡献，代码或文档都可以。

### 贡献代码
你可以直接向 [Android-PLog][1] 仓库的 `master` 分支提交你的修改。

为增加 PR 的处理效率，减少不必要的 Require Change， 请尽量将不同的原子修改点 (*例如一个 bugFix 或者一个 feature enhancement* ) 分割为不同的 [Pull Request][2] 来提交，具体操作上即是为不同的修改点创建不同的分支:

1. Fork repo 并 clone 到本地:
```bash
git clone https://github.com/<your-account>/Android-PLog.git
```
2. 从 master 检出新分支：
```bash
git checkout -b myChange1
```
3. 修改文件并提交到你的分支
4. 发起一个 Pull Request
5. 需要修改另一个问题时，从 master 继续检出新分支：
```bash
git checkout -b myChange2 master
```
6.重复 3-5 步。

### 贡献文档
修改文档与修改代码很类似，区别仅是你需要从 ``gh-pages`` 分支检出文档并发送 Pull Request。

[1]: {{ site.codeurl }}
[2]: https://help.github.com/articles/about-pull-requests/