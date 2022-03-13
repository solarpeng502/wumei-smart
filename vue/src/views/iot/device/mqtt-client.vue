<template>
<span></span>
</template>

<script>
import mqtt from 'mqtt'
import {
    cacheJsonThingsModel
} from "@/api/iot/model";
import {
    getToken
} from "@/utils/auth";

export default {
    name: "mqttClient",
    props: {
        publish: {
            type: Object,
            default: null
        },
        subscribes:{
            type:array,
            default:null
        }
    },
    watch: {
        // 获取到父组件传递的值
        publish: function (newVal, oldVal) {
            this.publishInfo = newVal;
            
        },
        subscribes:function(newVal,oldVal){
            this.subscribesInfo=newVal;
        }
    },
    data() {
        return {
            // 设备信息
            deviceInfo: {},
            // 发布消息
            publishInfo: {},
            // 订阅集合
            subscribesInfo: [],
        };
    },
    created() {
        this.connectMqtt();
    },
    methods: {
        /** 获取物模型*/
        getCacheThingsModdel(productId) {
            // 获取缓存的Json物模型
            cacheJsonThingsModel(productId).then(response => {
                let thingsModel = JSON.parse(response.data);
                // 筛选监测数据
                this.monitorThings = thingsModel.properties.filter(item => item.isMonitor == 1);
                // 加载图表
                this.$nextTick(function () {
                    this.getStatistic();
                    // 获取统计数据
                    this.getStatisticData(this.monitorThings);
                });

            });
        },

        /** 启动Mqtt*/
        startMqtt(){
        },
        /** 连接Mqtt */
        connectMqtt() {
            console.log(this.client);
            let options = {
                username: "wumei-smart",
                password: getToken(),
                cleanSession: false,
                keepAlive: 60,
                clientId: 'web-' + Math.random().toString(16).substr(2),
                connectTimeout: 10000
            }
            this.client = mqtt.connect(process.env.VUE_APP_BROKEN_URL, options);
            console.log(this.client);
            this.client.on("connect", (e) => {
                console.log("成功连接服务器:", e);
                // 订阅主题
                this.client.subscribe(['top/#', 'three/#', '#'], {
                    qos: 1
                }, (err) => {
                    if (!err) {
                        console.log("订阅成功");
                        // 发布主题
                        this.mqttPublish("pubtop", 'hello,this is a nice day!')
                    } else {
                        console.log('消息订阅失败！')
                    }
                });
            });
            // 重新连接
            this.reconnectMqtt()
            // 是否已经断开连接
            this.mqttError()
            // 监听获取信息
            this.mqttSubscribe()
        },

        /** 发布消息@topic主题  @message发布内容 */
        mqttPublish(topic, message) {
            if (!this.client.connected) {
                console.log('客户端未连接')
                return
            }
            this.client.publish(topic, message, {
                qos: 1
            }, (err) => {
                if (!err) {
                    console.log('主题为' + topic + "发布成功")
                }
            })
        },

        /** 监听Mqtt消息 */
        mqttSubscribe() {
            this.client.on("message", (topic, message) => {
                if (message) {
                    console.log('收到来着', topic, '的信息', message.toString())
                    const res = JSON.parse(message.toString())
                    //console.log(res, 'res')
                    switch (topic) {
                        case 'top/#':
                            this.msg = res
                            break;
                        case 'three/#':
                            this.msg = res
                            break;
                        case 'three/#':
                            this.msg = res
                            break;
                        default:
                            return
                            this.msg = res
                    }
                    this.msg = message

                    // 传递信息到父组件
                    let data={};
                    data.topic=topic;
                    data.message=message;
                    this.$emit('callbackEvent', data);
                }
            });
        },
        /** 监听服务器是否连接失败 */
        mqttError() {
            this.client.on('error', (error) => {
                console.log('连接失败：', error)
                this.client.end()
            })
        },
        /** 取消订阅 */
        unsubscribeMqtt() {
            this.client.unsubscribe(this.mtopic, (error) => {
                console.log('主题为' + this.mtopic + '取消订阅成功', error)
            })
        },
        /** 断开连接 */
        unconnectMqtt() {
            this.client.end()
            this.client = null
            console.log('服务器已断开连接！')
        },
        /** 监听服务器重新连接 */
        reconnectMqtt() {
            this.client.on('reconnect', (error) => {
                console.log('正在重连:', error)
            });
        },

    }
};
</script>
