import Env from './env'
import axios from 'axios'
import Qs from 'qs'
import router from '../router/index'
import $ from 'jquery'
import API from '../api/api_system'
// 注意：在js文件中使用组件 按照下面更优雅哦~~
import { message } from 'ant-design-vue'

let token = ''
let refreshTokenFalg = true
let noPermissionFalg = false
axios.defaults.withCredentials = true
axios.defaults.headers.common['token'] = token

// 添加一个请求拦截器
axios.interceptors.request.use(function(request) {
  noPermissionFalg = false
  // 如果有token 说明该用户已登陆
  // let accessUser = localStorage.getItem('access-user')
  const accessUser = sessionStorage.getItem('access-user')
  if (accessUser) {
    const sysUserToken = JSON.parse(accessUser)
    // 若token在15分钟内过期并且用户期间操作则刷新token
    if (refreshTokenFalg && (Date.parse(sysUserToken.expire_time) < (new Date(new Date().getTime() + 15 * 60 * 1000))) && (Date.parse(sysUserToken.expire_time) > new Date())) {
      // localStorage.clear() // 清除用户信息
      sessionStorage.clear()
      // 定义请求参数
      const params = sysUserToken
      // 调用接口
      API.refreshToken(params).then(function(result) {
        if (result.code === 200) {
          sessionStorage.setItem('access-user', JSON.stringify(result.map.sysUserToken)) // 将用户信息存到sessionStorage中
          sessionStorage.setItem('access-token', result.map.sysUserToken.token) // 将token信息存到sessionStorage中
          // localStorage.setItem('access-user', JSON.stringify(result.map.sysUserToken)); // 将用户信息存到localStorage中
          // localStorage.setItem('access-token', result.map.sysUserToken.token); // 将token信息存到localStorage中
        } else {
          router.push({ path: '/' })
        }
      })
      refreshTokenFalg = false
    }
  } else {
    // 没有登陆则访问任何页面都重定向到登陆页
    router.push({ path: '/' })
  }

  // token = localStorage.getItem('access-token')
  token = sessionStorage.getItem('access-token')
  if (token) {
    request.headers.common['token'] = token
  }
  return request
}, function(error) {
  return Promise.reject(error)
})

// 添加一个响应拦截器
axios.interceptors.response.use(function(response) {
  if (response.data && response.data.code) {
    if (!noPermissionFalg && parseInt(response.data.code) === 403) {
      noPermissionFalg = true
      // localStorage.clear()
      sessionStorage.clear()
      // 未登录
      message.error('TOKEN不存在或TOKEN失效，请重新登录')
      router.push({ path: '/' })
    } else if (!noPermissionFalg && parseInt(response.data.code) === 100) {
      message.error(response.data.msg)
      console.error('请求失败', response.data.msg)
    } else if (parseInt(response.data.code) !== 200) {
      message.error(response.data.msg)
      console.error('请求失败', response.data.msg)
    }
  }
  return response
}, function(error) {
  console.dir(error)
  console.error('服务器连接失败')
  return Promise.reject(error)
})

// 基地址
// let base = 'Env.baseURL';
const base = process.env.NODE_ENV === 'production' ? '' : 'api'

// 测试使用
export const ISDEV = Env.isDev

// 通用方法
export const POST = (url, params) => {
  const getTimestamp = new Date().getTime()
  // 请求参数携带用户信息
  const userInfo = JSON.parse(sessionStorage.getItem('access-user')) // 从sessionStorage中获取登录用户信息
  if (userInfo) {
    params = { ...params, ...{ userId: userInfo.userId, userNme: userInfo.name }}
  }
  const data = Qs.stringify(params)
  const config = { headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' }}
  return axios.post(`${base}${url}?timer=${getTimestamp}`, data, config).then(result => result.data)
}

export const GET = (url, params) => {
  const getTimestamp = new Date().getTime()
  // 请求参数携带用户信息
  const userInfo = JSON.parse(sessionStorage.getItem('access-user')) // 从sessionStorage中获取登录用户信息
  if (userInfo) {
    params = { ...params, ...{ userId: userInfo.userId, userNme: userInfo.name }}
  }
  return axios.get(`${base}${url}?timer=${getTimestamp}`, { params: params }).then(result => result.data)
}

export const PUT = (url, params) => {
  const config = { headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' }}
  // 请求参数携带用户信息
  const userInfo = JSON.parse(sessionStorage.getItem('access-user')) // 从sessionStorage中获取登录用户信息
  if (userInfo) {
    params = { ...params, ...{ userId: userInfo.userId, userNme: userInfo.name }}
  }
  return axios.put(`${base}${url}`, params, config).then(result => result.data)
}

export const DELETE = (url, params) => {
  const config = { headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' }}
  // 请求参数携带用户信息
  const userInfo = JSON.parse(sessionStorage.getItem('access-user')) // 从sessionStorage中获取登录用户信息
  params = { ...params, ...{ userId: userInfo.userId, userNme: userInfo.name }}
  return axios.delete(`${base}${url}`, { params: params }, config).then(result => result.data)
}

export const PATCH = (url, params) => {
  const config = { headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' }}
  // 请求参数携带用户信息
  const userInfo = JSON.parse(sessionStorage.getItem('access-user')) // 从sessionStorage中获取登录用户信息
  if (userInfo) {
    params = { ...params, ...{ userId: userInfo.userId, userNme: userInfo.name }}
  }
  return axios.patch(`${base}${url}`, params, config).then(result => result.data)
}

export const DOWNLOAD = (url, params) => {
  const getTimestamp = new Date().getTime()
  // 请求参数携带用户信息
  const userInfo = JSON.parse(sessionStorage.getItem('access-user')) // 从sessionStorage中获取登录用户信息
  if (userInfo) {
    params = { ...params, ...{ userId: userInfo.userId, userNme: userInfo.name }}
  }
  $.fileDownload(`${base}${url}?timer=${getTimestamp}`, {
    httpMethod: 'POST',
    data: {},
    prepareCallback: function(url) {
      // console.log('下载中...');
    },
    successCallback: function(url) {
      // console.log('下载成功！！');
    },
    failCallback: function(html, url) {
      // console.log('下载失败！！');
    }
  })
  // return axios.get(`${base}${url}?timer=${getTimestamp}`, {params: params}, config).then(result => result.data)
}

// 通用方法
export const viewImage = (url, params) => {
  const getTimestamp = new Date().getTime()
  // 请求参数携带用户信息
  const userInfo = JSON.parse(sessionStorage.getItem('access-user')) // 从sessionStorage中获取登录用户信息
  if (userInfo) {
    params = { ...params, ...{ userId: userInfo.userId, userNme: userInfo.name }}
  }
  let urlParams = '&'
  for (var item in params) {
    urlParams = urlParams + item + '=' + params[item] + '&'
    console.log(item, params[item])
  }
  urlParams = urlParams.substring(0, urlParams.lastIndexOf('&'))
  return `${base}${url}?timer=${getTimestamp}` + urlParams
}
