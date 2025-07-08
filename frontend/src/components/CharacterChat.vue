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
      // 绘制知识图谱的节点数据
      input: '',
      echartsData: [],
      nodesRelation: [],
      echartsNode: [],
      // 防止出现多个echarts初始化的情况
      myChart: '',
      options: {},
      test: null
    }
  },
  mounted() {
  },
  methods: {
    updateMsg(msg) {
      this.input = msg
      this.searchGraph()
    },

    async searchGraph() {

      //this.test=this.input
      const neo4j = require('neo4j-driver')

      const uri = process.env.VUE_APP_NEO4J_URI;
      const user = process.env.VUE_APP_NEO4J_USER;
      const password = process.env.VUE_APP_NEO4J_PASSWORD;
      const database = process.env.VUE_APP_NEO4J_DATABASE;

      const driver = neo4j.driver(uri, neo4j.auth.basic(user, password))
      const session = driver.session({database: database})

      try {

        const colorMap = ['#C04851', '#F0C9CF']

        let id = [];
        //let test1=[]
        const readQuery1 = `MATCH (m:Person{name:'${this.input}'}) -[p]->(n:Person) RETURN m,n,p`
        //var me = { records: [] }

        const result = await session.run(readQuery1, {})
        //this.test=result.records[0]


        for (let i = 0; i < result.records.length; i++) {
          //this.test=result.records[i]._fields[1].labels[0]
          //test1.push(result.records[i]._fields[1].properties.name)
          //test1.push(result.records[i]._fields[0].properties.name)
          if (id.indexOf(result.records[i]._fields[1].properties.id) == -1) {
            //this.test=result.records[i]._fields[1].properties.id+1
            id.push(result.records[i]._fields[1].properties.id)
            // this.test=id
            this.echartsData.push({
              name: result.records[i]._fields[1].properties.name,
            });
            // /this.test=this.echartsData
          }
          if (id.indexOf(result.records[i]._fields[0].properties.id) == -1) {
            //this.test=result.records[i]._fields[1].properties.id+1
            id.push(result.records[i]._fields[0].properties.id)
            // this.test=id
            this.echartsData.push({
              name: result.records[i]._fields[0].properties.name,
            });
            // /this.test=this.echartsData
          }
          let line_type = 0
          if (result.records[i]._fields[2].type == "人人关系山海经") {
            line_type = 0
          } else if (result.records[i]._fields[2].type == "人人关系淮南子") {
            line_type = 1
          }
          this.nodesRelation.push(
              {
                source: result.records[i]._fields[0].properties.name,
                target: result.records[i]._fields[1].properties.name,
                name: result.records[i]._fields[2].properties.type,
                lineStyle: {
                  color: colorMap[line_type]
                }
              }
          )
        }
        const readQuery2 = `MATCH (m:Person{name:'${this.input}'}) <-[p]-(n:Person) RETURN m,n,p`
        //var me = { records: [] }

        const result2 = await session.run(readQuery2, {})


        for (let i = 0; i < result2.records.length; i++) {
          //this.test=result.records[i]._fields[1].labels[0]
          //test1.push(result.records[i]._fields[1].properties.name)
          //test1.push(result.records[i]._fields[0].properties.name)
          if (id.indexOf(result2.records[i]._fields[1].properties.id) == -1) {
            //this.test=result.records[i]._fields[1].properties.id+1
            id.push(result2.records[i]._fields[1].properties.id)
            // this.test=id
            this.echartsData.push({
              name: result2.records[i]._fields[1].properties.name,
            });
            // /this.test=this.echartsData
          }
          if (id.indexOf(result2.records[i]._fields[0].properties.id) == -1) {
            //this.test=result.records[i]._fields[1].properties.id+1
            id.push(result2.records[i]._fields[0].properties.id)
            // this.test=id
            this.echartsData.push({
              name: result2.records[i]._fields[0].properties.name,
            });
            // /this.test=this.echartsData
          }
          let line_type = 0
          if (result.records[i]._fields[2].type == "人人关系山海经") {
            line_type = 0
          } else if (result.records[i]._fields[2].type == "人人关系淮南子") {
            line_type = 1
          }
          this.nodesRelation.push(
              {
                source: result2.records[i]._fields[1].properties.name,
                target: result2.records[i]._fields[0].properties.name,
                name: result.records[i]._fields[2].type + "-" + result.records[i]._fields[2].properties.type,
                lineStyle: {
                  color: colorMap[line_type]
                }
              }
          )
        }


        //this.test=test1
        session.close();

        //const readQuery2 = `MATCH p=()-->() RETURN p`

        // const readQuery = `MATCH (p:Person)

        //               WHERE p.name = $personName

        //               RETURN p.name AS name`

        /*var me = { records: [] }

        const result = await session.run(readQuery2, {})

        me.records = result.records;

        for (let i = 0; i < me.records.length; i++) {

          this.echartsData.push({

            name: me.records[i]._fields[0].segments[0].start.properties.name,

            category: me.records[i]._fields[0].segments[0].start.labels[0]

          });

          this.echartsData.push({

            name: me.records[i]._fields[0].segments[0].end.properties.name,

            category: me.records[i]._fields[0].segments[0].end.labels[0]

          });

          this.nodesRelation.push({

            source: me.records[i]._fields[0].segments[0].start.properties.name,

            target: me.records[i]._fields[0].segments[0].end.properties.name,

            name: me.records[i]._fields[0].segments[0].relationship.type,

          });

        }

        //删除arr中的重复对象

        var arrId = [];

        var legend = [];

        for (var item of this.echartsData) {

          legend.push({ name: item.category })

          if (arrId.indexOf(item.name) == -1) {

            arrId.push(item.name)

            this.echartsNode.push(item);

          }

        }

        this.category = Array.from(new Set(legend))

        session.close();*/

        // me.closeLoading(false);

        var options = {}

        options = {

          tooltip: {//弹窗

            show: false,

            // enterable: true,//鼠标是否可进入提示框浮层中

            // formatter: formatterHover,//修改鼠标悬停显示的内容

          },

          legend: {

            type: 'scroll',

            orient: 'vertical',

            left: 10,

            top: 20,

            bottom: 20,

            data: this.category

          },

          series: [

            {

              categories: this.category,

              type: "graph",

              layout: "force",
              zoom: 0.6,
              symbolSize: 60,
              // 节点是否可以拖动
              draggable: true,
              roam: true,
              hoverAnimation: false,
              legendHoverLink: false,
              nodeScaleRatio: 0.6, //鼠标漫游缩放时节点的相应缩放比例，当设为0时节点不随着鼠标的缩放而缩放
              focusNodeAdjacency: false, //是否在鼠标移到节点上的时候突出显示节点以及节点的边和邻接节点。
              // categories: categories,
              itemStyle: {
                color: "#5c2223",
              },
              edgeSymbol: ["", "arrow"],
              // edgeSymbolSize: [80, 10],
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
                  // 多字换行

                  formatter: function (params) {

                    // console.log(params);

                    var newParamsName = "";

                    var paramsNameNumber = params.name.length;

                    var provideNumber = 7; //一行显示几个字

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

                repulsion: 200, // 节点之间的斥力因子。支持数组表达斥力范围，值越大斥力越大。

                gravity: 0.01, // 节点受到的向中心的引力因子。该值越大节点越往中心点靠拢。

                edgeLength: 400, // 边的两个节点之间的距离，这个距离也会受 repulsion影响 。值越大则长度越长

                layoutAnimation: true, // 因为力引导布局会在多次迭代后才会稳定，这个参数决定是否显示布局的迭代动画

                // 在浏览器端节点数据较多（>100）的时候不建议关闭，布局过程会造成浏览器假死。

              },

              data: this.echartsData,

              links: this.nodesRelation,

              // categories: this.categories
              lineStyle: {
                opacity: 0.9,
                width: 1.1,
                curveness: 2,
              }

            }

          ]

        }

        console.log(this, 66633);

        //节点自定义拖拽不回弹

        this.myChart = this.$echarts.init(this.$refs.graph);

        const chart = this.myChart;

        this.myChart.setOption(options);

        chart.on('mouseup', function (params) {

          var option = chart.getOption();

          option.series[0].data[params.dataIndex].x = params.event.offsetX;

          option.series[0].data[params.dataIndex].y = params.event.offsetY;

          option.series[0].data[params.dataIndex].fixed = true;

          chart.setOption(option);

        });

      } catch (error) {

        console.error('Something went wrong: ', error)

      } finally {

        // await session.close()

      }

    },

  }

}

</script>

<style lang='less' scoped>

</style>
