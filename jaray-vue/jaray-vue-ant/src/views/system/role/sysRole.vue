<template>
  <div>
    <div>
      <div id="menu">
        <i-tree
          ref="iTree"
          :tree-data="treeData"
          style="max-height: 400px;overflow-y: auto;"
          @rowClick="rowClick"
          @transmit_parent_keys="receiveKeys"/>
        <div slot="footer" class="dialog-footer">
          <a-button size="small" @click="allChecked">全选</a-button>
          <a-button size="small" @click="clearTreeChecked">清空</a-button>
          <a-button size="small" @click="resetTreeChecked">重置</a-button>
          <a-button v-if="hasPermission('sys:role:edit')" size="small" type="primary" @click="editSysRoleMenuPerm">保存</a-button>
        </div>
      </div>

      <div id="table">
        <div style="margin-bottom: 10px">
          <a-input
            v-model="search"
            placeholder="请输入姓名"
            clearable
            style="width: 200px"/>
          <a-button type="success" plain @click="initTable">查询</a-button>
          <a-button v-if="hasPermission('sys:role:add')" type="success" plain @click="addSysRole">增加</a-button>
          <a-button v-if="hasPermission('sys:role:delete')" type="danger" plain @click="deleteSysRole">删除</a-button>
        </div>
        <i-table
          ref="iTable"
          :table-title="tableTitle"
          :table-data="tableData"
          :table-height="tableHeight"
          @transmitParent="receiveChild"
          @row_click="rowClick"/>
        <i-pagination
          ref="iPagination"
          :total="total"
          :current-page="pageNum"
          :page-size="pageSize"
          @handleSizeChange="handleSizeChange"
          @handleCurrentChange="handleCurrentChange"/>
      </div>

      <!-- 新增 -->
      <add-sys-role ref="addSysRole"/>

      <!-- 详情 -->
      <view-sys-role ref="viewSysRole"/>

      <!-- 编辑 -->
      <edit-sys-role ref="editSysRole"/>
    </div>
  </div>
</template>

<script>
import { mapActions, mapGetters } from 'vuex'
import API from '@api/api_sys_role'
import SYS_API from '@api/api_system'
import ROLE_MENU_PERM_API from '@api/api_sys_role_menu_perm'
import iTable from '@components/common/iTable'
import iPagination from '@components/common/iPagination'
import iTree from '@components/common/iTree'
import addSysRole from '@views/system/role/addSysRole'
import viewSysRole from '@views/system/role/viewSysRole'
import editSysRole from '@views/system/role/editSysRole'

export default {
  name: 'SysRole',
  components: { iTable, iPagination, iTree, addSysRole, viewSysRole, editSysRole },
  data() {
    return {
      // 表格高度
      tableHeight: 'calc(65vh - 260px)',
      loading: false,
      search: '',
      tableTitle: [
        { title: 'ID', dataIndex: 'id', key: 'id' },
        { title: '名称', dataIndex: 'name', key: 'name', filters: [] },
        { title: '备注', dataIndex: 'remark', key: 'remark' },
        { title: '创建人', dataIndex: 'createBy', key: 'createBy' },
        { title: '创建时间', dataIndex: 'createTime', key: 'createTime', formatter: this.dateTimeFormatter },
        { title: '更新人', dataIndex: 'lastUpdateBy', key: 'lastUpdateBy' },
        { title: '更新时间', dataIndex: 'lastUpdateTime', key: 'lastUpdateTime', formatter: this.dateTimeFormatter },
        // 此处为操作栏，不需要可以删除，clickFun绑定此操作按钮的事件
        { prop: 'operation', label: '操作', fixed: 'right', width: 143,
          operation: [
            { name: '查看', style: 'primary', clickFun: this.viewSysRole, disabled: this.hasPermission('sys:role:view') },
            { name: '修改', style: 'primary', clickFun: this.editViewSysRole, disabled: this.hasPermission('sys:role:edit') }
          ]
        }
      ],
      tableData: [],
      multipleSelection: [],
      total: 0,
      pageNum: 1,
      pageSize: 10,
      treeData: [],
      menuPerm: {
        roleId: '',
        menuPermId: '',
        menuId: '',
        permId: '',
        keys: []
      }
    }
  },
  computed: {
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
  created: function() {
    const that = this
    that.initTable()
    that.findLeftNav()
  },
  methods: {
    addSysRole: function() {
      const that = this
      that.$refs['addSysRole'].openDialog()
    },
    initTable: function() {
      const that = this
      // 定义请求参数
      const params = {
        pageNum: that.pageNum,
        pageSize: that.pageSize
      }
      // 调用接口
      API.findSysRoleList(params).then(function(result) {
        if (result.code === 200) {
          that.total = result.map.sysRoles.total
          that.currentPage = result.map.sysRoles.pageNum
          that.pageSize = result.map.sysRoles.pageSize
          that.tableData = result.map.sysRoles.list

          that.filtersHandler(that.tableData)
        }
      })
    },
    rowClick: function(row) {
      const that = this
      that.menuPerm.roleId = row.id

      // 定义请求参数
      const params = {
        roleId: that.menuPerm.roleId
      }
      // 调用接口
      ROLE_MENU_PERM_API.viewSysRoleMenuPermByRid(params).then(function(result) {
        if (result.code === 200) {
          const menuPermId = []
          if (result.map.sysRoleMenu) {
            that.menuPerm.menuId = result.map.sysRoleMenu.menuId
            menuPermId.push(result.map.sysRoleMenu.menuId)
          }
          if (result.map.sysRolePerm) {
            that.menuPerm.permId = result.map.sysRolePerm.permId
            menuPermId.push(result.map.sysRolePerm.permId)
          }

          if (menuPermId && menuPermId.length) {
            that.menuPerm.menuPermId = menuPermId.join(',')
          } else {
            that.menuPerm.menuPermId = ''
            that.menuPerm.permId = ''
            that.menuPerm.menuId = ''
          }
        } else {
          that.menuPerm.menuPermId = ''
          that.menuPerm.permId = ''
          that.menuPerm.menuId = ''
        }
        that.resetTreeChecked()
        if (row.name === 'admin') {
          that.allChecked()
        }
      })
    },
    receiveChild: function(data) {
      const that = this
      that.multipleSelection = data
      console.log('父组件接收的数据', that.multipleSelection)
    },
    receiveKeys: function(keys) {
      const that = this
      that.menuPerm.keys = keys
      console.log('树形组件接收子组件的数据', that.menuPerm.keys)
    },
    allChecked: function() {
      const that = this
      that.$refs.iTree.allChecked()
    },
    resetTreeChecked: function() {
      const that = this
      let permKeys = []
      if (that.menuPerm.permId !== '') {
        permKeys = that.menuPerm.permId.split(',')
      }
      let menuKeys = []
      if (that.menuPerm.menuId !== '') {
        menuKeys = that.menuPerm.menuId.split(',')
      }

      for (var i = 0; i < menuKeys.length; i++) {
        for (var j = 0; j < permKeys.length; j++) {
          if (menuKeys[i] === permKeys[j]) {
            menuKeys.splice(i, 1)
          }
        }
      }

      const keys = menuKeys.concat(permKeys)
      that.$refs.iTree.resetChecked(keys)
    },
    clearTreeChecked: function() {
      const that = this
      that.$refs['iTree'].clearChecked()
    },
    editSysRoleMenuPerm: function() {
      const that = this
      if (that.menuPerm.roleId === '1') {
        that.$message({
          message: '管理员不允许编辑',
          type: 'warning'
        })
        return false
      }

      // 定义请求参数
      const params = {
        roleId: that.menuPerm.roleId,
        menuPermId: that.menuPerm.keys.join(',')
      }
      // 调用接口
      ROLE_MENU_PERM_API.updateSysRoleMenuPerm(params).then(function(result) {
        if (result.code === 200) {
          that.initTable()
          that.$message.success('恭喜你，编辑成功')
          // 刷新左侧菜单（自己不能修改自己，所以不用刷新）
          that.refreshFindLeftNav()
        }
      })
    },
    viewSysRole: function(row) {
      const that = this
      that.$refs['viewSysRole'].openDialog(row)
    },
    editViewSysRole: function(row) {
      const that = this
      that.$refs['editSysRole'].openDialog(row)
    },
    deleteSysRole: function() {
      const that = this
      const ids = []
      for (var i = 0; i < that.multipleSelection.length; i++) {
        ids.push(that.multipleSelection[i].id)
      }
      // 定义请求参数
      const params = {
        ids: ids.join(',')
      }
      // 调用接口
      API.deleteSysRole(params).then(function(result) {
        if (result.code === 200) {
          that.initTable()
          that.$message({
            message: '恭喜你，删除成功',
            type: 'success'
          })
        }
      })
    },
    dateTimeFormatter: function(row, column, cellValue, index) {
      if (cellValue === undefined) {
        return ''
      }
      return this.$moment(cellValue).format('YYYY-MM-DD HH:mm:ss')
    },
    handleSizeChange: function(pageSize) {
      const that = this
      that.pageSize = pageSize
      that.initTable()
    },
    handleCurrentChange: function(currentPage) {
      const that = this
      that.pageNum = currentPage
      that.initTable()
    },
    filtersHandler: function(tableData) {
      const that = this
      for (var i = 0; i < that.tableTitle.length; i++) {
        if (that.tableTitle[i].filters) {
          const filters = []
          for (var j = 0; j < tableData.length; j++) {
            for (var item in tableData[j]) {
              if (item === that.tableTitle[i].key + '') { // item 表示Json串中的属性，如'title'
                filters.push({ text: tableData[j][that.tableTitle[i].key], value: tableData[j][that.tableTitle[i].key] })
              }
            }
          }
          that.tableTitle[i].filters = that.filtersArrayUnique(filters)
        }
      }
    },
    filtersArrayUnique: function(filters) {
      for (var i = 0; i < filters.length; i++) {
        for (var j = i + 1; j < filters.length; j++) {
          if (filters[i].text === filters[j].text) { // 第一个等于第二个，splice方法删除第二个
            filters.splice(j, 1)
            j--
          }
        }
      }
      return filters
    },
    findLeftNav: function() {
      const that = this
      // 定义请求参数
      const params = {}
      // 调用接口
      SYS_API.findLeftNav(params).then(function(result) {
        if (result.code === 200) {
          that.treeData = result.map.leftMenu
        }
      })
    },
    refreshFindLeftNav: function() {
      const that = this
      // 定义请求参数
      const accessUser = JSON.parse(sessionStorage.getItem('access-user'))

      const params = { userId: accessUser.userId }
      // 调用接口
      SYS_API.findLeftNav(params).then(function(result) {
        if (result.code === 200) {
          that.leftMenus = result.map.leftMenu
        }
      })
    },
    ...mapActions( // 语法糖
      ['modifyLeftMenus'] // 相当于this.$store.dispatch('modifyLeftMenus'),提交这个方法
    )
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
h1, h2 {
  font-weight: normal;
}
ul {
  list-style-type: none;
  padding: 0;
}
li {
  display: inline-block;
  margin: 0 10px;
}
a {
  color: #42b983;
}

#table {
  float:left;
  width: 80%;
}
#menu {
  float:left;
  width: 20%;
}
</style>
