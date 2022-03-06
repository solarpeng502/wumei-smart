<template>
<div class="app-container home">
    <el-row :gutter="40">
        <el-col :span="14">
            <el-card style="margin:-10px;" shadow="hover">
                <div ref="map" style="height:650px;margin:-10px;margin-top:-5px;"></div>
            </el-card>
        </el-col>

        <el-col :span="10">
            <el-card style="margin:-10px;" shadow="hover">
                <div style="height:270px;">
                    <h3 style="font-weight:bold">Mqtt 统计指标</h3>
                    <el-descriptions :column="3" border>
                        <el-descriptions-item labelStyle="line-height:34px;font-weight:bold;">
                            <template slot="label">
                                <i class="el-icon-s-data"></i>
                                发送字节
                            </template>
                            <el-link type="success" :underline="false">{{this.static["bytes.sent"]}}</el-link>
                        </el-descriptions-item>
                        <el-descriptions-item labelStyle="line-height:34px;font-weight:bold;" contentStyle="">
                            <template slot="label">
                                <i class="el-icon-s-data"></i>
                                接收字节
                            </template>
                            <el-link type="primary" :underline="false">{{this.static["bytes.received"]}}</el-link>
                        </el-descriptions-item>
                        <el-descriptions-item labelStyle="line-height:34px;font-weight:bold;">
                            <template slot="label">
                                <i class="el-icon-s-data"></i>
                                认证次数
                            </template>
                            <el-link type="danger" :underline="false">{{this.static["client.authenticate"]}}</el-link>
                        </el-descriptions-item>

                        <el-descriptions-item labelStyle="line-height:34px;font-weight:bold;">
                            <template slot="label">
                                <i class="el-icon-s-data"></i>
                                发送报文数
                            </template>
                            <el-link type="success" :underline="false">{{this.static["packets.sent"]}}</el-link>
                        </el-descriptions-item>
                        <el-descriptions-item labelStyle="line-height:34px;font-weight:bold;">
                            <template slot="label">
                                <i class="el-icon-s-data"></i>
                                接收报文数
                            </template>
                            <el-link type="primary" :underline="false">{{this.static["packets.received"]}}</el-link>
                        </el-descriptions-item>
                        <el-descriptions-item labelStyle="line-height:34px;font-weight:bold;">
                            <template slot="label">
                                <i class="el-icon-s-data"></i>
                                连接次数
                            </template>
                            <el-link type="danger" :underline="false">{{this.static["client.connected"]}}</el-link>
                        </el-descriptions-item>

                        <el-descriptions-item labelStyle="line-height:34px;font-weight:bold;">
                            <template slot="label">
                                <i class="el-icon-s-data"></i>
                                发送消息
                            </template>
                            <el-link type="success" :underline="false">{{this.static["messages.sent"]}}</el-link>
                        </el-descriptions-item>
                        <el-descriptions-item labelStyle="line-height:34px;font-weight:bold;">
                            <template slot="label">
                                <i class="el-icon-s-data"></i>
                                接收消息
                            </template>
                            <el-link type="primary" :underline="false">{{this.static["messages.received"]}}</el-link>
                        </el-descriptions-item>
                        <el-descriptions-item labelStyle="line-height:34px;font-weight:bold;">
                            <template slot="label">
                                <i class="el-icon-s-data"></i>
                                丢弃消息
                            </template>
                            <el-link type="danger" :underline="false">{{this.static["delivery.dropped"]}}</el-link>
                        </el-descriptions-item>

                        <el-descriptions-item labelStyle="line-height:34px;font-weight:bold;">
                            <template slot="label">
                                <i class="el-icon-s-data"></i>
                                创建会话数
                            </template>
                            <el-link type="success" :underline="false">{{this.static["session.created"]}}</el-link>
                        </el-descriptions-item>
                        <el-descriptions-item labelStyle="line-height:34px;font-weight:bold;">
                            <template slot="label">
                                <i class="el-icon-s-data"></i>
                                终结会话数
                            </template>
                            <el-link type="primary" :underline="false">{{this.static["session.terminated"]}}</el-link>
                        </el-descriptions-item>
                        <el-descriptions-item labelStyle="line-height:34px;font-weight:bold;">
                            <template slot="label">
                                <i class="el-icon-s-data"></i>
                                订阅次数
                            </template>
                            <el-link type="danger" :underline="false">{{this.static["client.subscribe"]}}</el-link>
                        </el-descriptions-item>
                    </el-descriptions>
                </div>
            </el-card>
            <el-card style="margin:-10px;margin-top:20px;" shadow="hover">
                <div ref="statsChart" style="height:298px;"></div>
            </el-card>
        </el-col>

    </el-row>

    <el-row :gutter="40" style="margin-top:80px;">
        <el-col :span="1">
            <el-card style="margin:-10px;height:218px;margin-right:-31px;padding-top:35px;text-align:center;font-weight:bold;" shadow="none">
                服<br />务<br />器<br />状<br />态
            </el-card>
        </el-col>
        <el-col :span="5">
            <el-card style="margin:-10px;height:218px;" shadow="hover">
                <el-descriptions :column="1">
                    <el-descriptions-item label="服务器名称"><strong>{{server.sys.computerName}}</strong></el-descriptions-item>
                    <el-descriptions-item label="服务器IP"><strong>{{server.sys.computerIp}}</strong></el-descriptions-item>
                    <el-descriptions-item label="操作系统"><strong>{{server.sys.osName}}</strong></el-descriptions-item>
                    <el-descriptions-item label="系统架构"><strong>{{server.sys.osArch}}</strong></el-descriptions-item>
                    <el-descriptions-item label="CPU核心"><strong>{{server.cpu.cpuNum}}核</strong></el-descriptions-item>
                    <el-descriptions-item label="总内存"><strong>{{server.mem.total}}G</strong></el-descriptions-item>
                </el-descriptions>
            </el-card>
        </el-col>
        <el-col :span="4">
            <el-card style="margin:-10px;" shadow="hover">
                <div ref="pieCpu" style="height:200px;margin-bottom:-20px;"></div>
            </el-card>
        </el-col>
        <el-col :span="4">
            <el-card style="margin:-10px;" shadow="hover">
                <div ref="pieMemery" style="height:200px;margin-bottom:-20px;"></div>
            </el-card>
        </el-col>
        <el-col :span="4">
            <el-card style="margin:-10px;" shadow="hover">
                <div ref="pieDisk" style="height:200px;margin-bottom:-20px;"></div>
            </el-card>
        </el-col>
        <el-col :span="6">
            <el-card style="margin:-10px;height:218px;" shadow="hover">
                <el-descriptions :column="1">
                    <el-descriptions-item label="Java名称"><strong>{{server.jvm.name}}</strong></el-descriptions-item>
                    <el-descriptions-item label="启动时间"><strong>{{ server.jvm.startTime }}</strong></el-descriptions-item>
                    <el-descriptions-item label="Java版本"><strong>{{ server.jvm.version }}</strong></el-descriptions-item>
                    <el-descriptions-item label="运行时长"><strong>{{ server.jvm.runTime }}</strong></el-descriptions-item>
                    <el-descriptions-item label="占用内存"><strong>{{server.jvm.used}}M</strong></el-descriptions-item>
                    <el-descriptions-item label="JVM总内存"><strong>{{server.jvm.total}}M</strong></el-descriptions-item>
                </el-descriptions>
            </el-card>
        </el-col>
    </el-row>

    <div style="margin:-21px;margin-top:100px;bottom:0;border:1px solid #ccc;padding:10px;margin-bottom:-21px;">
        <div>
            <el-link href="http://wumei.live" target="_blank" type="primary" style="margin-left:20px;">开源生活物联网平台 >></el-link>
            <el-link href="https://github.com/kerwincui/wumei-smart" target="_blank" type="danger" style="margin-left:30px;">Github源码 >></el-link>
            <el-link href="https://gitee.com/kerwincui/wumei-smart" target="_blank" type="success" style="margin-left:30px;">码云源码 >></el-link>
        </div>
    </div>

</div>
</template>

<style scoped>
.margin-top .el-descriptions-item__label {
    font-weight: bold;
}
</style>

<script>
import * as echarts from 'echarts';
require('echarts/theme/macarons') // echarts theme
import {
    loadBMap
} from './iot/map.js'
//安装的是echarts完整包，里面包含百度地图扩展，路径为 echarts/extension/bmap/bmap.js，将其引入
//ECharts的百度地图扩展，可以在百度地图上展现点图，线图，热力图等可视化
require('echarts/extension/bmap/bmap')
import {
    getServer
} from "@/api/monitor/server";
import {
    getMqttStats,
    statisticMqtt
} from "@/api/iot/emqx";

export default {
    name: "Index",
    data() {
        return {
            // 设备列表
            deviceList: [],
            // emqx状态数据
            stats: {},
            // emqx统计信息
            static: {},
            // 版本号
            version: "3.8.0",
            // 服务器信息
            server: {
                jvm: {
                    name: "",
                    version: "",
                    startTime: "",
                    runTime: "",
                    used: "",
                    total: 100
                },
                sys: {
                    computerName: "",
                    osName: "",
                    computerIp: "",
                    osArch: ""
                },
                cpu: {
                    cpuNum: 1
                },
                mem: {
                    total: 2
                }
            }
        };
    },
    created() {
        this.deviceList = [{
                id:21,
                name: '海门',
                value: 9,
                long: 121.15,
                lat: 31.89,
                address: "海门",
                product: "测试产品",
                version: 1.0,
                status: 1,
            },
            {
                id:21,
                name: '鄂尔多斯',
                value: 12,
                long: 109.781327,
                lat: 39.608266,
                address: "海门",
                product: "测试产品",
                version: 2.0,
                status: 2
            },
            {
                id:21,
                name: '招远',
                value: 12,
                long: 120.38,
                lat: 37.35,
                address: "海门",
                product: "测试产品",
                version: 1.0,
                status: 3
            },
            {
                id:21,
                name: '海口',
                value: 12,
                long: 110.38,
                lat: 20.35,
                address: "测试",
                product: "测试产品",
                version: 1.0,
                status: 4
            }
        ];
        this.getServer();
        this.getMqttStats();
        this.statisticMqtt();
        this.$nextTick(() => {
            loadBMap().then(() => {
                this.getmap();
            });
        })

    },
    methods: {
        /** 查询emqx统计*/
        statisticMqtt() {
            statisticMqtt().then(response => {
                console.log(response);
                this.static = response.data.data[0].metrics;
            })
        },
        /** 查询emqx状态数据*/
        getMqttStats() {
            getMqttStats().then(response => {
                this.stats = response.data.data[0].stats;
                this.drawStats();
            })
        },
        /** 查询服务器信息 */
        getServer() {
            getServer().then(response => {
                this.server = response.data;
                this.$nextTick(() => {
                    this.drawPieCpu();
                    this.drawPieMemery();
                    this.drawPieDisk();
                })
            });
        },

        /** 地图 */
        getmap() {
            var myChart = echarts.init(this.$refs.map);
            var option;

            // 格式化数据
            let convertData = function (data, status) {
                var res = [];
                for (var i = 0; i < data.length; i++) {
                    var geoCoord = [data[i].long, data[i].lat];
                    if (geoCoord && data[i].status == status) {
                        res.push({
                            name: data[i].name,
                            value: geoCoord,
                            test: "测试金风科技收到了"
                        });
                    }
                }
                return res;
            };
            option = {
                title: {
                    text: '终端设备分布和状态',
                    subtext: 'wumei-smart open source living iot platform',
                    sublink: 'http://www.wumei.live',
                    left: 'center'
                },
                tooltip: {
                    trigger: 'item',
                    formatter: function (params) {
                        var htmlStr = '<div style="padding:5px;line-height:28px;">';
                        htmlStr += "设备名称： " + params.data.value + "<br />";
                        htmlStr += "设备状态： " + "<span style='color:green'>在线</span>" + "<br />";
                        htmlStr += "设备影子： " + "<span style='color:green'>启用</span>" + "<br />";
                        htmlStr += "产品名称： " + params.data.value + "<br />";
                        htmlStr += "固件版本： " + params.data.value + "<br />";
                        htmlStr += "所在地址： " + params.data.value + "<br />";
                        htmlStr += "激活时间： " + params.data.value + "<br />";
                        htmlStr += '</div>';
                        return htmlStr;
                    }
                },
                bmap: {
                    center: [133, 37],
                    zoom: 5,
                    roam: true,
                    mapStyle: {
                        styleJson: [{
                                featureType: 'water',
                                elementType: 'all',
                                stylers: {
                                    color: '#a0cfff'
                                }
                            },
                            {
                                featureType: 'land',
                                elementType: 'all',
                                stylers: {
                                    color: '#FFFFFF'
                                }
                            },
                            {
                                featureType: 'railway',
                                elementType: 'all',
                                stylers: {
                                    visibility: 'off'
                                }
                            },
                            {
                                featureType: 'highway',
                                elementType: 'all',
                                stylers: {
                                    color: '#fdfdfd'
                                }
                            },
                            {
                                featureType: 'highway',
                                elementType: 'labels',
                                stylers: {
                                    visibility: 'off'
                                }
                            },
                            {
                                featureType: 'arterial',
                                elementType: 'geometry',
                                stylers: {
                                    color: '#fefefe'
                                }
                            },
                            {
                                featureType: 'arterial',
                                elementType: 'geometry.fill',
                                stylers: {
                                    color: '#fefefe'
                                }
                            },
                            {
                                featureType: 'poi',
                                elementType: 'all',
                                stylers: {
                                    visibility: 'off'
                                }
                            },
                            {
                                featureType: 'green',
                                elementType: 'all',
                                stylers: {
                                    visibility: 'off'
                                }
                            },
                            {
                                featureType: 'subway',
                                elementType: 'all',
                                stylers: {
                                    visibility: 'off'
                                }
                            },
                            {
                                featureType: 'manmade',
                                elementType: 'all',
                                stylers: {
                                    color: '#d1d1d1'
                                }
                            },
                            {
                                featureType: 'local',
                                elementType: 'all',
                                stylers: {
                                    color: '#d1d1d1'
                                }
                            },
                            {
                                featureType: 'arterial',
                                elementType: 'labels',
                                stylers: {
                                    visibility: 'off'
                                }
                            },
                            {
                                featureType: 'boundary',
                                elementType: 'all',
                                stylers: {
                                    color: '#999999'
                                }
                            },
                            {
                                featureType: 'building',
                                elementType: 'all',
                                stylers: {
                                    color: '#d1d1d1'
                                }
                            },
                            {
                                featureType: 'label',
                                elementType: 'labels.text.fill',
                                stylers: {
                                    color: '#999999'
                                }
                            }
                        ]
                    }
                },
                series: [{
                        type: 'scatter',
                        coordinateSystem: 'bmap',
                        data: convertData(this.deviceList, 1),
                        symbolSize: 20,
                        itemStyle: {
                            color: '#E6A23C'
                        }
                    },
                    {
                        type: 'scatter',
                        coordinateSystem: 'bmap',
                        data: convertData(this.deviceList, 2),
                        symbolSize: 20,
                        itemStyle: {
                            color: '#F56C6C'
                        }
                    }, {
                        type: 'scatter',
                        coordinateSystem: 'bmap',
                        data: convertData(this.deviceList, 4),
                        symbolSize: 20,
                        itemStyle: {
                            color: '#909399'
                        }
                    }, {
                        type: 'effectScatter',
                        coordinateSystem: 'bmap',
                        data: convertData(this.deviceList, 3),
                        symbolSize: 20,
                        showEffectOn: 'render',
                        rippleEffect: {
                            brushType: 'stroke',
                            scale: 5
                        },
                        label: {
                            formatter: '{b}',
                            position: 'right',
                            show: false
                        },
                        itemStyle: {
                            color: '#67C23A',
                            shadowBlur: 100,
                            shadowColor: '#333'
                        },
                        zlevel: 1
                    }
                ]
            };

            option && myChart.setOption(option);

        },
        /** EMQX状态统计 */
        drawStats() {
            // 基于准备好的dom，初始化echarts实例
            let myChart = echarts.init(this.$refs.statsChart);
            var option;

            option = {
                title: {
                    text: 'Mqtt 状态数据'
                },
                tooltip: {
                    trigger: 'axis',
                    axisPointer: {
                        type: 'shadow'
                    }
                },
                legend: {},
                grid: {
                    left: '3%',
                    right: '4%',
                    bottom: '3%',
                    containLabel: true
                },
                xAxis: {
                    type: 'value',
                    boundaryGap: [0, 0.01]
                },
                yAxis: {
                    type: 'category',
                    axisLabel: {
                        fontSize: 14
                    },
                    data: ['连接数量', '会话数量', '主题数量', '订阅数量', '路由数量', '保留消息']
                },
                series: [{
                        name: '历史最大数',
                        type: 'bar',
                        data: [this.stats["connections.max"], this.stats["sessions.max"], this.stats["topics.max"], this.stats["subscribers.max"], this.stats["routes.max"], this.stats["retained.max"]],
                        itemStyle: {
                            color: '#409EFF'
                        }
                    },
                    {
                        name: '当前数量',
                        type: 'bar',
                        data: [this.stats["connections.count"], this.stats["sessions.count"], this.stats["topics.count"], this.stats["subscribers.count"], this.stats["routes.count"], this.stats["retained.count"]],
                        itemStyle: {
                            color: '#67C23A'
                        }
                    }
                ]
            };

            option && myChart.setOption(option);

        },
        drawPieCpu() {
            // 基于准备好的dom，初始化echarts实例
            let myChart = echarts.init(this.$refs.pieCpu);
            var option;
            option = {
                title: {
                    text: 'CPU使用率 %',
                    left: 'left'
                },
                tooltip: {
                    trigger: 'item',
                },
                legend: {
                    orient: 'vertical',
                    left: 'right'
                },
                color: ['#E6A23C', '#F56C6C', '#DDD'],
                series: [{
                    name: 'CPU使用率 %',
                    type: 'pie',
                    radius: '55%',
                    label: {
                        show: false
                    },
                    labelLine: {
                        normal: {
                            position: "inner",
                            show: false
                        }
                    },
                    data: [{
                            value: this.server.cpu.used,
                            name: '用户'
                        },
                        {
                            value: this.server.cpu.sys,
                            name: '系统'
                        },
                        {
                            value: this.server.cpu.free,
                            name: '空闲'
                        }
                    ],
                }]
            };
            option && myChart.setOption(option);
        },
        drawPieMemery() {
            // 基于准备好的dom，初始化echarts实例
            let myChart = echarts.init(this.$refs.pieMemery);
            var option;
            option = {
                title: {
                    text: '内存使用 G',
                    left: 'left'
                },
                tooltip: {
                    trigger: 'item'
                },
                legend: {
                    orient: 'vertical',
                    left: 'right'
                },
                color: ['#F56C6C', '#DDD'],
                series: [{
                    name: '内存使用率 G',
                    type: 'pie',
                    radius: '55%',
                    label: {
                        show: false
                    },
                    labelLine: {
                        normal: {
                            position: "inner",
                            show: false
                        }
                    },
                    data: [{
                            value: this.server.mem.used,
                            name: '已用'
                        },
                        {
                            value: this.server.mem.free,
                            name: '剩余'
                        }
                    ]
                }]
            };
            option && myChart.setOption(option);
        },
        drawPieDisk() {
            // 基于准备好的dom，初始化echarts实例
            let myChart = echarts.init(this.$refs.pieDisk);
            var option;
            let one = this.server.sysFiles[0].used.replace("GB", "");
            let two = this.server.sysFiles[0].free.replace("GB", "");;
            option = {
                title: {
                    text: 'C盘状态 G',
                    left: 'left'
                },
                tooltip: {
                    trigger: 'item'
                },
                legend: {
                    orient: 'vertical',
                    left: 'right'
                },
                color: ['#F56C6C', '#DDD'],
                series: [{
                    name: '磁盘状态 G',
                    type: 'pie',
                    radius: '55%',
                    label: {
                        show: false
                    },
                    labelLine: {
                        normal: {
                            position: "inner",
                            show: false
                        }
                    },
                    data: [{
                            value: one,
                            name: '已用'
                        },
                        {
                            value: two,
                            name: '可用'
                        }
                    ]
                }]
            };
            option && myChart.setOption(option);
        }
    },
};
</script>
