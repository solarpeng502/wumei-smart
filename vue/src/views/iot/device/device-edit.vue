<template>
<el-card style="margin:6px;padding-bottom:100px;">
    <el-tabs v-model="activeName" tab-position="left" style="padding:10px;">
        <el-tab-pane name="basic">
            <span slot="label"> * 基本信息</span>
            <el-form ref="form" :model="form" :rules="rules" label-width="100px">
                <el-row :gutter="100">
                    <el-col :span="7">
                        <el-form-item label="设备名称" prop="deviceName">
                            <el-input v-model="form.deviceName" placeholder="请输入设备名称" />
                        </el-form-item>
                        <el-form-item label="设备编号" prop="serialNumber">
                            <el-input v-model="form.serialNumber" placeholder="请输入设备编号">
                                <el-button slot="append" @click="generateNum">生成</el-button>
                            </el-input>
                        </el-form-item>
                        <el-form-item label="所属产品" prop="productName">
                            <span style="color:#409EFF">{{form.productName}}</span>
                            <el-button type="info" size="mini" style="margin-left:10px;float:right;" @click="selectProduct">选择产品</el-button>
                        </el-form-item>
                        <el-form-item label="固件版本" prop="firmwareVersion">
                            <el-input v-model="form.firmwareVersion" placeholder="请输入固件版本" type="number"><template slot="prepend">Version</template></el-input>
                        </el-form-item>
                        <el-form-item label="禁用设备" prop="isDisabled">
                            <el-switch v-model="form.isDisabled" active-text="" inactive-text="" :active-value="1" :inactive-value="0" active-color="#F56C6C" @change="sttusChange">
                            </el-switch>
                        </el-form-item>
                        <el-form-item label="自定义位置" prop="isCustomerLocation">
                            <el-switch v-model="form.isCustomerLocation" active-text="" inactive-text="" :active-value="1" :inactive-value="0">
                            </el-switch>
                        </el-form-item>
                        <el-form-item label="备注" prop="remark">
                            <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" rows="1" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="7">
                        <!--
                        <el-form-item label="设备图片" prop="imageUrl">
                            <el-image style="height:225px;border:1px solid #dee4ed;border-radius:5px;padding:5px;" :src="imageUrl" :preview-src-list="[imageUrl]" fit="cover"></el-image>
                        </el-form-item>
                        -->
                        <el-form-item label="设备状态" prop="status">
                            <el-select v-model="form.status" disabled style="width:100%">
                                <el-option v-for="dict in dict.type.iot_device_status" :key="dict.value" :label="dict.label" :value="parseInt(dict.value)"></el-option>
                            </el-select>
                        </el-form-item>
                        <el-form-item label="激活时间" prop="activeTime">
                            <el-date-picker clearable v-model="form.activeTime" type="date" value-format="yyyy-MM-dd" placeholder="选择激活时间" disabled style="width:100%">
                            </el-date-picker>
                        </el-form-item>
                        <el-form-item label="设备信号" prop="rssi">
                            <el-input v-model="form.rssi" placeholder="请输入wifi信号强度" disabled />
                        </el-form-item>
                        <el-form-item label="入网IP" prop="networkIp">
                            <el-input v-model="form.networkIp" placeholder="请输入设备入网IP" disabled />
                        </el-form-item>
                        <el-form-item label="设备经度" prop="longitude">
                            <el-input v-model="form.longitude" placeholder="请输入设备经度" type="number" :disabled="form.isCustomerLocation==0">
                                <el-link slot="append" href="https://api.map.baidu.com/lbsapi/getpoint/index.html" target="_blank">坐标拾取</el-link>
                            </el-input>
                        </el-form-item>
                        <el-form-item label="设备纬度" prop="latitude">
                            <el-input v-model="form.latitude" placeholder="请输入设备纬度" type="number" :disabled="form.isCustomerLocation==0">
                                <el-link slot="append" href="https://api.map.baidu.com/lbsapi/getpoint/index.html" target="_blank">坐标拾取</el-link>
                            </el-input>
                        </el-form-item>
                        <el-form-item label="所在地址" prop="networkAddress">
                            <el-input v-model="form.networkAddress" placeholder="请输入设备所在地址" :disabled="form.isCustomerLocation==0" />
                        </el-form-item>
                    </el-col>

                    <el-col :span="10">
                        <div style="border:1px solid #dfe4ed;border-radius:5px;padding:5px;line-height:0px;">
                            <div id="allmap" style="height:400px;width:100%"></div>
                        </div>
                    </el-col>
                </el-row>
            </el-form>

            <el-form label-width="100px" style="margin-top:50px;">
                <el-form-item style="text-align: center;margin-left:-100px;margin-top:10px;">
                    <el-button size="small" type="primary" @click="submitForm">保 存</el-button>
                </el-form-item>
            </el-form>

            <!-- 选择产品 -->
            <product-list ref="productList" :productId="form.productId" @productEvent="getProductData($event)" />
        </el-tab-pane>

        <el-tab-pane name="runningStatus" :disabled="form.deviceId==undefined">
            <span slot="label">运行状态</span>
            <running-status ref="runningStatus" :device="form" />
        </el-tab-pane>

        <el-tab-pane name="deviceTimer" :disabled="form.deviceId==undefined">
            <span slot="label">设备定时</span>
            <device-timer ref="deviceTimer" :device="form" />
        </el-tab-pane>

        <el-tab-pane name="deviceUser" :disabled="form.deviceId==undefined">
            <span slot="label">设备用户</span>
            <device-user ref="deviceUser" :device="form" @userEvent="getUserData($event)" />
        </el-tab-pane>

        <el-tab-pane name="deviceLog" :disabled="form.deviceId==undefined">
            <span slot="label">设备日志</span>
            <device-log ref="deviceLog" :device="form" />
        </el-tab-pane>

        <el-tab-pane name="deviceStastic" :disabled="form.deviceId==undefined">
            <span slot="label">监测统计</span>
            <device-statistic ref="deviceStatistic" :device="form" />
        </el-tab-pane>

        <el-tab-pane label="" disabled name="01" />
        <el-tab-pane label="" disabled name="02" />
        <el-tab-pane label="" disabled name="03" />
        <el-tab-pane name="05">
            <span slot="label">
                <el-button type="info" size="mini" @click="goBack()">返回列表</el-button>
            </span>
        </el-tab-pane>
    </el-tabs>

</el-card>
</template>

<script>
import productList from "./product-list"
import deviceLog from './device-log';
import deviceUser from './device-user';
import runningStatus from './running-status';
import deviceStatistic from './device-statistic'
import deviceTimer from './device-timer'
import {
    loadBMap
} from '../map.js'
import {
    getDevice,
    addDevice,
    updateDevice
} from "@/api/iot/device";

export default {
    name: "device-edit",
    dicts: ['iot_device_status'],
    components: {
        deviceLog,
        deviceUser,
        deviceStatistic,
        runningStatus,
        productList,
        deviceTimer,
    },
    data() {
        return {
            // 选中选项卡
            activeName: 'basic',
            // 遮罩层
            loading: true,
            // 设备状态（1-未激活，2-禁用，3-在线，4-离线）
            deviceStatus: 1,
            // 表单参数
            form: {},
            // 图片地址
            imageUrl: require('@/assets/images/product.jpg'),
            // 地址
            baseUrl: process.env.VUE_APP_BASE_API,
            // 地图相关
            map: null,
            local: null,
            mk: null,
            latitude: '',
            longitude: '',
            keyWords: '',
            // 表单校验
            rules: {
                deviceName: [{
                    required: true,
                    message: "设备名称不能为空",
                    trigger: "blur"
                }],
                productName: [{
                    required: true,
                    message: "产品不能为空",
                    trigger: "blur"
                }],
                serialNumber: [{
                    required: true,
                    message: "设备编号不能为空",
                    trigger: "blur"
                }],
                firmwareVersion: [{
                    required: true,
                    message: "固件版本不能为空",
                    trigger: "blur"
                }],
            }
        };
    },
    created() {
        // 获取设备信息
        const deviceId = this.$route.query && this.$route.query.deviceId;
        if (deviceId != 0) {
            getDevice(deviceId).then(response => {
                this.form = response.data;
                this.deviceStatus = this.form.status;
                if (this.form.imgUrl != null && this.form.imgUrl != "") {
                    this.imageUrl = this.form.imgUrl;
                }
                // 获取地图
                this.$nextTick(() => {
                    loadBMap().then(() => {
                        this.getmap();
                    });
                })
            });
        }

    },
    methods: {
        /** 返回按钮 */
        goBack() {
            const obj = {
                path: "/iot/device",
                query: {
                    t: Date.now(),
                    pageNum: this.$route.query.pageNum
                }
            };
            this.$tab.closeOpenPage(obj);
            this.reset();
        },
        // 表单重置
        reset() {
            this.form = {
                deviceId: null,
                deviceName: null,
                productId: null,
                productName: null,
                userId: null,
                userName: null,
                tenantId: null,
                tenantName: null,
                serialNumber: null,
                firmwareVersion: null,
                status: 0,
                rssi: null,
                networkAddress: null,
                networkIp: null,
                longitude: null,
                latitude: null,
                activeTime: null,
                createBy: null,
                createTime: null,
                updateBy: null,
                updateTime: null,
                remark: null,
                isCustomerLocation: 0,
                isDisabled: 0,
            };
            this.resetForm("form");
        },
        /** 提交按钮 */
        submitForm() {
            this.$refs["form"].validate(valid => {
                if (valid) {
                    if (this.form.deviceId != 0) {
                        updateDevice(this.form).then(response => {
                            this.$modal.alertSuccess("修改成功");
                            this.open = false;
                        });
                    } else {
                        addDevice(this.form).then(response => {
                            this.$modal.alertSuccess("新增成功");
                            this.open = false;
                        });
                    }
                }
            });
        },
        /**选择产品 */
        selectProduct() {
            this.$refs.productList.open = true;
            this.$refs.productList.getList();
        },
        /**获取选中的产品 */
        getProductData(product) {
            this.form.productId = product.productId;
            this.form.productName = product.productName;
            console.log(product);
        },
        // 获取选中的用户
        getUserData(user) {

        },

        // 地图定位
        getmap() {
            this.map = new BMap.Map('allmap')
            let point = null;
            if (this.form.longitude != null && this.form.longitude != "" && this.form.latitude != null && this.form.latitude != "") {
                point = new BMap.Point(this.form.longitude, this.form.latitude)
            } else {
                point = new BMap.Point(116.404, 39.915)
            }
            this.map.centerAndZoom(point, 19)
            this.map.enableScrollWheelZoom(true) // 开启鼠标滚轮缩放
            this.map.addControl(new BMap.NavigationControl())

            // 标注设备位置
            this.mk = new BMap.Marker(point)
            this.map.addOverlay(this.mk)
            this.map.panTo(point)
        },
        // 禁用状态改变事件（1-未激活，2-禁用，3-在线，4-离线）
        sttusChange(data) {
            if (data == 1) {
                this.form.status = 2;
            } else {
                this.form.status = this.deviceStatus;
            }
        },
        // 生成随机字母和数字
        generateNum() {
            var chars = ["0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"];
            var res = "";
            for (var i = 0; i < 15; i++) {
                var id = Math.floor(Math.random() * 36);
                res += chars[id];
            }
            this.form.serialNumber = "D" + res;
        }

    }
};
</script>
