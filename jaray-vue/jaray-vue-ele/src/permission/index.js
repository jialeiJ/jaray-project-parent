import store from '@/store'

/**
 * 判断用户是否拥有操作权限
 * 根据传入的权限标识，查看是否存在用户权限标识集合
 * @param perm
 */
function hasPermission(perm) {
  let hasPermissionFlag = false
  const permissions = store.getters.permissions
  for (let i = 0; i < permissions.length; i++) {
    if (permissions[i].perm === perm) {
      hasPermissionFlag = true
      break
    }
  }
  return hasPermissionFlag
}

export default {
  // Vue.js的插件应当有一个公开方法 install.这个方法的第一个参数是Vue构造器，第二个参数是一个可选的选项对象
  install: function(Vue) {
    Vue.prototype.hasPermission = (perm) => hasPermission(perm)
  }
}
