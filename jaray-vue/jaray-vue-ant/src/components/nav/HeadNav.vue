<template>
  <fragment>
    <a-menu
      theme="dark"
      mode="horizontal"
      @select="addTab"
      @click="addTab">
      <!-- 菜单遍历的开始 -->
      <template v-for="(item, index) in leftMenus">
        <!-- 如果当前遍历项没有children，视为子菜单项，注意所有的key都是path用于路由跳转，以及当前选中记录 -->
        <a-menu-item v-if="item.type == 1" :key="item.path == ''?item.name+index:item.path">
          <i :class="item.icon" />
          <span>{{ item.name }}</span>
        </a-menu-item>
        <!-- 否则视为子菜单，传入菜单信息并且运用下面定义的函数式组件 -->
        <sub-menu v-else-if="item.type == 0" :key="item.path == ''?item.name+index:item.path" :menu-info="item" />
      </template>

      <a-sub-menu index="user" style="float: right;">
        <template slot="title">用户：{{ userInfo.name }}</template>
        <a-menu-item index="user-1" style="line-height: 42px;">选项1</a-menu-item>
        <a-menu-item index="user-2" style="line-height: 42px;" @click="editDialogFormVisible = true">修改密码</a-menu-item>
        <a-menu-item index="user-3" style="line-height: 42px;" @click="signOut">退出系统</a-menu-item>
      </a-sub-menu>
    </a-menu>

    <div id="edit">
      <a-modal
        :visible.sync="editDialogFormVisible"
        title="修改密码"
        @ok="handleOk"
        @cancel="handleCancel">
        <a-form-model ref="editForm" :model="editForm" :rules="rules" :label-col="labelCol" :wrapper-col="wrapperCol">
          <a-form-model-item label="原始密码" prop="srcPassword">
            <a-input v-model="editForm.srcPassword" type="password" has-feedback autocomplete="off"/>
          </a-form-model-item>
          <a-form-model-item label="密码" prop="password">
            <a-input v-model="editForm.password" type="password" has-feedback autocomplete="off"/>
          </a-form-model-item>
          <a-form-model-item label="确认密码" prop="checkPass">
            <a-input v-model="editForm.checkPass" type="password" has-feedback autocomplete="off"/>
          </a-form-model-item>
        </a-form-model>
        <div slot="footer" class="dialog-footer">
          <a-button @click="editDialogFormVisible = false">取 消</a-button>
          <a-button type="primary" @click="editPassword('editForm')">确 定</a-button>
        </div>
      </a-modal>
    </div>
  </fragment>
</template>

<script>
import { mapActions, mapGetters } from 'vuex'
import API from '../../api/api_system'

import { Menu } from 'ant-design-vue'
// 定义函数式组件
const SubMenu = {
  template: `
        <a-sub-menu :key="menuInfo.path == ''?menuInfo.name:menuInfo.path" v-bind="$props" v-on="$listeners">
            <span slot="title">
                <i :class="menuInfo.icon" /><span>{{ menuInfo.name }}</span>
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
  // must add isSubMenu: true 此项必须被定义
  isSubMenu: true,
  props: {
    // 解构a-sub-menu的属性，也就是文章开头提到的为什么使用函数式组件
    ...Menu.SubMenu.props,
    // 接收父级传递过来的菜单信息
    menuInfo: {
      type: Object,
      default: () => ({})
    }
  }
}
export default {
  name: 'HeadNav',
  // 注册局部组件
  components: { SubMenu },
  data() {
    const validatePass = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请输入密码'))
      } else {
        if (this.editForm.password !== '') {
          this.$refs.editForm.validateField('password')
        }
        callback()
      }
    }
    const validatePass2 = (rule, value, callback) => {
      const that = this
      if (value === '') {
        callback(new Error('请确认密码'))
      } else if (value !== that.editForm.password) {
        callback(new Error('密码不一致!'))
      } else {
        callback()
      }
    }
    return {
      labelCol: { span: 4 },
      wrapperCol: { span: 14 },
      editDialogFormVisible: false,
      tileLeftNavData: [],
      userInfo: {
        name: ''
      },
      editForm: {
        id: '',
        srcPassword: '',
        password: '',
        checkPass: ''
      },
      rules: {
        password: [{ required: true, validator: validatePass, trigger: 'change' }],
        checkPass: [{ required: true, validator: validatePass2, trigger: 'change' }]
      }
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
  mounted: function() {
    this.setUserInfo()
  },
  created: function() {

  },
  methods: {
    // 折叠导航栏
    toggleCollapsed: function() {
      this.collapsed = !this.collapsed
    },
    setUserInfo: function() {
      const that = this
      const accessUser = sessionStorage.getItem('access-user')
      that.userInfo = JSON.parse(accessUser)
      that.editForm.id = that.userInfo.user_id
    },
    editPassword: function(formName) {
      this.$refs[formName].validate(valid => {
        if (valid) {
          const that = this
          // 定义请求参数
          const params = that.editForm
          // 调用接口
          API.updatePassword(params).then(function(result) {
            if (result.code === 200) {
              that.$message({
                message: '恭喜你，密码修改成功,请重新登陆',
                type: 'success'
              })
              that.editDialogFormVisible = false
              sessionStorage.clear()
              that.$router.push({ path: '/' })
            } else {
              that.$message({
                message: '抱歉，密码修改失败',
                type: 'error'
              })
            }
          })
        } else {
          console.log('error submit!!')
          return false
        }
      })
    },
    signOut: function() {
      const that = this
      sessionStorage.clear()
      that.$router.push({ path: '/' })
      that.$store.dispatch('modifyLeftMenus', [])
      console.log(that.$store.getters.leftMenus)
      // window.location.reload()
    },
    handleOpen: function(key, keyPath) {
      // console.log(key, keyPath);
    },
    handleClose: function(key, keyPath) {
      // console.log(key, keyPath);
    },
    addTab: function({ item, key, selectedKeys }) {
      if (key) {
        const that = this
        const params = {}
        const obj = that.tileLeftNavData.filter(function(item) {
          return item.url === key
        })

        if (obj.length > 0) {
          params.title = obj[0].name
          params.path = obj[0].url

          // 通过 emit 触发
          this.$emit('add_tab', params)
        }
      }
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
    // 点击菜单，路由跳转,注意的是当点击MenuItem才会触发此函数
    menuClick({ item, key, keyPath }) {
      // 获取到当前的key,并且跳转,注意：用的tabs,故不需要路由跳转
      // this.$router.push({
      //     path: key
      // })
    },
    handleOk(e) {
      const that = this
      that.editDialogFormVisible = false
    },
    handleCancel(e) {
      const that = this
      that.editDialogFormVisible = false
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
/deep/ .ant-menu-submenu,.ant-menu-item {
  line-height: 64px;
  cursor: pointer;
  transition: color 0.3s;
}

#components-layout-demo-custom-trigger .trigger {
  font-size: 18px;
  line-height: 64px;
  padding: 0 24px;
  cursor: pointer;
  transition: color 0.3s;
}

#components-layout-demo-custom-trigger .trigger:hover {
  color: #1890ff;
}

#components-layout-demo-custom-trigger .logo {
  height: 32px;
  background: rgba(255, 255, 255, 0.2);
  margin: 16px;
}
</style>
