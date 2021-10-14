<template>
  <div id="edit">
    <el-dialog :visible.sync="editDialogFormVisible" title="编辑">
      <el-form :model="editForm">
        <el-form-item :label-width="formLabelWidth" label="ID">
          <el-input v-model="editForm.id" autocomplete="off"/>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="用户名">
          <el-input v-model="editForm.userName" autocomplete="off"/>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="用户操作">
          <el-input v-model="editForm.operation" autocomplete="off"/>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="方法">
          <el-input v-model="editForm.method" autocomplete="off"/>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="参数">
          <el-input v-model="editForm.params" autocomplete="off"/>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="IP地址">
          <el-input v-model="editForm.ip" autocomplete="off"/>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="创建人">
          <el-input v-model="editForm.createBy" autocomplete="off"/>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="创建时间">
          <el-date-picker
            v-model="editForm.createTime"
            :picker-options="pickerOptions"
            type="datetime"
            placeholder="选择日期时间"
            align="right"
            format="yyyy-MM-dd HH:mm:ss"
            value-format="yyyy-MM-dd HH:mm:ss"/>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="更新人">
          <el-input v-model="editForm.lastUpdateBy" autocomplete="off"/>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="更新时间">
          <el-date-picker
            v-model="editForm.lastUpdateTime"
            :picker-options="pickerOptions"
            type="datetime"
            placeholder="选择日期时间"
            align="right"
            format="yyyy-MM-dd HH:mm:ss"
            value-format="yyyy-MM-dd HH:mm:ss"/>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="editDialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="editSysLog">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import LOG_API from '@api/api_sys_log'

export default {
  name: 'EditSysLog',
  components: {},
  data() {
    return {
      // label宽度
      formLabelWidth: 'calc(14vh - 0px)',
      editDialogFormVisible: false,
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
      LOG_API.viewSysLog(params).then(function(result) {
        if (result.code === 200) {
          console.log()
          that.editForm = result.map.sysLog
          that.editDialogFormVisible = true
        }
      })
    },
    editSysLog: function() {
      const that = this
      // 定义请求参数
      const params = that.editForm
      // 调用接口
      LOG_API.updateSysLog(params).then(function(result) {
        if (result.code === 200) {
          that.$parent.initTable()
          that.$message({
            message: '恭喜你，编辑成功',
            type: 'success'
          })
          that.editDialogFormVisible = false
        }
      })
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
