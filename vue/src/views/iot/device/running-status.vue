<template>
<div style="margin-top:20px;">
    <el-row :gutter="80">
        <el-col :span="8">
            <el-descriptions :column="1" border :title="title">
                <!-- bool类型-->
                <el-descriptions-item v-for="(item,index) in deviceInfo.boolList" :key="index" :labelStyle="statusColor">
                    <template slot="label">
                        <i class="el-icon-open"></i>
                        {{item.name}}
                    </template>
                    <el-switch v-model="item.value" active-text="" inactive-text="" active-value="1" inactive-value="0" style="min-width:100px;" />
                </el-descriptions-item>

                <!-- enum类型-->
                <el-descriptions-item v-for="(item,index) in deviceInfo.enumList" :key="index" :labelStyle="statusColor">
                    <template slot="label">
                        <i class="el-icon-s-unfold"></i>
                        {{item.name}}
                    </template>
                    <el-select v-model="item.value" placeholder="请选择" clearable size="small">
                        <el-option v-for="subItem in item.enumList" :key="subItem.value" :label="subItem.text" :value="subItem.value" />
                    </el-select>
                </el-descriptions-item>

                <!-- string类型-->
                <el-descriptions-item v-for="(item,index) in deviceInfo.stringList" :key="index" :labelStyle="statusColor">
                    <template slot="label">
                        <i class="el-icon-tickets"></i>
                        {{item.name}}
                    </template>
                    <el-input v-model="item.value" placeholder="请输入字符串" size="medium">
                        <el-button slot="append" icon="el-icon-s-promotion" style="font-size:20px;" title="指令发送"></el-button>
                    </el-input>
                </el-descriptions-item>

                <!-- array类型-->
                <el-descriptions-item v-for="(item,index) in deviceInfo.arrayList" :key="index" :labelStyle="statusColor">
                    <template slot="label">
                        <i class="el-icon-tickets"></i>
                        {{item.name}}
                    </template>
                    <el-input v-model="item.value" placeholder="请输入引文逗号分隔的字符串" size="medium">
                        <el-button slot="append" icon="el-icon-s-promotion" style="font-size:20px;" title="指令发送"></el-button>
                    </el-input>
                </el-descriptions-item>

                <!-- decimal类型-->
                <el-descriptions-item v-for="(item,index) in deviceInfo.decimalList" :key="index" :labelStyle="statusColor">
                    <template slot="label">
                        <i class="el-icon-star-off"></i>
                        {{item.name}}
                    </template>
                    <el-input v-model="item.value" type="number" placeholder="请输入小数 ">
                        <el-button slot="append" icon="el-icon-s-promotion" style="font-size:20px;" title="指令发送"></el-button>
                    </el-input>
                </el-descriptions-item>

                <!-- integer类型-->
                <el-descriptions-item v-for="(item,index) in deviceInfo.integerList" :key="index" :labelStyle="statusColor">
                    <template slot="label">
                        <i class="el-icon-paperclip"></i>
                        {{item.name}}
                    </template>
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

        <el-col :span="15" :offset="1">
                <el-row :gutter="20">
                    <el-col :span="8" v-for="(item,index) in deviceInfo.readOnlyList" :key="index" style="margin-bottom:30px;">
                    <el-card shadow="hover" :body-style="{ padding: '0px' }">
                        <div ref="map" style="height:300px;width:300px;"></div>
                        </el-card>
                    </el-col>
                </el-row>
        </el-col>
    </el-row>

    <el-form label-width="100px" style="margin-top:50px;margin-bottom:150px;">
        <el-form-item style="text-align: center;margin-left:-100px;margin-top:10px;">
            <el-button size="small" @click="goBack" type="info">返 回</el-button>
        </el-form-item>
    </el-form>

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
            if (this.deviceInfo.deviceId != 0) {
                getDeviceRunningStatus(this.deviceInfo.deviceId).then(response => {
                    this.deviceInfo = response.data;
                    this.$nextTick(function () {
                        this.generateMap();
                    });
                });
            }
        }
    },
    data() {
        return {
            // 控制模块标题
            title: "设备控制",
            // 控制项标题背景
            statusColor: {
                'background': '#5c7bd9',
                'color': '#fff'
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
        },
        generateMap() {
            for (let i = 0; i < this.deviceInfo.readOnlyList.length; i++) {
                if (!(this.deviceInfo.readOnlyList[i].type == "decimal" || this.deviceInfo.readOnlyList[i].type == "integer")) {
                    continue;
                }
                var myChart = echarts.init(this.$refs.map[i]);
                var option;

                option = {
                    tooltip: {
                        formatter: '{a} <br/>{b} : {c}%'
                    },
                    series: [{
                        name: this.deviceInfo.readOnlyList[i].type,
                        type: 'gauge',
                        min: this.deviceInfo.readOnlyList[i].min,
                        max: this.deviceInfo.readOnlyList[i].max,
                        progress: {
                            show: true,
                        },
                        detail: {
                            valueAnimation: true,
                            formatter: '{value}' + this.deviceInfo.readOnlyList[i].unit,
                            offsetCenter: [0, "25%"],
                            fontSize: 20,
                        },
                        data: [{
                            value: this.deviceInfo.readOnlyList[i].value,
                            name: this.deviceInfo.readOnlyList[i].name
                        }],
                        title: {
                            offsetCenter: [0, "90%"],
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
