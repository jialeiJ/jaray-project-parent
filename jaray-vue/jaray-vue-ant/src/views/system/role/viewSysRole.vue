<template>
  <div id="view">
    <a-modal
      :visible.sync="viewDialogFormVisible"
      title="详情"
      @ok="handleOk"
      @cancel="handleCancel">
      <a-form-model :model="viewForm" :label-col="labelCol" :wrapper-col="wrapperCol" layout="horizontal">
        <a-form-model-item label="ID">
          <a-input v-model="viewForm.id" :disabled="true"/>
        </a-form-model-item>
        <a-form-model-item label="姓名">
          <a-input v-model="viewForm.name" :disabled="true"/>
        </a-form-model-item>
        <a-form-model-item label="备注">
          <a-input v-model="viewForm.remark" :disabled="true"/>
        </a-form-model-item>
        <a-form-model-item label="创建人">
          <a-input v-model="viewForm.createBy" :disabled="true"/>
        </a-form-model-item>
        <a-form-model-item label="创建时间">
          <a-date-picker
            :disabled="true"
            v-model="viewForm.createTime"
            show-time
            show-today
            placeholder="选择日期时间"
            @change="onChange"
            @ok="onOk" />
        </a-form-model-item>
        <a-form-model-item label="更新人">
          <a-input v-model="viewForm.lastUpdateBy" :disabled="true"/>
        </a-form-model-item>
        <a-form-model-item label="更新时间">
          <a-date-picker
            :disabled="true"
            v-model="viewForm.lastUpdateTime"
            show-time
            show-today
            placeholder="选择日期时间"
            @change="onChange"
            @ok="onOk" />
        </a-form-model-item>
      </a-form-model>
      <div slot="footer" class="dialog-footer">
        <a-button type="primary" @click="viewDialogFormVisible = false">关闭</a-button>
      </div>
    </a-modal>
  </div>
</template>

<script>
import SYS_ROLE_API from '@api/api_sys_role'

export default {
  name: 'ViewSysRole',
  components: {},
  data() {
    return {
      labelCol: { span: 4 },
      wrapperCol: { span: 14 },
      viewDialogFormVisible: false,
      viewForm: {}
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
          that.viewForm = result.map.sysRole
          that.viewDialogFormVisible = true
        }
      })
    },
    handleOk(e) {
      const that = this
      that.viewDialogFormVisible = false
    },
    handleCancel(e) {
      const that = this
      that.viewDialogFormVisible = false
    },
    onChange(value, dateString) {
    },
    onOk(value) {
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
