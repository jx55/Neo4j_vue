import Vue from 'vue'
import Router from 'vue-router'
import Know from '@/components/Know'
import Test from '@/components/Test'
import Pos from '@/components/Pos'
import Format from '@/components/Format'

Vue.use(Router)

export default new Router({
  mode:'history',
  routes: [
    {
      path: '/',
      name: 'Know',
      component: Know
    },
    {
      path: '/test',
      name: 'Test',
      component: Test
    },
    {
      path: '/pos',
      name: 'Pos',
      component: Pos
    },
    {
      path:"/f",
      name:"Format",
      component:Format
    }
  ]
})
