<template>
<div style="padding:6px;">
    <el-card v-show="showSearch" style="margin-bottom:6px;">
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
        </el-form>

        <el-row :gutter="10" class="mb8" style="margin-bottom:-5px;margin-top:10px;">
            <el-col :span="1.5">
                <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleEditDevice(0)" v-hasPermi="['iot:device:add']">新增</el-button>
            </el-col>
            <el-col :span="1.5">
                <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleEditDevice" v-hasPermi="['iot:device:edit']">修改</el-button>
            </el-col>
            <el-col :span="1.5">
                <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete" v-hasPermi="['iot:device:remove']">删除</el-button>
            </el-col>
            <el-col :span="1.5">
                <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport" v-hasPermi="['iot:device:export']">导出</el-button>
            </el-col>
            <el-col :span="1.5">
                <el-button type="info" plain icon="el-icon-document-copy" size="mini" @click="changeViewType" v-hasPermi="['iot:device:list']">切换视图</el-button>
            </el-col>
        </el-row>
    </el-card>

    <el-card style="padding-bottom:100px;">
        <el-row :gutter="40" v-loading="loading" v-if="viewType=='card'">
            <el-col :span="6" v-for="(item,index) in deviceList" :key="index" style="margin-bottom:40px;text-align:center;">
                <el-card :body-style="{ padding: '0px'}" shadow="always">
                    <el-image style="width:100%;height:90px;" lazy :preview-src-list="[baseUrl+item.imgUrl]" :src="baseUrl+item.imgUrl" fit="cover" v-if="item.imgUrl!=null && item.imgUrl!=''"></el-image>
                    <!-- 用于显示本地计算机、手机、树莓派等设备图片-->
                    <el-image style="width:100%;height:90px;" :preview-src-list="[require('@/assets/images/esp8266.jpg')]" :src="require('@/assets/images/esp8266.jpg')" fit="cover" v-else-if="item.productId==1"></el-image>
                    <el-image style="width:100%;height:90px;" :preview-src-list="[require('@/assets/images/esp32.jpg')]" :src="require('@/assets/images/esp32.jpg')" fit="cover" v-else-if="item.productId==2"></el-image>
                    <el-image style="width:100%;height:90px;" :preview-src-list="[require('@/assets/images/raspberry.jpg')]" :src="require('@/assets/images/raspberry.jpg')" fit="cover" v-else-if="item.productId==3"></el-image>
                    <el-image style="width:100%;height:90px;" :preview-src-list="[require('@/assets/images/telphone.jpg')]" :src="require('@/assets/images/telphone.jpg')" fit="cover" v-else-if="item.productId==4"></el-image>
                    <el-image style="width:100%;height:90px;" :preview-src-list="[require('@/assets/images/computer.jpg')]" :src="require('@/assets/images/computer.jpg')" fit="cover" v-else-if="item.productId==5"></el-image>
                    <el-image style="width:100%;height:90px;" :preview-src-list="[require('@/assets/images/product.jpg')]" :src="require('@/assets/images/product.jpg')" fit="cover" v-else></el-image>
                    <el-descriptions :column="2" size="medium" :title="item.deviceName+'（ v '+item.firmwareVersion+' ）'" style="padding:10px;">
                        <template slot="extra">
                            <div style="font-size:28px;color:#ccc;">
                                <svg-icon v-if="item.status==3 && item.rssi >= '-55'" icon-class="wifi_4" />
                                <svg-icon v-else-if="item.status==3 && item.rssi >= '-70' && item.rssi < '-55' " icon-class="wifi_3" />
                                <svg-icon v-else-if="item.status==3 && item.rssi >= '-85' && item.rssi < '-70' " icon-class="wifi_2" />
                                <svg-icon v-else-if="item.status==3 && item.rssi >= '-100' && item.rssi < '-85' " icon-class="wifi_1" />
                                <svg-icon v-else icon-class="wifi_0" />
                            </div>
                        </template>
                        <el-descriptions-item label="设备状态">
                            <dict-tag :options="dict.type.iot_device_status" :value="item.status" size="mini" />
                        </el-descriptions-item>
                        <el-descriptions-item label="产品名称">
                            <el-link type="primary" :underline="false">{{item.productName}}</el-link>
                        </el-descriptions-item>
                        <el-descriptions-item label="设备影子">
                            <dict-tag :options="dict.type.iot_is_enable" :value="item.isShadow" size="mini" />
                        </el-descriptions-item>
                        <el-descriptions-item label="激活时间">
                            <span>{{ parseTime(item.activeTime, '{y}-{m}-{d}') }}</span>
                        </el-descriptions-item>
                        <el-descriptions-item label="设备编号">
                            {{item.serialNumber}}
                        </el-descriptions-item>
                    </el-descriptions>

                    <el-descriptions :column="2" border size="mini" style="padding:0 10px;height:80px;overflow:hidden;">
                        <el-descriptions-item v-for="subItem in item.readOnlyList" :key="subItem.id">
                            <template slot="label">
                                <span style="white-space: nowrap;text-overflow: ellipsis">{{subItem.name}}</span>
                            </template>
                            <el-link type="primary" :underline="false" style="white-space: nowrap;">{{subItem.value}} {{subItem.unit==null?"":subItem.unit}}</el-link>
                        </el-descriptions-item>
                        <el-descriptions-item v-for="subItem in item.boolList" :key="subItem.id">
                            <template slot="label">
                                <div style="white-space: nowrap;text-overflow:ellipsis;width:40px;overflow:hidden;" :title="subItem.name">{{subItem.name}}</div>
                            </template>
                            <el-switch v-model="subItem.value" active-text="" inactive-text="" :active-value="'1'" :inactive-value="'0'" />
                        </el-descriptions-item>
                        <el-descriptions-item v-for="subItem in item.enumList" :key="subItem.id">
                            <template slot="label">
                                <div style="white-space: nowrap;text-overflow:ellipsis;width:40px;overflow:hidden;" :title="subItem.name">{{subItem.name}}</div>
                            </template>
                            <el-select v-model="subItem.value" placeholder="请选择" clearable size="mini" :title="subItem.name">
                                <el-option v-for="children in subItem.enumList" :key="children.value" :label="children.text" :value="children.value" />
                            </el-select>
                        </el-descriptions-item>
                        <el-descriptions-item v-for="subItem in item.decimalList" :key="subItem.id">
                            <template slot="label">
                                <div style="white-space: nowrap;text-overflow:ellipsis;width:40px;overflow:hidden;" :title="subItem.name">{{subItem.name}}</div>
                            </template>
                            <el-input v-model="subItem.value" :placeholder="'小数：'+subItem.name" size="mini" :title="'小数：'+subItem.name">
                                <el-button slot="append" icon="el-icon-s-promotion" style="font-size:16px;padding:10px;" title="指令发送"></el-button>
                            </el-input>
                        </el-descriptions-item>
                        <el-descriptions-item v-for="subItem in item.integerList" :key="subItem.id">
                            <template slot="label">
                                <div style="white-space: nowrap;text-overflow:ellipsis;width:40px;overflow:hidden;" :title="subItem.name">{{subItem.name}}</div>
                            </template>
                            <el-input v-model="subItem.value" :placeholder="'整数：'+subItem.name" :title="'整数：'+subItem.name" size="mini">
                                <el-button slot="append" icon="el-icon-s-promotion" style="font-size:16px;padding:10px;" title="指令发送"></el-button>
                            </el-input>
                        </el-descriptions-item>
                        <el-descriptions-item v-for="subItem in item.arrayList" :key="subItem.id">
                            <template slot="label">
                                <div style="white-space: nowrap;text-overflow:ellipsis;width:40px;overflow:hidden;" :title="subItem.name">{{subItem.name}}</div>
                            </template>
                            <el-input v-model="subItem.value" :placeholder="'数组：'+subItem.name" :title="'数组：'+subItem.name" size="mini">
                                <el-button slot="append" icon="el-icon-s-promotion" style="font-size:16px;padding:10px;" title="指令发送"></el-button>
                            </el-input>
                        </el-descriptions-item>
                        <el-descriptions-item v-for="subItem in item.stringList" :key="subItem.id">
                            <template slot="label">
                                <div style="white-space: nowrap;text-overflow:ellipsis;width:40px;overflow:hidden;" :title="subItem.name">{{subItem.name}}</div>
                            </template>
                            <el-input v-model="subItem.value" :placeholder="'字符串：'+subItem.name" :title="'字符串：'+subItem.name" size="mini">
                                <el-button slot="append" icon="el-icon-s-promotion" style="font-size:16px;padding:10px;" title="指令发送"></el-button>
                            </el-input>
                        </el-descriptions-item>
                    </el-descriptions>
                    <el-button-group style="padding:10px 10px 20px 10px;">
                        <el-button type="success" size="mini" icon="el-icon-odometer" @click="handleMonitor(item)" v-hasPermi="['iot:device:edit']">实时监测</el-button>
                        <el-button type="danger" size="mini" icon="el-icon-delete" @click="handleDelete(item)" v-hasPermi="['iot:device:remove']">删除</el-button>
                        <el-button type="primary" size="mini" icon="el-icon-edit" @click="handleEditDevice(item)" v-hasPermi="['iot:device:edit']">查看详情 </el-button>
                    </el-button-group>
                </el-card>
            </el-col>
        </el-row>

        <el-table v-loading="loading" :data="deviceList" @selection-change="handleSelectionChange" v-if="viewType=='list'">
            <el-table-column type="selection" width="55" align="center" />
            <el-table-column label="图片" align="center" prop="imgUrl">
                <template slot-scope="scope">
                    <el-image style="height: 70px" lazy :preview-src-list="[baseUrl+scope.row.imgUrl]" :src="baseUrl+scope.row.imgUrl" fit="cover" v-if="scope.row.imgUrl!=null && scope.row.imgUrl!=''"></el-image>
                    <el-image style="height:70px;" :preview-src-list="[require('@/assets/images/esp8266.jpg')]" :src="require('@/assets/images/esp8266.jpg')" fit="cover" v-else-if="scope.row.productId==1"></el-image>
                    <el-image style="height:70px;" :preview-src-list="[require('@/assets/images/esp32.jpg')]" :src="require('@/assets/images/esp32.jpg')" fit="cover" v-else-if="scope.row.productId==2"></el-image>
                    <el-image style="height:70px;" :preview-src-list="[require('@/assets/images/raspberry.jpg')]" :src="require('@/assets/images/raspberry.jpg')" fit="cover" v-else-if="scope.row.productId==3"></el-image>
                    <el-image style="height:70px;" :preview-src-list="[require('@/assets/images/telphone.jpg')]" :src="require('@/assets/images/telphone.jpg')" fit="cover" v-else-if="scope.row.productId==4"></el-image>
                    <el-image style="height:70px;" :preview-src-list="[require('@/assets/images/computer.jpg')]" :src="require('@/assets/images/computer.jpg')" fit="cover" v-else-if="scope.row.productId==5"></el-image>
                    <el-image style="height:70px;" :preview-src-list="[require('@/assets/images/product.jpg')]" :src="require('@/assets/images/product.jpg')" fit="cover" v-else></el-image>
                </template>
            </el-table-column>
            <el-table-column label="设备名称" align="center" prop="deviceName">
                <template slot-scope="scope">
                    <el-link type="primary" :underline="false" @click="handleEditDevice(scope.row)">{{scope.row.deviceName}}</el-link>
                </template>
            </el-table-column>
            <el-table-column label="设备编号" align="center" prop="serialNumber" />
            <el-table-column label="产品名称" align="center" prop="productName" />
            <el-table-column label="运行状态" align="left" min-width="200" prop="list">
                <template slot-scope="scope">
                    <el-descriptions :column="1" border size="mini">
                        <el-descriptions-item v-for="subItem in scope.row.readOnlyList" :key="subItem.id">
                            <template slot="label">
                                <span style="white-space: nowrap;">{{subItem.name}}</span>
                            </template>
                            <el-link type="primary" :underline="false">{{subItem.value}} {{subItem.unit==null?"":subItem.unit}}</el-link>
                        </el-descriptions-item>
                        <el-descriptions-item v-for="subItem in scope.row.boolList" :key="subItem.id">
                            <template slot="label">
                                <span style="white-space: nowrap;">{{subItem.name}}</span>
                            </template>
                            <el-switch v-model="subItem.value" active-text="" inactive-text="" :active-value="'1'" :inactive-value="'0'" />
                        </el-descriptions-item>
                        <el-descriptions-item v-for="subItem in scope.row.enumList" :key="subItem.id">
                            <template slot="label">
                                <span style="white-space: nowrap;">{{subItem.name}}</span>
                            </template>
                            <el-select v-model="subItem.value" placeholder="请选择" clearable size="mini" :title="subItem.name">
                                <el-option v-for="children in subItem.enumList" :key="children.value" :label="children.text" :value="children.value" />
                            </el-select>
                        </el-descriptions-item>
                        <el-descriptions-item v-for="subItem in scope.row.decimalList" :key="subItem.id">
                            <template slot="label">
                                <span style="white-space: nowrap;">{{subItem.name}}</span>
                            </template>
                            <el-input v-model="subItem.value" :placeholder="'小数：'+subItem.name" size="mini" :title="'小数：'+subItem.name">
                                <el-button slot="append" icon="el-icon-s-promotion" style="font-size:16px;padding:10px;" title="指令发送"></el-button>
                            </el-input>
                        </el-descriptions-item>
                        <el-descriptions-item v-for="subItem in scope.row.integerList" :key="subItem.id">
                            <template slot="label">
                                <span style="white-space: nowrap;">{{subItem.name}}</span>
                            </template>
                            <el-input v-model="subItem.value" :placeholder="'整数：'+subItem.name" :title="'整数：'+subItem.name" size="mini">
                                <el-button slot="append" icon="el-icon-s-promotion" style="font-size:16px;padding:10px;" title="指令发送"></el-button>
                            </el-input>
                        </el-descriptions-item>
                        <el-descriptions-item v-for="subItem in scope.row.arrayList" :key="subItem.id">
                            <template slot="label">
                                <span style="white-space: nowrap;">{{subItem.name}}</span>
                            </template>
                            <el-input v-model="subItem.value" :placeholder="'数组：'+subItem.name" :title="'数组：'+subItem.name" size="mini">
                                <el-button slot="append" icon="el-icon-s-promotion" style="font-size:16px;padding:10px;" title="指令发送"></el-button>
                            </el-input>
                        </el-descriptions-item>
                        <el-descriptions-item v-for="subItem in scope.row.stringList" :key="subItem.id">
                            <template slot="label">
                                <span style="white-space: nowrap;">{{subItem.name}}</span>
                            </template>
                            <el-input v-model="subItem.value" :placeholder="'字符串：'+subItem.name" :title="'字符串：'+subItem.name" size="mini">
                                <el-button slot="append" icon="el-icon-s-promotion" style="font-size:16px;padding:10px;" title="指令发送"></el-button>
                            </el-input>
                        </el-descriptions-item>
                    </el-descriptions>
                </template>
            </el-table-column>
            <!-- wifi信号强度(信号极好4格[-55—— 0]，信号好3格[-70—— -55），信号一般2格[-85—— -70），信号差1格[-100—— -85）) -->
            <el-table-column label="信号" align="center" prop="rssi" width="100">
                <template slot-scope="scope">
                    <div style="font-size: 30px;color:#ccc;">
                        <svg-icon v-if="scope.row.status==3 && scope.row.rssi >= '-55'" icon-class="wifi_4" />
                        <svg-icon v-else-if="scope.row.status==3 && scope.row.rssi >= '-70' && scope.row.rssi < '-55' " icon-class="wifi_3" />
                        <svg-icon v-else-if="scope.row.status==3 && scope.row.rssi >= '-85' && scope.row.rssi < '-70' " icon-class="wifi_2" />
                        <svg-icon v-else-if="scope.row.status==3 && scope.row.rssi >= '-100' && scope.row.rssi < '-85' " icon-class="wifi_1" />
                        <svg-icon v-else icon-class="wifi_0" />
                    </div>
                </template>
            </el-table-column>
            <!--设备状态（1-未激活，2-禁用，3-在线，4-离线）-->
            <el-table-column label="设备状态" align="center" prop="status">
                <template slot-scope="scope">
                    <dict-tag :options="dict.type.iot_device_status" :value="scope.row.status" />
                </template>
            </el-table-column>
            <el-table-column label="设备影子" align="center" prop="isShadow">
                <template slot-scope="scope">
                    <dict-tag :options="dict.type.iot_is_enable" :value="scope.row.isShadow" />
                </template>
            </el-table-column>
            <el-table-column label="固件版本" align="center" prop="firmwareVersion" width="100">
                <template slot-scope="scope">
                    <span>v </span> {{scope.row.firmwareVersion}}
                </template>
            </el-table-column>
            <el-table-column label="用户昵称" align="center" prop="userName" />
            <el-table-column label="激活时间" align="center" prop="activeTime" width="150">
                <template slot-scope="scope">
                    <span>{{ parseTime(scope.row.activeTime, '{y}-{m}-{d}') }}</span>
                </template>
            </el-table-column>
            <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="200">
                <template slot-scope="scope">
                    <el-button size="small" type="success" style="padding:5px;" icon="el-icon-odometer" @click="handleMonitor(scope.row)" v-hasPermi="['iot:device:edit']">监测</el-button>
                    <el-button size="small" type="primary" style="padding:5px;" icon="el-icon-edit" @click="handleEditDevice(scope.row)" v-hasPermi="['iot:device:edit']">详情</el-button>
                    <el-button size="small" type="danger" style="padding:5px;" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['iot:device:remove']">删除</el-button>
                </template>
            </el-table-column>
        </el-table>

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
            // 视图类型
            viewType: "card",
            // mqtt客户端
            client: {},
            // 遮罩层
            loading: true,
            // 选中数组
            ids: [],
            // 非单个禁用
            single: true,
            // 非多个禁用
            multiple: true,
            // 显示搜索条件
            showSearch: true,
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
                username: "xxx",
                password: getToken(),
                cleanSession: false,
                keepAlive: 60,
                clientId: 'web-' + Math.random().toString(16).substr(2, 8),
                connectTimeout: 4000
            }
            this.client = mqtt.connect('ws://wumei.live:8083/mqtt', options);
            this.client.on("connect", (e) => {
                console.log("成功连接服务器:", e);
                //订阅三个名叫'top/#', 'three/#'和'#'的主题
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
        // 多选框选中数据
        handleSelectionChange(selection) {
            this.ids = selection.map(item => item.deviceId)
            this.single = selection.length !== 1
            this.multiple = !selection.length
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
        /** 导出按钮操作 */
        handleExport() {
            this.download('iot/device/export', {
                ...this.queryParams
            }, `device_${new Date().getTime()}.xlsx`)
        },
        /**改变视图**/
        changeViewType() {
            this.viewType = this.viewType == "card" ? "list" : "card";
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
