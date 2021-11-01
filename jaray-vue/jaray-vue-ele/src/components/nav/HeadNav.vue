<template>
  <div>
    <el-menu
      :default-active="$route.path"
      unique-opened
      class="el-menu-demo"
      background-color="#545c64"
      text-color="#fff"
      active-text-color="#ffd04b"
      mode="horizontal"
      @select="addTab"
      @open="handleOpen"
      @close="handleClose">
      <el-menu-item index="title">
        <a style="text-decoration:none;color:white;font-size:20px;">
          后台管理系统
        </a>
      </el-menu-item>
      <el-menu-item index="collapsed">
        <i :class="[collapsed?'iconfont icon-collapse-white go':'iconfont icon-collapse-white to']" style="display : block;"/>
      </el-menu-item>
      <left-nav v-if="isShow" id="leftNav" :left-menus="leftMenus" :icon="false" @addTab="addTab"/>

      <el-submenu index="user" style="float: right;">
        <template slot="title">用户：{{ userInfo.name }}</template>
        <el-menu-item index="user-1">选项1</el-menu-item>
        <el-menu-item index="user-2" @click="editDialogFormVisible = true">修改密码</el-menu-item>
        <el-menu-item index="user-3" @click="signOut">退出系统</el-menu-item>
      </el-submenu>
    </el-menu>
    <div id="edit">
      <el-dialog :visible.sync="editDialogFormVisible" title="修改密码">
        <el-form ref="editForm" :model="editForm" :rules="rules">
          <el-form-item :label-width="formLabelWidth" label="原始密码" prop="srcPassword">
            <el-input v-model="editForm.srcPassword" type="password" autocomplete="off"/>
          </el-form-item>
          <el-form-item :label-width="formLabelWidth" label="密码" prop="password">
            <el-input v-model="editForm.password" type="password" autocomplete="off"/>
          </el-form-item>
          <el-form-item :label-width="formLabelWidth" label="确认密码" prop="password2">
            <el-input v-model="editForm.password2" type="password" autocomplete="off"/>
          </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button @click="editDialogFormVisible = false">取 消</el-button>
          <el-button type="primary" @click="editPassword('editForm')">确 定</el-button>
        </div>
      </el-dialog>
    </div>
  </div>
</template>

<script>
import { mapActions, mapGetters } from 'vuex'
import API from '../../api/api_system'
import LeftNav from '@/components/nav/LeftNav'

export default {
  name: 'HeadNav',
  components: { LeftNav },
  data() {
    var validatePass = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请输入密码'))
      } else {
        if (this.editForm.password !== '') {
          this.$refs.editForm.validateField('password')
        }
        callback()
      }
    }
    var validatePass2 = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请再次输入密码'))
      } else if (value !== this.editForm.password) {
        callback(new Error('两次输入密码不一致!'))
      } else {
        callback()
      }
    }
    return {
      formLabelWidth: '120px',
      isShow: true,
      editDialogFormVisible: false,
      tileLeftNavData: [],
      userInfo: {
        name: ''
      },
      editForm: {
        id: '',
        srcPassword: '',
        password: '',
        password2: ''
      },
      rules: {
        password: [
          { validator: validatePass, trigger: 'blur' }
        ],
        password2: [
          { validator: validatePass2, trigger: 'blur' }
        ]
      }
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
    ...mapGetters(['leftMenus']), // 动态计算属性，相当于this.$store.getters.leftMenus
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
  },
  mounted: function() {
    const that = this
    that.setUserInfo()

    window.onresize = () => {
      return (() => {
        window.fullHeight = document.documentElement.clientHeight
        window.fullWidth = document.documentElement.clientWidth
        console.log('实时屏幕高度：', window.fullHeight)
        console.log('实时屏幕宽度：', window.fullWidth)
        if (window.fullWidth < 1220) {
          that.isShow = false
        } else {
          that.isShow = true
        }
      })()
    }
  },
  methods: {
    // 折叠导航栏
    changeCollapse: function() {
      this.collapsed = !this.collapsed
    },
    setUserInfo: function() {
      const that = this
      const accessUser = sessionStorage.getItem('access-user')
      that.userInfo = JSON.parse(accessUser)
      that.editForm.id = that.userInfo.user_id
    },
    editPassword: function(formName) {
      this.$refs[formName].validate((valid) => {
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
    },
    handleOpen: function(key, keyPath) {
      console.log(key, keyPath)
    },
    handleClose: function(key, keyPath) {
      console.log(key, keyPath)
    },
    addTab: function(key, keyPath) {
      const that = this

      if (key === 'collapsed') {
        that.changeCollapse()
        return false
      }

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
/* 去除el-menu-item下划线样式 */
.el-menu-demo .el-menu-item {
    border-bottom-color:black;
    //border-bottom-color:#ffffff!important;
    font-size: 16px;
}

.el-menu-demo .el-menu-item:nth-child(1) {
    border: 0px;
    text-align: center;
    width: 200px;
}
.el-menu-demo .el-menu-item:nth-child(2) {
    border: 0px;
}

.to {
    transition: all 0.5s;
    // 解决内联元素使用transform的rotate属性不起作用
    display: inline-block;
}

.go {
    transform: rotate(-90deg)!important;
    -ms-transform: rotate(-90deg) !important; /* IE 9 */
    -moz-transform: rotate(-90deg) !important; /* Firefox */
    -webkit-transform: rotate(-90deg) !important; /* Safari 和 Chrome */
    -o-transform: rotate(-90deg) !important; /* Opera */
    transition: all 0.5s;
    // 解决内联元素使用transform的rotate属性不起作用
    display: inline-block;
}
</style>
