<template>
  <div id="edit">
    <el-dialog :visible.sync="editDialogFormVisible" title="编辑">
      <el-form :model="form">
        <el-form-item :label-width="formLabelWidth" label="标题">
          <el-input v-model="form.title"/>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="描述">
          <el-input v-model="form.desc"/>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="描述">
          <el-input v-model="form.desc" type="textarea" readonly="readonly"/>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="editDialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="editComplaint">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import COMPLAINT_API from '@api/api_complaint'

export default {
  name: 'EditComplaint',
  components: {},
  data() {
    return {
      // label宽度
      formLabelWidth: 'calc(14vh - 0px)',
      editDialogFormVisible: false,
      form: {

      }
    }
  },
  created: function() {

  },
  methods: {
    openDialog: function(row) {
      const that = this
      // 定义请求参数
      const params = row
      // 调用接口
      COMPLAINT_API.viewComplaint(params).then(function(result) {
        if (result.code === 200) {
          that.form = result.map.complaint
          that.editDialogFormVisible = true
        }
      })
    },
    editComplaint: function() {
      const that = this
      // 定义请求参数
      const params = that.form
      // 调用接口
      COMPLAINT_API.updateComplaint(params).then(function(result) {
        if (result.code === 200) {
          that.$parent.initTable()
          that.$message({
            message: '恭喜你，编辑成功',
            type: 'success'
          })
          that.editDialogFormVisible = false
        }
      })
    },
    viewComplaint: function(row) {
      const that = this
      // 定义请求参数
      const params = row
      // 调用接口
      COMPLAINT_API.viewComplaint(params).then(function(result) {
        if (result.code === 200) {
          that.form = result.map.complaint
          that.viewDialogFormVisible = true
        }
      })
    },
    titleFormatter: function(row, column, cellValue, index) {
      return cellValue
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
    }
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
</style>
