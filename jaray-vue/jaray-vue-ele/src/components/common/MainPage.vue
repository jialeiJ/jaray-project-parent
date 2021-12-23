<template>
  <el-container>
    <el-header>
      <head-nav :nav-icon="false" :left-menus="leftMenus" @addTab="addTab"/>
    </el-header>
    <el-container>
      <el-aside :width="asideWidth" style="width:auto;">
        <head-nav :is-left-nav="true" :nav-mode="'vertical'" :left-menus="leftMenus" @addTab="addTab"/>
      </el-aside>
      <el-main>
        <i-tabs ref="iTabs"/>
        <!-- <router-view /> -->
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
import store from '@/store'
import API from '../../api/api_system'
import LeftNav from '@/components/nav/LeftNav'
import HeadNav from '@/components/nav/HeadNav'
import iTabs from '@/components/common/iTabs'

export default {
  name: 'MainPage',
  components: { LeftNav, HeadNav, iTabs },
  data() {
    return {
      // 左边菜单默认宽度
      asideWidth: 'c200'
    }
  },
  computed: {
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
  created: function() {
    const that = this
    that.findLeftNav()
  },
  mounted: function() {

  },
  methods: {
    addTab: function(params) {
      this.$refs['iTabs'].addTab(params)
    },
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
      const params = {}
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
    }
  }
}
</script>

<style scoped>
.el-header, .el-footer {
    padding:  0px;
    background-color: #B3C0D1;
    color: #333;
    line-height: 60px;
}

.el-aside {
    width: auto;
    height:90vh;
    background-color: #FFFFFF;
    color: #333;
    line-height: 200px;
}

.el-main {
    padding: 6px 6px;
    background-color: #FFFFFF;
    color: #333;
    height: 100%;
    /* padding: 0 0px; */
}

body > .el-container {
    margin-bottom: 40px;
}

.el-container {
    width: 100%;
}
</style>
