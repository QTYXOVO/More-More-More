
# More More More Mod

## 概述
模组用AI写的，别指望代码质量有多高
More More More 是一个基于 NeoForge 的 Minecraft 模组，为游戏添加了各种新物品、功能和内容，增强游戏体验。

## 功能特点
- **物品护符**：死亡后保留物品并复活，可使用三次，必须放在背包中
- 更多功能即将推出...

## 安装方法
1. 确保已安装 Minecraft Forge 或 NeoForge
2. 下载最新版本的 More More More 模组
3. 将模组文件放入 Minecraft 的 mods 文件夹
4. 启动游戏即可体验

## 开发指南
### 环境设置
1. 克隆本仓库
2. 使用 IntelliJ IDEA 或 Eclipse 打开项目
3. 运行 `gradlew genSources` 生成源代码
4. 运行 `gradlew eclipse` 或 `gradlew idea` 生成 IDE 项目文件
5. 在 IDE 中刷新项目依赖

### 构建模组
```
gradlew build
```
构建后的模组文件将位于 `build/libs` 目录下

### 开发命令
- `gradlew runClient` - 运行客户端进行测试
- `gradlew runServer` - 运行服务器进行测试
- `gradlew --refresh-dependencies` - 刷新依赖缓存
- `gradlew clean` - 清理构建文件

## 许可证
本项目采用 LGPL 许可证 - 详情参见 LICENSE 文件