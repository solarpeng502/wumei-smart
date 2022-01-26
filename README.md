
### 一、项目简介

1. 物美智能([wumei-smart](http://www.wumei.live/introduce.html))]是一个简单易用的生活物联网平台。可用于搭建物联网平台以及二次开发和学习。

2. 服务端使用spring boot；数据库mysql、redis和InfluxDB；前端vue；移动端uniapp，支持安卓、苹果和小程序；硬件端目前支持esp8266、esp32和树莓派。软硬件交互基于Mqtt协议，使用EMQX的broken作为消息服务器。



<br />


### 二、功能
- 物模型管理（属性、功能、事件）
- 多租户（管理员-管理系统、角色、权限、产品、设备；租户-管理账号下产品和设备；用户-管理自己设备和分组）
- 产品管理
- 产品分类
- 固件管理
- 设备管理（设备分享、设备禁用）
- 设备分组
- 设备告警
- 设备分享
- 硬件SDK生成（目前支持esp-idf、arduino以及python语言）
- 可选时序数据库（采集数据量大，建议配置）
- 分布式集群部署

### 三、待完善功能
- 自定义告警
- 接入智能音箱
- 定时任务
- 场景联动
- 自定义Topic
- 相关文档编写
- 设备通用的Http查询接口（例如天气）
- ...


 
### 四、技术栈    
* 后端
    - 相关技术：Spring boot、MyBatis、Spring Security、Jwt、Mysql、Redis、InfluxDB、Mqtt等
    - 开发工具：IDEA    
* 前端
    - 相关技术：ES6、Vue、Vuex、Vue-router、Vue-cli、Axios、Element-ui等 
    - 开发工具：Visual Studio Code    
* 安卓
    - 相关技术：uniapp、smartconfig
    - 开发工具：HBuilder
* 硬件端
    - 相关技术： ESP-IDF、Arduino、FreeRTOS等
    - 开发工具：Visual Studio Code 和 Arduino








<br />

### 五、加入互助交流群:1073236354
<a target="_blank" href="https://qm.qq.com/cgi-bin/qm/qr?k=P_oc91N6KC39zp2PEV_-BY3xMnAokeZ8&jump_from=webapi"><img border="0" src="//pub.idqqimg.com/wpa/images/group.png" alt="物美智能wumeismart" title="物美智能wumeismart"></a> 
<br /><br />

### 八、测试账号：默认
* [进入登录界面>>>](http://wumei.live:89/)
