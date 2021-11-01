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
        <el-button v-if="hasPermission('sys:user:add')" type="success" plain @click="addSysUser">增加</el-button>
        <el-button v-if="hasPermission('sys:user:delete')" type="danger" plain @click="deleteSysUser">删除</el-button>
        <el-button type="danger" plain @click="importFile">导入</el-button>
      </div>

      <i-table
        ref="iTable"
        :table-title="tableTitle"
        :table-data="tableData"
        :table-height="tableHeight"
        @transmitParent="receiveChild"
        @rowClick="rowClick"
        @handleView="viewSysUser"
        @handleEdit="editViewSysUser"/>
      <i-pagination
        ref="iPagination"
        :total="total"
        :current-page="pageNum"
        :page-size="pageSize"
        @handleSizeChange="handleSizeChange"
        @handleCurrentChange="handleCurrentChange"/>
    </div>

    <!-- 新增 -->
    <add-sys-user ref="addSysUser" :dept-options="deptOptions" :status-options="statusOptions"/>

    <!-- 详情 -->
    <view-sys-user ref="viewSysUser" :dept-options="deptOptions" :status-options="statusOptions" :tile-dept-data="tileDeptData" :sys-role-list="sysRoleList"/>

    <!-- 编辑 -->
    <edit-sys-user ref="editSysUser" :dept-options="deptOptions" :status-options="statusOptions" :tile-dept-data="tileDeptData" :sys-role-list="sysRoleList"/>

    <el-dialog :visible.sync="uploadDialogFormVisible" title="文件上传">
      <el-upload
        ref="upload"
        :auto-upload="false"
        :http-request="httpRequest"
        class="upload-demo"
        drag
        action=""
        multiple>
        <i class="el-icon-upload"/>
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
        <div slot="tip" class="el-upload__tip">只能上传jpg/png文件，且不超过500kb</div>
      </el-upload>
      <div slot="footer" class="dialog-footer">
        <el-button @click="uploadDialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="uploadSimit">上 传</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import USER_API from '@api/api_sys_user'
import DICT_API from '@api/api_sys_dict'
import DEPT_API from '@api/api_sys_dept'
import iTable from '@components/common/iTable'
import iPagination from '@components/common/iPagination'
import addSysUser from '@views/system/user/addSysUser'
import editSysUser from '@views/system/user/editSysUser'
import viewSysUser from '@views/system/user/viewSysUser'

import $ from 'jquery'

export default {
  name: 'SysUser',
  components: { iTable, iPagination, addSysUser, editSysUser, viewSysUser },
  data() {
    return {
      // label宽度
      formLabelWidth: 'calc(14vh - 0px)',
      // 表格高度
      tableHeight: 'calc(95vh - 200px)',
      search: '',
      tableTitle: [
        { prop: 'id', label: 'ID', fixed: true, sort: true },
        { prop: 'name', label: '姓名', sort: true, filters: [] },
        { prop: 'email', label: '邮箱' },
        { prop: 'mobile', label: '手机号' },
        { prop: 'status', label: '状态', formatter: this.statusFormatter },
        { prop: 'deptId', label: '部门', formatter: this.deptFormatter },
        { prop: 'roleId', label: '角色', formatter: this.roleFormatter },
        { prop: 'createBy', label: '创建人' },
        { prop: 'createTime', label: '创建时间', formatter: this.dateTimeFormatter },
        { prop: 'lastUpdateBy', label: '更新人' },
        { prop: 'lastUpdateTime', label: '更新时间', formatter: this.dateTimeFormatter },
        // 此处为操作栏，不需要可以删除，clickFun绑定此操作按钮的事件
        { prop: 'operation', label: '操作', fixed: 'right', width: 143,
          operation: [
            { name: '查看', style: 'primary', clickFun: this.viewSysUser, disabled: this.hasPermission('sys:user:view') },
            { name: '修改', style: 'primary', clickFun: this.editViewSysUser, disabled: this.hasPermission('sys:user:edit') }
          ]
        }
      ],
      tableData: [],
      multipleSelection: [],
      total: 0,
      pageNum: 1,
      pageSize: 10,
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
      statusOptions: [],
      sysDeptList: [],
      sysRoleList: [],
      role_ids: [],
      deptOptions: [],
      tileDeptData: [],
      // 部门ids,用于回显
      deptIds: [],
      uploadDialogFormVisible: false
    }
  },
  created: function() {
    const that = this
    that.initTable()
    that.findSysDictByDesc()
  },
  methods: {
    addSysUser: function() {
      const that = this
      that.$refs['addSysUser'].openDialog()
    },
    viewSysUser: function(row) {
      const that = this
      that.$refs['viewSysUser'].openDialog(row)
    },
    editViewSysUser: function(row) {
      const that = this
      that.$refs['editSysUser'].openDialog(row)
    },
    initTable: function() {
      const that = this
      // 定义请求参数
      let params = {
        search: that.search,
        pageNum: that.pageNum,
        pageSize: that.pageSize
      }
      // 调用接口
      USER_API.findAllSysUser(params).then(function(result) {
        if (result.code === 200) {
          that.sysDeptList = result.map.sysDeptList
          that.sysRoleList = result.map.sysRoleList

          that.total = result.map.sysUsers.total
          that.currentPage = result.map.sysUsers.pageNum
          that.pageSize = result.map.sysUsers.pageSize
          that.tableData = result.map.sysUsers.list

          that.filtersHandler(that.tableData)
        }
      })
      // 重置请求参数
      params = {}
      // 调用接口
      DEPT_API.findSysDeptList(params).then(function(result) {
        if (result.code === 200) {
          that.deptOptions = result.map.sysDepts.list
          that.tileDeptList(result.map.sysDepts.list)
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
      USER_API.deleteSysUserByIds(params).then(function(result) {
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
    findSysDictByDesc: function() {
      const that = this
      // 定义请求参数
      const params = { description: '用户状态' }
      // 调用接口
      DICT_API.findSysDictByDesc(params).then(function(result) {
        if (result.code === 200) {
          that.statusOptions = result.map.selectOptions
        }
      })
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
    },
    deptFormatter: function(row, column, cellValue, index) {
      if (cellValue === undefined) {
        return ''
      }
      let result = ''
      for (var i = 0; i < this.sysDeptList.length; i++) {
        if (cellValue === this.sysDeptList[i].value) {
          result = this.sysDeptList[i].label
        }
      }

      return result
    },
    roleFormatter: function(row, column, cellValue, index) {
      if (cellValue === undefined || cellValue === null) {
        return ''
      }
      let result = ''
      const valueArr = cellValue.split(',')
      if (valueArr.length > 1) {
        const resultArr = []
        for (var i = 0; i < valueArr.length; i++) {
          for (var j = 0; j < this.sysRoleList.length; j++) {
            if (valueArr[i] === this.sysRoleList[j].value) {
              resultArr.push(this.sysRoleList[j].remark)
            }
          }
        }
        result = resultArr.join(',')
      } else {
        for (var k = 0; k < this.sysRoleList.length; k++) {
          if (cellValue === this.sysRoleList[k].value) {
            result = this.sysRoleList[k].remark
          }
        }
      }

      return result
    },
    // 平铺数据
    tileDeptList(deptDataList) {
      const that = this
      deptDataList.forEach(function(item, index) {
        that.tileDeptData.push(item)
        if (item.children && item.children.length) {
          that.tileDeptList(item.children)
        }
      })
    },
    test() {
      // 定义请求参数
      const params = { description: '用户状态' }
      // 调用接口
      USER_API.export(params)
      var url = '/downloadFile'
      $.fileDownload(url, {
        httpMethod: 'POST',
        data: {
          result: '123456'
        },
        prepareCallback: function(url) {
          console.log('下载中...')
        },
        successCallback: function(url) {
          console.log('下载成功！！')
        },
        failCallback: function(html, url) {
          console.log('下载失败！！')
        }
      })
    },
    importFile() {
      const that = this
      that.uploadDialogFormVisible = true
    },
    httpRequest(param) {
      const that = this
      var file = param.file
      var formData = new FormData() // 获取上传表单（文件）
      formData.append('file', file)
      // 调用接口
      USER_API.import(formData).then(function(result) {
        if (result.code === 200) {
          that.$message({
            message: '恭喜你，上传成功 ' + result.map.fileName,
            type: 'success'
          })
        }
      })
    },
    uploadSimit() {
      this.$refs.upload.submit()
    }
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style lang="scss" scoped>
/deep/ .el-select,.el-cascader {
    display: block;
    position: relative;
}

/deep/ .el-dialog .el-dialog__body {
  border-top: 1px solid #dcdfe6;
  border-bottom: 1px solid #dcdfe6;
  max-height: calc(85vh - 260px);
  overflow-y: auto;
}
</style>
