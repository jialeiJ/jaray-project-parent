<template>
  <div>
    <div>
      <div id="table">
        <div style="margin-bottom: 10px">
          <a-input
            v-model="search"
            placeholder="请输入姓名"
            clearable
            style="width: 200px"/>
          <a-button type="success" plain @click="initTable">查询</a-button>
          <a-button v-if="hasPermission('sys:menu:add')" type="success" plain @click="addSysMenu">增加</a-button>
          <a-button v-if="hasPermission('sys:menu:delete')" type="danger" plain @click="deleteSysMenu">删除</a-button>
        </div>
        <i-table
          ref="iTable"
          :table-title="tableTitle"
          :table-data="tableData"
          :table-height="tableHeight"
          @transmit_parent="receiveChild"/>
        <i-pagination
          ref="iPagination"
          :total="total"
          :current-page="pageNum"
          :page-size="pageSize"
          @handleSizeChange="handleSizeChange"
          @handleCurrentChange="handleCurrentChange"/>
      </div>

      <!-- 新增 -->
      <add-sys-menu ref="addSysMenu" :dir-tree-data="dirTreeData" :tile-menu-data="tileMenuData"/>

      <!-- 详情 -->
      <view-sys-menu ref="viewSysMenu" :dir-tree-data="dirTreeData" :tile-menu-data="tileMenuData"/>

      <!-- 编辑 -->
      <edit-sys-menu ref="editSysMenu" :dir-tree-data="dirTreeData" :tile-menu-data="tileMenuData"/>
    </div>
  </div>
</template>

<script>
import MENU_API from '@api/api_sys_menu'
import DICT_API from '@api/api_sys_dict'
import iTable from '@components/common/iTable'
import iPagination from '@components/common/iPagination'
import addSysMenu from '@views/system/menu/addSysMenu'
import viewSysMenu from '@views/system/menu/viewSysMenu'
import editSysMenu from '@views/system/menu/editSysMenu'

export default {
  name: 'SysMenu',
  components: { iTable, iPagination, addSysMenu, viewSysMenu, editSysMenu },
  data() {
    return {
      // 表格高度
      tableHeight: 'calc(95vh - 200px)',
      addDialogFormVisible: false,
      viewDialogFormVisible: false,
      editDialogFormVisible: false,
      search: '',
      tableTitle: [
        { title: 'ID', dataIndex: 'id', key: 'id', fixed: 'left', width: 100 },
        { title: '名称', dataIndex: 'name', key: 'name', filters: [] },
        { title: '类型', dataIndex: 'type', key: 'type', formatter: this.typeFormatter, tagRender: this.tagRender },
        { title: '链接', dataIndex: 'url', key: 'url' },
        { title: '图标', dataIndex: 'icon', key: 'icon' },
        { title: '排序', dataIndex: 'orderNum', key: 'orderNum' },
        { title: '创建人', dataIndex: 'createBy', key: 'createBy' },
        { title: '创建时间', dataIndex: 'createTime', key: 'createTime', formatter: this.dateTimeFormatter },
        { title: '更新人', dataIndex: 'lastUpdateBy', key: 'lastUpdateBy' },
        { title: '更新时间', dataIndex: 'lastUpdateTime', key: 'lastUpdateTime', formatter: this.dateTimeFormatter },
        // 此处为操作栏，不需要可以删除，clickFun绑定此操作按钮的事件
        { prop: 'operation', title: '操作', dataIndex: 'operation', key: 'operation', fixed: 'right', width: 140,
          operation: [
            { name: '查看', style: 'primary', clickFun: this.viewSysMenu, disabled: this.hasPermission('sys:menu:view') },
            { name: '修改', style: 'primary', clickFun: this.editViewSysMenu, disabled: this.hasPermission('sys:menu:edit') }
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
      dirTreeData: [],
      tileMenuData: [],
      // 菜单ids,用于回显
      menuIds: [],
      typeOptions: []
    }
  },
  created: function() {
    const that = this
    that.initTable()
    that.findSysDictByDesc()
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
          // 删除children为空的属性
          that.deleteEmptyChildren(result.map.sysMenus.list)
          that.tableData = result.map.sysMenus.list

          // 平铺数据
          that.tileMenuList(result.map.sysMenus.list)

          // 解决双向修改问题：JSON.parse(JSON.stringify(result.map.sysMenus.list))
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
    deleteEmptyChildren(tableData) {
      const that = this
      tableData.forEach(function(item, index) {
        if (item.children.length > 0) {
          that.deleteEmptyChildren(item.children)
        } else {
          that.$delete(item, 'children')
        }
      })
      that.tableData = tableData
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
    deleteSysMenu: function() {
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
          that.initTable()
          that.$message({
            message: '恭喜你，删除成功',
            type: 'success'
          })
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
        if (cellValue === this.typeOptions[i].value) {
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
    tagRender: function(row, column, cellValue, index) {
      if (cellValue === 0) {
        return 'blue'
      }
      if (cellValue === 1) {
        return 'green'
      }
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
      // 定义请求参数
      const params = { description: '菜单类型' }
      // 调用接口
      DICT_API.findSysDictByDesc(params).then(function(result) {
        if (result.code === 200) {
          that.typeOptions = result.map.selectOptions
        }
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
