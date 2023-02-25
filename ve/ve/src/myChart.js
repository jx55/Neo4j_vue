import * as echarts from 'echarts';

let chart=function(datas,links,categories){
 // console.log(datas);
 console.log(links);
 // console.log(categories);
let option = {
    // title: {
    //   text: "知识图谱可视化",
    //   subtext: "",
    //   top: "top",
    //   left: "center",
    //   textStyle: {
    //     fontSize: 30
    //   },
    // },
    tooltip: {},
    legend: [{
      formatter: function (name) {
          return echarts.format.truncateText(name, 80, '14px Microsoft Yahei', '…');
      },
      tooltip: {
          show: true
      },
      selectedMode: 'false',
      orient: 'vertical',
      // orient: 'horizontal'
      left: "right",
      align: "auto",
      top:40,
      right: 20,
      // padding: 5,
      data: categories.map(function (a) {
        return a.name;
      }),
    }],
    toolbox: {
        show: true,
        feature: {
          dataView: {
              show: true,
              readOnly: true
          },
          restore: {
              show: true
          },
          saveAsImage: {
              show: true
          }
        }
    },
    animationDuration: 3000,
    animationEasingUpdate: 'quinticInOut',
    series: [{
        name: 'NBA联赛',
        type: 'graph',
        layout: 'force',

        force: {
          repulsion: 800,
          gravity: 0.2,
          edgeLength: 120,
          layoutAnimation:true
        },

        data:datas,
        links:links,
        categories:categories,

        roam: true,
        draggable:true,
        label: {
          show: true,
          position: 'right',
          textStyle:{
            fontSize:14,
            color: '#000000',
          },
          formatter: '{b}'
        },
        nodeScaleRatio: 1, //图标大小是否随鼠标滚动而变
        //关系相关配置
        edgeLabel: {
            normal: {
                show: true,
                position:"middle",
                textStyle: {
                    fontSize: 10,
                    color: '#000000',
                },
                formatter: '{c}',
            },
        },
        symbolKeepAspect: false,
        focusNodeAdjacency: false, // 指定的节点以及其所有邻接节点高亮

        scaleLimit: {
          min: 0.4,
          max: 3
        },
        lineStyle: {
          color: 'source',
          curveness: 0.1,
          width: 1,
          cap: "round"
        },
        emphasis: {
          focus: 'adjacency'
        },
        autoCurveness: "number",
        edgeSymbol: ['none', 'arrow'],
        edgeSymbolSize: 6,
        // selectedMode: "multiple",

    }],
};

return option;
}

export default chart;
