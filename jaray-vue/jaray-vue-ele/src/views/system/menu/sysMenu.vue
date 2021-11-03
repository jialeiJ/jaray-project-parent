<template>
  <div>
    <div>
      <div id="table">
        <div style="margin-bottom: 10px">
          <el-input
            v-model="search"
            placeholder="请输入姓名"
            clearable
            style="width: 200px"/>
          <el-button type="success" plain @click="initTable">查询</el-button>
          <el-button v-if="hasPermission('sys:menu:add')" type="success" plain @click="addSysMenu">增加</el-button>
          <el-button v-if="hasPermission('sys:menu:delete')" type="danger" plain @click="batchDeleteSysMenu">删除</el-button>
        </div>
        <i-tree-table
          ref="iTable"
          :have-checkbox="true"
          :table-title="tableTitle"
          :table-data="tableData"
          :table-height="tableHeight"
          @transmitParent="receiveChild"
          @handleView="viewSysMenu"
          @handleEdit="editViewSysMenu"
          @handleDelete="deleteSysMenu"/>
        <i-pagination
          ref="iPagination"
          :total="total"
          :current-page="pageNum"
          :page-size="pageSize"
          @handleSizeChange="handleSizeChange"
          @handleCurrentChange="handleCurrentChange"/>
      </div>

      <!-- 新增 -->
      <add-sys-menu ref="addSysMenu" :dir-tree-data="dirTreeData"/>

      <!-- 详情 -->
      <view-sys-menu ref="viewSysMenu" :dir-tree-data="dirTreeData"/>

      <!-- 编辑 -->
      <edit-sys-menu ref="editSysMenu" :dir-tree-data="dirTreeData"/>
    </div>
  </div>
</template>

<script>
import store from '@/store'
import API from '@api/api_system'
import MENU_API from '@api/api_sys_menu'
import DICT_API from '@api/api_sys_dict'
import iTreeTable from '@components/common/iTreeTable'
import iPagination from '@components/common/iPagination'
import addSysMenu from '@views/system/menu/addSysMenu'
import viewSysMenu from '@views/system/menu/viewSysMenu'
import editSysMenu from '@views/system/menu/editSysMenu'

export default {
  name: 'SysMenu',
  components: { iTreeTable, iPagination, addSysMenu, viewSysMenu, editSysMenu },
  data() {
    return {
      // label宽度
      formLabelWidth: 'calc(14vh - 0px)',
      // 表格高度
      tableHeight: 'calc(95vh - 200px)',
      addDialogFormVisible: false,
      viewDialogFormVisible: false,
      editDialogFormVisible: false,
      search: '',
      tableTitle: [
        { prop: 'id', label: 'ID', fixed: true, sort: true },
        { prop: 'name', label: '名称', sort: true, filters: [] },
        { prop: 'type', label: '类型', formatter: this.typeFormatter, renderComponent: 'tag' },
        { prop: 'url', label: '链接' },
        { prop: 'icon', label: '图标' },
        { prop: 'orderNum', label: '排序' },
        { prop: 'createBy', label: '创建人' },
        { prop: 'createTime', label: '创建时间', formatter: this.dateTimeFormatter },
        { prop: 'lastUpdateBy', label: '更新人' },
        { prop: 'lastUpdateTime', label: '更新时间', formatter: this.dateTimeFormatter },
        // 此处为操作栏，不需要可以删除，clickFun绑定此操作按钮的事件
        { prop: 'operation', label: '操作', fixed: 'right', width: 240,
          operation: [
            { name: '查看', style: 'primary', clickFun: this.viewSysMenu, disabled: this.hasPermission('sys:menu:view') },
            { name: '修改', style: 'primary', clickFun: this.editViewSysMenu, disabled: this.hasPermission('sys:menu:edit') },
            { name: '删除', style: 'danger', clickFun: this.deleteSysMenu, disabled: this.hasPermission('sys:menu:delete') }
          ]
        }
      ],
      tableData: [],
      multipleSelection: [],
      total: 0,
      pageNum: 1,
      pageSize: 10,
      addForm: {},
      viewForm: {},
      editForm: {},
      pickerOptions: {
        shortcuts: [{
          text: '今天',
          onClick(picker) {
            picker.$emit('pick', new Date())
          }
        }, {
          text: '昨天',
          onClick(picker) {
            const date = new Date()
            date.setTime(date.getTime() - 3600 * 1000 * 24)
            picker.$emit('pick', date)
          }
        }, {
          text: '一周前',
          onClick(picker) {
            const date = new Date()
            date.setTime(date.getTime() - 3600 * 1000 * 24 * 7)
            picker.$emit('pick', date)
          }
        }]
      },
      dirTreeData: [],
      tileMenuData: [],
      // 菜单ids,用于回显
      menuIds: [],
      typeOptions: []
    }
  },
  created: function() {
    const that = this
    that.findSysDictByDesc().then((result) => {
      that.initTable()
    })
  },
  mounted: function() {
  },
  methods: {
    addSysMenu: function() {
      const that = this
      that.$refs['addSysMenu'].openDialog()
    },
    initTable: function() {
      const that = this
      // 定义请求参数
      const params = {
        pageNum: that.pageNum,
        pageSize: that.pageSize
      }
      // 调用接口
      MENU_API.findSysMenuList(params).then(function(result) {
        if (result.code === 200) {
          that.total = result.map.sysMenus.total
          that.currentPage = result.map.sysMenus.pageNum
          that.pageSize = result.map.sysMenus.pageSize
          that.tableData = result.map.sysMenus.list

          that.tileMenuList(result.map.sysMenus.list)

          // 解决双向修改问题
          // JSON.parse(JSON.stringify(result.map.sysMenus.list))
          const dirTreeData = JSON.parse(JSON.stringify(result.map.sysMenus.list))
          const data = dirTreeData.filter(function(item) {
            return item.type === 0
          })
          that.dirTreeData = data
          that.getDirTreeData(that.dirTreeData)

          that.filtersHandler(that.tableData)
        }
      })
    },
    receiveChild: function(data) {
      const that = this
      that.multipleSelection = data
      console.log('父组件接收的数据', that.multipleSelection)
    },
    viewSysMenu: function(row) {
      const that = this
      that.$refs['viewSysMenu'].openDialog(row)
    },
    getAllPidById: function(id, tileMenuData) {
      const that = this
      if (id && that.menuIds.indexOf(id) === -1) {
        that.menuIds.push(id)
      }
      tileMenuData.forEach(function(item, index) {
        if (item.id === id) {
          that.getAllPidById(item.parentId, tileMenuData)
        }
      })
    },
    editViewSysMenu: function(row) {
      const that = this
      that.$refs['editSysMenu'].openDialog(row)
    },
    deleteSysMenu: function(row) {
      const that = this
      // 定义请求参数
      const params = {
        ids: row.id
      }
      // 调用接口
      MENU_API.deleteSysMenu(params).then(function(result) {
        if (result.code === 200) {
          that.$message({
            message: '恭喜你，删除成功',
            type: 'success'
          })
          that.initTable()
          that.updateLeftNav()
        }
      })
    },
    batchDeleteSysMenu: function() {
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
      MENU_API.deleteSysMenu(params).then(function(result) {
        if (result.code === 200) {
          that.$message({
            message: '恭喜你，删除成功',
            type: 'success'
          })
          that.initTable()
          that.updateLeftNav()
        }
      })
    },
    updateLeftNav: function() {
      // 定义请求参数
      const params = {}
      // 调用接口
      API.findLeftNav(params).then(function(result) {
        if (result.code === 200) {
          store.state.leftMenus = result.map.leftMenu
          // location.reload()
        }
      })
    },
    handleChange(value) {
      const that = this
      that.parentId = value[value.length - 1]
    },
    typeFormatter: function(row, column, cellValue, index) {
      if (cellValue === undefined) {
        return ''
      }
      let result = ''
      for (var i = 0; i < this.typeOptions.length; i++) {
        if (String(cellValue) === this.typeOptions[i].value) {
          result = this.typeOptions[i].label
        }
      }

      return result
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
              if (item === that.tableTitle[i].prop + '') { // item 表示Json串中的属性，如'title'
                filters.push({ text: tableData[j][that.tableTitle[i].prop], value: tableData[j][that.tableTitle[i].prop] })
              }
            }
          }
          that.tableTitle[i].filters = that.filtersArrayUnique(filters)
        }
      }
    },
    getDirTreeData: function(dirTreeData) {
      const that = this
      for (var i = 0; i < dirTreeData.length; i++) {
        if (dirTreeData[i].children && dirTreeData[i].children.length) {
          const data = dirTreeData[i].children.filter(function(item) {
            return item.type === 0
          })

          if (data && data.length) {
            dirTreeData[i].children = data
          } else {
            // 删除children属性
            that.$delete(dirTreeData[i], 'children')
          }

          if (data.length) {
            that.getDirTreeData(dirTreeData[i].children)
          } else {
            continue
          }
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
    // 平铺数据
    tileMenuList(menuDataList) {
      const that = this
      menuDataList.forEach(function(item, index) {
        that.tileMenuData.push(item)
        if (item.children && item.children.length) {
          that.tileMenuList(item.children)
        }
      })
    },
    findSysDictByDesc: function() {
      const that = this
      return new Promise((resolve, reject) => {
        // 定义请求参数
        const params = { description: '菜单类型' }
        // 调用接口
        DICT_API.findSysDictByDesc(params).then(function(result) {
          if (result.code === 200) {
            that.typeOptions = result.map.selectOptions
            resolve(result)
          }
        })
      })
    }
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style lang="scss" scoped>
/deep/ .el-dialog .el-dialog__body {
  border-top: 1px solid #dcdfe6;
  border-bottom: 1px solid #dcdfe6;
  max-height: calc(85vh - 260px);
  overflow-y: auto;
}
</style>
