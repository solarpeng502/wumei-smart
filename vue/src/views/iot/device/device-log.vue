<template>
<div class="app-container">
    <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch" label-width="68px">
        <el-form-item label="日志名称" prop="logName">
            <el-input v-model="queryParams.logName" placeholder="请输入日志名称" clearable size="small" @keyup.enter.native="handleQuery" />
        </el-form-item>
        <el-form-item label="类型" prop="logType">
            <el-select v-model="queryParams.logType" placeholder="请选择类型" clearable size="small">
                <el-option v-for="dict in dict.type.iot_device_log_type" :key="dict.value" :label="dict.label" :value="dict.value" />
            </el-select>
        </el-form-item>
        <el-form-item label="标识符" prop="identity">
            <el-input v-model="queryParams.identity" placeholder="请输入标识符" clearable size="small" @keyup.enter.native="handleQuery" />
        </el-form-item>
        <el-form-item>
            <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
            <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
        </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="deviceLogList" size="mini">
        <el-table-column label="设备日志ID" align="center" prop="logId" />
        <el-table-column label="日志名称" align="center" prop="logName" />
        <el-table-column label="类型" align="center" prop="logType">
            <template slot-scope="scope">
                <dict-tag :options="dict.type.iot_device_log_type" :value="scope.row.logType" />
            </template>
        </el-table-column>
        <el-table-column label="日志值" align="center" prop="logValue" />
        <el-table-column label="设备ID" align="center" prop="deviceId" />
        <el-table-column label="设备名称" align="center" prop="deviceName" />
        <el-table-column label="标识符" align="center" prop="identity" />
        <el-table-column label="数据类型" align="center" prop="datatype" />
        <el-table-column label="是否监测数据" align="center" prop="isMonitor" />
        <el-table-column label="创建时间" align="center" prop="createTime" width="180">
            <template slot-scope="scope">
                <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d}') }}</span>
            </template>
        </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

</div>
</template>

<script>
import {
    listDeviceLog
} from "@/api/iot/deviceLog";

export default {
    name: "DeviceLog",
    dicts: ['iot_device_log_type'],
    data() {
        return {
            // 遮罩层
            loading: true,
            // 显示搜索条件
            showSearch: true,
            // 总条数
            total: 0,
            // 设备日志表格数据
            deviceLogList: [],
            queryParams: {
                pageNum: 1,
                pageSize: 10,
                logName: null,
                logType: null,
                logValue: null,
                deviceId: null,
                deviceName: null,
                identity: null,
                isMonitor: null,
            },
        };
    },
    created() {
        this.getList();
    },
    methods: {
        /** 查询设备日志列表 */
        getList() {
            this.loading = true;
            listDeviceLog(this.queryParams).then(response => {
                this.deviceLogList = response.rows;
                this.total = response.total;
                this.loading = false;
            });
        },
        /** 搜索按钮操作 */
        handleQuery() {
            this.queryParams.pageNum = 1;
            this.getList();
        },
        /** 重置按钮操作 */
        resetQuery() {
            this.resetForm("queryForm");
            this.handleQuery();
        },
        /** 导出按钮操作 */
        handleExport() {
            this.download('iot/deviceLog/export', {
                ...this.queryParams
            }, `deviceLog_${new Date().getTime()}.xlsx`)
        }
    }
};
</script>
