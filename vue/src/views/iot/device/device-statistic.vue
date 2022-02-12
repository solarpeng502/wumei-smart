<template>
<div style="padding:6px;">
    <el-row :gutter="10">
        <el-col :span="24" id="parent">
            <div id="monitor" ref="monitor" style="height:600px;width:1500px;"></div>
        </el-col>
        <el-col :span="24"></el-col>
    </el-row>
</div>
</template>

<script>
import * as echarts from 'echarts';
require('echarts/theme/macarons') // echarts theme
export default {
    name: "device-statistic",
    data() {
        return {

        };
    },
    created() {
        this.$nextTick(function () {
            this.getMonitor();
        });
    },
    methods: {
        /**监测数据 */
        getMonitor() {
            var myChart = echarts.init(this.$refs.monitor,"macarons");
            var option;

            let base = +new Date(2010, 9, 3);
            let oneDay = 24 * 3600 * 100;
            let date = [];
            let data = [Math.random() * -10];
            for (let i = 1; i < 20000; i++) {
                var now = new Date((base += oneDay));
                date.push([now.getFullYear(), now.getMonth() + 1, now.getDate(), now.getHours(), now.getMinutes()].join('/'));
                data.push(Math.round((Math.random() - 0.5) * 2 + data[i - 1]));
            }
            option = {
                tooltip: {
                    trigger: 'axis',
                    position: function (pt) {
                        return [pt[0], '10%'];
                    }
                },
                title: {
                    left: 'center',
                    text: '历史数据'
                },
                grid: {
                    top: '50px',
                    left: '30px',
                    right: '50px',
                    bottom: '50px',
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
                        padding: 2 //距离坐标位置的距离    
                    },
                    data: date
                },
                yAxis: {
                    type: 'value',
                    boundaryGap: [0, '100%'],
                    name: "温度", //坐标名字
                    nameLocation: "end", //坐标位置，支持start,end，middle
                    nameTextStyle: { //字体样式            
                        fontSize: 16, //字体大小            
                        padding: 2 //距离坐标位置的距离    
                    },
                },
                dataZoom: [{
                        type: 'inside',
                        start: 0,
                        end: 20
                    },
                    {
                        start: 0,
                        end: 10
                    }
                ],
                series: [{
                    name: '温度',
                    type: 'line',
                    symbol: 'none',
                    sampling: 'lttb',
                    itemStyle: {
                        color: 'rgb(255, 70, 131)'
                    },
                    areaStyle: {
                        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
                                offset: 0,
                                color: 'rgb(255, 158, 68)'
                            },
                            {
                                offset: 1,
                                color: 'rgb(255, 70, 131)'
                            }
                        ])
                    },
                    data: data
                }]
            };

            option && myChart.setOption(option);

        },
    }
};
</script>
