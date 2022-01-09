<template>
<div style="padding:6px;">
    <el-card v-show="showSearch" style="margin-bottom:6px;">
        <el-form :model="queryParams" ref="queryForm" :inline="true" label-width="68px" style="margin-bottom:-20px;">
            <el-form-item label="产品名称" prop="productName">
                <el-input v-model="queryParams.productName" placeholder="请输入产品名称" clearable size="small" @keyup.enter.native="handleQuery" />
            </el-form-item>
            <el-form-item label="分类名称" prop="categoryName">
                <el-input v-model="queryParams.categoryName" placeholder="请输入产品分类名称" clearable size="small" @keyup.enter.native="handleQuery" />
            </el-form-item>
            <el-form-item label="状态" prop="status">
                <el-select v-model="queryParams.status" placeholder="请选择状态" clearable size="small">
                    <el-option v-for="dict in dict.type.iot_product_status" :key="dict.value" :label="dict.label" :value="dict.value" />
                </el-select>
            </el-form-item>
            <el-form-item>
                <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
                <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
            </el-form-item>
        </el-form>
    </el-card>

    <el-card style="padding-bottom:100px;">
        <el-row :gutter="10" class="mb8">
            <el-col :span="1.5">
                <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleEditProduct(0)" v-hasPermi="['iot:product:add']">新增</el-button>
            </el-col>
            <el-col :span="1.5">
                <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleEditProduct" v-hasPermi="['iot:product:edit']">修改</el-button>
            </el-col>
            <el-col :span="1.5">
                <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete" v-hasPermi="['iot:product:remove']">删除</el-button>
            </el-col>
            <el-col :span="1.5">
                <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport" v-hasPermi="['iot:product:export']">导出</el-button>
            </el-col>
            <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
        </el-row>

        <el-table v-loading="loading" :data="productList" @selection-change="handleSelectionChange" border>
            <input type="hidden" prop="productId" />
            <el-table-column type="selection" width="55" align="center" />
            <el-table-column label="产品名称" align="center" prop="productName">
                <template slot-scope="scope">
                    <el-link type="primary" :underline="false" @click="handleEditProduct(scope.row)">{{scope.row.productName}}</el-link>
                </template>
            </el-table-column>
            <el-table-column label="分类名称" align="center" prop="categoryName" />
            <el-table-column label="状态" align="center" prop="status">
                <template slot-scope="scope">
                    <dict-tag :options="dict.type.iot_product_status" :value="scope.row.status" />
                </template>
            </el-table-column>
            <el-table-column label="设备类型" align="center" prop="deviceType">
                <template slot-scope="scope">
                    <dict-tag :options="dict.type.iot_device_type" :value="scope.row.deviceType" />
                </template>
            </el-table-column>
            <el-table-column label="系统定义" align="center" prop="isSys">
                <template slot-scope="scope">
                    <dict-tag :options="dict.type.iot_yes_no" :value="scope.row.isSys" />
                </template>
            </el-table-column>
            <el-table-column label="联网方式" align="center" prop="networkMethod">
                <template slot-scope="scope">
                    <dict-tag :options="dict.type.iot_network_method" :value="scope.row.networkMethod" />
                </template>
            </el-table-column>
            <el-table-column label="认证方式" align="center" prop="vertificateMethod">
                <template slot-scope="scope">
                    <dict-tag :options="dict.type.iot_vertificate_method" :value="scope.row.vertificateMethod" />
                </template>
            </el-table-column>
            <el-table-column label="创建时间" align="center" prop="createTime" width="150">
                <template slot-scope="scope">
                    <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d}') }}</span>
                </template>
            </el-table-column>
            <el-table-column label="备注" align="left" prop="remark" min-width="150" />
            <el-table-column label="操作" align="center" class-name="" width="150">
                <template slot-scope="scope">
                    <div style="text-align:left;margin-left:10px;">
                        <el-button size="small" type="primary" icon="el-icon-edit" @click="handleEditProduct(scope.row)" v-hasPermi="['tool:gen:edit']" style="padding:5px;">详情</el-button>
                        <el-button size="small" type="danger" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['iot:product:remove']" style="padding:5px;">删除</el-button><br />
                        <el-button size="small" type="success" icon="el-icon-edit" @click="handleGeneratorSDK(scope.row)" v-hasPermi="['iot:product:edit']" style="padding:5px;margin-top:8px;">生成设备端SDK</el-button>
                    </div>
                </template>
            </el-table-column>
        </el-table>

        <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

        <!-- 下载SDK -->
        <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
            <el-form label-width="80px">
                <el-form-item label="选择设备">
                    <el-radio-group v-model="form.datatype">
                        <el-radio v-for="dict in dict.type.iot_device_chip" :key="dict.value" :label="dict.value" style="margin-top:15px;width:160px;">{{dict.label}}</el-radio>
                    </el-radio-group>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button type="primary" @click="downloadSdk">下 载</el-button>
                <el-button @click="cancel">取 消</el-button>
            </div>
        </el-dialog>

    </el-card>
</div>
</template>

<script>
import {
    listProduct,
    delProduct,
} from "@/api/iot/product";

export default {
    name: "Product",
    dicts: ['iot_yes_no', 'iot_product_status', 'iot_device_type', 'iot_network_method', 'iot_vertificate_method', 'iot_device_chip'],
    data() {
        return {
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
            // 产品表格数据
            productList: [],
            // 弹出层标题
            title: "",
            // 是否显示弹出层
            open: false,
            // 查询参数
            queryParams: {
                pageNum: 1,
                pageSize: 10,
                productName: null,
                categoryId: null,
                categoryName: null,
                tenantId: null,
                tenantName: null,
                isSys: null,
                status: null,
                deviceType: null,
                networkMethod: null,
            },
            // 表单参数
            form: {},
        };
    },
    created() {
        this.getList();
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
        /** 查询产品列表 */
        getList() {
            this.loading = true;
            listProduct(this.queryParams).then(response => {
                this.productList = response.rows;
                this.total = response.total;
                this.loading = false;
            });
        },
        // 取消按钮
        cancel() {
            this.open = false;
            this.reset();
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
            this.ids = selection.map(item => item.productId)
            this.single = selection.length !== 1
            this.multiple = !selection.length
        },
        /** 生成SDK */
        handleGeneratorSDK(row) {
            this.title = "生成SDK"
            this.open = true;
        },
        /** 下载SDK */
        downloadSdk() {
            this.$download.zip("/iot/tool/genSdk?deviceChip=" + 1, "wumeismart-sdk");
        },
        /** 删除按钮操作 */
        handleDelete(row) {
            const productIds = row.productId || this.ids;
            this.$modal.confirm('是否确认删除产品编号为"' + productIds + '"的数据项？').then(function () {
                return delProduct(productIds);
            }).then(() => {
                this.getList();
                this.$modal.msgSuccess("删除成功");
            }).catch(() => {});
        },
        /** 导出按钮操作 */
        handleExport() {
            this.download('iot/product/export', {
                ...this.queryParams
            }, `product_${new Date().getTime()}.xlsx`)
        },
        /** 修改按钮操作 */
        handleEditProduct(row) {
            let productId = 0;
            if (row != 0) {
                productId = row.productId || this.ids
            }
            this.$router.push({
                path: '/iot/product-edit',
                query: {
                    productId: productId,
                    pageNum: this.queryParams.pageNum
                }
            });
        },
    }
};
</script>
