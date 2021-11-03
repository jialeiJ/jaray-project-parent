<template>
  <el-row>
    <el-col :span="24">
      <el-tabs
        ref="tabs"
        v-model="tabsValue"
        type="border-card"
        style="height: 100%;"
        @tab-click="handleClick"
        @tab-remove="removeTab">
        <el-tab-pane
          v-for="(item, index) in tabs"
          :closable="!(index == 0)"
          :key="item.name"
          :label="item.title"
          :name="item.name">
          <span slot="label"><i class="el-icon-date"/> {{ item.title }}</span>
          <!-- <template>
                        <tab-component :is="item.content" :index="index" :name="index" class="tabComponent"></tab-component>
                    </template> -->
          <!-- 若是Iframe，则不重复渲染，否则渲染 -->
          <template v-if="item.path.indexOf('http') > -1">
            <tab-component :is="item.content" :index="index" :name="index" class="tabComponent"/>
          </template>
          <template v-else>
            <tab-component :index="index" :name="index" class="tabComponent"/>
          </template>
        </el-tab-pane>
      </el-tabs>
      <div class="tags-close-box">
        <el-dropdown size="medium" @command="handleTags">
          <el-button>
            标签选项
            <i class="el-icon-arrow-down el-icon--right"/>
          </el-button>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item command="other">关闭其他</el-dropdown-item>
            <el-dropdown-item command="left">关闭左边</el-dropdown-item>
            <el-dropdown-item command="right">关闭右边</el-dropdown-item>
            <el-dropdown-item command="all">关闭所有</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </div>
    </el-col>
  </el-row>
</template>

<script>
import Vue from 'vue'

export default {
  name: 'ITabs',
  data() {
    return {
      tabsValue: '',
      tabs: [],
      index: 0,
      tabComponent: {},
      iframeComponent: {}
    }
  },
  watch: {
    'tabsValue': function(tabName) {
      const that = this
      for (var i = 0; i < that.tabs.length; i++) {
        if (tabName === that.tabs[i].name) {
          that.index = i
          break
        }
      }
    }
  },
  created: function() {
    const tempRoutes = this.$router.options.routes
    const isRefresh = false
    for (let i = 0; i < tempRoutes.length; i++) {
      const temp = tempRoutes[i]
      if (temp.path === '/introduction') {
        const menu = {}
        menu.name = temp.title
        menu.component = temp.component
        menu.path = temp.path
        this.addOneTab(menu, isRefresh)
      }
    }
  },
  mounted: function() {
    const that = this
    const isRefresh = true
    const tabs = JSON.parse(sessionStorage.getItem('tabs'))
    if (tabs) {
      var tabsPromise = new Promise(function(resolve, reject) {
        tabs.forEach(tab => {
          that.addTab(tab, isRefresh)
        })
        resolve()
      })

      tabsPromise.then(function() {
        that.tabsValue = sessionStorage.getItem('tabsValue')
        console.log(that.tabsValue)
      })
    }
  },
  methods: {
    handleClick(tab, event) {
      sessionStorage.setItem('tabsValue', tab.name)
    },
    addOneTab(menu, isRefresh) {
      const that = this
      var exist = false
      for (var i = 0; i < that.tabs.length; i++) {
        if (menu.name === that.tabs[i].name) {
          exist = true
          that.index = i
          break
        }
      }
      if (exist === true) {
        if (!isRefresh) {
          that.tabsValue = menu.name
        }
        return
      } else {
        that.index = that.tabs.length
      }
      that.tabs.push({
        title: menu.name,
        name: menu.name,
        content: menu.component,
        path: menu.path
      })
      if (!isRefresh) {
        that.tabsValue = menu.name
      }

      if (menu.path !== '/introduction') {
        sessionStorage.setItem('tabs', JSON.stringify(that.tabs))
      }

      that.render()
    },
    addTab(params, isRefresh) {
      const that = this
      const tempRoutes = that.$router.options.routes
      const menu = {}
      for (let i = 0; i < tempRoutes.length; i++) {
        const temp = tempRoutes[i]
        if (temp.path === params.path) {
          menu.name = temp.title
          menu.component = temp.component
          menu.path = params.path
          that.addOneTab(menu, isRefresh)
        }
      }

      if (params.path && (params.path.indexOf('http') === 0 || params.path.indexOf('https') === 0)) {
        const uid = that.$moment(new Date()).format('YYYYMMDDHHmmss')
        that.iframeComponent = Vue.component('iframe-component', {
          template: '<iframe id="' + uid + '" name="' + uid + '" src="' + params.path + '" scrolling="yes" width="100%" height="100%" frameborder="0" style="height: calc(95vh - 100px);overflow: auto;"></iframe>'
        })
        menu.name = params.title
        menu.component = that.iframeComponent.options
        menu.path = params.path
        that.index++
        that.addOneTab(menu, isRefresh)
      }

      if (!isRefresh) {
        sessionStorage.setItem('tabsValue', menu.name)
      }
    },
    removeTab(targetName) {
      const that = this
      if (targetName === '介绍') {
        return
      }
      const tabs = that.tabs
      let activeName = that.tabsValue
      if (activeName === targetName) {
        tabs.forEach((tab, index) => {
          if (tab.name === targetName) {
            const nextTab = tabs[index + 1] || tabs[index - 1]
            if (nextTab) {
              activeName = nextTab.name
            }
          }
        })
      }
      that.tabsValue = activeName
      that.tabs = tabs.filter(tab => tab.name !== targetName)
      that.tabs.forEach((tab, index) => {
        if (tab.name === activeName) {
          that.index = index
        }
      })

      sessionStorage.setItem('tabs', JSON.stringify(that.tabs))
      sessionStorage.setItem('tabsValue', that.tabsValue)
    },
    handleTags(command) {
      const that = this
      switch (command) {
        case 'other':
          this.closeOther()
          break
        case 'left':
          this.closeLeft()
          break
        case 'right':
          this.closeRight()
          break
        case 'all':
          this.closeAll()
          break
        default:
          // 这里是没有找到对应的值处理
          break
      }
      sessionStorage.setItem('tabsValue', that.tabsValue)
    },
    closeOther() {
      const that = this
      const tabs = that.tabs
      const curTabs = tabs.filter(item => {
        return item.name === that.tabsValue
      })

      const otherTabs = tabs.filter(item => {
        return item.name !== that.tabsValue
      })

      for (const item of otherTabs) {
        that.removeTab(item.name)
      }

      for (const item of curTabs) {
        that.addOneTab(item)
      }
    },
    closeLeft() {
      const that = this
      const tabs = that.tabs
      const leftTabs = []
      for (const item of tabs) {
        if (that.tabsValue === item.name) {
          break
        }
        leftTabs.push(item)
      }

      for (const item of leftTabs) {
        that.removeTab(item.name)
      }
    },
    closeRight() {
      const that = this
      const tabs = that.tabs
      const rightTabs = []
      let pushFlag = false
      const curTabs = tabs.filter(item => {
        return item.name === that.tabsValue
      })

      for (const item of tabs) {
        if (pushFlag) {
          rightTabs.push(item)
        }

        if (that.tabsValue === item.name) {
          pushFlag = true
        }
        continue
      }

      for (const item of rightTabs) {
        that.removeTab(item.name)
      }

      for (const item of curTabs) {
        that.addOneTab(item)
      }
    },
    closeAll() {
      const that = this
      const tabs = that.tabs

      for (const item of tabs) {
        that.removeTab(item.name)
      }
    },
    render: function() {
      const that = this
      that.tabComponent = Vue.component('tab-component', {
        props: {
          index: {
            type: Number,
            required: true
          }
        },
        render: function(h) {
          // 每次只渲染当前tab
          if (this.index === that.index) {
            var comp = that.tabs[this.index].content
            return h(comp)
          }
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.el-header, .el-footer {
    padding:  0px;
    background-color: #B3C0D1;
    color: #333;
    line-height: 60px;
}

.el-aside {
    height:90vh;
    background-color: #FFFFFF;
    color: #333;
    line-height: 200px;
}

.el-main {
    padding: 6px 6px;
    background-color: #FFFFFF;
    color: #333;
    /* padding: 0 0px; */
}

body > .el-container {
    margin-bottom: 40px;
}

.el-container {
    width: 100%;
    height: 100%;
}

.el-row {
    display: flex;
    flex-wrap: wrap;
}

.tabComponent {
    padding-top: 0px;
    width: 100%;
}

.scroll-container {
    white-space: nowrap;
    position: relative;
    overflow: hidden;
    width: 100%;
    .el-scrollbar__bar {
        bottom: 0px;
    }
    .el-scrollbar__wrap {
        height: 59px;
    }
}

/deep/ .el-tabs--border-card>.el-tabs__header {
    background-color: #F5F7FA;
    border-bottom: 1px solid #E4E7ED;
    margin: 0;
    margin-right: 100px;
}

.tags-li:not(.active):hover {
  background: #f8f8f8;
}

.tags-li.active {
  color: #fff;
  border: 1px solid #418BEB;
	background-color: #418BEB;
	.iconfont {
		color: #fff;
	}
}

.tags-li.active .tags-li-title {
  color: #fff;
}

.tags-close-box {
    position: absolute;
    right: 0;
    top: 0;
    box-sizing: border-box;
    padding-top: 1px;
    text-align: center;
    width: 100px;
    height: 40px;
    background: #fff;
    box-shadow: -3px 0 15px 3px rgba(0, 0, 0, 0.1);
    z-index: 10;
    .el-dropdown {
        height: 40px;
        .el-button {
            border-radius: 0;
            background: #fff;
            color: #218ff0;
            border: none;
            padding: 12px 14px;
            font-size: 12px;
        }
    }
}
</style>
