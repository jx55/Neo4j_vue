<template>
  <div class="box">
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

    <div id="know"></div>

    <div class="op">
      <!-- 搜索后恢复所有数据 -->
      <el-button @click="AllData">全部节点</el-button>

      <!-- 添加节点部分-->
        <el-popover
          placement="top-start"
          title="添加节点"
          width="200"
          trigger="click"
          content=""
          v-model="addnodevis"
          >

            <el-input v-model="nodeinput" placeholder="请输入节点名称"
                    style="margin-bottom: 5px;">
            </el-input>

            <el-select v-model="nodevalue"
                        placeholder="请选择节点类别"
                        style="margin-bottom: 5px;">
                <el-option
                  v-for="item in options"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value">
                </el-option>
            </el-select>

            <el-button class="btn" @click="AddNodeSubmit" style="margin-left: 30%;">
              添加
            </el-button>

          <el-button class="btn" slot="reference">
            添加节点
          </el-button>
        </el-popover>

        <!-- 删除节点部分 -->
        <el-button  @click="DeleteNodeSubmit" :disabled="delbut">删除节点</el-button>

        <!-- 添加关系部分 -->
        <el-button @click="AddRelationSubmit" :disabled="addrelabut">添加关系</el-button>

        <!-- 删除关系部分 -->
        <el-button class="btn" @click="DeleteRelationSubmit" :disabled="delrelabut">删除关系</el-button>

        <!-- 修改关系部分 -->
        <el-popover
          ref="popover"
          placement="right"
          title="修改关系"
          width="200"
          trigger="click"
          content="这是一段内容。">
        </el-popover>
        <el-button slot="reference" @click="UpdateRelation" :disabled="updaterelabut">修改关系</el-button>


        <!-- 添加属性部分 -->
        <el-popover
          placement="bottom"
          title="添加属性"
          width="200"
          trigger="click"
          v-model="updateproperteis"
          >
          <el-input v-model="addproperties" placeholder="请输入属性名"
                    style="margin-bottom: 5px;">
          </el-input>
          <el-input v-model="addpropertiescontent" placeholder="请输入属性值"
                    style="margin-bottom: 5px;">
          </el-input>
          <el-button class="btn" @click="UpdateProperties" style="margin-left: 30%;">添加</el-button>

          <el-button class="btn" slot="reference" :disabled="updateprobut">添加属性</el-button>
        </el-popover>


        <el-button
          plain
          @click="open2"
          v-show="inventory"
          id="C">
          右下角
        </el-button>
    </div>

    <div>

    </div>

  </div>
</template>

<script>
  import * as echarts from 'echarts';
  import Vue from 'vue';
  import { Search,Toast  } from 'vant';
  import chart from '../myChart.js';
  import content from '../innerhtml.js';
  import $ from 'jquery';
  Vue.use(Search);
  Vue.use(Toast);

  let know;
  let myChart;
  const labelmap=new Map([
    ["联赛",40],
    ["球队",30],
    ["教练",20],
    ["球员",15]
  ]);

  export default{
    name:"Pos",
    data(){
      return{
        //右下角提示信息
        inventory:false,
        dblclickobj:[],

        //echarts图表相关
        datas:[],
        categorydatas:[],
        linksdatas:[],
        nodelabels:[],
        categorydatas_:[],
        datas_:[],
        linksdatas_:[],

        //搜索
        searchvalue:"",

        //监听事件
        clickobj:[],
        keyclick1:{},
        keyclick2:{},
        keyclickrestore:{},


        //
        options: [{
          value: '球员',
          label: '球员'
        }, {
          value: '球队',
          label: '球队'
        }, {
          value: '教练',
          label: '教练'
        }, {
          value: '联赛',
          label: '联赛'
        }],

        //下面按钮的绑定信息
        nodevalue: '',
        nodeinput:'',
        addproperties:'',
        addpropertiescontent:'',


        //按钮状态
        delbut:true,
        delrelabut:true,
        updaterelabut:true,
        addrelabut:true,
        updateprobut:true,

        //弹出框是否可见
        addnodevis:false,
        updateproperteis:false,


      }
    },
    methods:{
      //恢复原有节点
      AllData(){
        console.log(this.categorydatas_);
        console.log(this.linksdatas_);
        console.log(this.datas_);

        let target = event.target;
        if(target.nodeName == "SPAN"){
          console.log("ddddd");
            target = event.target.parentNode;
        }
        target.blur();

        this.categorydatas=this.categorydatas_;
        this.linksdatas=this.linksdatas_;
        this.datas=this.datas_;
      },

      //搜索
      onSearch(val) {
        Toast("正在搜索");
        console.log(val);

        let labelstr;
        let uid;
        for(let x=0;x<this.datas.length;x++)
        {
          if(this.datas[x].name==val){
            labelstr=this.datas[x].labels;
            uid=this.datas[x].uid;
            break;
          }
        }
        console.log(labelstr,uid);
        this.$axios({
          method:"post",
          url:"graph/search/node",
          data:{
            "label":labelstr,
            "nodeId":uid
          }
        }).then(res=>{
          // console.log(res.data.data);
          let arrnode=res.data.data.node;
          let arrlinks=res.data.data.relationship;
          // console.log(arrnode);
          // console.log(arrlinks);

          let arr1=[]; //存储节点
          let arr2=[]; //存储关系
          let arr3=[]; //存储目录
          for(let x=0;x<arrnode.length;x++){
            let obj={};
            obj.name=arrnode[x].name;
            obj.draggable="true";
            obj.value=1;
            obj.category=arrnode[x].category;

            //检查目录是否在arr3中，没有则加入
            let findsymbol=0;
            for(let i=0;i<arr3.length;i++){
              if(arr3[i].name==obj.category){
                findsymbol=1;
                break;
              }
            }
            if(findsymbol==0){
              let obj4={};
              obj4.name=obj.category;
              arr3.push(obj4);
            }

            obj.uid=arrnode[x].uuid;
            obj.labels="";
            for(let y=0;y<this.nodelabels.length;y++)
            {
              if(this.nodelabels[y].name==obj.name){
                obj.labels=this.nodelabels[y].labels;
                break;
              }
            }
            obj.symbolSize=labelmap.get(obj.labels);
            arr1.push(obj);
          }

          console.log(arrlinks);
          for(let z=0;z<arrlinks.length;z++){
            let sourcename,targetname;
            let flag1=0,flag2=0;
            for(let x=0;x<this.datas.length;x++){
              if(this.datas[x].uid==arrlinks[z].sourceid && flag1==0){
                sourcename=this.datas[x].name;
                flag1++;
              }
              if(this.datas[x].uid==arrlinks[z].targetid && flag2==0){
                targetname=this.datas[x].name;
                flag2++;
              }
              if(flag1==1 && flag2==1){
                break;
              }
            }
            let valuename;
            for(let x=0;x<this.linksdatas.length;x++){
              if(this.linksdatas[x].source==sourcename && this.linksdatas[x].target==targetname){
                valuename=this.linksdatas[x].value;
                break;
              }
            }

            let obj0={};
            obj0.source=sourcename;
            obj0.target=targetname;
            obj0.value=valuename;
            arr2.push(obj0);
          }

          //保存原来的完整数据
          this.datas_=this.datas;
          this.linksdatas_=this.linksdatas;
          this.categorydatas_=this.categorydatas;

          this.linksdatas=arr2;
          this.categorydatas=arr3;
          this.datas=arr1;

          // console.log(this.linksdatas);
          // console.log(this.categorydatas);
          // console.log(this.datas);
        }).catch(err=>{
          console.log("搜索异常");
          Toast("搜索异常!");
        })
      },

      onCancel() {
        Toast('取消');
      },

      //添加节点
      AddNodeSubmit() {
        console.log(this.nodeinput);
        console.log(this.nodevalue);

        //设置弹出框消失
        this.addnodevis=false;

        this.$axios({
          method:"post",
          url:"graph/add/node",
          data:{
            "name":this.nodeinput,
            "label":this.nodevalue
          }
        }).then(res=>{
          let obj={};
          obj.name=this.nodeinput;
          obj.draggable="true";
          obj.value=1;
          obj.symbolSize=labelmap.get(this.nodevalue);
          if(this.nodevalue=="球队"){
            obj.category=this.nodeinput;
          }
          else{
            obj.category="NBA";
          }
          obj.labels=this.nodevalue;
          obj.uid=res.data.data[0].uuid;
          (this.datas).push(obj);

          if(obj.labels=="球队"){
            let obj2={};
            obj2.name=this.nodeinput;
            (this.categorydatas).push(obj2);
          }

          Toast('添加成功!');

        }).catch(err=>{
          console.log("添加节点失败");
          Toast('添加失败!');
        })
      },

      //添加关系
      AddRelationSubmit() {
        console.log(this.keyclick1);
        console.log(this.keyclick2);

        console.log(event);
        let target = event.target;
        if(target.nodeName == "SPAN"){
            target = event.target.parentNode;
        }
        target.blur();

        let sourceId,targetId;
        let sourceLabel,targetLabel;
        let sourcename,targetname;
        let flag=0;
        for(let x=0;x<this.datas.length;x++){
          if(this.datas[x].name==this.keyclick1.name){
            sourceId=this.datas[x].uid;
            sourceLabel=this.datas[x].labels;
            sourcename=this.datas[x].name;
            flag++;
            if(flag==2) break;
          }
          if(this.datas[x].name==this.keyclick2.name){
            targetId=this.datas[x].uid;
            targetLabel=this.datas[x].labels;
            targetname=this.datas[x].name;
            flag++;
            if(flag==2) break;
          }
        }

        this.$axios({
          method:"post",
          url:"graph/add/relation",
          data:{
            "label1":sourceLabel,
            "label2":targetLabel,
            "sourceId":sourceId,
            "targetId":targetId,
            "name":sourcename
          }
        }).then(res=>{
          console.log(res.data.data);
          let obj={};
          obj.source=sourcename;
          obj.target=targetname;
          this.linksdatas.push(obj);

          //还应该恢复两个节点的状态
          let findone=0;
          let findtwo=0;
          for(let r=0;r<this.datas.length;r++){
            if(findone==0 && this.datas[r].name==this.keyclick1.name){
              this.datas.splice(r,1);
              this.datas.push(this.keyclick1);
              findone++;
            }
            if(findtwo==0 && this.datas[r].name==this.keyclick2.name){
              this.datas.splice(r,1);
              this.datas.push(this.keyclick2);
              findtwo++;
            }
            if(findone==1 && findtwo==2){
              break;
            }
          }
          console.log(this.datas);

          // restorenode=0;

          //已经添加过关系了，将两个对象置空
          this.keyclick1={};
          this.keyclick2={};
          this.addrelabut=true;

          Toast("关系添加成功");
        }).catch(err=>{
          console.log("添加关系异常");

          //添加失败也应该置空
          this.keyclick1={};
          this.keyclick2={};
          this.addrelabut=true;

          Toast("关系添加异常");
        })
      },

      //修改关系
      UpdateRelation(){
        console.log("修改关系");
      },

      //删除关系
      DeleteRelationSubmit(){
        console.log(this.clickobj);
        console.log(this.linksdatas_);

        let label;
        let rid;
        for(let x=0;x<this.linksdatas_.length;x++){
          if(this.linksdatas_[x].source==this.clickobj.source && this.linksdatas_[x].target==this.clickobj.target){
            rid=this.linksdatas_[x].id;
            break;
          }
        }
        for(let y=0;y<this.datas.length;y++){
          if(this.datas[y].name==this.clickobj.source){
            label=this.datas[y].labels;
            break;
          }
        }

        console.log(label,rid);
        this.$axios({
          method:"delete",
          url:"graph/del/relation",
          data:{
            "label":label,
            "rid":rid
          }
        }).then(res=>{
          console.log(res.data);
          for(let z=0;z<this.linksdatas.length;z++){
            if(this.linksdatas[z].source==this.clickobj.source && this.linksdatas[z].target==this.clickobj.target){
              this.linksdatas.splice(z,1);
              break;
            }
          }

        }).catch(err=>{
          console.log("关系删除失败");
        })
      },

      //添加属性
      UpdateProperties(){
        console.log("修改属性");
        console.log(this.addproperties);
        console.log(this.addpropertiescontent);
        console.log(this.clickobj);

        //设置弹出框消失
        this.updateproperteis=false;

        this.$axios({
          method:"post",
          url:"graph/update/node",
          data:{
            "nodeId":this.clickobj.uid,
            "label":this.clickobj.labels,
            "properties":this.addproperties,
            "propertiesName":this.addpropertiescontent
          }
        }).then(res=>{
          console.log(res.data.data[0].name);

          for(let i=0;i<this.datas.length;i++){
            if(this.datas[i].name==res.data.data[0].name){
              let obj=this.clickobj;
              this.datas.splice(i,1);
              obj[this.addproperties]=this.addpropertiescontent;
              console.log(obj);
              this.datas.push(obj);
              break;
            }
          }

          this.updateprobut=true;
          this.delbut=true;
          this.clickobj={};

          Toast("添加成功");
          console.log(this.datas);
        }).catch(err=>{
          console.log("添加属性失败");
          Toast("添加失败");
        })
      },

      //删除节点
      DeleteNodeSubmit(event) {
        console.log(this.clickobj);

        let label;
        let nodeid;
        nodeid=this.clickobj.uid;
        label=this.clickobj.labels;

        console.log(event);
        let target = event.target;
        if(target.nodeName == "SPAN"){
          // console.log("ddddd");
            target = event.target.parentNode;
        }
        target.blur();

        this.$axios({
          method:"delete",
          url:"graph/del/node",
          data:{
            "label":label,
            "nodeId":nodeid
          }
        }).then(res=>{
          console.log(res.data.data);

          let arrt=this.datas;
          for(let i=0;i<arrt.length;i++){
            if(arrt[i].uid==this.clickobj.uid){
              arrt.splice(i,1);
              break;
            }
          }
          let arrt1=this.categorydatas;
          if(this.clickobj.labels=="球队"){
            for(let x=0;x<arrt1.length;x++){
              if(arrt1[x].name==this.clickobj.name){
                arrt1.splice(x,1);
                break;
              }
            }
          }

          this.categorydatas=arrt1;
          this.datas=arrt;
          this.clickobj={};

          Toast("删除节点成功!");
        }).catch(err=>{
          console.log("删除节点失败");
          Toast("删除节点失败!");
        })
      },

      //监听双击事件
      open2(){
        // const h = this.$createElement;
        console.log(this.dblclickobj);
        this.$notify({
          title: this.dblclickobj.name,
          dangerouslyUseHTMLString:true,
          message: content(this.dblclickobj),
          position: 'bottom-right',
          // duration: 5000,
        });
      },
    },
    created(){

    },
    watch:{
      datas:function(){
          console.log("watch datas");
          // console.log(this.datas);
          // console.log(this.linksdatas);
          // console.log(this.categorydatas);

          // myChart.clear();
          let option=chart(this.datas,this.linksdatas,this.categorydatas);
          myChart.setOption(option);
      },
      // {
      //   //深度监听
      //   // handler:
      //   // deep:true,
      // },
      linksdatas:function(){
        console.log("watch links");
        // console.log(this.linksdatas);
        // myChart.clear();
        let option=chart(this.datas,this.linksdatas,this.categorydatas);
        myChart.setOption(option);
      },
      categorydatas:function(){
        console.log("watch category");
        // console.log(this.categorydatas);
        // myChart.clear();
        // let option=chart(this.datas,this.linksdatas,this.categorydatas);
        // myChart.setOption(option);
      }
    },
    mounted(){
      var _this=this;

      _this.$axios.get("graph/links")
        .then(res1=>{
          console.log("获取关系成功!");
          let arr0=res1.data.data;
          _this.linksdatas_=res1.data.data;
          // console.log(arr0);

          for(let j=0;j<arr0.length;j++){
            let obj0={};
            obj0.source=arr0[j].source;
            obj0.target=arr0[j].target;
            obj0.value=arr0[j].type;
            _this.linksdatas[j]=obj0;
          }
          console.log(_this.linksdatas);

          _this.$axios.get("graph/category")
            .then(res2=>{
              console.log("获取目录成功!");
              let arr1=res2.data.data;

              for(let i=0;i<arr1.length;i++){
                let obj1={};
                obj1.name=arr1[i].name;
                 _this.categorydatas[i]=obj1;
              }

          _this.$axios.get("graph/nodeandlabels")
          .then(res2=>{
            _this.nodelabels=res2.data.data;

              know=document.getElementById('know');
              myChart = echarts.init(know);

              _this.$axios.get("graph/node")
                .then(res3=>{
                  let arr=res3.data.data;
                  console.log("获取节点成功!");
                  // console.log(res3.data.data);

                  for(let k=0;k<arr.length;k++){
                    let obj={};
                    if(arr[k].hasOwnProperty('height'))
                      obj.height=arr[k].height;
                    if(arr[k].hasOwnProperty('weight'))
                      obj.weight=arr[k].weight;
                    if(arr[k].hasOwnProperty('description'))
                      obj.description=arr[k].description;
                    if(arr[k].hasOwnProperty('age'))
                      obj.age=arr[k].age;
                    if(arr[k].hasOwnProperty('compionNum'))
                      obj.compionNum=arr[k].compionNum;
                    if(arr[k].hasOwnProperty('location'))
                      obj.location=arr[k].location;
                    obj.name=arr[k].name;
                    obj.draggable="true";
                    obj.value=1;
                    // if(obj.hasOwnProperty('category'))
                    obj.category=arr[k].category;
                    obj.uid=arr[k].uuid;
                    obj.labels="";
                    for(let x=0;x<_this.nodelabels.length;x++)
                    {
                      if(_this.nodelabels[x].name==obj.name){
                        obj.labels=_this.nodelabels[x].labels;
                        break;
                      }
                    }
                    obj.symbolSize=labelmap.get(obj.labels);
                    _this.datas[k]=obj;
                  }
                  // if(obj.labels=="联赛"){
                  //   obj.x
                  // }

                    let option=chart(_this.datas,_this.linksdatas,_this.categorydatas);
                    myChart.setOption(option);

                      let shiftclick=0;
                      $(".box div").click(function (e){
                        if(e.shiftKey){console.log("click+shift");
                              shiftclick=1;
                        }else{
                            console.log("only click");
                            // _this.keyclick1=params.data;
                        }
                    });

                    //监听单击事件
                    myChart.on('click', function(params) {

                      //开启定时器，300ms间隔区分单击和双击事件
                      clearTimeout(_this.time);
                      _this.time=setTimeout(()=>{
                        //单击事件的逻辑部分

                        // 控制台打印数据的名称
                        // console.log(params.name);
                        console.log(params.data);

                        if(shiftclick==1){
                          console.log("shift");

                          if(Object.keys(_this.keyclick2).length === 0){
                            _this.keyclick2=params.data;
                          }
                          else{
                            _this.keyclick1=_this.keyclick2;
                            _this.keyclick2=params.data;
                          }
                          console.log(_this.keyclick1);
                          console.log(_this.keyclick2);

                          // _this.keyclickrestore=_this.keyclick2;

                          //改变选中的点的样式
                          //先恢复单个节点，再改变选中的两个节点
                          //也可以再将第二个选中的状态改变即可（选用这种方式）
                          for(let r=0;r<_this.datas.length;r++){
                            if(_this.datas[r].name==_this.keyclick2.name){
                              let obj=_this.datas[r];
                              _this.datas.splice(r,1);
                              obj.itemStyle={
                                  normal: {
                                      borderColor: '#FFFFFF',
                                      borderWidth: 4,
                                      shadowBlur: 10,
                                      shadowColor: '#04f2a7',
                                      color: '#EEF2FF',
                                  },
                              };
                              _this.datas.push(obj);
                              break;
                            }
                          }

                          _this.addrelabut=false;
                          _this.delbut=true;
                          _this.updateprobut=true;
                          shiftclick=0;
                        }
                        else{
                          console.log("no shift");

                          //用户单击的可能是节点，也可能是关系
                          if(params.data.hasOwnProperty("name")){
                            //如果有name属性，则说明是节点
                            // console.log("点击了节点");

                            //判断是否已经有了选中的节点了，如果有，则将其状态恢复
                            if(Object.keys(_this.clickobj).length === 0){
                              //为空，就不用处理
                            }
                            else{
                              for(let j=0;j<_this.datas.length;j++){
                                if(_this.datas[j].name==_this.clickobj.name){
                                  _this.datas.splice(j,1);
                                  _this.datas.push(_this.clickobj);
                                  break;
                                }
                              }
                            }

                            //接收新的值
                            _this.clickobj=params.data;
                            _this.keyclick1=params.data;

                            for(var i=0;i<_this.datas.length;i++){
                              if(_this.datas[i].name==params.name){
                                let obj=_this.datas[i];
                                _this.datas.splice(i,1);
                                // obj.symbol="diamond";
                                obj.itemStyle={
                                    normal: {
                                        borderColor: '#FFFFFF',
                                        borderWidth: 4,
                                        shadowBlur: 10,
                                        shadowColor: '#04f2a7',
                                        color: '#EEF2FF',
                                    },
                                };
                                // obj.symbolSize=40;
                                _this.datas.push(obj);
                                break;
                              }
                            }
                            //console.log(_this.datas);

                            _this.delbut=false;
                            _this.updateprobut=false;
                          }
                          else{
                            //否则说明是关系
                            // console.log("点击了关系");

                            //判断是否已经有了选中的关系了，如果有，则将其状态恢复
                            if(Object.keys(_this.clickobj).length === 0){
                              //为空，就不用处理
                            }
                            else{
                              console.log("恢复原来样式");
                              for(let jj=0;jj<_this.linksdatas.length;jj++){
                                if(_this.linksdatas[jj].source==_this.clickobj.source && _this.linksdatas[jj].target==_this.clickobj.target){
                                  let obj5={};
                                  obj5.source=_this.linksdatas[jj].source;
                                  obj5.target=_this.linksdatas[jj].target;
                                  obj5.value=_this.linksdatas[jj].value;
                                  _this.linksdatas.splice(jj,1);
                                  _this.linksdatas.push(obj5);
                                  _this.clickobj={};
                                  break;
                                }
                              }
                            }

                            //接收新的值
                            _this.clickobj=params.data;

                            let sn="";
                            let tn="";
                            for(let k=0;k<params.name.length;k++){
                              if(params.name[k]!=' '){
                                sn+=params.name[k];
                              }
                              else{
                                break;
                              }
                            }
                            for(let l=params.name.length-1;l>=0;l--){
                              if(params.name[l]!=' '){
                                tn=params.name[l]+tn;
                              }
                              else{
                                break;
                              }
                            }
                            // console.log(sn);
                            // console.log(tn);

                            //设置新的样式
                            for(var ii=0;ii<_this.linksdatas.length;ii++){
                              if(_this.linksdatas[ii].source==sn && _this.linksdatas[ii].target==tn){
                                let obj6=_this.linksdatas[ii];
                                _this.linksdatas.splice(ii,1);
                                obj6.lineStyle={
                                  type:"dashed",
                                  color:"#FF6666"
                                }
                                _this.linksdatas.push(obj6);
                                // console.log(obj6);
                                break;
                              }
                            }
                            //console.log(_this.datas);

                            _this.delrelabut=false;
                          }
                        }
                      },300)

                    });

                    //监听空白处
                    myChart.getZr().on('click', function(event) {
                      // 没有 target 意味着鼠标/指针不在任何一个图形元素上，它是从“空白处”触发的。
                      if (!event.target) {
                        // 点击在了空白处，做些什么。

                        //处理clickobj的选中事件
                        if(Object.keys(_this.clickobj).length === 0){
                          //为空，就不用处理
                          console.log("clickobj为空");
                        }
                        else{
                          if(_this.clickobj.hasOwnProperty("name")){
                              //将选中的状态恢复
                              //这里有有几种情况，
                              //一是选中后没做任何处理，就点了空白处
                              //二是将节点删除了，那应该在删除事件中将clickobj置空
                              //三是选中节点后，又选了新的节点，那应该恢复原来的，渲染新的（在单击事件中进行处理）
                              for(var i=0;i<_this.datas.length;i++){
                                if(_this.datas[i].name==_this.clickobj.name){
                                  _this.datas.splice(i,1);
                                  _this.datas.push(_this.clickobj);
                                  _this.clickobj={};
                                  break;
                                }
                              }

                              //还要判断是不是从shift事件点击的空白处
                              if(Object.keys(_this.keyclick2).length === 0){
                                //不用处理
                              }
                              else{
                                for(let r=0;r<_this.datas.length;r++){
                                  if(_this.datas[r].name==_this.keyclick2.name){
                                    _this.datas.splice(r,1);
                                    _this.datas.push(_this.keyclick2);
                                    _this.keyclick2={};
                                    _this.keyclick1={};
                                  }
                                }
                              }

                            //只要点了空白处，就意味着clickobj被置空了
                            //那删除节点按钮就不能有效
                            //delbut为true，按钮无效
                            _this.delbut=true;
                            _this.updateprobut=true;
                          }
                          else{
                            //同理 来处理关系
                            for(var i=0;i<_this.linksdatas.length;i++){
                                if(_this.linksdatas[i].source==_this.clickobj.source && _this.linksdatas[i].target==_this.clickobj.target){
                                  let obj7={};
                                  obj7.source=_this.linksdatas[i].source;
                                  obj7.target=_this.linksdatas[i].target;
                                  obj7.value=_this.linksdatas[i].value;
                                  _this.linksdatas.splice(i,1);
                                  _this.linksdatas.push(obj7);
                                  _this.clickobj={};
                                  break;
                                }
                              }
                            }
                            //只要点了空白处，就意味着clickobj被置空了
                            //那删除节点按钮就不能有效
                            //delbut为true，按钮无效
                            _this.delrelabut=true;
                          }

                        _this.dblclickobj=[];
                        _this.addrelabut=true;

                        console.log("已置空");
                      }
                    });

                    //监听双击事件
                    myChart.on('dblclick', function(params) {
                      clearTimeout(_this.time);
                      //触发双击事件

                      // 控制台打印数据的名称
                      console.log(params.name);
                      _this.dblclickobj=params.data;
                      // console.log(_this.dblclickobj);
                      let c=document.getElementById("C");
                      c.click();
                    });

                    //获取节点位置
                    // let model  = myChart.getModel().getSeriesByIndex(0).getData()._itemLayouts;
                    // console.log('model',model);

                    window.addEventListener("resize", function() {
                      myChart.resize();
                    });

                  })

                .catch(err=>{
                  console.log("获取节点数据异常！");
                })

               }).catch(err=>{
                 console.log("获取节点及标签异常！");
               })

            })
            .catch(err=>{
              console.log("获取目录数据异常！");
            })

        })
        .catch(err=>{
          console.log("获取关系数据异常！");
        })

    }
  }
</script>

<style>
 .btn{
    background-color:rgb(255,255,255);
    color: rgb(96,98,102);
  }
  .btn:hover{
    background-color: rgb(236,245,255);
    color: rgb(96,174,255);
  }
 .btn:active{
    background-color:rgb(255,255,255);
    color: rgb(96,98,102);
  }
  .btn:focus{
    background-color:rgb(255,255,255);
    color: rgb(96,98,102);
  }

  #know{
    width:100%;
    height:540px;
    /* border:1px solid #42B983; */
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

  .search{
    width:400px;
    height:30px;
    float:right;
    margin-top: -30px;
    margin-bottom: 20px;
    /* border:1px solid mediumpurple; */
  }

  .op{
    width:100%;
    height:50px;
    /* border:1px solid firebrick; */
    float: left;
  }
</style>
