<template>
  <div>
    <div id="table">
      <div style="margin-bottom: 10px">
        <a-input
          v-model="search"
          placeholder="请输入姓名"
          clearable
          style="width: 200px"/>
        <a-button type="success" plain @click="initTable">查询</a-button>
        <a-button v-if="hasPermission('sys:dept:add')" type="success" plain @click="addSysDept">增加</a-button>
        <a-button v-if="hasPermission('sys:dept:delete')" type="danger" plain @click="deleteSysDept">删除</a-button>
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
        :current-page="currentPage"
        :page-size="pageSize"
        @handleSizeChange="handleSizeChange"
        @handleCurrentChange="handleCurrentChange"/>
    </div>

    <!-- 新增 -->
    <add-sys-dept ref="addSysDept" :dept-options="deptOptions"/>

    <!-- 编辑 -->
    <edit-sys-dept ref="editSysDept" :dept-options="deptOptions" :tile-dept-data="tileDeptData"/>

    <!-- 详情 -->
    <view-sys-dept ref="viewSysDept" :dept-options="deptOptions" :tile-dept-data="tileDeptData"/>
  </div>
</template>

<script>
import API from '@api/api_sys_dept'
import iTable from '@components/common/iTable'
import iPagination from '@components/common/iPagination'
import addSysDept from '@views/system/dept/addSysDept'
import editSysDept from '@views/system/dept/editSysDept'
import viewSysDept from '@views/system/dept/viewSysDept'

export default {
  name: 'SysDept',
  components: { iTable, iPagination, addSysDept, editSysDept, viewSysDept },
  data() {
    return {
      // 表格高度
      tableHeight: 'calc(95vh - 200px)',
      search: '',
      tableTitle: [
        { title: 'ID', dataIndex: 'id', key: 'id' },
        { title: '名称', dataIndex: 'name', key: 'name', filters: [] },
        { title: '创建人', dataIndex: 'createBy', key: 'createBy' },
        { title: '创建时间', dataIndex: 'createTime', key: 'createTime', formatter: this.dateTimeFormatter },
        { title: '更新人', dataIndex: 'lastUpdateBy', key: 'lastUpdateBy' },
        { title: '更新时间', dataIndex: 'lastUpdateTime', key: 'lastUpdateTime', formatter: this.dateTimeFormatter },
        // 此处为操作栏，不需要可以删除，clickFun绑定此操作按钮的事件
        { prop: 'operation', label: '操作', fixed: 'right', width: 143,
          operation: [
            { name: '查看', style: 'primary', clickFun: this.viewSysDept, disabled: this.hasPermission('sys:dept:view') },
            { name: '修改', style: 'primary', clickFun: this.editViewSysDept, disabled: this.hasPermission('sys:dept:edit') }
          ]
        }
      ],
      tableData: [],
      selectedRowKeys: [],
      total: 0,
      currentPage: 1,
      pageSize: 10,
      deptOptions: [],
      tileDeptData: []
    }
  },
  created: function() {
    const that = this
    that.initTable()
  },
  methods: {
    addSysDept: function() {
      const that = this
      that.$refs['addSysDept'].openDialog()
    },
    initTable: function() {
      const that = this
      // 定义请求参数
      const params = {
        search: that.search,
        pageNum: that.currentPage,
        pageSize: that.pageSize
      }
      // 调用接口
      API.findSysDeptList(params).then(function(result) {
        if (result.code === 200) {
          that.total = result.map.sysDepts.total
          that.currentPage = result.map.sysDepts.pageNum
          that.pageSize = result.map.sysDepts.pageSize
          that.tableData = result.map.sysDepts.list

          that.deptOptions = result.map.sysDepts.list
          that.tileDeptData = []
          that.tileDeptList(result.map.sysDepts.list)

          that.filtersHandler(that.tableData)
        }
      })
    },
    receiveChild: function(data) {
      const that = this
      that.selectedRowKeys = data
      console.log('父组件接收的数据', that.selectedRowKeys)
    },
    viewSysDept: function(row) {
      const that = this
      that.$refs['viewSysDept'].openDialog(row)
    },
    editViewSysDept: function(row) {
      const that = this
      that.$refs['editSysDept'].openDialog(row)
    },
    deleteSysDept: function() {
      const that = this
      const ids = []
      for (var i = 0; i < that.selectedRowKeys.length; i++) {
        ids.push(that.selectedRowKeys[i])
      }
      // 定义请求参数
      const params = {
        ids: ids.join(',')
      }
      // 调用接口
      API.deleteSysDept(params).then(function(result) {
        if (result.code === 200) {
          that.initTable()
          that.$message.success('恭喜你，删除成功')
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
      // 当选择页面显示条数时初始化当前页
      that.currentPage = 1
      that.pageSize = pageSize
      that.initTable()
    },
    handleCurrentChange: function(currentPage) {
      const that = this
      that.currentPage = currentPage
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
    tileDeptList: function(deptDataList) {
      const that = this
      deptDataList.forEach(function(item, index) {
        that.tileDeptData.push(item)
        if (item.children && item.children.length) {
          that.tileDeptList(item.children)
        }
      })
    }
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style lang="scss" scoped>
</style>
