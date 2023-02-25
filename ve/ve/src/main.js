// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import App from './App'
import router from './router'

Vue.config.productionTip = false

import Axios from 'axios';
//配置axios
Vue.prototype.$axios=Axios;
// Axios.defaults.baseURL="http://127.0.0.1:8081/";
Axios.defaults.baseURL="http://120.24.248.200:8081/";
// Axios.defaults.baseURL="http://127.0.0.1:8083/";

//引入vant的样式文件
import 'vant/lib/index.css';

//引入element-ui
import Element from 'element-ui'
Vue.use(Element);
import "element-ui/lib/theme-chalk/index.css"

/* eslint-disable no-new */
new Vue({
  el: '#app',
  router,
  components: { App },
  template: '<App/>'
})
