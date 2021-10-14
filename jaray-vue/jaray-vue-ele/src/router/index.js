import Vue from 'vue'
import Router from 'vue-router'
import system from './system'

Vue.use(Router)

export default new Router({
  mode: 'history',
  routes: [
    ...system,
    {
      path: '/complaint',
      name: 'complaint',
      title: '投诉',
      component: () => import('@views/complaint/complaint')
    }, {
      path: '/quartz',
      name: 'quartz',
      title: '定时任务',
      component: () => import('@views/quartz/quartz')
    }, {
      path: '/leave',
      name: 'leave',
      title: '我的申请',
      component: () => import('@views/workflow/leave/apply/leaveApply')
    }, {
      path: '/myTodo',
      name: 'leaveApprove',
      title: '我的待办',
      component: () => import('@views/workflow/leave/todo/leaveApprove')
    }
  ]
})
