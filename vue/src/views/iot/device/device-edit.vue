<template>
<el-card style="margin:6px;">
    <el-tabs v-model="activeName">

        <el-tab-pane label="基本信息" name="basic">
            <el-form ref="form" :model="form" :rules="rules" label-width="80px">
                <el-row :gutter="50">
                    <el-col :span="8">
                        <el-form-item label="设备名称" prop="deviceName">
                            <el-input v-model="form.deviceName" placeholder="请输入设备名称" />
                        </el-form-item>
                        <el-form-item label="设备编号" prop="serialNumber">
                            <el-input v-model="form.serialNumber" placeholder="请输入设备编号" />
                        </el-form-item>
                        <el-form-item label="固件版本" prop="firmwareVersion">
                            <el-input v-model="form.firmwareVersion" placeholder="请输入固件版本" type="number" />
                        </el-form-item>
                        <el-form-item label="备注" prop="remark">
                            <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" rows="4" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="8">
                        <el-form-item label="是否禁用" prop="isTop">
                            <el-switch v-model="form.disabled" active-text="" inactive-text="" :active-value="1" :inactive-value="0" active-color="#F56C6C">
                            </el-switch>
                        </el-form-item>
                        <el-form-item label="所属产品" prop="productName">
                            <span style="color:#409EFF">{{form.productName}}</span>
                            <el-button type="success" size="mini" style="margin-left:10px;" @click="selectProduct">选择产品</el-button>
                        </el-form-item>
                        <el-form-item label="设备状态" prop="status">
                            <el-select v-model="form.status" disabled>
                                <el-option v-for="dict in dict.type.iot_device_status" :key="dict.value" :label="dict.label" :value="parseInt(dict.value)"></el-option>
                            </el-select>
                        </el-form-item>
                        <el-form-item label="激活时间" prop="activeTime">
                            <el-date-picker clearable v-model="form.activeTime" type="date" value-format="yyyy-MM-dd" placeholder="选择激活时间" disabled>
                            </el-date-picker>
                        </el-form-item>
                        <el-form-item label="用户昵称" prop="userName">
                            <el-input v-model="form.userName" placeholder="请输入用户昵称" disabled />
                        </el-form-item>
                    </el-col>

                    <el-col :span="8">
                        <el-form-item label="wifi信号" prop="rssi">
                            <el-input v-model="form.rssi" placeholder="请输入wifi信号强度" disabled />
                        </el-form-item>
                        <el-form-item label="入网IP" prop="networkIp">
                            <el-input v-model="form.networkIp" placeholder="请输入设备入网IP" disabled />
                        </el-form-item>
                        <el-form-item label="设备经度" prop="longitude">
                            <el-input v-model="form.longitude" placeholder="请输入设备经度" disabled />
                        </el-form-item>
                        <el-form-item label="设备纬度" prop="latitude">
                            <el-input v-model="form.latitude" placeholder="请输入设备纬度" disabled />
                        </el-form-item>
                        <el-form-item label="所在地址" prop="networkAddress">
                            <el-input v-model="form.networkAddress" placeholder="请输入设备所在地址" disabled />
                        </el-form-item>
                    </el-col>
                </el-row>

            </el-form>
            <el-form label-width="100px" style="margin-top:50px;margin-bottom:150px;">
                <el-form-item style="text-align: center;margin-left:-100px;margin-top:10px;">
                    <el-button size="small" type="primary" @click="submitForm">保 存</el-button>
                    <el-button size="small" @click="goBack" type="info">返 回</el-button>
                </el-form-item>
            </el-form>

            <!-- 选择产品 -->
            <product-list ref="productList" :productId="form.productId" @productEvent="getProductData($event)" />
        </el-tab-pane>

        <el-tab-pane label="设备控制" name="runningStatus" :disabled="device.deviceId==undefined">
            <running-status ref="runningStatus" :device="device" />
        </el-tab-pane>

        <el-tab-pane label="设备定时" name="deviceTimer" :disabled="device.deviceId==undefined">
            <device-timer ref="deviceTimer" :device="device" />            
        </el-tab-pane>

        <el-tab-pane label="设备用户" name="deviceUser" :disabled="device.deviceId==undefined">
            <device-user ref="deviceUser" :device="device" @userEvent="getUserData($event)" />
        </el-tab-pane>

        <el-tab-pane label="设备日志" name="deviceLog" :disabled="device.deviceId==undefined">
            <device-log ref="deviceLog" :device="device" />
        </el-tab-pane>

        <el-tab-pane label="设备统计" name="deviceStastic" :disabled="device.deviceId==undefined">
            <device-statistic ref="deviceStatistic" :device="device" />
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
            // 设备信息
            device: {},
            // 选中选项卡
            activeName: 'basic',
            // 遮罩层
            loading: true,
            // 表单参数
            form: {},
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
                this.device = response.data;
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
                remark: null
            };
            this.resetForm("form");
        },
        /** 提交按钮 */
        submitForm() {
            this.$refs["form"].validate(valid => {
                if (valid) {
                    if (this.form.deviceId != null) {
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
            this.device.productId = product.productId;
            this.device.productName = product.productName;
        },
        // 获取选中的用户
        getUserData(user) {

        }
    }
};
</script>
