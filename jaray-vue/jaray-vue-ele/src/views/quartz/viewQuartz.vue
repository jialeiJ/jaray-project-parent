<template>
  <div id="view">
    <el-dialog :visible.sync="viewDialogFormVisible" title="详情">
      <el-form :model="viewForm">
        <el-form-item :label-width="formLabelWidth" label="任务名称">
          <el-col :span="24">
            <el-input v-model="viewForm.jobName" readonly="readonly"/>
          </el-col>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="任务组名">
          <el-col :span="24">
            <el-input v-model="viewForm.jobGroupName" readonly="readonly"/>
          </el-col>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="时间表达式">
          <el-col :span="24">
            <el-input v-model="viewForm.jobTime" readonly="readonly"/>
          </el-col>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="任务状态">
          <el-col :span="24">
            <el-input v-model="viewForm.jobStatus" readonly="readonly"/>
          </el-col>
        </el-form-item>
        <el-form-item :label-width="formLabelWidth" label="任务描述">
          <el-col :span="24">
            <el-input v-model="viewForm.description" readonly="readonly"/>
          </el-col>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="viewDialogFormVisible = false">关闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import QUARTZ_API from '@api/api_quartz'

export default {
  name: 'ViewQuartz',
  components: {},
  props: {},
  data() {
    return {
      // label宽度
      formLabelWidth: 'calc(14vh - 0px)',
      viewDialogFormVisible: false,
      viewForm: {}
    }
  },
  created: function() {

  },
  methods: {
    openDialog: function(row) {
      const that = this
      that.viewForm = row

      that.viewDialogFormVisible = true
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
