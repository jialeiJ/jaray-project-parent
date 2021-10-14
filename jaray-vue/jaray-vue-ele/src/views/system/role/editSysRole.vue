<template>
  <div id="edit">
    <el-dialog :visible.sync="editDialogFormVisible" title="编辑">
      <el-form :model="editForm">
        <el-form-item :label-width="formLabelWidth" label="ID">
          <el-input v-model="editForm.id" autocomplete="off"/>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="姓名">
          <el-input v-model="editForm.name" autocomplete="off"/>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="备注">
          <el-input v-model="editForm.remark" autocomplete="off"/>
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
        <el-button type="primary" @click="editSysRole">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import SYS_ROLE_API from '@api/api_sys_role'

export default {
  name: 'SysRole',
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
  computed: {

  },
  created: function() {

  },
  methods: {
    openDialog: function(row) {
      const that = this
      // 定义请求参数
      const params = row
      // 调用接口
      SYS_ROLE_API.viewSysRole(params).then(function(result) {
        if (result.code === 200) {
          that.editForm = result.map.sysRole
          that.editDialogFormVisible = true
        }
      })
    },
    editSysRole: function() {
      const that = this
      // 定义请求参数
      const params = that.editForm
      // 调用接口
      SYS_ROLE_API.updateSysRole(params).then(function(result) {
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
