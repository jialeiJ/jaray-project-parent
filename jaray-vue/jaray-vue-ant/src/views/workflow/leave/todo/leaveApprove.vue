<template>
  <div>
    <div id="table">
      <div style="margin-bottom: 10px">
        <el-input
          v-model="search"
          placeholder="请输入姓名"
          clearable
          style="width: 200px"/>
        <el-button type="success" plain @click="initTable">查询</el-button>
        <el-button v-if="hasPermission('sys:user:delete')" type="danger" plain @click="deleteLeaveApply">删除</el-button>
      </div>

      <i-table
        ref="iTable"
        :table-title="tableTitle"
        :table-data="tableData"
        :table-height="tableHeight"
        @transmitParent="receiveChild"
        @rowClick="rowClick"
        @handleView="viewLeaveApply"
        @handleEdit="approveLeaveApply"/>
      <i-pagination
        ref="iPagination"
        :total="total"
        :current-page="pageNum"
        :page-size="pageSize"
        @handleSizeChange="handleSizeChange"
        @handleCurrentChange="handleCurrentChange"/>
    </div>

    <!-- 详情 -->
    <view-leave-approve ref="viewLeaveApply" :status-options="statusOptions"/>

    <!-- 编辑 -->
    <edit-leave-approve ref="editLeaveApply" :status-options="statusOptions"/>
  </div>
</template>

<script>
import LEAVE_APPLY_API from '@api/api_leave_apply'
import iTable from '@components/common/iTable'
import iPagination from '@components/common/iPagination'
import editLeaveApprove from '@views/workflow/leave//todo/editLeaveApprove'
import viewLeaveApprove from '@views/workflow/leave//todo/viewLeaveApprove'

export default {
  name: 'LeaveApprove',
  components: { iTable, iPagination, editLeaveApprove, viewLeaveApprove },
  data() {
    return {
      // label宽度
      formLabelWidth: 'calc(14vh - 0px)',
      // 表格高度
      tableHeight: 'calc(95vh - 200px)',
      search: '',
      tableTitle: [
        { prop: 'id', label: 'ID', fixed: true, sort: true },
        { prop: 'processInstanceId', label: '流程实例ID', sort: true, filters: [] },
        { prop: 'userId', label: '用户ID' },
        { prop: 'reason', label: '原因' },
        { prop: 'leaveType', label: '请假类型' },
        { prop: 'days', label: '请假天数' },
        { prop: 'startTime', label: '开始时间' },
        { prop: 'endTime', label: '结束时间' },
        { prop: 'userName', label: '请假人' },
        { prop: 'processStatus', label: '状态' },
        { prop: 'createTime', label: '创建时间', formatter: this.dateTimeFormatter },
        { prop: 'submitTime', label: '提交时间', formatter: this.dateTimeFormatter },
        { prop: 'updateTime', label: '更新时间', formatter: this.dateTimeFormatter },
        // 此处为操作栏，不需要可以删除，clickFun绑定此操作按钮的事件
        { prop: 'operation', label: '操作', fixed: 'right', width: 143,
          operation: [
            { name: '查看', style: 'primary', clickFun: this.viewLeaveApply, disabled: this.hasPermission('sys:user:view') },
            { name: '审批', style: 'primary', clickFun: this.approveLeaveApply, disabled: this.hasPermission('sys:user:view') }
          ]
        }
      ],
      tableData: [],
      multipleSelection: [],
      total: 0,
      pageNum: 1,
      pageSize: 10,
      statusOptions: []
    }
  },
  created: function() {
    const that = this
    that.initTable()
  },
  methods: {
    addLeaveApply: function() {
      const that = this
      that.$refs['addLeaveApply'].openDialog()
    },
    viewLeaveApply: function(row) {
      const that = this
      that.$refs['viewLeaveApply'].openDialog(row)
    },
    approveLeaveApply: function(row) {
      const that = this
      that.$refs['editLeaveApply'].openDialog(row)
    },
    initTable: function() {
      const that = this
      const userInfo = JSON.parse(sessionStorage.getItem('access-user')) // 从sessionStorage中获取登录用户信息
      // 定义请求参数
      let params = {
        search: that.search,
        pageNum: that.pageNum,
        pageSize: that.pageSize
      }
      params = { ...params, ...userInfo }
      // 调用接口
      LEAVE_APPLY_API.findMyToDo(params).then(function(result) {
        if (result.code === 200) {
          that.total = result.map.leaveApplyList.total
          that.currentPage = result.map.leaveApplyList.pageNum
          that.pageSize = result.map.leaveApplyList.pageSize
          that.tableData = result.map.leaveApplyList.list

          that.filtersHandler(that.tableData)
        }
      })
    },
    receiveChild: function(data) {
      const that = this
      that.multipleSelection = data
      console.log('表格组件接收子组件的数据', that.multipleSelection)
    },
    deleteLeaveApply: function() {
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
      LEAVE_APPLY_API.deleteLeaveApplyByIds(params).then(function(result) {
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
      const that = this
      if (cellValue === undefined) {
        return ''
      }
      return that.$moment(cellValue).format('YYYY-MM-DD HH:mm:ss')
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
    rowClick: function(row) {
      console.log(row)
    },
    statusFormatter: function(row, column, cellValue, index) {
      if (cellValue === undefined) {
        return ''
      }
      let result = ''
      for (var i = 0; i < this.statusOptions.length; i++) {
        if (cellValue === this.statusOptions[i].value) {
          result = this.statusOptions[i].label
        }
      }

      return result
    }
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style lang="scss" scoped>
::v-deep .el-select,.el-cascader {
    display: block;
    position: relative;
}

::v-deep .el-dialog .el-dialog__body {
  border-top: 1px solid #dcdfe6;
  border-bottom: 1px solid #dcdfe6;
  max-height: calc(85vh - 260px);
  overflow-y: auto;
}
</style>
