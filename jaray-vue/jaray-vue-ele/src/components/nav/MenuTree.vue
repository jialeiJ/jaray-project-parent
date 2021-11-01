<template>
  <el-menu
    :default-active="$route.path"
    :collapse="collapsed"
    unique-opened
    class="el-menu-vertical-demo"
    @select="addTab"
    @open="handleOpen"
    @close="handleClose">
    <left-nav id="leftNav" :left-menus="leftMenus" @addTab="addTab"/>
  </el-menu>
</template>

<script>
import { mapActions, mapGetters } from 'vuex'
import API from '../../api/api_system'
import LeftNav from '@/components/nav/LeftNav'

export default {
  name: 'MenuTree',
  components: { LeftNav },
  data() {
    return {
      url: '',
      // leftMenus: [],
      tileLeftNavData: []
    }
  },
  computed: {
    ...mapGetters(['collapsed']), // 动态计算属性，相当于this.$store.getters.collapsed
    collapsed: {
      get() {
        return this.$store.state.collapsed
      },
      set(val) {
        this.$store.state.collapsed = val
      }
    },
    ...mapGetters(['leftMenus']), // 动态计算属性，相当于this.$store.getters.collapsed
    leftMenus: {
      get() {
        return this.$store.state.leftMenus
      },
      set(val) {
        this.$store.state.leftMenus = val
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
    findLeftNav: function() {
      const that = this
      // 定义请求参数
      const params = {}
      // 调用接口
      API.findLeftNav(params).then(function(result) {
        if (result.code === 200) {
          that.leftMenus = result.map.leftMenu
        }
      })
    },
    handleOpen: function(key, keyPath) {
      console.log(key, keyPath)
    },
    handleClose: function(key, keyPath) {
      console.log(key, keyPath)
    },
    addTab: function(key, keyPath) {
      const that = this

      const params = {
        path: key
      }
      if (key.indexOf('http') === 0 || key.indexOf('https') === 0) {
        const obj = that.tileLeftNavData.filter(function(item) {
          return item.url === key
        })
        params.title = obj[0].label
        params.path = obj[0].url
      }

      // 通过 emit 触发
      this.$emit('addTab', params)
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
    },
    ...mapActions( // 语法糖
      ['modifyCollapsed'] // 相当于this.$store.dispatch('modifyCollapsed'),提交这个方法
    ),
    ...mapActions( // 语法糖
      ['modifyLeftMenus'] // 相当于this.$store.dispatch('modifyLeftMenus'),提交这个方法
    )
  }
}
</script>

<style lang="scss" scoped>
.el-menu-vertical-demo:not(.el-menu--collapse) {
    width: 200px;
    min-height: 400px;
}
</style>
