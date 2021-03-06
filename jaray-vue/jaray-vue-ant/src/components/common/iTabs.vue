<template>
  <a-row>
    <a-col :span="24">
      <a-tabs
        :active-key="activeKey"
        default-active-key="introduction"
        hide-add
        type="editable-card"
        @edit="tabEdit"
        @change="tabChange">
        <a-tab-pane v-for="(item, index) in tabs" :closable="!(index == 0)" :key="item.key" :tab="item.title">
          <!-- 若是Iframe，则不重复渲染，否则渲染 -->
          <template v-if="item.path.indexOf('http') > -1">
            <tab-component ref="iframe" :is="item.content" :index="index" :name="index" class="tabComponent"/>
          </template>
          <template v-else>
            <tab-component :index="index" :name="index" class="tabComponent"/>
          </template>
        </a-tab-pane>
      </a-tabs>
    </a-col>
  </a-row>
</template>

<script>
import Vue from 'vue'

export default {
  name: 'ITabs',
  data() {
    return {
      activeKey: '',
      tabs: [],
      index: 0,
      tabComponent: {},
      iframeComponent: {}
    }
  },
  watch: {
    'activeKey': function(tabName) {
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
        menu.closable = false
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
        that.activeKey = sessionStorage.getItem('activeKey')
        console.log(that.activeKey)
      })
    }
  },
  methods: {
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
          that.activeKey = menu.name
        }
        return
      } else {
        that.index = that.tabs.length
      }
      that.tabs.push({
        title: menu.name,
        name: menu.name,
        key: menu.name,
        content: menu.component,
        path: menu.path
      })

      if (!isRefresh) {
        that.activeKey = menu.name
      }

      if (menu.path !== '/introduction') {
        sessionStorage.setItem('tabs', JSON.stringify(that.tabs))
      }
      console.log(that.index)

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
        setTimeout(function() {
          that.$refs.iframe[0].contentWindow.location.href = params.path
        }, 10000)

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
        sessionStorage.setItem('activeKey', menu.name)
      }
    },
    tabEdit(targetKey, action) {
      const that = this
      const tabs = that.tabs
      let activeName = that.activeKey
      if (activeName === targetKey) {
        tabs.forEach((tab, index) => {
          if (tab.name === targetKey) {
            const nextTab = tabs[index + 1] || tabs[index - 1]
            if (nextTab) {
              activeName = nextTab.name
            }
          }
        })
      }
      that.activeKey = activeName
      that.tabs = tabs.filter(tab => tab.name !== targetKey)

      that.tabs.forEach((tab, index) => {
        if (tab.name === activeName) {
          that.index = index
        }
      })

      sessionStorage.setItem('tabs', JSON.stringify(that.tabs))
      sessionStorage.setItem('activeKey', that.activeKey)
    },
    tabChange(activeKey) {
      const that = this
      that.activeKey = activeKey
      sessionStorage.setItem('activeKey', that.activeKey)
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

<style scoped>
.ant-header, .ant-footer {
    padding:  0px;
    background-color: #B3C0D1;
    color: #333;
    line-height: 60px;
}

.ant-aside {
    height:90vh;
    background-color: #FFFFFF;
    color: #333;
    line-height: 200px;
}

.ant-main {
    padding: 6px 6px;
    background-color: #FFFFFF;
    color: #333;
    /* padding: 0 0px; */
}

body > .ant-container {
    margin-bottom: 40px;
}

.ant-container {
    width: 100%;
    height: 100%;
}

.ant-row {
    display: flex;
    flex-wrap: wrap;
}

.tabComponent {
    padding-top: 0px;
    width: 100%;
}
</style>
