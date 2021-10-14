<template>
  <div id="view">
    <a-modal
      :visible.sync="viewDialogFormVisible"
      title="详情"
      @ok="handleOk"
      @cancel="handleCancel">
      <a-form-model :model="viewForm" :label-col="labelCol" :wrapper-col="wrapperCol" layout="horizontal">
        <a-form-model-item label="任务名称">
          <a-input v-model="viewForm.jobName" :disabled="true"/>
        </a-form-model-item>
        <a-form-model-item label="任务组名">
          <a-input v-model="viewForm.jobGroupName" :disabled="true"/>
        </a-form-model-item>
        <a-form-model-item label="时间表达式">
          <a-input v-model="viewForm.jobTime" :disabled="true"/>
        </a-form-model-item>
        <a-form-model-item label="任务状态">
          <a-input v-model="viewForm.jobStatus" :disabled="true"/>
        </a-form-model-item>
        <a-form-model-item label="任务描述">
          <a-input v-model="viewForm.description" :disabled="true"/>
        </a-form-model-item>
      </a-form-model>
      <div slot="footer" class="dialog-footer">
        <a-button type="primary" @click="viewDialogFormVisible = false">关闭</a-button>
      </div>
    </a-modal>
  </div>
</template>

<script>

export default {
  name: 'ViewQuartz',
  components: {},
  props: {},
  data() {
    return {
      labelCol: { span: 4 },
      wrapperCol: { span: 14 },
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
    },
    handleOk(e) {
      const that = this
      that.viewDialogFormVisible = false
    },
    handleCancel(e) {
      const that = this
      that.viewDialogFormVisible = false
    }
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style lang="scss" scoped>
::v-deep .ant-modal .ant-modal-body {
    border-top: 1px solid #dcdfe6;
    border-bottom: 1px solid #dcdfe6;
    max-height: calc(85vh - 260px);
    overflow-y: auto;
}

.code-box-demo .ant-select {
    margin: 0 8px 10px 0;
}

.ant-row-rtl .code-box-demo .ant-select {
    margin: 0 0 10px 8px;
}

#components-select-demo-search-box .code-box-demo .ant-select {
    margin: 0;
}
</style>
