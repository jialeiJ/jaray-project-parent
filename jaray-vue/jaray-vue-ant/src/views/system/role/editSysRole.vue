<template>
  <div id="edit">
    <a-modal
      :visible.sync="editDialogFormVisible"
      title="编辑"
      @ok="handleOk"
      @cancel="handleCancel">
      <a-form-model :model="editForm" :label-col="labelCol" :wrapper-col="wrapperCol">
        <a-form-model-item label="ID">
          <a-input v-model="editForm.id" autocomplete="off"/>
        </a-form-model-item>
        <a-form-model-item label="姓名">
          <a-input v-model="editForm.name" autocomplete="off"/>
        </a-form-model-item>
        <a-form-model-item label="备注">
          <a-input v-model="editForm.remark" autocomplete="off"/>
        </a-form-model-item>
        <a-form-model-item label="创建人">
          <a-input v-model="editForm.createBy" autocomplete="off"/>
        </a-form-model-item>
        <a-form-model-item label="创建时间">
          <a-date-picker
            v-model="editForm.createTime"
            show-time
            show-today
            placeholder="选择日期时间"
            @change="onChange"
            @ok="onOk" />
        </a-form-model-item>
        <a-form-model-item label="更新人">
          <a-input v-model="editForm.lastUpdateBy" autocomplete="off"/>
        </a-form-model-item>
        <a-form-model-item label="更新时间">
          <a-date-picker
            v-model="editForm.lastUpdateTime"
            show-time
            show-today
            placeholder="选择日期时间"
            @change="onChange"
            @ok="onOk" />
        </a-form-model-item>
      </a-form-model>
      <div slot="footer" class="dialog-footer">
        <a-button @click="editDialogFormVisible = false">取 消</a-button>
        <a-button type="primary" @click="editSysRole">确 定</a-button>
      </div>
    </a-modal>
  </div>
</template>

<script>
import SYS_ROLE_API from '@api/api_sys_role'

export default {
  name: 'SysRole',
  components: {},
  data() {
    return {
      labelCol: { span: 4 },
      wrapperCol: { span: 14 },
      editDialogFormVisible: false,
      editForm: {}
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
/deep/ .ant-modal .ant-modal-body {
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
