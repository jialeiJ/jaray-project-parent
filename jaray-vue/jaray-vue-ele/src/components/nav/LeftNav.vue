<template>
  <fragment>
    <fragment v-for="(navMenu, index) in leftMenus" :key="index">
      <el-submenu
        v-if="navMenu.type == 0 && navMenu.children && navMenu.children.length >= 1"
        :key="index"
        :data="navMenu"
        :index="(navMenu.url == null || navMenu.url =='')?navMenu.id:navMenu.url">
        <template slot="title">
          <i v-if="icon" :class="navMenu.icon"/>
          <span slot="title"> {{ navMenu.name }}</span>
        </template>

        <LeftNav :left-menus="navMenu.children"/>
      </el-submenu>
      <el-menu-item
        v-else
        :key="index"
        :data="navMenu"
        :index="(navMenu.url == null || navMenu.url =='')?navMenu.id:navMenu.url"
        :route="navMenu.url">
        <i :class="navMenu.icon"/>
        <span slot="title">{{ navMenu.name }}</span>
      </el-menu-item>
    </fragment>
  </fragment>
</template>

<script>
import { mapActions, mapGetters } from 'vuex'

export default {
  name: 'LeftNav',
  props: {
    leftMenus: {
      type: Array,
      default: function() {
        return []
      }
    },
    icon: {
      type: Boolean,
      default: function() {
        return true
      }
    }
  },
  data() {
    return {

    }
  },
  computed: {
    ...mapGetters(['collapsed']) // 动态计算属性，相当于this.$store.getters.name
  },
  methods: {
    ...mapActions( // 语法糖
      ['modifyCollapsed'] // 相当于this.$store.dispatch('modifyCollapsed'),提交这个方法
    ),
    handleOpen: function(key, keyPath) {
      console.log(key, keyPath)
    },
    handleClose: function(key, keyPath) {
      console.log(key, keyPath)
    }
  }
}
</script>

<style>

</style>
