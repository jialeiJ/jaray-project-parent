<template>
  <a-menu
    :style="{ 'overflow-x': 'hidden', 'overflow-y': 'auto', height: '90vh', left: 0 }"
    :inline-collapsed="collapsed"
    mode="inline"
    theme="light"
    @select="addTab"
    @click="addTab">
    <!-- 菜单遍历的开始 -->
    <template v-for="(item, index) in leftMenus">
      <!-- 如果当前遍历项没有children，视为子菜单项，注意所有的key都是path用于路由跳转，以及当前选中记录 -->
      <a-menu-item v-if="item.type == 1" :key="item.path == ''?item.name+index:item.path">
        <!-- <i v-if="item.icon" :class="item.icon" /> -->
        <a-icon type="setting" />
        <span>{{ item.name }}</span>
      </a-menu-item>
      <!-- 否则视为子菜单，传入菜单信息并且运用下面定义的函数式组件 -->
      <!-- <a-menu-item v-else-if="item.type == 0 && item.children.length > 0">{{item.path}}--{{item.name}}--{{item}}</a-menu-item> -->
      <sub-menu v-else-if="item.type == 0" :key="item.path == ''?item.name+index:item.path" :menu-info="item" />
    </template>
  </a-menu>
</template>

<script>
import store from '@/store'
import API from '../../api/api_system'

import { Menu } from 'ant-design-vue'
const SubMenu = {
  template: `
        <a-sub-menu :key="menuInfo.path == ''?menuInfo.name:menuInfo.path" v-bind="$props" v-on="$listeners">
            <span slot="title">
                <a-icon type="setting" /><span>{{ menuInfo.name }}</span>
            </span>
            <template v-for="(item, index) in menuInfo.children">
                <a-menu-item v-if="item.type == 1" :key="item.path == ''?item.name+index:item.path">
                    <i :class="item.icon" />
                    <span>{{ item.name }}</span>
                </a-menu-item>
                <sub-menu v-else-if="item.type == 0" :key="item.path == ''?item.name+index:item.path" :menu-info="item" />
            </template>
        </a-sub-menu>
    `,
  name: 'SubMenu',
  // must add isSubMenu: true
  isSubMenu: true,
  props: {
    ...Menu.SubMenu.props,
    // Cannot overlap with properties within Menu.SubMenu.props
    menuInfo: {
      type: Object,
      default: () => ({})
    }
  }
}
export default {
  name: 'LeftNav',
  // 注册局部组件
  components: { SubMenu },
  data() {
    return {
      tileLeftNavData: []
    }
  },
  computed: {
    collapsed: {
      get() {
        return this.$store.state.collapsed
      },
      set(val) {
        this.$store.state.collapsed = val
      }
    },
    leftMenus: {
      get() {
        return this.$store.state.leftMenus
      },
      set(val) {
        this.$store.state.leftMenus = val
      }
    },
    permissions: {
      get() {
        return this.$store.state.permissions
      },
      set(val) {
        this.$store.state.permissions = val
      }
    }
  },
  watch: {
    // 使用监听
    leftMenus: {
      handler(newVal, oldVal) {
        if (newVal) {
          this.tileLeftNav(newVal)
        }
      },
      immediate: true, // 立即执行
      deep: true // 深度监听
    }
  },
  created: function() {
    const that = this
    that.findLeftNav()
  },
  mounted: function() {

  },
  methods: {
    getAllPermission: function(leftMenus) {
      const that = this
      for (let i = 0; i < leftMenus.length; i++) {
        if (leftMenus[i].type === 1 && leftMenus[i].perms && leftMenus[i].perms.length) {
          that.permissions = that.permissions.concat(leftMenus[i].perms)
        } else if (leftMenus[i].type === 0 && leftMenus[i].children && leftMenus[i].children.length) {
          that.getAllPermission(leftMenus[i].children)
        }
      }
      store.dispatch('modifyPermissions', that.permissions)
    },
    findLeftNav: function() {
      const that = this
      // 定义请求参数
      const accessUser = JSON.parse(sessionStorage.getItem('access-user'))

      const params = { user_id: accessUser.user_id }
      // 调用接口
      API.findLeftNav(params).then(function(result) {
        if (result.code === 200) {
          that.leftMenus = result.map.leftMenu
          that.permissions = []
          that.getAllPermission(that.leftMenus)
          // 防止菜单权限未刷新，导致没权限按钮存在
          sessionStorage.setItem('state', JSON.stringify(store.state))
        }
      })
    },
    handleOpen: function(key, keyPath) {
      // console.log(key, keyPath);
    },
    handleClose: function(key, keyPath) {
      // console.log(key, keyPath);
    },
    addTab: function({ item, key, selectedKeys }) {
      const that = this

      const params = {}
      const obj = that.tileLeftNavData.filter(function(item) {
        return item.url === key
      })
      params.title = obj[0].name
      params.path = obj[0].url

      // 通过 emit 触发
      this.$emit('add_tab', params)
    },
    // 平铺数据
    tileLeftNav(leftNavData) {
      const that = this
      leftNavData.forEach(function(item, index) {
        that.tileLeftNavData.push(item)
        if (item.children.length) {
          that.tileLeftNav(item.children)
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
</style>
