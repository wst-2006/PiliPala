# 噼里啪啦

一款基于 [BiliLite](https://github.com/HYXTPSP/BiliLite) 修改的非官方哔哩哔哩 Android 客户端。它保留简洁、无推荐流的内容管理方式，并补充了后台播放、系统画中画和关注 UP 主批量选择等能力。

当前版本：`v0.6.2`（versionCode 21）

> 本项目与哔哩哔哩官方无隶属、合作或授权关系，仅供个人学习与技术研究使用。

## 本版改进

- v0.6.2：首页增加“推荐 / 新发布”标签，新发布按发布日期从新到旧排列
- 批量添加先保存 UP，再最多同时同步 3 个 UP；每页视频保存后立即出现在首页和 UP 合集中
- 管理 UP 和关注添加页显示进度条、已处理 UP 数、成功/失败数、已获取视频数及剩余数量；总量未知时显示“统计中”
- 单个 UP 同步失败不会中止整批任务，可查看失败详情并重试失败项
- 应用内切换页面继续同步；同步完成前请保持应用在前台，避免退出或息屏。进程被关闭后，已保存的数据保留，但任务不会自动断点恢复

- 支持后台音频播放，切换应用或息屏后播放进度可继续推进
- 接入 Android 系统级画中画，缩小播放器时不再重新加载或退出原视频
- 首页全部视频卡片显示发布日期
- 可从当前登录账号的关注列表中选择 UP 主并批量添加
- 关注列表使用 B 站接口返回的关注时间顺序和固定分页，支持上一页、下一页、跳页
- 支持当前页搜索和跨全部关注列表搜索；跨页勾选状态会保留
- 应用安装名称和图标已更新为“噼里啪啦”
- 移除“我的”页面底部的原作者 QQ 交流群信息
- v0.6.1 修正关注列表排序参数：使用关注顺序，移除误用的“最常访问”排序；分页与全局搜索共用此修正

## 原有功能

- 二维码、验证码和密码登录
- 仅展示用户主动添加的 UP 主视频，无推荐流和广告
- 竖卡、横排两种首页布局
- 观看进度记录、断点续播、多 P、倍速、画质、字幕与手势控制
- 视频书签、收藏队列、观看历史和离线缓存
- 主题及功能插件系统

## 下载与兼容性

APK 将发布在本仓库的 [Releases](../../releases) 页面。

当前个人测试版本使用 debug 签名，包名为 `com.bililite.app.personal`，可与原版并存安装。主要使用环境为华为 Pura 70 Pro+、HarmonyOS 6 的卓易通 Android 兼容环境；后台播放和画中画最终表现仍会受到卓易通及系统电池策略影响。

## 从源码构建

需要 JDK 17 和 Android SDK 35。在 `android/local.properties` 中配置 SDK 路径后执行：

```powershell
cd android
.\gradlew.bat assembleDebug
```

产物位于 `android/app/build/outputs/apk/debug/app-debug.apk`。

Linux/macOS 可执行：

```bash
cd android
./gradlew assembleDebug
```

## 技术栈

- Kotlin + Jetpack Compose
- Room 本地数据库
- Media3 ExoPlayer + MediaSessionService
- OkHttp + B 站网页 API
- LuaJ 插件脚本引擎
- minSdk 24 / targetSdk 35

## 插件开发

示例插件位于 `plugins_examples/`，接口规范参见 [插件开发规范.md](插件开发规范.md)。

## 开源许可与署名

本项目依据 [MIT License](LICENSE) 发布，因此允许修改、分发及上传到 GitHub，但必须保留原版权声明和许可文本。

本项目为 BiliLite 的二次修改版本：

- 原项目：[HYXTPSP/BiliLite](https://github.com/HYXTPSP/BiliLite)
- 原作者：HYXTPSP（B 站 UID：1045428546）
- 当前修改版维护者：[wst-2006](https://github.com/wst-2006)

第三方组件信息见 [THIRD_PARTY_NOTICES.md](THIRD_PARTY_NOTICES.md)，更完整的说明见应用内 [LEGAL](android/app/src/main/assets/legal.md) 与 [隐私政策](android/app/src/main/assets/privacy.md)。

## 风险提示

- 登录凭据和应用数据仅保存在设备本地；网络请求会直接发送至哔哩哔哩相关域名
- 哔哩哔哩接口或风控策略变化可能导致部分功能失效或账号受限
- 请勿将本项目用于商业运营、付费分发、批量采集或侵犯他人权益的用途
- 软件按“现状”提供，使用者需自行承担账号、数据及兼容性风险
