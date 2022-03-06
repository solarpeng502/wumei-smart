<template>
<div style="padding-left:20px;">

    <div v-for="(item,index) in dataList" :key="index" style="margin-bottom:50px;width:1200px;">
        <el-card shadow="hover" :body-style="{ padding: '0px' }">
            <div ref="statisticMap" style="height:300px;width:1200px;padding-bottom:20px;"></div>
        </el-card>
    </div>

</div>
</template>

<script>
import * as echarts from 'echarts';
export default {
    name: "device-statistic",
    data() {
        return {
            dataList: []
        };
    },
    created() {
        this.dataList = [{
            id: "1",
            name: "温度",
            unit: "℃",
            data: [21, 22, 23, 25, 26, 27, 21, 31],
            date: ['2021-02-01', '2021-02-02', '2021-02-03', '2021-02-04', '2021-02-05', '2021-02-06', '2021-02-07', '2021-02-08']
        }, {
            id: "1",
            name: "湿度",
            unit: '%',
            data: [30, 32, 23, 35, 40, 57, 21, 31],
            date: ['2021-02-01', '2021-02-02', '2021-02-03', '2021-02-04', '2021-02-05', '2021-02-06', '2021-02-07', '2021-02-08']
        }]
        this.$nextTick(function () {
            this.getStatistic();
        });
    },
    methods: {
        /**监测统计数据 */
        getStatistic() {
            console.log(this.dataList);
            for (let i = 0; i < this.dataList.length; i++) {
                var myChart = echarts.init(this.$refs.statisticMap[i]);
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
                        text: this.dataList[i].name+'统计 （单位 '+this.dataList[i].unit+"）",
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
                            padding: 10 //距离坐标位置的距离    
                        },
                        data: this.dataList[i].date
                    },
                    yAxis: {
                        type: 'value',
                        boundaryGap: [0, '100%'],
                        name: this.dataList[i].name, //坐标名字
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
                        name: '温度',
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
                        data: this.dataList[i].data
                    }]
                };

                option && myChart.setOption(option);
            }
        },
    }
};
</script>
