<template>
  <div id="edit">
    <a-modal
      :visible.sync="editDialogFormVisible"
      title="编辑"
      @ok="handleOk"
      @cancel="handleCancel">
      <a-form-model :model="editForm">
        <a-form-model-item :label-width="formLabelWidth" label="任务名称">
          <a-input v-model="editForm.jobName"/>
        </a-form-model-item>
        <a-form-model-item :label-width="formLabelWidth" label="任务组名">
          <a-input v-model="editForm.jobGroupName"/>
        </a-form-model-item>
        <a-form-model-item :label-width="formLabelWidth" label="时间表达式">
          <a-input v-model="editForm.jobTime"/>
        </a-form-model-item>
        <a-form-model-item :label-width="formLabelWidth" label="任务状态">
          <a-input v-model="editForm.jobStatus"/>
        </a-form-model-item>
        <a-form-model-item :label-width="formLabelWidth" label="任务描述">
          <a-input v-model="editForm.description"/>
        </a-form-model-item>
      </a-form-model>
      <div slot="footer" class="dialog-footer">
        <a-button @click="editDialogFormVisible = false">取 消</a-button>
        <a-button type="primary" @click="editQuartz">确 定</a-button>
      </div>
    </a-modal>
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
          that.$message.success('恭喜你，编辑成功')
          that.editDialogFormVisible = false
        }
      })
    },
    handleOk(e) {
      const that = this
      that.editDialogFormVisible = false
    },
    handleCancel(e) {
      const that = this
      that.editDialogFormVisible = false
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
