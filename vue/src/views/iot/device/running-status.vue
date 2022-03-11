<template>
<div style="padding-left:20px;">
    <el-row :gutter="80">
        <el-col :span="9">
            <el-descriptions :column="1" border :title="title">
                <!-- 设备升级-->
                <el-descriptions-item :labelStyle="statusColor">
                    <template slot="label">
                        <svg-icon icon-class="ota" />
                        OTA升级
                    </template>
                    <el-link :underline="false" style="line-height:36px;font-size:16px;padding-right:10px;">Version {{deviceInfo.firmwareVersion}}</el-link>
                    <el-link type="success" :underline="false" style="font-size:12px;">已经是最新版本</el-link>
                    <el-button type="success" size="small" disabled style="float:right;">升级</el-button>
                    <!-- <el-input v-model="deviceInfo.firmwareVersion" placeholder="版本 " disabled>
                        <el-button slot="prepend" style="font-size:20px;">version</el-button>
                        <el-button slot="append" disabled="true">升级</el-button>
                    </el-input> -->
                </el-descriptions-item>
                <!-- bool类型-->
                <el-descriptions-item v-for="(item,index) in deviceInfo.boolList" :key="index" :labelStyle="statusColor">
                    <template slot="label">
                        <i class="el-icon-open"></i>
                        {{item.name}}
                    </template>
                    <el-switch v-model="item.value" active-text="" inactive-text="" active-value="1" inactive-value="0" style="min-width:100px;" :disabled="shadowUnEnable" />
                </el-descriptions-item>

                <!-- enum类型-->
                <el-descriptions-item v-for="(item,index) in deviceInfo.enumList" :key="index" :labelStyle="statusColor">
                    <template slot="label">
                        <i class="el-icon-s-unfold"></i>
                        {{item.name}}
                    </template>
                    <el-select v-model="item.value" placeholder="请选择" clearable size="small" :disabled="shadowUnEnable">
                        <el-option v-for="subItem in item.enumList" :key="subItem.value" :label="subItem.text" :value="subItem.value" />
                    </el-select>
                </el-descriptions-item>

                <!-- string类型-->
                <el-descriptions-item v-for="(item,index) in deviceInfo.stringList" :key="index" :labelStyle="statusColor">
                    <template slot="label">
                        <i class="el-icon-tickets"></i>
                        {{item.name}}
                    </template>
                    <el-input v-model="item.value" placeholder="请输入字符串" size="medium" :disabled="shadowUnEnable">
                        <el-button slot="append" icon="el-icon-s-promotion" style="font-size:20px;" title="指令发送"></el-button>
                    </el-input>
                </el-descriptions-item>

                <!-- array类型-->
                <el-descriptions-item v-for="(item,index) in deviceInfo.arrayList" :key="index" :labelStyle="statusColor">
                    <template slot="label">
                        <i class="el-icon-tickets"></i>
                        {{item.name}}
                    </template>
                    <el-input v-model="item.value" placeholder="请输入英文逗号分隔的字符串" size="medium" :disabled="shadowUnEnable">
                        <el-button slot="append" icon="el-icon-s-promotion" style="font-size:20px;" title="指令发送"></el-button>
                    </el-input>
                </el-descriptions-item>

                <!-- decimal类型-->
                <el-descriptions-item v-for="(item,index) in deviceInfo.decimalList" :key="index" :labelStyle="statusColor">
                    <template slot="label">
                        <i class="el-icon-star-off"></i>
                        {{item.name}}
                    </template>
                    <el-input v-model="item.value" type="number" placeholder="请输入小数 " :disabled="shadowUnEnable">
                        <el-button slot="append" icon="el-icon-s-promotion" style="font-size:20px;" title="指令发送"></el-button>
                    </el-input>
                </el-descriptions-item>

                <!-- integer类型-->
                <el-descriptions-item v-for="(item,index) in deviceInfo.integerList" :key="index" :labelStyle="statusColor">
                    <template slot="label">
                        <i class="el-icon-paperclip"></i>
                        {{item.name}}
                    </template>
                    <el-input v-model="item.value" type="integer" placeholder="请输入整数 " :disabled="shadowUnEnable">
                        <el-button slot="append" icon="el-icon-s-promotion" style="font-size:20px;" title="指令发送"></el-button>
                    </el-input>
                </el-descriptions-item>
            </el-descriptions>

            <!-- 监测数据-->
            <el-descriptions :column="2" border size="mini" style="margin-top:35px;" title="监测数据">
                <el-descriptions-item v-for="(item,index) in deviceInfo.readOnlyList" :key="index">
                    <template slot="label">
                        <i class="el-icon-odometer"></i>
                        {{item.name}}
                    </template>
                    <el-link type="primary" :underline="false">{{item.value}} {{item.unit==null?"":item.unit}}</el-link>
                </el-descriptions-item>
            </el-descriptions>
        </el-col>

        <el-col :span="14" :offset="1">
            <el-row :gutter="20">
                <el-col :span="8" v-for="(item,index) in deviceInfo.readOnlyList" :key="index" style="margin-bottom:30px;">
                    <div style="border:1px solid #eee;border-radius:8px;background-color:#FAFAFA;">
                        <div ref="map" style="height:280px;width:200px;margin:0 auto;"></div>
                    </div>
                </el-col>
            </el-row>
        </el-col>
    </el-row>
</div>
</template>

<script>
import {
    getDeviceRunningStatus
} from "@/api/iot/device"
import * as echarts from 'echarts';
export default {
    name: "running-status",
    dicts: ['iot_yes_no'],
    props: {
        device: {
            type: Object,
            default: null
        }
    },
    watch: {
        // 获取到父组件传递的device后，刷新列表
        device: function (newVal, oldVal) {
            this.deviceInfo = newVal;
            if (this.deviceInfo && this.deviceInfo.deviceId != 0) {
                getDeviceRunningStatus(this.deviceInfo.deviceId).then(response => {
                    this.deviceInfo = response.data;
                    this.getDeviceStatus(this.deviceInfo);
                    this.$nextTick(function () {
                        this.MonitorChart();
                    });
                });
            }
        }
    },
    data() {
        return {
            // 控制模块标题
            title: "设备控制 ",
            // 未启用设备影子
            shadowUnEnable: false,
            // 控制项标题背景
            statusColor: {
                background: '#67C23A',
                color: '#fff',
            },
            // 遮罩层
            loading: true,
            // 设备信息
            deviceInfo: {},
        }
    },
    created() {

    },
    methods: {
        /** 是否启用设备影子*/
        getDeviceStatus(device) {
            if (device.status == 3) {
                this.statusColor.background = '#67C23A';
                this.title += "（在线）";
            } else {
                if (device.isShadow == 1) {
                    this.statusColor.background = '#409EFF';
                    this.title += "（影子模式）";
                } else {
                    this.statusColor.background = '#909399';
                    this.title += "（设备不在线 未启用影子）";
                    this.shadowUnEnable = true;
                }
            }
        },
        /**监测图表*/
        MonitorChart() {
            for (let i = 0; i < this.deviceInfo.readOnlyList.length; i++) {
                var myChart = echarts.init(this.$refs.map[i]);
                var option;

                option = {
                    tooltip: {
                        formatter: '{a} <br/>{b} : {c}' + this.deviceInfo.readOnlyList[i].unit
                    },

                    series: [{
                        name: this.deviceInfo.readOnlyList[i].type,
                        type: 'gauge',
                        min: this.deviceInfo.readOnlyList[i].min,
                        max: this.deviceInfo.readOnlyList[i].max,
                        colorBy: 'data',
                        splitNumber: 8,
                        radius: '100%',
                        // 分割线
                        splitLine: {
                            distance: 5,
                        },
                        axisLabel: {
                            fontSize: 10
                        },
                        // 刻度线
                        axisTick: {
                            distance: 5,
                        },
                        // 仪表盘轴线
                        axisLine: {
                            lineStyle: {
                                width: 8
                            }
                        },
                        progress: {
                            show: true,
                        },
                        detail: {
                            valueAnimation: true,
                            formatter: '{value}',
                            offsetCenter: [0, "70%"],
                            fontSize: 20,
                        },
                        data: [{
                            value: this.deviceInfo.readOnlyList[i].value,
                            name: this.deviceInfo.readOnlyList[i].name + "（" + this.deviceInfo.readOnlyList[i].unit + "）"
                        }],
                        title: {
                            offsetCenter: [10, "110%"],
                            fontSize: 16
                        }

                    }]
                };

                option && myChart.setOption(option);
            }
        }
    },
}
</script>
