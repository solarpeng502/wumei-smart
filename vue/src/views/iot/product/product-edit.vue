<template>
<el-card style="margin:6px;">
    <el-tabs v-model="activeName">
        <el-tab-pane label="基本信息" name="basic">
            <el-form ref="form" :model="form" :rules="rules" label-width="80px" style="margin-top:20px;">
                <el-row :gutter="50">
                    <el-col :span="8">
                        <el-form-item label="产品名称" prop="productName">
                            <el-input v-model="form.productName" placeholder="请输入产品名称" />
                        </el-form-item>
                        <el-form-item label="备注" prop="remark">
                            <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" rows="4" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="8">
                        <el-form-item label="产品分类" prop="categoryId">
                            <el-select v-model="form.categoryId" placeholder="请选择分类" @change="selectCategory">
                                <el-option v-for="category in categoryShortList" :key="category.id" :label="category.name" :value="category.id"></el-option>
                            </el-select>
                        </el-form-item>
                        <el-form-item label="联网方式" prop="networkMethod">
                            <el-select v-model="form.networkMethod" placeholder="请选择联网方式" disabled>
                                <el-option v-for="dict in dict.type.iot_network_method" :key="dict.value" :label="dict.label" :value="parseInt(dict.value)"></el-option>
                            </el-select>
                        </el-form-item>
                        <el-form-item label="设备类型" prop="deviceType">
                            <el-select v-model="form.deviceType" placeholder="请选择设备类型" disabled>
                                <el-option v-for="dict in dict.type.iot_device_type" :key="dict.value" :label="dict.label" :value="parseInt(dict.value)"></el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="8">
                        <el-form-item label="MQTT认证" prop="vertificateMethod">
                            <el-select v-model="form.vertificateMethod" placeholder="请选择认证方式" disabled>
                                <el-option v-for="dict in dict.type.iot_vertificate_method" :key="dict.value" :label="dict.label" :value="parseInt(dict.value)"></el-option>
                            </el-select>
                        </el-form-item>
                        <el-form-item label="Mqtt账号" prop="mqttAccount">
                            <el-input v-model="form.mqttAccount" placeholder="自动生成" disabled />
                        </el-form-item>
                        <el-form-item label="Mqtt密码" prop="mqttPassword">
                            <el-input v-model="form.mqttPassword" placeholder="自动生成" disabled />
                        </el-form-item>

                    </el-col>
                    <el-col :span="20">
                        <el-form-item style="text-align: center;margin:30px 0px;">
                            <el-button type="primary" @click="submitForm(1)">提交</el-button>
                            <el-button type="danger" @click="submitForm(2)" v-show="product.productId!=undefined">提交并发布</el-button>
                            <el-button type="info" @click="goBack()">返回</el-button>
                        </el-form-item>
                    </el-col>

                </el-row>
            </el-form>
        </el-tab-pane>

        <el-tab-pane label="物模型" name="things" :disabled="product.productId==undefined">
            <product-things-model ref="productThingsModel" :product="product" />
        </el-tab-pane>

        <el-tab-pane label="告警" name="alert" :disabled="product.productId==undefined">
            <product-alert ref="productAlert"></product-alert>
        </el-tab-pane>

        <el-tab-pane label="APP自定义" name="productApp" :disabled="product.productId==undefined">
            <product-app ref="productApp" :product="product" />
        </el-tab-pane>
    </el-tabs>

</el-card>
</template>

<script>
import productThingsModel from "./product-things-model";
import productApp from "./product-app"
import productAlert from "./product-alert"
import {
    listShortCategory
} from "@/api/iot/category";
import {
    getProduct,
    addProduct,
    updateProduct
} from "@/api/iot/product";

export default {
    name: "Product-edit",
    dicts: ['iot_device_type', 'iot_network_method', 'iot_vertificate_method'],
    components: {
        productThingsModel,
        productApp,
        productAlert,
    },
    data() {
        return {
            // 产品信息
            product: {},
            // 选中选项卡
            activeName: 'basic',
            // 分类短列表
            categoryShortList: [],
            // 表单参数
            form: {
                networkMethod: 1,
                deviceType: 1,
                vertificateMethod: 3,
            },
            // 表单校验
            rules: {
                productName: [{
                    required: true,
                    message: "产品名称不能为空",
                    trigger: "blur"
                }],
                categoryId: [{
                    required: true,
                    message: "产品分类ID不能为空",
                    trigger: "blur"
                }]
            },
        };
    },
    created() {
        // 获取产品信息
        const productId = this.$route.query && this.$route.query.productId;
        if (productId != 0) {
            getProduct(productId).then(response => {
                this.form = response.data;
                this.product = response.data;
            });
        }
        // 获取简短分类列表
        listShortCategory().then(response => {
            this.categoryShortList = response.data;
        })
    },
    methods: {
        /** 返回按钮 */
        goBack() {
            const obj = {
                path: "/iot/product",
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
                productId: 0,
                productName: null,
                categoryId: null,
                categoryName: null,
                status: 0,
                tslJson: null,
                deviceType: 1,
                networkMethod: 1,
                vertificateMethod: 3,
                mqttAccount: null,
                mqttPassword: null,
                remark: null
            };
            this.resetForm("form");
        },
        /** 提交按钮 */
        submitForm(status) {
            // 状态（1-未发布，2-已发布，不能修改）
            this.form.status = status;
            this.$refs["form"].validate(valid => {
                if (valid) {
                    if (this.form.productId != null) {
                        updateProduct(this.form).then(response => {
                            this.$modal.alertSuccess("修改成功");
                        });
                    } else {
                        addProduct(this.form).then(response => {
                            this.$modal.alertSuccess("添加成功");
                            this.form = response.data;
                            this.product = response.data;
                        });
                    }
                }
            });
        },
        /** 选择分类 */
        selectCategory(val) {
            for (var i = 0; i < this.categoryShortList.length; i++) {
                if (this.categoryShortList[i].id == val) {
                    this.form.categoryName = this.categoryShortList[i].name;
                    return;
                }
            }
        },
    }
};
</script>
