<template>
  <div>
    <div id="table">
      <div style="margin-bottom: 10px">
        <a-input
          v-model="search"
          placeholder="请输入名称"
          clearable
          style="width: 200px"/>
        <a-button type="success" plain @click="initTable">查询</a-button>
        <a-button type="danger" plain @click="addJob">添加任务</a-button>
        <a-button type="danger" plain @click="deleteJob">删除任务</a-button>
      </div>

      <i-table
        ref="iTable"
        :table-title="tableTitle"
        :table-data="tableData"
        :table-height="tableHeight"
        @transmitParent="receiveChild"/>
      <i-pagination
        ref="iPagination"
        :total="total"
        :current-page="pageNum"
        :page-size="pageSize"
        @handleSizeChange="handleSizeChange"
        @handleCurrentChange="handleCurrentChange"/>
    </div>

    <!-- 新增 -->
    <add-quartz ref="addQuartz"/>

    <!-- 详情 -->
    <view-quartz ref="viewQuartz"/>

    <!-- 编辑 -->
    <edit-quartz ref="editQuartz"/>
  </div>
</template>

<script>
import QUARTZ_API from '@api/api_quartz'
import iTable from '@components/common/iTable'
import iPagination from '@components/common/iPagination'
import addQuartz from '@views/quartz/addQuartz'
import editQuartz from '@views/quartz/editQuartz'
import viewQuartz from '@views/quartz/viewQuartz'

export default {
  name: 'SysUser',
  components: { iTable, iPagination, addQuartz, editQuartz, viewQuartz },
  data() {
    return {
      // label宽度
      formLabelWidth: 'calc(14vh - 0px)',
      // 表格高度
      tableHeight: 'calc(95vh - 200px)',
      search: '',
      tableTitle: [
        { title: '任务实现类', dataIndex: 'jobClass', key: 'jobClass' },
        { title: '任务名称', dataIndex: 'jobName', key: 'jobName' },
        { title: '任务组名', dataIndex: 'jobGroupName', key: 'jobGroupName', filters: [] },
        { title: '时间表达式', dataIndex: 'jobTime', key: 'jobTime' },
        { title: '任务状态', dataIndex: 'jobStatus', key: 'jobStatus' },
        { title: '任务描述', dataIndex: 'description', key: 'description' },
        // 此处为操作栏，不需要可以删除，clickFun绑定此操作按钮的事件
        { prop: 'operation', label: '操作', fixed: 'right', width: 285,
          operation: [
            { name: '查看', style: 'primary', clickFun: this.viewJob, disabled: true },
            { name: '暂停', style: 'primary', clickFun: this.pauseJob, disabled: true },
            { name: '恢复', style: 'primary', clickFun: this.resumeJob, disabled: true },
            { name: '修改', style: 'primary', clickFun: this.editViewJob, disabled: true }
          ]
        }
      ],
      tableData: [],
      multipleSelection: [],
      total: 0,
      pageNum: 1,
      pageSize: 10
    }
  },
  created: function() {
    const that = this
    that.initTable()
  },
  methods: {
    addJob: function() {
      const that = this
      that.$refs['addQuartz'].openDialog()
    },
    viewJob: function(row) {
      const that = this
      that.$refs['viewQuartz'].openDialog(row)
    },
    editViewJob: function(row) {
      const that = this
      that.$refs['editQuartz'].openDialog(row)
    },
    initTable: function() {
      const that = this
      // 定义请求参数
      const params = {
        search: that.search,
        pageNum: that.pageNum,
        pageSize: that.pageSize
      }
      // 调用接口
      QUARTZ_API.findAllJob(params).then(function(result) {
        if (result.code === 200) {
          that.total = result.map.jobList.total
          that.currentPage = result.map.jobList.pageNum
          that.pageSize = result.map.jobList.pageSize
          that.tableData = result.map.jobList.list
        }
      })
    },
    receiveChild: function(data) {
      const that = this
      that.multipleSelection = data
      console.log('表格组件接收子组件的数据', that.multipleSelection)
    },
    deleteSysUser: function() {
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
      QUARTZ_API.deleteSysUserByIds(params).then(function(result) {
        if (result.code === 200) {
          that.initTable()
          that.$message({
            message: '恭喜你，删除成功',
            type: 'success'
          })
        }
      })
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
    pauseJob: function(row) {
      const that = this
      // 定义请求参数
      const params = row
      // 调用接口
      QUARTZ_API.pauseJob(params).then(function(result) {
        if (result.code === 200) {
          that.$message.success('恭喜你，暂停成功')
          that.initTable()
        }
      })
    },
    resumeJob: function(row) {
      const that = this
      // 定义请求参数
      const params = row
      // 调用接口
      QUARTZ_API.resumeJob(params).then(function(result) {
        if (result.code === 200) {
          that.$message.success('恭喜你，恢复成功')
          that.initTable()
        }
      })
    },
    deleteJob: function() {
      const that = this
      // 定义请求参数
      const params = {}
      // 调用接口
      QUARTZ_API.deleteJob(params).then(function(result) {
        if (result.code === 200) {
          that.initTable()
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
