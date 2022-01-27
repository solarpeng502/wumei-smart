<template>
<div class="app-container">
    <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch" label-width="68px">
        <el-form-item label="定时名称" prop="jobName">
            <el-input v-model="queryParams.jobName" placeholder="请输入定时名称" clearable size="small" @keyup.enter.native="handleQuery" />
        </el-form-item>
        <el-form-item label="定时状态" prop="status">
            <el-select v-model="queryParams.status" placeholder="请选择定时状态" clearable size="small">
                <el-option v-for="dict in dict.type.sys_job_status" :key="dict.value" :label="dict.label" :value="dict.value" />
            </el-select>
        </el-form-item>
        <el-form-item>
            <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
            <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
        </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
        <el-col :span="1.5">
            <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['iot:job:add']">新增</el-button>
        </el-col>
        <el-col :span="1.5">
            <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate" v-hasPermi="['iot:job:edit']">修改</el-button>
        </el-col>
        <el-col :span="1.5">
            <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete" v-hasPermi="['iot:job:remove']">删除</el-button>
        </el-col>
        <el-col :span="1.5">
            <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport" v-hasPermi="['iot:job:export']">导出</el-button>
        </el-col>
        <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="jobList" @selection-change="handleSelectionChange" size="mini">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="定时编号" width="100" align="center" prop="jobId" />
        <el-table-column label="定时名称" align="center" prop="jobName" :show-overflow-tooltip="true" />
        <el-table-column label="定时组名" align="center" prop="jobGroup">
            <template slot-scope="scope">
                <dict-tag :options="dict.type.sys_job_group" :value="scope.row.jobGroup" />
            </template>
        </el-table-column>
        <el-table-column label="动作" align="center" prop="actions" :show-overflow-tooltip="true" />
        <el-table-column label="CRON表达式" align="center" prop="cronExpression" :show-overflow-tooltip="true" />
        <el-table-column label="状态" align="center">
            <template slot-scope="scope">
                <el-switch v-model="scope.row.status" active-value="0" inactive-value="1" @change="handleStatusChange(scope.row)"></el-switch>
            </template>
        </el-table-column>
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
            <template slot-scope="scope">
                <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-hasPermi="['iot:job:edit']">修改</el-button>
                <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['iot:job:remove']">删除</el-button>
                <el-dropdown size="mini" @command="(command) => handleCommand(command, scope.row)" v-hasPermi="['iot:job:changeStatus', 'iot:job:query']">
                    <span class="el-dropdown-link">
                        <i class="el-icon-d-arrow-right el-icon--right"></i>更多
                    </span>
                    <el-dropdown-menu slot="dropdown">
                        <el-dropdown-item command="handleRun" icon="el-icon-caret-right" v-hasPermi="['iot:job:changeStatus']">执行一次</el-dropdown-item>
                        <el-dropdown-item command="handleView" icon="el-icon-view" v-hasPermi="['iot:job:query']">定时详细</el-dropdown-item>
                    </el-dropdown-menu>
                </el-dropdown>
            </template>
        </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <!-- 添加或修改定时定时对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="800px" append-to-body>
        <el-form ref="form" :model="form" :rules="rules" label-width="120px">
            <el-row>
                <el-col :span="15">
                    <el-form-item label="定时名称" prop="jobName">
                        <el-input v-model="form.jobName" placeholder="请输入定时名称" />
                    </el-form-item>
                </el-col>
                <el-col :span="24">
                    <el-form-item label="执行时间" prop="timerTimeValue">
                        <el-time-picker v-model="timerTimeValue" value-format="HH:mm" placeholder="选择时间" style="width:354px;" @change="timeChange"></el-time-picker>
                    </el-form-item>
                </el-col>
                <el-col :span="24">
                    <el-form-item label="重复执行" prop="timerWeek">
                        <el-row>
                            <el-col :span="4">
                                <el-select v-model="timerWeekRepeatValue" placeholder="请选择" @change="repeatChange">
                                    <el-option v-for="item in timerWeekRepeats" :key="item.value" :label="item.label" :value="item.value">
                                    </el-option>
                                </el-select>
                            </el-col>
                            <el-col :span="15" :offset="1" v-if="timerWeekRepeatValue==3">
                                <el-select v-model="timerWeekValue" placeholder="请选择" multiple style="width:485px" @change="weekChange">
                                    <el-option v-for="item in timerWeeks" :key="item.value" :label="item.label" :value="item.value">
                                    </el-option>
                                </el-select>
                            </el-col>
                        </el-row>
                    </el-form-item>
                </el-col>
                <el-col :span="24">
                    <el-form-item label="cron表达式" prop="">
                        <el-row>
                            <el-col :span="18">
                                <el-input v-model="form.cronExpression" placeholder="cron执行表达式" :disabled="form.isAdvance==0">
                                    <template slot="append">
                                        <el-button type="primary" @click="handleShowCron" :disabled="form.isAdvance==0">
                                            生成表达式
                                            <i class="el-icon-time el-icon--right"></i>
                                        </el-button>
                                    </template>
                                </el-input>
                            </el-col>
                            <el-col :span="4" :offset="1">
                                <el-checkbox v-model="form.isAdvance" :true-label="1" :false-label="0" @change="customerCronChange">自定义表达式</el-checkbox>
                            </el-col>
                        </el-row>
                    </el-form-item>
                </el-col>

                <el-col :span="24">
                    <div style="padding-bottom:15px;padding:0 20px;">
                        <el-divider></el-divider>
                    </div>
                    <el-form-item label="执行动作">
                        <el-row v-for="(item,index) in form.actions" :key="index" style="margin-bottom:10px;">
                            <el-col :span="4">
                                <el-select v-model="item.type" placeholder="请选择类别">
                                    <el-option v-for="subItem in modelTypes" :key="subItem.value" :label="subItem.label" :value="subItem.value" @change="thingsModelTypeChange($event,index)">
                                    </el-option>
                                </el-select>
                            </el-col>
                            <el-col :span="4" :offset="1">
                                <el-select v-model="item.id" placeholder="请选择" v-if="item.type==1" @change="thingsModelItemChange($event,index)">
                                    <el-option v-for="subItem in thingsModel.properties" :key="subItem.id" :label="subItem.name" :value="subItem.id">
                                    </el-option>
                                </el-select>
                                <el-select v-model="item.id" placeholder="请选择" v-else-if="item.type==2" @change="thingsModelItemChange($event,index)">
                                    <el-option v-for="subItem in thingsModel.functions" :key="subItem.id" :label="subItem.name" :value="subItem.id">
                                    </el-option>
                                </el-select>
                            </el-col>
                            <el-col :span="10" :offset="1">
                                <!--物模型项的值-->
                                <span v-if="thingsModelItems[index].datatype.type=='integer' || thingsModelItems[index].datatype.type=='decimal'">
                                    <el-input v-model="item.value" placeholder="值" :max="thingsModelItems[index].datatype.max" :min="thingsModelItems[index].datatype.min" type="number" />
                                </span>
                                <span v-else-if="thingsModelItems[index].datatype.type=='bool'">
                                    <el-switch v-model="item.value" :active-text="thingsModelItems[index].datatype.trueText" :inactive-text="thingsModelItems[index].datatype.falseText" active-value="1" inactive-value="0">
                                    </el-switch>
                                </span>
                                <span v-else-if="thingsModelItems[index].datatype.type=='enum'">
                                    <el-select v-model="item.value" placeholder="请选择">
                                        <el-option v-for="subItem in thingsModelItems[index].datatype.enumList" :key="subItem.value" :label="subItem.text" :value="subItem.value">
                                        </el-option>
                                    </el-select>
                                </span>
                                <span v-else-if="thingsModelItems[index].datatype.type=='string'">
                                    <el-input v-model="item.value" placeholder="值" :max="thingsModelItems[index].datatype.maxLength" />
                                </span>
                                <span v-else-if="thingsModelItems[index].datatype.type=='array'">
                                    <el-input v-model="item.value" placeholder="值" />
                                </span>
                            </el-col>
                            <el-col :span="2" :offset="1" v-if="index!=0"><a style="color:#F56C6C" @click="removeEnumItem(index)">删除</a></el-col>
                        </el-row>
                        <div>+ <a style="color:#409EFF" @click="addEnumItem()">添加执行动作</a></div>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="定时状态">
                        <el-radio-group v-model="form.status">
                            <el-radio v-for="dict in dict.type.sys_job_status" :key="dict.value" :label="dict.value">{{dict.label}}</el-radio>
                        </el-radio-group>
                    </el-form-item>
                </el-col>
            </el-row>
        </el-form>
        <div slot="footer" class="dialog-footer">
            <el-button type="primary" @click="submitForm">确 定</el-button>
            <el-button @click="cancel">取 消</el-button>
        </div>
    </el-dialog>

    <el-dialog title="Cron表达式生成器" :visible.sync="openCron" append-to-body destroy-on-close class="scrollbar">
        <crontab @hide="openCron=false" @fill="crontabFill" :expression="expression" style="padding-bottom:80px;"></crontab>
    </el-dialog>

    <!-- 定时日志详细 -->
    <el-dialog title="定时详细" :visible.sync="openView" width="700px" append-to-body>
        <el-form ref="form" :model="form" label-width="120px" size="mini">
            <el-row>
                <el-col :span="12">
                    <el-form-item label="定时编号：">{{ form.jobId }}</el-form-item>
                    <el-form-item label="定时名称：">{{ form.jobName }}</el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="定时分组：">{{ jobGroupFormat(form) }}</el-form-item>
                    <el-form-item label="创建时间：">{{ form.createTime }}</el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="cron表达式：">{{ form.cronExpression }}</el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="下次执行时间：">{{ parseTime(form.nextValidTime) }}</el-form-item>
                </el-col>
                <el-col :span="24">
                    <el-form-item label="执行动作：">{{ form.actions }}</el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="定时状态：">
                        <div v-if="form.status == 0">正常</div>
                        <div v-else-if="form.status == 1">失败</div>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="是否并发：">
                        <div v-if="form.concurrent == 0">允许</div>
                        <div v-else-if="form.concurrent == 1">禁止</div>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="执行策略：">
                        <div v-if="form.misfirePolicy == 0">默认策略</div>
                        <div v-else-if="form.misfirePolicy == 1">立即执行</div>
                        <div v-else-if="form.misfirePolicy == 2">执行一次</div>
                        <div v-else-if="form.misfirePolicy == 3">放弃执行</div>
                    </el-form-item>
                </el-col>
            </el-row>
        </el-form>
        <div slot="footer" class="dialog-footer">
            <el-button @click="openView = false">关 闭</el-button>
        </div>
    </el-dialog>
</div>
</template>

<script>
import {
    listJob,
    getJob,
    delJob,
    addJob,
    updateJob,
    runJob,
    changeJobStatus
} from "@/api/iot/deviceJob";
import Crontab from '@/components/Crontab'
import {
    cacheJsonThingsModel
} from "@/api/iot/model";
export default {
    components: {
        Crontab
    },
    name: "device-timer",
    dicts: ['sys_job_group', 'sys_job_status'],
    props: {
        device: {
            type: Object,
            default: null
        }
    },
    watch: {
        // 获取到父组件传递的device后
        device: function (newVal, oldVal) {
            this.deviceId = newVal.deviceId;
            this.deviceName = newVal.deviceName;
            console.log(this.form);
            // 获取缓存的Json物模型
            cacheJsonThingsModel(newVal.productId).then(response => {
                this.thingsModel = JSON.parse(response.data);
                // 过滤监测数据，监测数据未只读
                this.thingsModel.properties = this.thingsModel.properties.filter(item => item.isMonitor == 0);
            });
        }
    },
    data() {
        return {
            // 物模型JSON
            thingsModel: {},
            // 物模型项,对应actions中的项
            thingsModelItems: [{
                id: "",
                name: "",
                datatype: {
                    type: "",
                }
            }],
            // 设备ID
            deviceId:0,
            // 设备名称
            deviceName:"",
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
            // 定时定时表格数据
            jobList: [],
            // 弹出层标题
            title: "",
            // 是否显示弹出层
            open: false,
            // 是否显示详细弹出层
            openView: false,
            // 是否显示Cron表达式弹出层
            openCron: false,
            // 传入的表达式
            expression: "",
            // 查询参数
            queryParams: {
                pageNum: 1,
                pageSize: 10,
                jobName: undefined,
                jobGroup: undefined,
                status: undefined
            },
            // 周
            timerWeekRepeats: [{
                value: '1',
                label: '每天'
            }, {
                value: '2',
                label: '仅此一次'
            }, {
                value: '3',
                label: '指定'
            }],
            timerWeekRepeatValue: "1",

            timerWeeks: [{
                value: 1,
                label: '周一'
            }, {
                value: 2,
                label: '周二'
            }, {
                value: 3,
                label: '周三'
            }, {
                value: 4,
                label: '周四'
            }, {
                value: 5,
                label: '周五'
            }, {
                value: 6,
                label: '周六'
            }, {
                value: 7,
                label: '周日'
            }],
            timerWeekValue: [1, 2, 3, 4, 5, 6, 7],
            // 时间
            timerTimeValue: '',
            // 物模型类别
            modelTypes: [{
                value: 1,
                label: '属性'
            }, {
                value: 2,
                label: '功能'
            }],
            // 表单参数
            form: {
                jobGroup: "DEFAULT", // 定时分组
                misfirePolicy: 2, // 1=立即执行，2=执行一次，3=放弃执行
                concurrent: 1, // 是否并发，1=禁止，0=允许
                isAdvance: 0, // 是否详细cron表达式
                isRepeat: 1, // 重复
                actions: [{
                    id: "",
                    name: "",
                    value: "",
                    type: 2, // 1=属性，2=功能
                    source: 2, // 1=设备，2=定时，3=告警输出
                }]
            },
            // 表单校验
            rules: {
                jobName: [{
                    required: true,
                    message: "定时名称不能为空",
                    trigger: "blur"
                }],
                actions: [{
                    required: true,
                    message: "执行动作不能为空",
                    trigger: "blur"
                }],
                cronExpression: [{
                    required: true,
                    message: "cron执行表达式不能为空",
                    trigger: "blur"
                }]
            }
        };
    },
    created() {
        this.getList();
    },
    methods: {
        /** 查询定时定时列表 */
        getList() {
            this.loading = true;
            listJob(this.queryParams).then(response => {
                this.jobList = response.rows;
                this.total = response.total;
                this.loading = false;
            });
        },
        // 定时组名字典翻译
        jobGroupFormat(row, column) {
            return this.selectDictLabel(this.dict.type.sys_job_group, row.jobGroup);
        },
        // 取消按钮
        cancel() {
            this.open = false;
            this.reset();
        },
        // 表单重置
        reset() {
            this.form = {
                jobId: undefined,
                jobName: undefined,
                cronExpression: undefined,
                status: "0",
                jobGroup: "DEFAULT", // 定时分组
                misfirePolicy: 2, // 1=立即执行，2=执行一次，3=放弃执行
                concurrent: 1, // 是否并发，1=禁止，0=允许
                isAdvance: 0, // 是否详细cron表达式
                actions: [{
                    id: "",
                    name: "",
                    value: "",
                    type: 2, // 1=属性，2=功能
                    source: 2, // 1=设备，2=定时，3=告警输出
                }],
            };
            // 物模型项,对应actions
            this.thingsModelItems = [{
                    id: "",
                    name: "",
                    datatype: {
                        type: "",
                    }
                }],
                this.resetForm("form");
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
        // 多选框选中数据
        handleSelectionChange(selection) {
            this.ids = selection.map(item => item.jobId);
            this.single = selection.length != 1;
            this.multiple = !selection.length;
        },
        // 更多操作触发
        handleCommand(command, row) {
            switch (command) {
                case "handleRun":
                    this.handleRun(row);
                    break;
                case "handleView":
                    this.handleView(row);
                    break;
                case "handleJobLog":
                    this.handleJobLog(row);
                    break;
                default:
                    break;
            }
        },
        // 定时状态修改
        handleStatusChange(row) {
            let text = row.status === "0" ? "启用" : "停用";
            this.$modal.confirm('确认要"' + text + '""' + row.jobName + '"定时吗？').then(function () {
                return changeJobStatus(row.jobId, row.status);
            }).then(() => {
                this.$modal.msgSuccess(text + "成功");
            }).catch(function () {
                row.status = row.status === "0" ? "1" : "0";
            });
        },
        /* 立即执行一次 */
        handleRun(row) {
            this.$modal.confirm('确认要立即执行一次"' + row.jobName + '"定时吗？').then(function () {
                return runJob(row.jobId, row.jobGroup);
            }).then(() => {
                this.$modal.msgSuccess("执行成功");
            }).catch(() => {});
        },
        /** 定时详细信息 */
        handleView(row) {
            getJob(row.jobId).then(response => {
                this.form = response.data;
                this.openView = true;
            });
        },
        /** cron表达式按钮操作 */
        handleShowCron() {
            this.expression = this.form.cronExpression;
            this.openCron = true;
        },
        /** 确定后回传值 */
        crontabFill(value) {
            this.form.cronExpression = value;
        },
        /** 新增按钮操作 */
        handleAdd() {
            this.reset();
            this.open = true;
            this.title = "添加定时";
        },
        /** 修改按钮操作 */
        handleUpdate(row) {
            this.reset();
            const jobId = row.jobId || this.ids;
            getJob(jobId).then(response => {
                this.form = response.data;
                this.open = true;
                this.title = "修改定时";
            });
        },
        /** 提交按钮 */
        submitForm: function () {
            this.$refs["form"].validate(valid => {
                if (valid) {
                    console.log(this.form);
                    this.form.actions[0].deviceId = this.deviceId;
                    this.form.actions[0].deviceName = this.deviceName;
                    this.form.actions = JSON.stringify(this.form.actions);
                    if (this.form.jobId != undefined) {
                        updateJob(this.form).then(response => {
                            this.$modal.msgSuccess("修改成功");
                            this.open = false;
                            this.getList();
                        });
                    } else {
                        addJob(this.form).then(response => {
                            this.$modal.msgSuccess("新增成功");
                            this.open = false;
                            this.getList();
                        });
                    }
                }
            });
        },
        /** 删除按钮操作 */
        handleDelete(row) {
            const jobIds = row.jobId || this.ids;
            this.$modal.confirm('是否确认删除定时定时编号为"' + jobIds + '"的数据项？').then(function () {
                return delJob(jobIds);
            }).then(() => {
                this.getList();
                this.$modal.msgSuccess("删除成功");
            }).catch(() => {});
        },
        /** 导出按钮操作 */
        handleExport() {
            this.download('iot/job/export', {
                ...this.queryParams
            }, `job_${new Date().getTime()}.xlsx`)
        },
        /** 添加枚举项 */
        addEnumItem() {
            this.form.actions.push({
                id: "",
                name: "",
                value: "",
                type: 2, // 1=属性，2=功能，3=事件，5=设备上线，6=设备下线
                source: 2, // 1=设备，2=定时，3=告警输出
                deviceId: this.deviceId,
                deviceName: this.deviceName,
            });
            this.thingsModelItems.push({
                datatype: {}
            });
            console.log(this.form.actions);
        },
        /** 删除枚举项 */
        removeEnumItem(index) {
            this.form.actions.splice(index, 1);
            this.thingsModelItems.splice(index, 1);
        },
        /** 修改重复事件 **/
        repeatChange(data) {
            if (this.timerWeekRepeatValue == 1) {
                // 每天
                this.timerWeekValue = [1, 2, 3, 4, 5, 6, 7];
                this.form.isRepeat = 1;
            } else if (this.timerWeekRepeatValue == 2) {
                // 仅此一次
                this.timerWeekValue = [];
                this.form.isRepeat = 0;
            } else {
                // 指定
                this.form.isRepeat = 1;
            }
            this.gentCronExpression();
        },
        /** 星期改变事件 **/
        weekChange(data) {
            this.gentCronExpression();
        },
        /** 时间改变事件 **/
        timeChange(data) {
            this.gentCronExpression();
        },
        /**自定义cron表达式选项改变事件 */
        customerCronChange(data) {
            this.gentCronExpression();
        },
        /** 生成cron表达式**/
        gentCronExpression() {
            if (this.timerTimeValue == "") {
                this.$modal.alertError("执行时间不能为空");
            }
            let hour = this.timerTimeValue.substring(0, 2);
            let minute = this.timerTimeValue.substring(3);
            let week = "*";
            if (this.timerWeekValue.length > 0) {
                week = this.timerWeekValue;
            }
            this.form.cronExpression = "0 " + minute + " " + hour + " ? * " + week;
        },
        /** 物模型项改变事件 **/
        thingsModelItemChange(identifier, index) {
            this.form.actions[index].value = "";
            if (this.form.actions[index].type == 1) {
                //属性
                for (let i = 0; i < this.thingsModel.properties.length; i++) {
                    if (this.thingsModel.properties[i].id == identifier) {
                        this.form.actions[index].name = this.thingsModel.properties[i].name;
                        this.thingsModelItems[index] = this.thingsModel.properties[i];
                        break;
                    }
                }
            } else if (this.form.actions[index].type == 2) {
                //事件
                for (let i = 0; i < this.thingsModel.functions.length; i++) {
                    if (this.thingsModel.functions[i].id == identifier) {
                        this.form.actions[index].name = this.thingsModel.functions[i].name;
                        this.thingsModelItems[index] = this.thingsModel.functions[i];
                        break;
                    }
                }
            }
            console.log(this.thingsModelItems[index])
        },
        /** 物模型类别改变事件 */
        thingsModelTypeChange(value, index) {
            // this.form.actions[index]="";
        }
    }
};
</script>
