<template>
<div>

</div>
</template>

<script>

export default {
    name: "device-alert",
    props: {
        product: {
            type: Object,
            default: null
        }
    },
    data() {
        return {
            
        };
    },
    watch: {
        // 获取到父组件传递的productId后，刷新列表
        product: function (newVal, oldVal) {
            this.productInfo = newVal;
            this.queryParams.productId = this.productInfo.productId;
            this.getList();
        }
    },
    created() {

    },
    methods: {
        
    }
};
</script>


