<template>
<div>

    <el-descriptions :column="3" border>
        <!-- 监测数据-->
        <el-descriptions-item v-for="(item,index) in deviceInfo.monitorList" :key="index">
            <template slot="label">
                <i class="el-icon-odometer"></i>
                {{item.name}}
            </template>
            <el-link type="primary" :underline="false">{{item.value}}</el-link>
        </el-descriptions-item>

        <!-- bool类型-->
        <el-descriptions-item v-for="(item,index) in deviceInfo.boolList" :key="index">
            <template slot="label">
                <i class="el-icon-open"></i>
                {{item.name}}
            </template>
            <el-switch v-model="item.value" active-text="" inactive-text="" active-value="1" inactive-value="0" style="min-width:100px;" />
        </el-descriptions-item>

        <!-- enum类型-->
        <el-descriptions-item v-for="(item,index) in deviceInfo.enumList" :key="index">
            <template slot="label">
                <i class="el-icon-s-unfold"></i>
                {{item.name}}
            </template>
            <el-select v-model="item.value" placeholder="请选择" clearable size="small">
                <el-option v-for="subItem in item.enumList" :key="subItem.value" :label="subItem.text" :value="subItem.value" />
            </el-select>
        </el-descriptions-item>

        <!-- string类型-->
        <el-descriptions-item v-for="(item,index) in deviceInfo.stringList" :key="index">
            <template slot="label">
                <i class="el-icon-tickets"></i>
                {{item.name}}
            </template>
            <el-input v-model="item.value" placeholder="请输入字符串" size="medium">
                <el-button slot="append" icon="el-icon-s-promotion" style="font-size:20px;" title="指令发送"></el-button>
            </el-input>
        </el-descriptions-item>

        <!-- array类型-->
        <el-descriptions-item v-for="(item,index) in deviceInfo.arrayList" :key="index">
            <template slot="label">
                <i class="el-icon-tickets"></i>
                {{item.name}}
            </template>
            <el-input v-model="item.value" placeholder="请输入引文逗号分隔的字符串" size="medium">
                <el-button slot="append" icon="el-icon-s-promotion" style="font-size:20px;" title="指令发送"></el-button>
            </el-input>
        </el-descriptions-item>

        <!-- decimal类型-->
        <el-descriptions-item v-for="(item,index) in deviceInfo.decimalList" :key="index">
            <template slot="label">
                <i class="el-icon-star-off"></i>
                {{item.name}}
            </template>
            <el-input v-model="item.value" type="number" placeholder="请输入小数 ">
                <el-button slot="append" icon="el-icon-s-promotion" style="font-size:20px;" title="指令发送"></el-button>
            </el-input>
        </el-descriptions-item>

        <!-- integer类型-->
        <el-descriptions-item v-for="(item,index) in deviceInfo.integerList" :key="index">
            <template slot="label">
                <i class="el-icon-paperclip"></i>
                {{item.name}}
            </template>
            <!-- <el-slider v-model="item.value" :step="item.step" :min="item.min" :max="item.max" :show-input="true" style="max-width:500px;">
            </el-slider>-->
        </el-descriptions-item>
    </el-descriptions>

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
export default {
    name: "running-status",
    dicts: ['iot_yes_no'],
    props: {
        device: {
            type: Object,
            default: null
        }
    },
    data() {
        return {
            // 遮罩层
            loading: true,
            // 设备信息
            deviceInfo: {},
        }
    },
    watch: {
        // 获取到父组件传递的device后，刷新列表
        device: function (newVal, oldVal) {
            this.deviceInfo = newVal;
            if (this.deviceInfo.deviceId != 0) {
                getDeviceRunningStatus(this.deviceInfo.deviceId).then(response => {
                    this.deviceInfo = response.data;
                });
            }
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
            this.reset();
        },
    },
}
</script>
