<template>
    <div style="width:100vw;height:100vh">
        <div ref="graph" style="width:100vw;height:100vh"></div>
    </div>
</template>

<script>
export default {
    name: "CharacterChat",
    data() {
        return {
            input: '', // 输入的搜索人物
            echartsData: [], // 存储节点数据
            nodesRelation: [], // 存储节点关系数据
            myChart: '', // 存储ECharts实例
            options: {}, // 存储ECharts配置
            category: [], // 添加缺少的属性
            test: null
        }
    },
    mounted() {
        this.init();
    },
    methods: {
        // 添加缺少的 init 方法
        init() {
            // 初始化操作
            console.log('Component initialized');
        },

        updateMsg(msg) {
            this.input = msg
            this.searchGraph()
        },

        async searchGraph() {
            // 开始时清空数据
            this.echartsData = [];
            this.nodesRelation = [];

            // 数据库连接相关配置
            const neo4j = require('neo4j-driver')

            const uri = process.env.VUE_APP_NEO4J_URI;
            const user = process.env.VUE_APP_NEO4J_USER;
            const password = process.env.VUE_APP_NEO4J_PASSWORD;
            const database = process.env.VUE_APP_NEO4J_DATABASE;

            const driver = neo4j.driver(uri, neo4j.auth.basic(user, password))
            const session = driver.session({ database: database })

            try {
                //关系的颜色: 红色和粉色
                const colorMap = ['#C04851', '#F0C9CF']

                let id = [];
                // 根据input查找和它有关的节点 
                // m 源节点 n 目标节点 p 关系
                const readQuery1 = `MATCH (m:Person{name:'${this.input}'}) -[p]->(n:Person) RETURN m,n,p`

                const result = await session.run(readQuery1, {})
                console.log(result.records[0].get('p'))

                for (let i = 0; i < result.records.length; i++) {
                    const record = result.records[i];
                    const sourceNode = record.get('m');      // 源节点
                    const targetNode = record.get('n');     // 目标节点
                    const relationship = record.get('p');   // 关系

                    // 处理目标节点
                    if (id.indexOf(targetNode.properties.id) === -1) {
                        id.push(targetNode.properties.id);
                        this.echartsData.push({
                            name: targetNode.properties.name,
                        });
                    }

                    // 处理源节点
                    if (id.indexOf(sourceNode.properties.id) === -1) {
                        id.push(sourceNode.properties.id);
                        this.echartsData.push({
                            name: sourceNode.properties.name,
                        });
                    }

                    let line_type = 0;
                    if (relationship.type === "人人关系山海经") {
                        line_type = 0;
                    } else if (relationship.type === "人人关系淮南子") {
                        line_type = 1;
                    }

                    this.nodesRelation.push({
                        source: sourceNode.properties.name,
                        target: targetNode.properties.name,
                        name: relationship.properties.type,
                        lineStyle: {
                            color: colorMap[line_type]
                        }
                    });
                }

                // 第二次查询 相反 指向该人物的关系
                const readQuery2 = `MATCH (m:Person{name:'${this.input}'}) <-[p]-(n:Person) RETURN m,n,p`

                const result2 = await session.run(readQuery2, {})

                for (let i = 0; i < result2.records.length; i++) {
                    const record = result2.records[i];
                    const targetNode = record.get('m');     // 目标节点 (被指向的节点)
                    const sourceNode = record.get('n');     // 源节点 (指向的节点)
                    const relationship = record.get('p');   // 关系

                    // 处理源节点
                    if (id.indexOf(sourceNode.properties.id) === -1) {
                        id.push(sourceNode.properties.id);
                        this.echartsData.push({
                            name: sourceNode.properties.name,
                        });
                    }

                    // 处理目标节点
                    if (id.indexOf(targetNode.properties.id) === -1) {
                        id.push(targetNode.properties.id);
                        this.echartsData.push({
                            name: targetNode.properties.name,
                        });
                    }

                    let line_type = 0;
                    if (relationship.type === "人人关系山海经") {
                        line_type = 0;
                    } else if (relationship.type === "人人关系淮南子") {
                        line_type = 1;
                    }

                    this.nodesRelation.push({
                        source: sourceNode.properties.name,
                        target: targetNode.properties.name,
                        name: relationship.properties.type,
                        lineStyle: {
                            color: colorMap[line_type]
                        }
                    });
                }

                await session.close(); // 添加 await

                const options = {
                    tooltip: {
                        show: false,
                    },
                    legend: {
                        type: 'scroll',
                        orient: 'vertical',
                        left: 10,
                        top: 20,
                        bottom: 20,
                        data: this.category
                    },
                    series: [{
                        categories: this.category,
                        type: "graph",
                        layout: "force",
                        zoom: 0.6,
                        symbolSize: 60,
                        draggable: true,
                        roam: true,
                        hoverAnimation: false,
                        legendHoverLink: false,
                        nodeScaleRatio: 0.6,
                        focusNodeAdjacency: false,
                        itemStyle: {
                            color: "#5c2223",
                        },
                        edgeSymbol: ["", "arrow"],
                        edgeLabel: {
                            normal: {
                                show: true,
                                textStyle: {
                                    fontSize: 12,
                                },
                                formatter(x) {
                                    return x.data.name;
                                },
                            },
                        },
                        label: {
                            normal: {
                                show: true,
                                textStyle: {
                                    fontSize: 12,
                                },
                                color: "#f6f6f6",
                                textBorderColor: '#EEA2A4',
                                textBorderWidth: '1.3',
                                formatter: function (params) {
                                    var newParamsName = "";
                                    var paramsNameNumber = params.name.length;
                                    var provideNumber = 7;
                                    var rowNumber = Math.ceil(paramsNameNumber / provideNumber);

                                    if (paramsNameNumber > provideNumber) {
                                        for (var p = 0; p < rowNumber; p++) {
                                            var tempStr = "";
                                            var start = p * provideNumber;
                                            var end = start + provideNumber;

                                            if (p == rowNumber - 1) {
                                                tempStr = params.name.substring(start, paramsNameNumber);
                                            } else {
                                                tempStr = params.name.substring(start, end) + "\n\n";
                                            }
                                            newParamsName += tempStr;
                                        }
                                    } else {
                                        newParamsName = params.name;
                                    }
                                    return newParamsName;
                                },
                            },
                        },
                        force: {
                            repulsion: 200,
                            gravity: 0.01,
                            edgeLength: 400,
                            layoutAnimation: true,
                        },
                        data: this.echartsData,
                        links: this.nodesRelation,
                        lineStyle: {
                            opacity: 0.9,
                            width: 1.1,
                            curveness: 2,
                        }
                    }]
                }

                // 优化图表初始化
                if (this.myChart) {
                    this.myChart.dispose();
                }

                this.myChart = this.$echarts.init(this.$refs.graph);
                this.myChart.setOption(options);

                // 优化事件监听
                this.myChart.on('mouseup', (params) => {
                    if (params.dataIndex !== undefined) {
                        const option = this.myChart.getOption();
                        option.series[0].data[params.dataIndex].x = params.event.offsetX;
                        option.series[0].data[params.dataIndex].y = params.event.offsetY;
                        option.series[0].data[params.dataIndex].fixed = true;
                        this.myChart.setOption(option);
                    }
                });

            } catch (error) {
                console.error('Something went wrong: ', error)
            } finally {
                await driver.close(); // 确保驱动关闭
            }
        },
    }
}
</script>

<style lang='less' scoped></style>