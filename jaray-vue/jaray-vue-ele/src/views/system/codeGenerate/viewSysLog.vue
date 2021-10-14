<template>
  <div id="view">
    <el-dialog :visible.sync="viewDialogFormVisible" title="详情">
      <el-form :model="viewForm">
        <el-form-item :label-width="formLabelWidth" label="ID">
          <el-input v-model="viewForm.id" readonly="readonly"/>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="用户名">
          <el-input v-model="viewForm.userName" readonly="readonly"/>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="用户操作">
          <el-input v-model="viewForm.operation" readonly="readonly"/>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="方法">
          <el-input v-model="viewForm.method" readonly="readonly"/>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="参数">
          <el-input v-model="viewForm.params" readonly="readonly"/>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="IP地址">
          <el-input v-model="viewForm.ip" readonly="readonly"/>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="创建人">
          <el-input v-model="viewForm.createBy" readonly="readonly"/>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="创建时间">
          <el-date-picker
            v-model="viewForm.createTime"
            :picker-options="pickerOptions"
            type="datetime"
            placeholder="选择日期时间"
            align="right"
            format="yyyy-MM-dd HH:mm:ss"
            value-format="yyyy-MM-dd HH:mm:ss"/>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="更新人">
          <el-input v-model="viewForm.lastUpdateBy" readonly="readonly"/>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="更新时间">
          <el-date-picker
            v-model="viewForm.lastUpdateTime"
            :picker-options="pickerOptions"
            type="datetime"
            placeholder="选择日期时间"
            align="right"
            format="yyyy-MM-dd HH:mm:ss"
            value-format="yyyy-MM-dd HH:mm:ss"/>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="viewDialogFormVisible = false">关闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import LOG_API from '@api/api_sys_log'

export default {
  name: 'ViewSysLog',
  components: {},
  data() {
    return {
      // label宽度
      formLabelWidth: 'calc(14vh - 0px)',
      viewDialogFormVisible: false,
      viewForm: {},
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
          that.viewForm = result.map.sysLog
          that.viewDialogFormVisible = true
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
