import Vue from 'vue'
import Router from 'vue-router'
// import HomeView from '../views/HomeView.vue'

// 加载路由配置
import { constantRouterMap } from '@/config/router.config'

/*
Vue Router 的 push 方法的一个改进。它确保即使在不传递 onResolve 或 onReject 回调的情况下，路由跳转发生的任何错误也会被捕获和处理，而不会影响到全局错误处理或导致应用崩溃
 */
// hack（侵入，骇客） router push callback
const originalPush = Router.prototype.push
Router.prototype.push = function push (location, onResolve, onReject) {
  if (onResolve || onReject) return originalPush.call(this, location, onResolve, onReject)
  return originalPush.call(this, location).catch(err => err)
}

Vue.use(Router)

const createRouter = () =>
    new Router({
      mode: 'history',
      routes: constantRouterMap
    })

// 把实际的路由配置封装在constantRouterMap里 import进来。new Router的操作也封装在一个方法方法里，调用一下生成
const router = createRouter()
// const routes = [
//   {
//     path: '/',
//     name: 'home',
//     component: HomeView
//   },
//   {
//     path: '/about',
//     name: 'about',
//     // route level code-splitting
//     // this generates a separate chunk (about.[hash].js) for this route
//     // which is lazy-loaded when the route is visited.
//     component: () => import(/* webpackChunkName: "about" */ '../views/AboutView.vue')
//   }
// ]
//
// const router = new Router({
//   routes
// })

// 定义一个resetRouter 方法，在退出登录后或token过期后 需要重新登录时，调用即可
export function resetRouter () {
  const newRouter = createRouter()
  router.matcher = newRouter.matcher
}

export default router
