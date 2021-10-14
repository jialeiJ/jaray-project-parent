<template>
  <div id="edit">
    <el-dialog :visible.sync="editDialogFormVisible" title="编辑">
      <el-form :model="editForm">
        <el-form-item :label-width="formLabelWidth" label="任务名称">
          <el-col :span="24">
            <el-input v-model="editForm.jobName"/>
          </el-col>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="任务组名">
          <el-col :span="24">
            <el-input v-model="editForm.jobGroupName"/>
          </el-col>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="时间表达式">
          <el-col :span="24">
            <el-input v-model="editForm.jobTime"/>
          </el-col>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="任务状态">
          <el-col :span="24">
            <el-input v-model="editForm.jobStatus"/>
          </el-col>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="任务描述">
          <el-col :span="24">
            <el-input v-model="editForm.description"/>
          </el-col>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="editDialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="editQuartz">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import QUARTZ_API from '@api/api_quartz'

export default {
  name: 'EditQuartz',
  components: {},
  props: {},
  data() {
    return {
      // label宽度
      formLabelWidth: 'calc(14vh - 0px)',
      editDialogFormVisible: false,
      editForm: {}
    }
  },
  created: function() {

  },
  methods: {
    openDialog: function(row) {
      const that = this
      that.editForm = row

      that.editDialogFormVisible = true
    },
    editQuartz: function() {
      const that = this
      // 定义请求参数
      const params = that.editForm
      // 调用接口
      QUARTZ_API.updateJob(params).then(function(result) {
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
