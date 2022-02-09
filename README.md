
### 一、项目简介（项目开发中...      &nbsp;&nbsp;&nbsp;&nbsp;[查看旧版本>>](https://github.com/kerwincui/wumei-iot)）

1. 物美智能([wumei-smart](http://wumei.live/)是一个简单易用的生活物联网平台。可用于搭建物联网平台以及二次开发和学习。

2. 设备接入采用EMQX消息服务器，认证采用EMQX内置的Http认证插件，需要关闭匿名认证。后端采用Spring boot；前端采用Vue；移动端采用Uniapp；数据库采用Mysql、Redis和TDengine；设备端支持多种硬件SDK生成，例如ESP32、ESP8266、树莓派等；设备模拟器采用Android和Winform框架，不仅能模拟硬件设备，还可以控制和监测电脑、手机。

3. 系统架构图

![架构图](https://github.com/kerwincui/wumei-smart/blob/master/document/sys.png?raw=true)


### 二、功能
- 物模型管理（属性、功能、事件）
- 多租户
- 产品管理
- 产品分类
- 固件管理
- 设备管理（设备分享、设备禁用）
- 设备分组
- 设备分享
- 定时任务
- 硬件SDK生成（目前支持esp-idf、arduino以及python语言）
- 设备加密认证
- 可选时序数据库（采集数据量大，建议配置）
- 分布式集群部署
- 场景联动（进度50%）
- 设备告警（进度60%）
- 云云对接智能音箱（进度60%）
- 视频流处理（进度0%）

 
### 三、技术栈    
* 服务端
    - 相关技术：Spring boot、MyBatis、Spring Security、Jwt、Mysql、Redis、InfluxDB、Mqtt等
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
    - 相关技术：android xui
    - 开发工具：Android Studio
* 电脑端模拟器/监控
    - 相关技术：winfom
    - 开发工具：Visual Studio



### 四、加入互助交流群:1073236354


### 五、测试账号：默认
* [进入登录界面>>>](https://iot.wumei.live/)

### 六、界面图片

### 七、官网 [物美智能](http://wumei.live/)
