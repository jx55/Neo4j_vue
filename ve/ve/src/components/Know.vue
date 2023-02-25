<template>
  <div>
    <div class="title">
      知识图谱可视化
    </div>

    <div class="search">
        <van-search
          v-model="searchvalue"
          show-action
          placeholder="请输入搜索内容"
          @search="onSearch"
          @cancel="onCancel"
        />
    </div>

    <div class="choose">
      <el-collapse v-model="activeNames" @change="handleChange">
        <el-collapse-item title="添加节点 Add Node" name="1">
          <el-form ref="form" :model="form" label-width="80px">
            <el-form-item label="节点名称">
              <el-input v-model="form.name"></el-input>
            </el-form-item>

            <el-form-item style="margin-top: -10px;">
              <el-button type="primary" @click="AddNodeSubmit">添加节点</el-button>
            </el-form-item>
          </el-form>
        </el-collapse-item>

        <el-collapse-item title="添加关系 Add Relation" name="2">
         <el-form ref="form" :model="form" label-width="80px">
          <el-form-item label="源节点">
            <el-input v-model="form.sourcenode"></el-input>
          </el-form-item>
          <el-form-item label="目标节点" style="margin-top: -10px;">
            <el-input v-model="form.targetnode"></el-input>
          </el-form-item>

          <el-form-item style="margin-top: -10px;">
            <el-button type="primary" @click="AddRelationSubmit">添加关系</el-button>
          </el-form-item>
         </el-form>
        </el-collapse-item>

        <el-collapse-item title="搜索节点" name="3">
         <el-form ref="form" :model="form" label-width="80px">
          <el-form-item label="节点名称">
            <el-input v-model="form.searchname"></el-input>
          </el-form-item>

          <el-form-item style="margin-top: -10px;">
            <el-button type="primary" @click="SearchNodeSubmit">搜索节点</el-button>
          </el-form-item>
         </el-form>
        </el-collapse-item>

        <el-collapse-item title="删除节点" name="4">
          <el-form ref="form" :model="form" label-width="80px">
           <el-form-item label="节点名称">
             <el-input v-model="form.deletename"></el-input>
           </el-form-item>

           <el-form-item style="margin-top: -10px; margin-lef:50% ">
             <el-button  type="primary" @click="DeleteNodeSubmit">删除节点</el-button>
           </el-form-item>
          </el-form>
        </el-collapse-item>
      </el-collapse>
    </div>

    <div id="know"></div>
  </div>
</template>

<script>
  import * as echarts from 'echarts';
  import $ from 'jquery';
  import Vue from 'vue';
  import { Search,Toast  } from 'vant';
  Vue.use(Search);
  Vue.use(Toast);

  export default{
    name:"Know",
    data(){
      return{
        datas:[],
        categorydatas:[],
        linksdatas:[],
        searchvalue:"",
        form:{
          name: '',
          sourcenode: "",
          targetnode: "",
          searchname: "",
          deletename: ""
        },
        tabPosition: 'left',
        activeNames: ['1'],
      }
    },
    methods:{
      onSearch(val) {
        Toast("正在搜索");
        console.log(val);
      },
      onCancel() {
        Toast('取消');
      },
      AddNodeSubmit() {
        console.log(this.form.name);
        this.$axios.get("search/addnode?name="+this.form.name)
          .then()
          .catch()
      },
      AddRelationSubmit() {
        console.log(this.form.sourcenode);
        console.log(this.form.targetnode);
      },
      SearchNodeSubmit() {
        console.log(this.form.searchname);
      },
      DeleteNodeSubmit() {
        console.log(this.form.deletename);
      },
      onSubmit() {
        console.log(this.form.name);
      },
      onSubmit1() {
        console.log(this.form.name);
      },
      onSubmit2() {
        console.log(this.form.name);
      },
      onSubmit3() {
        console.log(this.form.name);
      },
      onSubmit3() {
        console.log(this.form.name);
      }
    },
    created(){

    },
    mounted(){
      var _this=this;

      _this.$axios.get("relation/all")
        .then(res=>{
          _this.linksdatas=res.data.data;
          // console.log("获取数据成功！");
          // console.log(_this.linksdatas);
        })
        .catch(err=>{
          console.log("获取数据异常！");
        })

      _this.$axios.get("category/all")
        .then(res=>{
          _this.categorydatas=res.data.data;
          console.log(_this.categorydatas);
        })
        .catch(err=>{
          console.log("获取数据异常！");
        })

      var know=document.getElementById('know');
      // know.style.height=screen.height;
      // console.log();
      var myChart = echarts.init(know);

      _this.$axios.get("search/all")
        .then(res=>{
          _this.datas=res.data.data;

          console.log(_this.linksdatas);
          console.log(_this.datas);
          console.log(_this.categorydatas);

          var option = {
              // backgroundColor: new echarts.graphic.RadialGradient(0.3, 0.3, 0.8, [{
              //     offset: 0,
              //     color: '#FFFFFF'
              // }, {
              //     offset: 1,
              //     color: '#FFFFCC'
              // }]),

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
                data: _this.categorydatas.map(function (a) {
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
                  name: 'C语言知识图谱',
                  type: 'graph',
                  layout: 'force',

                  force: {
                    repulsion: 400,
                    gravity: 0.2,
                    edgeLength: 40,
                    layoutAnimation:true
                  },

                  data:_this.datas,
                  links:_this.linksdatas,
                  categories:_this.categorydatas,

                  roam: true,
                  label: {
                    show: true,
                    position: 'right',
                    formatter: '{b}'
                  },
                  labelLayout: {
                    hideOverlap: true
                  },
                  scaleLimit: {
                    min: 0.4,
                    max: 3
                  },
                  lineStyle: {
                    color: 'source',
                    // curveness: 0.3,
                    width: 1,
                    cap: "round"
                  },
                  emphasis: {
                    focus: 'adjacency'
                  },
                  autoCurveness: "number",
              }]
            };

            myChart.setOption(option);
            window.addEventListener("resize", function() {
              myChart.resize();
            });
          })
        .catch(err=>{
          console.log("获取数据异常！");
        })
    }
  }
</script>

<style>
  #know{
    width:70%;
    height:700px;
    border:1px solid #42B983;
    float:left;
  }
  .title{
    width:100%;
    height:50px;
    text-align: center;
    font-size: 40px;
    /* border:1px solid red; */
    margin-top:10px;
  }
  .choose{
    width:23%;
    height:700px;
    /* border:1px solid salmon; */
    float:left;
    margin-top:30px;
    margin-left:2%;
    margin-right: 1%;
  }
  .search{
    width:400px;
    height:30px;
    float:right;
    margin-top: -30px;
    margin-bottom: 20px;
    /* border:1px solid mediumpurple; */
  }
</style>
