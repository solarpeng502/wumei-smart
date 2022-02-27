
### 一、项目简介（&nbsp;&nbsp;&nbsp;&nbsp;[查看旧版本>>](https://github.com/kerwincui/wumei-iot)）

1. 物美智能([wumei-smart](http://wumei.live/)是一个简单易用的生活物联网平台。可用于搭建物联网平台以及二次开发和学习。

2. 设备接入使用EMQX消息服务器，认证采用EMQX内置的Http插件对称加密认证。后端采用Spring boot；前端采用Vue；移动端采用Uniapp；数据库采用Mysql、Redis和TDengine；设备端支持硬件SDK生成，例如ESP32、ESP8266、树莓派等；设备模拟器采用Android和WPF框架，不仅能模拟硬件设备，还可以控制和监测电脑、手机。

3. 系统架构图
![系统架构图](https://github.com/kerwincui/wumei-smart/blob/master/document/sys.png?raw=true)
4. 使用流程
![使用流程图](https://raw.githubusercontent.com/kerwincui/wumei-smart/master/document/process.png)


### 二、功能
- 权限管理、系统监控基于若依Vue版本
- 物模型管理（属性、功能、事件）
- 产品管理
- 产品分类
- 产品固件
- 设备管理
- 设备分组
- 设备分享
- 设备定时
- 设备控制
- 设备影子
- 设备加密认证
- 多租户
- 设备SDK生成（进度45%）
- 场景联动（进度50%）
- 设备告警（进度60%）
- 云云对接智能音箱（进度60%）
- 可选时序数据库（进度10%）
- 视频流处理（进度0%）
- 桌面端模拟器/监控（进度0%）
- 安卓端模拟器/监控（进度0%）
- 移动端（进度10%）
 
### 三、技术栈    
* 服务端
    - 相关技术：Spring boot、MyBatis、Spring Security、Jwt、Mysql、Redis、TDengine、EMQX、Mqtt等
    - 开发工具：IDEA    
* Web端
    - 相关技术：ES6、Vue、Vuex、Vue-router、Vue-cli、Axios、Element-ui等 
    - 开发工具：Visual Studio Code    
* 移动端（android / ios / 微信小程序）
    - 相关技术：uniapp、smartconfig
    - 开发工具：HBuilder
* 硬件端
    - 相关技术： ESP-IDF、Arduino、FreeRTOS等
    - 开发工具：Visual Studio Code 和 Arduino
* 安卓端模拟器/监控
    - 相关技术：android、xui
    - 开发工具：Android Studio
* 电脑端模拟器/监控
    - 相关技术：WPF
    - 开发工具：Visual Studio

### 四、项目目录
spring-boot ---------------------------------------------------- 后端<br/>
vue ------------------------------------------------------------- 前端<br />
docker ---------------------------------------------------------- docker部署文件<br />
SDK ------------------------------------------------------------- 硬件SDK<br />
download ------------------------------------------------------- 工具下载<br />

### 五、相关文档
#### 权限管理基于ruoyi-vue系统，Android基于XUI框架，消息服务器基于EMQX4.0开源版
* [物美智能官网 >>](http://wumei.live/)
* [项目文档 >>](http://wumei.live/kerwincui/document/wiki/)
* [权限管理系统ruoyi-vue >>](https://gitee.com/y_project/RuoYi-Vue)
* [安卓UI框架 XUI >>](https://github.com/xuexiangjys/XUI)
* [Mqtt消息服务器EMQX4.0 >>](https://github.com/emqx/emqx)

### 六、其他
* 互助交流群：1073236354
* [演示地址>>](https://iot.wumei.live/)


### 七、界面图片

