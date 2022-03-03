<template>
<div style="padding:6px;">
    <el-card style="margin-bottom:6px;">
        <el-form :model="queryParams" ref="queryForm" :inline="true" label-width="75px" style="margin-bottom:-20px;">
            <el-form-item label="设备名称" prop="deviceName">
                <el-input v-model="queryParams.deviceName" placeholder="请输入设备名称" clearable size="small" @keyup.enter.native="handleQuery" />
            </el-form-item>
            <el-form-item label="产品名称" prop="productName">
                <el-input v-model="queryParams.productName" placeholder="请输入产品名称" clearable size="small" @keyup.enter.native="handleQuery" />
            </el-form-item>
            <el-form-item label="设备状态" prop="status">
                <el-select v-model="queryParams.status" placeholder="请选择设备状态" clearable size="small">
                    <el-option v-for="dict in dict.type.iot_device_status" :key="dict.value" :label="dict.label" :value="dict.value" />
                </el-select>
            </el-form-item>
            <el-form-item label="激活时间">
                <el-date-picker v-model="daterangeActiveTime" size="small" style="width: 240px" value-format="yyyy-MM-dd" type="daterange" range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期"></el-date-picker>
            </el-form-item>
            <el-form-item>
                <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
                <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
            </el-form-item>
            <el-form-item style="float:right;">
                <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleEditDevice(0)" v-hasPermi="['iot:device:add']">新增</el-button>
            </el-form-item>
        </el-form>
    </el-card>

    <el-card style="padding-bottom:100px;">
        <el-row :gutter="30" v-loading="loading">
            <el-col :span="6" v-for="(item,index) in deviceList" :key="index" style="margin-bottom:30px;text-align:center;">
                <el-card :body-style="{ padding: '20px'}" shadow="always">
                    <el-row type="flex" :gutter="10" justify="space-between">
                        <el-col :span="20" style="text-align:left;">
                            <el-link type="" :underline="false" @click="handleEditDevice(item)" style="font-weight:bold;font-size:16px;line-height:32px;">
                                <svg-icon icon-class="device" /> {{item.deviceName}}
                                <el-tag size="mini" type="info">Version {{item.firmwareVersion}}</el-tag>
                            </el-link>
                        </el-col>
                        <el-col :span="4">
                            <div style="font-size:28px;color:#ccc;">
                                <svg-icon v-if="item.status==3 && item.rssi >= '-55'" icon-class="wifi_4" />
                                <svg-icon v-else-if="item.status==3 && item.rssi >= '-70' && item.rssi < '-55' " icon-class="wifi_3" />
                                <svg-icon v-else-if="item.status==3 && item.rssi >= '-85' && item.rssi < '-70' " icon-class="wifi_2" />
                                <svg-icon v-else-if="item.status==3 && item.rssi >= '-100' && item.rssi < '-85' " icon-class="wifi_1" />
                                <svg-icon v-else icon-class="wifi_0" />
                            </div>
                        </el-col>
                    </el-row>
                    <el-row :gutter="10">
                        <el-col :span="15">
                            <div style="text-align:left;line-height:40px;font-size:14px;">
                                <dict-tag :options="dict.type.iot_device_status" :value="item.status" size="small" style="width:60px;display:inline-block;" />
                                <el-tag type="success" size="small" v-if="item.isShadow==1">启用影子</el-tag>
                                <el-tag type="info" size="small" v-else>禁用影子</el-tag>
                            </div>
                            <el-descriptions :column="1" size="mini">
                                <el-descriptions-item label="产品">
                                    <el-link type="primary" :underline="false">{{item.productName}}</el-link>
                                </el-descriptions-item>
                                <el-descriptions-item label="编号">
                                    {{item.serialNumber}}
                                </el-descriptions-item>
                                <el-descriptions-item label="激活时间">
                                    {{ parseTime(item.activeTime, '{y}-{m}-{d}') }}
                                </el-descriptions-item>
                            </el-descriptions>
                        </el-col>
                        <el-col :span="9">
                            <div style="margin-top:10px;">
                                <el-image style="width:100%;height:100px;border:1px solid #ccc;border-radius:5px;" lazy :preview-src-list="[baseUrl+item.imgUrl]" :src="baseUrl+item.imgUrl" fit="cover" v-if="item.imgUrl!=null && item.imgUrl!=''"></el-image>
                                <!-- 用于显示本地计算机、手机、树莓派等设备图片-->
                                <el-image style="width:100%;height:100px;border:1px solid #ccc;border-radius:5px;" :preview-src-list="[require('@/assets/images/esp8266.jpg')]" :src="require('@/assets/images/esp8266.jpg')" fit="cover" v-else-if="item.productId==1"></el-image>
                                <el-image style="width:100%;height:100px;border:1px solid #ccc;border-radius:5px;" :preview-src-list="[require('@/assets/images/esp32.jpg')]" :src="require('@/assets/images/esp32.jpg')" fit="cover" v-else-if="item.productId==2"></el-image>
                                <el-image style="width:100%;height:100px;border:1px solid #ccc;border-radius:5px;" :preview-src-list="[require('@/assets/images/raspberry.jpg')]" :src="require('@/assets/images/raspberry.jpg')" fit="cover" v-else-if="item.productId==3"></el-image>
                                <el-image style="width:100%;height:100px;border:1px solid #ccc;border-radius:5px;" :preview-src-list="[require('@/assets/images/telphone.jpg')]" :src="require('@/assets/images/telphone.jpg')" fit="cover" v-else-if="item.productId==4"></el-image>
                                <el-image style="width:100%;height:100px;border:1px solid #ccc;border-radius:5px;" :preview-src-list="[require('@/assets/images/computer.jpg')]" :src="require('@/assets/images/computer.jpg')" fit="cover" v-else-if="item.productId==5"></el-image>
                                <el-image style="width:100%;height:100px;border:1px solid #ccc;border-radius:5px;" :preview-src-list="[require('@/assets/images/product.jpg')]" :src="require('@/assets/images/product.jpg')" fit="cover" v-else></el-image>
                            </div>
                        </el-col>
                    </el-row>
                    <el-descriptions :column="2" border size="mini" style="height:82px;margin-top:10px;overflow:hidden;">
                        <el-descriptions-item v-for="subItem in item.readOnlyList" :key="subItem.id" :contentStyle="{height:'40px'}">
                            <template slot="label">
                                <span style="white-space: nowrap;text-overflow: ellipsis;width:40px;overflow:hidden;height:40px;">{{subItem.name}}</span>
                            </template>
                            <el-link type="primary" :underline="false" style="white-space: nowrap;">{{subItem.value}} {{subItem.unit==null?"":subItem.unit}}</el-link>
                        </el-descriptions-item>
                        <el-descriptions-item v-for="subItem in item.boolList" :key="subItem.id" :contentStyle="{height:'40px'}">
                            <template slot="label">
                                <div style="white-space: nowrap;text-overflow:ellipsis;width:40px;overflow:hidden;" :title="subItem.name">{{subItem.name}}</div>
                            </template>
                            <el-switch v-model="subItem.value" active-text="" inactive-text="" :active-value="'1'" :inactive-value="'0'" />
                        </el-descriptions-item>
                        <el-descriptions-item v-for="subItem in item.enumList" :key="subItem.id" :contentStyle="{height:'40px'}">
                            <template slot="label">
                                <div style="white-space: nowrap;text-overflow:ellipsis;width:40px;overflow:hidden;" :title="subItem.name">{{subItem.name}}</div>
                            </template>
                            <el-select v-model="subItem.value" placeholder="请选择" clearable size="mini" :title="subItem.name">
                                <el-option v-for="children in subItem.enumList" :key="children.value" :label="children.text" :value="children.value" />
                            </el-select>
                        </el-descriptions-item>
                        <el-descriptions-item v-for="subItem in item.decimalList" :key="subItem.id" :contentStyle="{height:'40px'}">
                            <template slot="label">
                                <div style="white-space: nowrap;text-overflow:ellipsis;width:40px;overflow:hidden;" :title="subItem.name">{{subItem.name}}</div>
                            </template>
                            <el-input v-model="subItem.value" :placeholder="'小数：'+subItem.name" size="mini" :title="'小数：'+subItem.name">
                                <el-button slot="append" icon="el-icon-s-promotion" style="font-size:16px;padding:10px;" title="指令发送"></el-button>
                            </el-input>
                        </el-descriptions-item>
                        <el-descriptions-item v-for="subItem in item.integerList" :key="subItem.id" :contentStyle="{height:'40px'}">
                            <template slot="label">
                                <div style="white-space: nowrap;text-overflow:ellipsis;width:40px;overflow:hidden;" :title="subItem.name">{{subItem.name}}</div>
                            </template>
                            <el-input v-model="subItem.value" :placeholder="'整数：'+subItem.name" :title="'整数：'+subItem.name" size="mini">
                                <el-button slot="append" icon="el-icon-s-promotion" style="font-size:16px;padding:10px;" title="指令发送"></el-button>
                            </el-input>
                        </el-descriptions-item>
                        <el-descriptions-item v-for="subItem in item.arrayList" :key="subItem.id" :contentStyle="{height:'40px'}">
                            <template slot="label">
                                <div style="white-space: nowrap;text-overflow:ellipsis;width:40px;overflow:hidden;" :title="subItem.name">{{subItem.name}}</div>
                            </template>
                            <el-input v-model="subItem.value" :placeholder="'数组：'+subItem.name" :title="'数组：'+subItem.name" size="mini">
                                <el-button slot="append" icon="el-icon-s-promotion" style="font-size:16px;padding:10px;" title="指令发送"></el-button>
                            </el-input>
                        </el-descriptions-item>
                        <el-descriptions-item v-for="subItem in item.stringList" :key="subItem.id" :contentStyle="{height:'40px'}">
                            <template slot="label">
                                <div style="white-space: nowrap;text-overflow:ellipsis;width:40px;overflow:hidden;" :title="subItem.name">{{subItem.name}}</div>
                            </template>
                            <el-input v-model="subItem.value" :placeholder="'字符串：'+subItem.name" :title="'字符串：'+subItem.name" size="mini">
                                <el-button slot="append" icon="el-icon-s-promotion" style="font-size:16px;padding:10px;" title="指令发送"></el-button>
                            </el-input>
                        </el-descriptions-item>
                    </el-descriptions>
                    <el-button-group style="margin-top:15px;">
                        <el-button type="primary" size="mini" icon="el-icon-edit" @click="handleEditDevice(item)" v-hasPermi="['iot:device:edit']">详情 </el-button>
                        <el-button type="danger" size="mini" icon="el-icon-delete" @click="handleDelete(item)" v-hasPermi="['iot:device:remove']">删除</el-button>
                        <el-button type="success" size="mini" icon="el-icon-odometer" @click="handleMonitor(item)" v-hasPermi="['iot:device:edit']">实时监测</el-button>
                    </el-button-group>
                </el-card>
            </el-col>
        </el-row>

        <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

        <!-- 查看监测数据 -->
        <el-dialog title="实时监测" :visible.sync="open" width="800px">
            <div style="margin-top:-50px;">
                <el-divider></el-divider>
            </div>
            <div ref="monitor" style="width:100%;height:250px;"></div>
            <div ref="monitor1" style="width:100%;height:250px;"></div>
            <div slot="footer" class="dialog-footer">
                <el-button @click="cancel">关 闭</el-button>
            </div>
        </el-dialog>

    </el-card>
</div>
</template>

<script>
import mqtt from 'mqtt'
import * as echarts from 'echarts';
import {
    getToken
} from "@/utils/auth";
import {
    listDeviceShort,
    delDevice,
} from "@/api/iot/device";

export default {
    name: "Device",
    dicts: ['iot_device_status', 'iot_is_enable'],
    data() {
        return {
            // mqtt客户端
            client: {},
            // 遮罩层
            loading: true,
            // 总条数
            total: 0,
            // 设备列表数据
            deviceList: [],
            // 弹出层标题
            title: "",
            // 是否显示弹出层
            open: false,
            // 激活时间范围
            daterangeActiveTime: [],
            // 查询参数
            queryParams: {
                pageNum: 1,
                pageSize: 10,
                deviceName: null,
                productId: null,
                groupId: null,
                productName: null,
                userId: null,
                userName: null,
                tenantId: null,
                tenantName: null,
                serialNumber: null,
                status: null,
                networkAddress: null,
                activeTime: null,
            },
        };
    },
    created() {
        this.getList();
        this.connectMqtt();
    },
    activated() {
        const time = this.$route.query.t;
        if (time != null && time != this.uniqueId) {
            this.uniqueId = time;
            this.queryParams.pageNum = Number(this.$route.query.pageNum);
            this.getList();
        }
    },
    methods: {
        /** 连接Mqtt */
        connectMqtt() {
            let options = {
                username: "wumei-smart",
                password: getToken(),
                cleanSession: false,
                keepAlive: 60,
                clientId: 'web-' + Math.random().toString(16).substr(2, 8),
                connectTimeout: 60000
            }
            this.client = mqtt.connect('wss://iot.wumei.live/mqtt', options);
            this.client.on("connect", (e) => {
                console.log("成功连接服务器:", e);
                // 订阅三个名叫'top/#', 'three/#'和'#'的主题
                this.client.subscribe(['top/#', 'three/#', '#'], {
                    qos: 1
                }, (err) => {
                    if (!err) {
                        console.log("订阅成功");
                        //向主题叫“pubtop”发布一则内容为'hello,this is a nice day!'的消息
                        this.mqttPublish("pubtop", 'hello,this is a nice day!')
                    } else {
                        console.log('消息订阅失败！')
                    }
                });
            });
            //重新连接
            this.reconnectMqtt()
            //是否已经断开连接
            this.mqttError()
            //监听获取信息
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

        /** 查询设备列表 */
        getList() {
            this.loading = true;
            if (null != this.daterangeActiveTime && '' != this.daterangeActiveTime) {
                this.queryParams.params["beginActiveTime"] = this.daterangeActiveTime[0];
                this.queryParams.params["endActiveTime"] = this.daterangeActiveTime[1];
            }
            listDeviceShort(this.queryParams).then(response => {
                this.deviceList = response.rows;
                this.total = response.total;
                this.loading = false;
            });
        },
        // 取消按钮
        cancel() {
            this.open = false;
        },
        /** 搜索按钮操作 */
        handleQuery() {
            this.queryParams.pageNum = 1;
            this.getList();
        },
        /** 重置按钮操作 */
        resetQuery() {
            this.daterangeActiveTime = [];
            this.resetForm("queryForm");
            this.handleQuery();
        },
        /** 查看监测数据 */
        handleMonitor() {
            this.open = true;
            this.$nextTick(function () {
                this.getMonitor();
                this.getMonitor1();
            });

        },
        /** 修改按钮操作 */
        handleEditDevice(row) {
            let deviceId = 0;
            if (row != 0) {
                deviceId = row.deviceId || this.ids
            }
            this.$router.push({
                path: '/iot/device-edit',
                query: {
                    deviceId: deviceId,
                    pageNum: this.queryParams.pageNum
                }
            });
        },
        /** 删除按钮操作 */
        handleDelete(row) {
            const deviceIds = row.deviceId || this.ids;
            this.$modal.confirm('是否确认删除设备编号为"' + deviceIds + '"的数据项？').then(function () {
                return delDevice(deviceIds);
            }).then(() => {
                this.getList();
                this.$modal.msgSuccess("删除成功");
            }).catch(() => {});
        },
        /**监测数据 */
        getMonitor() {
            var myChart = echarts.init(this.$refs.monitor);
            var option;

            function randomData() {
                now = new Date(+now + oneDay);
                value = value + Math.random() * 21 - 10;
                return {
                    name: now.toString(),
                    value: [
                        [now.getFullYear(), now.getMonth() + 1, now.getDate()].join('/'),
                        Math.round(value)
                    ]
                };
            }
            let data = [];
            let now = new Date(1997, 9, 3);
            let oneDay = 24 * 3600 * 1000;
            let value = Math.random() * 1000;
            for (var i = 0; i < 1000; i++) {
                data.push(randomData());
            }
            option = {
                title: {
                    text: '温度'
                },
                tooltip: {
                    trigger: 'axis',
                    formatter: function (params) {
                        params = params[0];
                        var date = new Date(params.name);
                        return (
                            date.getDate() +
                            '/' +
                            (date.getMonth() + 1) +
                            '/' +
                            date.getFullYear() +
                            ' : ' +
                            params.value[1]
                        );
                    },
                    axisPointer: {
                        animation: false
                    }
                },
                xAxis: {
                    type: 'time',
                    splitLine: {
                        show: false
                    }
                },
                yAxis: {
                    type: 'value',
                    boundaryGap: [0, '100%'],
                    splitLine: {
                        show: false
                    }
                },
                series: [{
                    name: 'Fake Data',
                    type: 'line',
                    showSymbol: false,
                    data: data
                }]
            };
            setInterval(function () {
                for (var i = 0; i < 5; i++) {
                    data.shift();
                    data.push(randomData());
                }
                myChart.setOption({
                    series: [{
                        data: data
                    }]
                });
            }, 1000);

            option && myChart.setOption(option);

        },

        /**监测数据 */
        getMonitor1() {
            var myChart = echarts.init(this.$refs.monitor1);
            var option;

            function randomData() {
                now = new Date(+now + oneDay);
                value = value + Math.random() * 21 - 10;
                return {
                    name: now.toString(),
                    value: [
                        [now.getFullYear(), now.getMonth() + 1, now.getDate()].join('/'),
                        Math.round(value)
                    ]
                };
            }
            let data = [];
            let now = new Date(1997, 9, 3);
            let oneDay = 24 * 3600 * 1000;
            let value = Math.random() * 1000;
            for (var i = 0; i < 1000; i++) {
                data.push(randomData());
            }
            option = {
                title: {
                    text: '湿度'
                },
                tooltip: {
                    trigger: 'axis',
                    formatter: function (params) {
                        params = params[0];
                        var date = new Date(params.name);
                        return (
                            date.getDate() +
                            '/' +
                            (date.getMonth() + 1) +
                            '/' +
                            date.getFullYear() +
                            ' : ' +
                            params.value[1]
                        );
                    },
                    axisPointer: {
                        animation: false
                    }
                },
                xAxis: {
                    type: 'time',
                    splitLine: {
                        show: false
                    }
                },
                yAxis: {
                    type: 'value',
                    boundaryGap: [0, '100%'],
                    splitLine: {
                        show: false
                    }
                },
                series: [{
                    name: 'Fake Data',
                    type: 'line',
                    showSymbol: false,
                    data: data
                }]
            };
            setInterval(function () {
                for (var i = 0; i < 5; i++) {
                    data.shift();
                    data.push(randomData());
                }
                myChart.setOption({
                    series: [{
                        data: data
                    }]
                });
            }, 1000);

            option && myChart.setOption(option);

        }
    }
};
</script>
