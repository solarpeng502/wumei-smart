<template>
<div style="padding-left:20px;">
    <el-row>
        <el-col :span="24">
            <div v-for="(item,index) in monitorThings" :key="index" style="margin-bottom:50px;">
                <el-card shadow="hover" :body-style="{ padding: '10px 0px',overflow:'auto' }">
                    <div ref="statisticMap" style="height:280px;width:1470px;"></div>
                </el-card>
            </div>
        </el-col>
    </el-row>

</div>
</template>

<script>
import * as echarts from 'echarts';
import {
    cacheJsonThingsModel
} from "@/api/iot/model";
import {
    listDeviceLog
} from "@/api/iot/deviceLog";

export default {
    name: "device-statistic",
    props: {
        device: {
            type: Object,
            default: null
        }
    },
    watch: {
        // 获取到父组件传递的device后
        device: function (newVal, oldVal) {
            this.deviceInfo = newVal;
            if (this.deviceInfo && this.deviceInfo.deviceId != 0) {
                this.getCacheThingsModdel(this.deviceInfo.productId);
            }
        }
    },
    data() {
        return {
            // 设备信息
            deviceInfo: {},
            // 监测数据
            // dataList: [],
            // 监测物模型
            monitorThings: [],
            // 图表集合
            chart: [],
        };
    },
    mounted() {

        // this.dataList = [{
        //     id: "1",
        //     name: "温度",
        //     unit: "℃",
        //     dat: [{
        //         time: "2021-09-09",
        //         value: "21"
        //     }],
        //     data: [],
        //     date: [],
        // }, {
        //     id: "1",
        //     name: "湿度",
        //     unit: '%',
        //     data: [30, 32, 23, 35, 40, 57, 21, 31],
        //     date: ['2021-02-01', '2021-02-02', '2021-02-03', '2021-02-04', '2021-02-05', '2021-02-06', '2021-02-07', '2021-02-08']
        // }]
        // this.$nextTick(function () {
        //     this.getStatistic();

        //     this.dataList[0].data = ['21', '22', '23', '25', '26', '27', '21', '31'];
        //     this.dataList[0].date = ['2021-02-01', '2021-02-02', '2021-02-03', '2021-02-04', '2021-02-05', '2021-02-06', '2021-02-07', '2021-02-08'];
        //     this.chart[0].setOption({
        //         xAxis: {
        //             data: this.dataList[0].date
        //         },
        //         series: [{
        //             data: this.dataList[0].data
        //         }]
        //     });

        //     // this.dataList[0].data= ['21', '22', '23', '25', '26', '27', '21', '31'];
        //     // this.dataList[0].date=['2021-02-01', '2021-02-02', '2021-02-03', '2021-02-04', '2021-02-05', '2021-02-06', '2021-02-07', '2021-02-08'];
        // });
    },
    methods: {
        /** 获取物模型*/
        getCacheThingsModdel(productId) {
            // 获取缓存的Json物模型
            cacheJsonThingsModel(productId).then(response => {
                let thingsModel = JSON.parse(response.data);
                // 筛选监测数据
                this.monitorThings = thingsModel.properties.filter(item => item.isMonitor == 1);
                console.log(this.monitorThings);
                // 加载图表
                this.$nextTick(function () {
                    this.getStatistic();

                    // 获取统计数据
                    this.getStatisticData(this.monitorThings);

                    // 测试数据
                    this.chart[0].setOption({
                        // xAxis: {
                        //     data: ['2021-09-09', '2021-09-10', '2021-09-11', '2021-09-12', ]
                        // },
                        series: [{
                            data: [{
                                time: '2021-09-01',
                                value: 30
                            }, {
                                time: '2021-09-09',
                                value: 60
                            }]
                        }]
                    });

                });

            });
        },
        /** 获取统计数据 */
        getStatisticData(monitorThingsModel) {
            for (let i = 0; i < monitorThingsModel.length; i++) {
                let queryParams = {};
                queryParams.deviceId = this.deviceInfo.deviceId;
                queryParams.identity = monitorThingsModel[i].id;
                console.log(queryParams)
                listDeviceLog(queryParams).then(response => {
                    let data = response.rows;
                    console.log(data)
                });
            }
        },
        /**监测统计数据 */
        getStatistic() {
            for (let i = 0; i < this.monitorThings.length; i++) {
                this.chart[i] = echarts.init(this.$refs.statisticMap[i]);
                var option;

                option = {
                    tooltip: {
                        trigger: 'axis',
                        position: function (pt) {
                            return [pt[0], '30%'];
                        }
                    },
                    title: {
                        left: 'center',
                        text: this.monitorThings[i].name + '统计 （单位 ' + (this.monitorThings[i].datatype.unit != undefined ? this.monitorThings[i].datatype.unit : "无") + "）",
                    },
                    grid: {
                        top: '80px',
                        left: '40px',
                        right: '100px',
                        bottom: '60px',
                        containLabel: true
                    },
                    toolbox: {
                        feature: {
                            dataZoom: {
                                yAxisIndex: 'none'
                            },
                            restore: {},
                            saveAsImage: {}
                        }
                    },
                    xAxis: {
                        type: 'category',
                        boundaryGap: false,
                        name: "时间", //坐标名字
                        nameLocation: "end", //坐标位置，支持start,end，middle
                        nameTextStyle: { //字体样式            
                            fontSize: 16, //字体大小            
                            padding: 30 //距离坐标位置的距离    
                        },
                        // data: this.monitorThings[i].dataList.time
                    },
                    yAxis: {
                        type: 'value',
                        boundaryGap: [0, '100%'],
                        name: this.monitorThings[i].name, //坐标名字
                        nameLocation: "end", //坐标位置，支持start,end，middle
                        nameTextStyle: { //字体样式            
                            fontSize: 16, //字体大小            
                            padding: 10 //距离坐标位置的距离    
                        },
                    },
                    dataZoom: [{
                            type: 'inside',
                            start: 0,
                            end: 100
                        },
                        {
                            start: 0,
                            end: 100
                        }
                    ],
                    series: [{
                        name: this.monitorThings[i].name,
                        type: 'line',
                        symbol: 'none',
                        sampling: 'lttb',
                        itemStyle: {
                            color: 'rgb(64, 158, 255)'
                        },
                        areaStyle: {
                            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
                                    offset: 0,
                                    color: 'rgb(64, 158, 255)'
                                },
                                {
                                    offset: 1,
                                    color: 'rgb(255, 255, 255)'
                                }
                            ])
                        },
                        // data: this.monitorThings[i].dataList.data
                    }]
                };

                option && this.chart[i].setOption(option);
            }
        },
    }
};
</script>
