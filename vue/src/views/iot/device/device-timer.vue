<template>
<div style="padding:6px;">



    <el-form label-width="100px" style="margin-top:50px;margin-bottom:150px;">
        <el-form-item style="text-align: center;margin-left:-100px;margin-top:10px;">
            <el-button size="small" @click="goBack" type="info">返 回</el-button>
        </el-form-item>
    </el-form>
</div>
</template>

<script>
export default {
    name: "device-timer",
    data() {
        return {

        };
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
    }
};
</script>
