<template>
  <div id="add">
    <el-dialog :visible.sync="addDialogFormVisible" title="新增">
      <el-form :model="addForm">
        <el-form-item :label-width="formLabelWidth" label="ID">
          <el-input v-model="addForm.id" autocomplete="off" placeholder="自动生成ID，无需填写"/>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="用户名">
          <el-input v-model="addForm.userName" autocomplete="off"/>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="用户操作">
          <el-input v-model="addForm.operation" autocomplete="off"/>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="方法">
          <el-input v-model="addForm.method" autocomplete="off"/>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="参数">
          <el-input v-model="addForm.params" autocomplete="off"/>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="IP地址">
          <el-input v-model="addForm.ip" autocomplete="off"/>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="创建人">
          <el-input v-model="addForm.createBy" autocomplete="off"/>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="创建时间">
          <el-date-picker
            v-model="addForm.createTime"
            :picker-options="pickerOptions"
            type="datetime"
            placeholder="选择日期时间"
            align="right"
            format="yyyy-MM-dd HH:mm:ss"
            value-format="yyyy-MM-dd HH:mm:ss"/>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="更新人">
          <el-input v-model="addForm.lastUpdateBy" autocomplete="off"/>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="更新时间">
          <el-date-picker
            v-model="addForm.lastUpdateTime"
            :picker-options="pickerOptions"
            type="datetime"
            placeholder="选择日期时间"
            align="right"
            format="yyyy-MM-dd HH:mm:ss"
            value-format="yyyy-MM-dd HH:mm:ss"/>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="addDialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="addSysLog">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import Log_API from '@api/api_sys_log'

export default {
  name: 'AddSysLog',
  components: {},
  data() {
    return {
      // label宽度
      formLabelWidth: 'calc(14vh - 0px)',
      addDialogFormVisible: false,
      addForm: {},
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
    openDialog: function() {
      const that = this
      that.addDialogFormVisible = true
    },
    addSysLog: function() {
      const that = this
      // 定义请求参数
      const params = that.addForm
      // 调用接口
      Log_API.addSysLog(params).then(function(result) {
        if (result.code === 200) {
          that.$parent.initTable()
          that.$message({
            message: '恭喜你，添加成功',
            type: 'success'
          })
          that.addDialogFormVisible = false
          that.addForm = {}
        }
      })
    }
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style lang="scss" scoped>
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

/deep/ .el-dialog .el-dialog__body {
  border-top: 1px solid #dcdfe6;
  border-bottom: 1px solid #dcdfe6;
  max-height: calc(85vh - 260px);
  overflow-y: auto;
}
</style>
